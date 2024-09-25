<template>
    <div>
        <div class="pb10px">
            <el-input v-model="searchForm.name" @input="handleSearch" class="input" clearable
                placeholder="请输入名称"></el-input>
            <el-button type="primary" icon="el-icon-search" class="mr10px" @click="handleSearch">搜索
            </el-button>
            <el-button type="primary" @click="handleAdd">新增</el-button>
            <el-button type="danger" size="small" @click="batchDelete" :disabled="ids.length <= 0">批量删除</el-button>
        </div>
        <el-table ref="multipleTable" :data="listData" tooltip-effect="dark" style="width: 100%"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column prop="id" label="ID" width="50"></el-table-column>
            <el-table-column prop="name" label="名称"></el-table-column>
            <el-table-column prop="content" label="内容" show-overflow-tooltip></el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
            <el-table-column prop="user" label="发布用户"></el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
                <template slot-scope="scope">
                    <el-button @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                    <el-button type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <div style="margin-top: 20px">
            <el-pagination @current-change="currentChange" @size-change="sizeChange" :page-sizes="pageSizes"
                :page-size="pageInfo.pageSize" :current-page="pageInfo.currentPage" background
                layout="sizes, prev, pager, next" :total="pageInfo.total">
            </el-pagination>
        </div>

        <!--  抽屉组件  -->
        <el-drawer :title="formData.id ? '编辑' : '新增'" :visible.sync="isDrawerDialog" size="400px" direction="rtl"
            :wrapper-closable="false" :before-close="closeDrawer">
            <div class="demo-drawer_content" style="padding-right: 40px;box-sizing: border-box">
                <el-form :model="formData" label-width="100px" class="demo-ruleForm">
                    <slot name="content">
                        <el-form-item label="名称">
                            <el-input v-model="formData.name"></el-input>
                        </el-form-item>
                        <el-form-item label="内容">
                            <el-input v-model="formData.content" type="textarea" rows="10"></el-input>
                        </el-form-item>
                        <!--                        <el-form-item label="创建时间" >-->
                        <!--                            <el-date-picker v-model="formData.createTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择日期时间"></el-date-picker>-->
                        <!--                        </el-form-item>-->
                    </slot>
                </el-form>
            </div>
            <div class="demo-drawer_footer" style="display: flex; justify-content: space-around;">
                <el-button @click="closeDrawer">取消</el-button>
                <el-button type="primary" @click="submitDrawer">提交</el-button>
            </div>
        </el-drawer>

    </div>
</template>

<script>

export default {
    data() {
        return {
            //抽屉控制
            isDrawerDialog: false,
            //列表数据
            listData: [],
            //分页详情
            pageInfo: {
                //当前页
                pageNum: 1,
                //分页大小
                pageSize: 10,
                //总条数
                total: 0
            },
            //搜索
            searchForm: {
                name: ""
            },
            //删除数据的id
            ids: [],
            formData: {},
            value: '',
            pageSizes: [5, 10, 20, 30]
        }
    },

    created() {
        this.getPageList()
    },
    methods: {
        //获取分页数据
        getPageList() {
            let data = Object.assign(this.searchForm, this.pageInfo)
            this.$axios.get("/notice/page", {
                params: data
            }).then(res => {
                this.listData = res.data.list
                this.pageInfo.total = res.data.total
                console.log(res)
            })
        },
        //选择分页
        currentChange(e) {
            this.pageInfo.pageNum = e
            this.getPageList()
            console.log(e)
        },
        //改变分页数量
        sizeChange(e) {
            this.pageInfo.pageSize = e
            this.getPageList()
            console.log(e)
        },
        //搜索
        handleSearch() {
            this.pageInfo.pageNum = 1
            this.getPageList()
        },
        //关闭抽屉
        closeDrawer() {
            this.isDrawerDialog = false
        },
        //提交数据
        submitDrawer() {
            //新增
            if (!this.formData.id) {
                this.$axios.post("/notice/add", this.formData).then(res => {
                    console.log(res)
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
                this.$axios.put("/notice/update", this.formData).then(res => {
                    console.log(res)
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
            this.isDrawerDialog = true
            this.formData = {}
            console.log(this.formData)

        },
        //编辑
        handleEdit(index, row) {
            this.isDrawerDialog = true
            console.log(index, row);
            //https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Object/assign
            this.formData = Object.assign({}, row)
        },
        //表格多选操作
        handleSelectionChange(val) {
            console.log(val)
            //赋值以前先清空
            this.ids = []
            val.forEach(item => {
                this.ids.push(item.id)
            })
        },
        //单选和多选删除 获取id
        batchDelete(rows) {
            if (!Array.isArray(rows)) {
                rows = this.$refs.multipleTable.selection;
            }
            this.ids = rows.map(item => item.id);
            this.$confirm(`此操作将永久删除ID为[${this.ids}]的数据, 是否继续?`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$axios.delete("/notice/delBatch", { data: this.ids }).then(res => {
                    if (res.code === 200) {
                        this.$message({ type: 'success', message: res.msg });
                        this.ids = [];
                        this.getPageList();
                    }
                });
            }).catch(() => {
                this.$message({ type: 'info', message: '已取消删除' });
                this.$refs.multipleTable.clearSelection();
            });
        },
        //单个删除
        handleDelete(index,row) {
            console.log(index,row)
            this.batchDelete([row])
        },
    }
};
</script>

<style scoped>
.footer {
    display: flex;
    justify-content: space-around;
}
</style>
