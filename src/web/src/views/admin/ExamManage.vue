<template>
    <div>
        <div class="pb10px">
            <el-form ref="searchForm" :model="searchForm" inline>
                <el-form-item label="考试标题" prop="name">
                    <el-input v-model="searchForm.name" clearable placeholder="请输入考试标题"></el-input>
                </el-form-item>
                <el-form-item label="">
                    <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜 索</el-button>
                    <el-button icon="el-icon-refresh-left" @click="resetForm">重 置</el-button>
                </el-form-item>
            </el-form>
        </div>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增</el-button>
        <el-button icon="el-icon-remove-outline" @click="batchDelete" :disabled="ids.length<=0">批量删除
        </el-button>
        <el-table ref="multipleTable"
                  :data="listData"
                  tooltip-effect="dark"
                  style="width: 100%">
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column prop="id" label="ID" width="50"></el-table-column>
            <el-table-column prop="name" label="考试标题"></el-table-column>
            <el-table-column label="封面图">
                <template slot-scope="scope">
                    <el-image style="width: 60px; height: 60px" :src="scope.row.img"
                              :preview-src-list="[scope.row.img]"></el-image>
                </template>
            </el-table-column>
            <el-table-column prop="startTime" label="开始时间"></el-table-column>
            <el-table-column prop="endTime" label="结束时间"></el-table-column>
            <el-table-column prop="score" label="考试总分"></el-table-column>
            <el-table-column prop="user" label="发布人"></el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
                <template slot-scope="scope">
                    <el-button icon="el-icon-edit-outline" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                    <el-button type="danger" icon="el-icon-remove-outline"
                               @click="handleDelete(scope.$index, scope.row)">删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <div style="margin-top: 20px">
            <el-pagination
                @current-change="currentChange"
                @size-change="sizeChange"
                :page-size="pageInfo.pageSize"
                :current-page="pageInfo.currentPage"
                background
                layout="sizes, prev, pager, next"
                :total="total">
            </el-pagination>
        </div>
        <el-drawer
            :title="formData.id?'编辑':'新增'"
            :visible.sync="isDrawerDialog"
            size="500px"
            direction="rtl"
            :wrapper-closable="false"
        >
            <div class="demo-drawer_content" style="padding-right: 40px;box-sizing: border-box">
                <el-form :model="formData" label-width="100px" class="demo-ruleForm">
                    <slot name="content">
                        <el-form-item label="考试标题">
                            <el-input v-model="formData.name"></el-input>
                        </el-form-item>
                        <el-form-item label="封面图" prop="img">
                            <el-upload
                                style="text-align: center;padding-bottom: 10px;" :action="$httpUploadFile"
                                :show-file-list="false" :on-success="uploadImgSuccess">
                                <img v-if="formData.img" :src="formData.img"
                                     style="width: 150px;height: 150px;display: block;">
                                <i v-else class="el-icon-plus"
                                   style="font-size: 28px;color: #8c939d;width: 150px;height: 150px;line-height: 150px;text-align: center;border: 1px solid #ccc;"></i>
                            </el-upload>
                        </el-form-item>
                        <el-form-item label="开始时间">
                            <el-date-picker v-model="formData.startTime" type="datetime"
                                            value-format="yyyy-MM-dd HH:mm:ss"
                                            placeholder="选择日期时间"></el-date-picker>
                        </el-form-item>
                        <el-form-item label="结束时间">
                            <el-date-picker v-model="formData.endTime" type="datetime"
                                            value-format="yyyy-MM-dd HH:mm:ss"
                                            placeholder="选择日期时间"></el-date-picker>
                        </el-form-item>
                        <el-form-item label="试题列表">
                            <el-select
                                v-model="formData.question"
                                multiple
                                collapse-tags
                                style="width:100%"
                                @change="changeItem"
                                placeholder="请选择">
                                <el-option
                                    v-for="item in options"
                                    :key="item.id"
                                    :label="'[分值：'+item.score+']'+'[题型：'+item.type+']'+item.name"
                                    :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="考试总分">
                            <el-input v-model="formData.score" disabled></el-input>
                        </el-form-item>
                    </slot>
                </el-form>
            </div>
            <div class="demo-drawer_footer" style="display: flex; justify-content: space-around;">
                <el-button @click="isDrawerDialog=false">取消</el-button>
                <el-button type="primary" @click="submit">提交</el-button>
            </div>
        </el-drawer>
    </div>
</template>

<script>
import utils from "@/utils/utils";

export default {
    data() {
        return {
            //抽屉控制
            isDrawerDialog: false,
            //列表数据
            listData: [],
            //总条数
            total: 0,
            //分页详情
            pageInfo: {
                //当前页
                pageNum: 1,
                //分页大小
                pageSize: 10
            },
            //搜索
            searchForm: {
                name: ""
            },
            formData: {},
            //删除数据的id
            ids: [],
            userInfo: utils.getUserInfo(),
            options: [],

        }
    },

    created() {
        this.getPageList()
    },
    methods: {
        //获取分页数据
        getPageList() {
            let data = Object.assign(this.searchForm, this.pageInfo)
            this.$axios.get("/exam/page", {
                params: data
            }).then(res => {
                this.listData = res.data.list
                this.listData.forEach((item) => {
                    item.question = item.question.split(',').map(Number);
                });
                this.total = res.data.total
                console.log(this.listData)
            })

            this.$axios.get("/examQuestion/list").then(res => {
                this.options = res.data
            })
        },
        resetForm() {
            this.$refs['searchForm'].resetFields();
        },
        //选择分页
        currentChange(e) {
            this.pageInfo.pageNum = e
            this.getPageList()
        },
        //改变分页数量
        sizeChange(e) {
            this.pageInfo.pageSize = e
            this.getPageList()
        },
        //搜索
        handleSearch() {
            this.pageInfo.pageNum = 1
            this.getPageList()
        },
        //提交数据
        submit() {
            this.formData.question = this.formData.question.join(",")
            //新增
            if (!this.formData.id) {
                this.$axios.post("/exam/add", this.formData).then(res => {
                    if (res.code === 200) {
                        this.isDrawerDialog = false
                        this.$message({
                            message: res.msg,
                            type: 'success'
                        });
                        this.getPageList()
                    }
                })
            } else {
                //更新
                this.$axios.put("/exam/update", this.formData).then(res => {
                    if (res.code === 200) {
                        this.isDrawerDialog = false
                        this.$message({
                            message: res.msg,
                            type: 'success'
                        });
                        this.getPageList()
                    }
                })
            }
        },
        //添加数据
        handleAdd() {
            this.formData = {}
            this.isDrawerDialog = true
        },
        //编辑
        handleEdit(index, row) {
            this.formData = Object.assign({}, row)
            this.isDrawerDialog = true
        },
        //单个删除
        handleDelete(index, row) {
            this.batchDelete([row])
        },
        //批量删除
        batchDelete(rows) {
            if (rows.length === undefined) {
                rows = this.$refs.multipleTable.selection;
            }
            let ids = rows.map(item => item.id);
            this.$confirm(`此操作将永久删除ID为[${ids}]的数据, 是否继续?`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
                center: true
            }).then(() => {
                //批量删除数据
                this.$axios.delete("/exam/delBatch", {data: ids}).then(res => {
                    if (res.code === 200) {
                        this.$message({
                            message: res.msg,
                            type: 'success'
                        });
                        this.getPageList()
                    }
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
                this.$refs.multipleTable.clearSelection();
            });
        },
        changeItem(e) {
            console.log(e)
            //提交的时候拿到数组 算分

            // 分数
            let score = 0;
            // 遍历列表，检查对象的 id 是否在结果集中，如果相同就累加分数
            this.options.forEach(question => {
                for (let i = 0; i < e.length; i++) {
                    if (parseInt(e[i]) === question.id) {
                        score += question.score; // 累加每个相同的ID对应的分数
                    }
                }
            });
            this.formData.score = score
            console.log("总分：" + this.formData)
        },
        uploadImgSuccess(res) {
            this.$set(this.formData,"img",res.data.url)
        }
    }
};
</script>


