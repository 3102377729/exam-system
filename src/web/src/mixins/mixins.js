/**
 * 定义混入
 * @type {{data(): {message: string}}}
 */
const mixins = {
    data() {
        return {
            //api路径
            api: {
                pageUrl: "",
                addUrl: "",
                updateUrl: "",
                delUrl: ""
            },
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
            searchForm: {},
            //删除数据的id
            ids: [],
            stateList: [
                {
                    value: "启用"
                },
                {
                    value: "禁用"
                }
            ],
            pageSizes: [5,10, 20, 30]
        }
    },
    created() {
        this.getPageList()
    },
    methods: {
        //获取分页数据
        getPageList() {
            let data = Object.assign(this.searchForm, this.pageInfo)
            this.$axios.get(this.api.pageUrl, {
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
        closeDrawer(e) {
            this.isDrawerDialog = e
        },
        //提交数据
        submitDrawer(e) {
            //新增
            if (e.id === undefined) {
                this.$axios.post(this.api.addUrl, e).then(res => {
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
                this.$axios.put(this.api.updateUrl, e).then(res => {
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
        //批量删除
        batchDelete() {
            this.$confirm(`此操作将永久删除ID为[${this.ids}]的数据, 是否继续?`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
                center: true
            }).then(() => {
                //批量删除数据
                this.$axios.delete(this.api.delUrl + "/" + this.ids.splice(",")).then(res => {
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

        //单个删除
        handleDelete(index, row) {
            console.log(index, row);
            this.ids = [row.id]
            this.batchDelete()
        },
    }
}

export default mixins
