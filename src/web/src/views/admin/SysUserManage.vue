<template>
    <div>
        <div class="pb10px">
            <el-input v-model="searchForm.username" @input="handleSearch" class="input" clearable
                placeholder="请输入用户名称"></el-input>
            <el-button type="primary" icon="el-icon-search" class="mr10px" @click="handleSearch">搜索</el-button>
            <el-button v-if="isAdmin" type="primary" @click="handleAdd">新增</el-button>
            <el-button v-if="isAdmin" type="danger" size="small" @click="batchDelete"
                :disabled="ids.length<=0">批量删除</el-button>
            <el-button type="info" icon="el-icon-plus" @click="exportData">批量导出</el-button>
            <el-upload action="http://localhost:8080/sysUser/import" style="display: inline-block;margin: 10px;">
                <el-button type="info" icon="el-icon-plus" @click="importData">批量导入</el-button>
            </el-upload>
        </div>
        <el-table ref="multipleTable" :data="listData" tooltip-effect="dark" style="width: 100%"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column prop="id" label="ID" width="50"></el-table-column>
            <el-table-column prop="username" label="用户名称"></el-table-column>
            <el-table-column label="用户头像">
                <template slot-scope="scope">
                    <el-image style="width: 50px; height: 50px" :src="scope.row.img"
                        :preview-src-list="[scope.row.img]"></el-image>
                </template>
            </el-table-column>
            <el-table-column prop="content" label="个人简介" show-overflow-tooltip></el-table-column>
            <el-table-column prop="email" label="邮箱" width="100"></el-table-column>
            <el-table-column prop="mobile" label="电话"></el-table-column>
            <el-table-column prop="address" label="地址" width="100" show-overflow-tooltip></el-table-column>
            <el-table-column prop="gender" label="性别"></el-table-column>
            <el-table-column prop="roleType" label="角色类型"></el-table-column>
            <el-table-column prop="state" label="状态">
                <template slot-scope="scope">
                    <el-tag type="success" v-if="scope.row.state==='启用'">启用</el-tag>
                    <el-tag type="danger" v-if="scope.row.state==='禁用'">禁用</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="300" fixed="right">
                <template slot-scope="scope">
                    <el-button v-if="isAdmin" icon="el-icon-edit-outline"
                        @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                    <el-button v-if="isAdmin" icon="el-icon-remove-outline" type="danger"
                        @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                    <el-button v-if="isAdmin" icon="el-icon-edit-outline"
                        @click="resetPassword(scope.row)">密码重置</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!--自定义分页组件-->
        <custom-pages @current-change="currentChange" @size-change="sizeChange" :total="pageInfo.total"
            :page-size="pageInfo.pageSize" :current-page="pageInfo.pageNum">
        </custom-pages>
        <!--自定义分页组件-->

        <!--自定义抽屉组件-->
        <custom-drawer :form-data="formData" :rules-form="ruleForm" @close-drawer="closeDrawer"
            @submit-drawer="submitDrawer" :is-drawer-dialog="isDrawerDialog">
            <div slot="content">
                <el-form-item label="用户头像" prop="img">
                    <el-upload class="avatar-uploader" :action="$httpUploadFile" :show-file-list="false"
                        :on-success="uploadImgSuccess">
                        <img v-if="formData.img" :src="formData.img" class="avatar">
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-form-item>
                <el-form-item label="用户名称" prop="username">
                    <el-input v-model="formData.username" placeholder="用户名称" :disabled="formData.id"></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="formData.password" placeholder="密码" type="password"></el-input>
                </el-form-item>
                <el-form-item label="个人简介" prop="content">
                    <el-input v-model="formData.content" placeholder="个人简介"></el-input>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="formData.email" placeholder="邮箱"></el-input>
                </el-form-item>
                <el-form-item label="电话" prop="mobile">
                    <el-input v-model="formData.mobile" placeholder="电话"></el-input>
                </el-form-item>
                <el-form-item label="地址" prop="address">
                    <el-input v-model="formData.address" placeholder="地址"></el-input>
                </el-form-item>
                <el-form-item label="角色类型" prop="roleType">
                    <el-select v-model="formData.roleType" placeholder="请选择">
                        <el-option v-for="item in roleList" :key="item.id" :label="item.name" :value="item.role">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="性别">
                    <el-radio-group v-model="formData.gender">
                        <el-radio label="男"></el-radio>
                        <el-radio label="女"></el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="状态" prop="state">
                    <el-radio-group v-model="formData.state">
                        <el-radio label="启用"></el-radio>
                        <el-radio label="禁用"></el-radio>
                    </el-radio-group>
                </el-form-item>
            </div>
        </custom-drawer>
        <!--自定义抽屉组件-->
    </div>
</template>

<script>

import mixins from "@/mixins/mixins"
import utils from "@/utils/utils";

export default {
    data() {
        return {
            api: {
                pageUrl: "/sysUser/page",
                addUrl: "/sysUser/add",
                updateUrl: "/sysUser/update",
                delUrl: "/sysUser/delBatch"
            },
            ruleForm: {
                username: [
                    {required: true, message: '请输入', trigger: 'blur'},
                ],
            },
            formData: {},
            searchForm: {
                username: ""
            },
            roleList: [],
            value: '',
            isAdmin:utils.isAdmin()
        }
    },
    mixins: [mixins],
    created() {
        this.getRoleList()
    },
    methods: {

    importData(){

    },
    handleSearch(){
            this.pageInfo.pageNum = 1
            this.getRoleList()
    },
        exportData() {
            if (!this.ids.length) {
                window.open('http://localhost:8080/sysUser/export')
            }
        },
        getRoleList(){
            this.$axios.get("/sysRole/list").then(res => {
                this.roleList = res.data
            })
        },
        resetPassword(row){
            this.$axios.put("/sysUser/resetPassword", row).then(res => {
                console.log(res)
                if (res.code === 200) {
                    this.isDrawerDialog = false
                    this.$message({
                        message: res.msg,
                        type: 'success'
                    });
                }
            })
        },
        uploadImgSuccess(res) {
            this.$set(this.formData,"img",res.data.url)
        }
    }
};
</script>

<style scoped>
.avatar-uploader {
    text-align: center;
    padding-bottom: 10px;
}

.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
}

.avatar-uploader .el-upload:hover {
    border-color: #409EFF;
}

.avatar-uploader-icon {
    border: 1px solid #ccc;
    font-size: 28px;
    color: #8c939d;
    width: 150px;
    height: 150px;
    line-height: 150px;
    text-align: center;
}

.avatar {
    width: 150px;
    height: 150px;
    display: block;
}
</style>
