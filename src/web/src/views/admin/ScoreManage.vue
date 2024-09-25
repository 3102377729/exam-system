<template>
    <div>
        <div class="pb10px">
            <el-form ref="searchForm" :model="searchForm" inline>
                <el-form-item label="考试名称" prop="name">
                    <el-input v-model="searchForm.name" clearable></el-input>
                </el-form-item>
                <el-form-item label="">
                    <el-button type="primary" @click="handleSearch" icon="el-icon-search">搜 索</el-button>
                    <el-button @click="resetForm" icon="el-icon-refresh-left">重 置</el-button>
                </el-form-item>

            </el-form>
        </div>

        <el-button type="primary" @click="handleAdd" icon="el-icon-plus">新 增</el-button>

        <el-button @click="batchDelete" :disabled="ids.length <= 0" icon="el-icon-remove-outline"
            v-if="userInfo.roleType !== 'USER'">批量删除
        </el-button>
        <el-table ref="multipleTable" :data="listData" tooltip-effect="dark" style="width: 100%"
            @selection-change="handleSelectionChange">
            >
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column prop="id" label="ID"></el-table-column>

            <el-table-column prop="name" label="考试名称"></el-table-column>

            <el-table-column prop="user" label="学生姓名"></el-table-column>
            <el-table-column prop="score" label="分数"></el-table-column>
            <el-table-column prop="isScore" label="是否评分"></el-table-column>
            <el-table-column label="操作" fixed="right">
                <template slot-scope="scope">
                    <el-button type="primary" @click="score(scope.row)" icon="el-icon-edit-outline" v-if="(userInfo.roleType === 'USER' && scope.row.isScore === '已评分')
                ||
                (userInfo.roleType === 'TEACHER')">
                        {{ userInfo.roleType === 'USER' ? '查看答题情况' : '评分' }}
                    </el-button>
                    <el-button @click="handleDelete(scope.row)" icon="el-icon-remove-outline">删 除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-pagination @current-change="currentChange" @size-change="sizeChange" :page-size="pageInfo.pageSize"
            :current-page="pageInfo.pageNum" background layout="prev, sizes,pager, next"
            :page-sizes="[10, 20, 30, 40, 50, 100]" :total="total">
        </el-pagination>

        <!--        <el-drawer-->
        <!--            :title="formData.id?'编辑':'新增'"-->
        <!--            :visible.sync="isDrawerDialog"-->
        <!--            size="400px"-->
        <!--            direction="rtl"-->
        <!--            :wrapper-closable="false"-->
        <!--        >-->
        <!--            <div class="demo-drawer__content" style="padding-right:40px;box-sizing: border-box">-->
        <!--                <el-form :model="formData" label-width="100px">-->
        <!--                    <el-form-item label="轮播备注">-->
        <!--                        <el-input v-model="formData.name" autocomplete="off"></el-input>-->
        <!--                    </el-form-item>-->

        <!--                    <el-form-item label="轮播图">-->
        <!--                        <el-upload-->
        <!--                            style="text-align: center;padding-bottom:10px;"-->
        <!--                            :action="$httpUploadFile"-->
        <!--                            :show-file-list="false"-->
        <!--                            :on-success="uploadImgSuccess"-->
        <!--                        >-->
        <!--                            <img v-if="formData.img" :src="formData.img" alt="" style="width: 150px;height: 150px">-->
        <!--                            <i v-else class="el-icon-plus"-->
        <!--                               style="font-size: 28px;width: 150px;height: 150px;line-height: 150px;text-align: center;border: 1px solid #ccc"> </i>-->
        <!--                        </el-upload>-->

        <!--                    </el-form-item>-->

        <!--                    <el-form-item label="跳转网址">-->
        <!--                        <el-input v-model="formData.link" autocomplete="off"></el-input>-->
        <!--                    </el-form-item>-->

        <!--                </el-form>-->
        <!--                <div class="demo-drawer__footer" style="display: flex;justify-content: space-around">-->
        <!--                    <el-button @click="isDrawerDialog=false" icon="el-icon-close">取 消</el-button>-->
        <!--                    <el-button type="primary" @click="submit" icon="el-icon-check">提 交</el-button>-->
        <!--                </div>-->
        <!--            </div>-->
        <!--        </el-drawer>-->

    </div>
</template>

<script>
import utils from "@/utils/utils";

export default {
    name: "BannerManage",
    data() {
        return {
            searchForm: {},
            listData: [{
                name: "123"
            }],
            formData: {},
            isDrawerDialog: false,
            pageInfo: {
                //当前页
                pageNum: 1,
                //分页大小
                pageSize: 10
            },
            total: 0,
            ids: [],
            userInfo: utils.getUserInfo()
        }
    },
    created() {
        this.getPageList()
    },
    methods: {
        //获取分页数据 定义方法
        getPageList() {
            //分页条件 查询条件
            let data = Object.assign(this.searchForm, this.pageInfo)
            this.$axios.get("/score/page", {
                params: data
            }).then(res => {
                //获取列表数据
                this.listData = res.data.list
                //获取分页数据
                this.total = res.data.total
            })
        },
        //点击切换页面
        currentChange(e) {
            this.pageInfo.pageNum = e
            this.getPageList()
        },
        sizeChange(e) {
            this.pageInfo.pageSize = e
            this.getPageList()
        },
        handleAdd() {
            this.formData = {}
            this.isDrawerDialog = true
        },
        submit() {
            //新增
            if (!this.formData.id) {
                this.$axios.post("/score/add", this.formData).then(res => {
                    if (res.code === 200) {
                        this.isDrawerDialog = false
                        this.$message({
                            message: res.msg,
                            type: "success"
                        })
                        this.getPageList()
                    }
                })
            } else {
                //更新
                this.$axios.put("/score/update", this.formData).then(res => {
                    if (res.code === 200) {
                        this.isDrawerDialog = false
                        this.$message({
                            message: res.msg,
                            type: "success"
                        })
                        this.getPageList()
                    }
                })
            }
        },
        handleEdit(index, row) {
            this.formData = Object.assign({}, row)
            this.isDrawerDialog = true
        },

        uploadImgSuccess(res) {
            this.$set(this.formData, "img", res.data.url)
        },
        //单选和多选删除 获取id
        batchDelete(rows) {

            console.log(rows)
            console.log("==================")
            console.log(typeof rows)
            console.log(rows.length)
            //区别：多选单击的时候undefined,单选的时候显示为1

            if (rows.length === undefined) {
                //获取表格中已经被选择的元素
                rows = this.$refs.multipleTable.selection
            }

            //获取id
            this.ids = rows.map(item => item.id)
            //发送请求
            //es6中的模板字符串
            this.$confirm(`此操作将永久删除ID为[${this.ids}]的数据, 是否继续?`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$axios.delete("/score/delBatch", { data: this.ids }).then(res => {
                    if (res.code === 200) {
                        this.$message({
                            type: 'success',
                            message: res.msg
                        });
                        //重置ids变量
                        this.ids = []
                        //重新发起请求
                        this.getPageList()
                    }
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });

                //取消选中
                this.$refs.multipleTable.clearSelection()
            });

        },
        handleDelete(row) {
            this.batchDelete([row])
        },
        handleSelectionChange(rows) {
            //获取id
            this.ids = rows.map(item => item.id)
        },
        handleSearch() {
            //当我们点击搜索的时候，页面会切换到第一页
            this.pageInfo.pageNum = 1
            this.getPageList()
        },
        resetForm() {
            this.$refs['searchForm'].resetFields()
        },
        score(e) {

            //根据角色跳转
            if ("USER" === this.userInfo.roleType) {
                this.$router.push("/front/examPaper?ids=" + e.ids + "&examName=" + e.name + "&type=评分&id=" + e.id)

            } else {
                this.$router.push("/examPaper?ids=" + e.ids + "&examName=" + e.name + "&type=评分&id=" + e.id)
            }
        }


    }
}
</script>

<style scoped></style>