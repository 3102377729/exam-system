<template>
    <div>
        <el-row :gutter="10">
            <el-col :span="6" style="margin-bottom: 10px" v-for="item in listData" :key="item.id">
                <div style="border: 1px solid #ccc;padding-bottom: 10px">
                    <img :src="item.img" alt="" style="width: 100%;height: 150px">
                    <div style="padding:5px;font-size: 16px;font-weight: bold">考试题目：{{ item.name }}</div>
                    <div style="padding:5px">开始时间：{{ item.startTime }}</div>
                    <div style="padding:5px">结束时间：{{ item.endTime }}</div>
                    <div style="padding:5px">发布者：{{ item.user }}</div>
                    <div style="padding:5px">
                        <!--disabled-->
                        <el-button type="primary" :disabled="item.isExam === '已考试'" @click="$router.push(
            {
                path: '/front/examPaper?' +
                    'ids=' + item.question
                    + '&examName=' + item.name
                    + '&endTime=' + item.endTime
                    + '&type=考试'
                    + '&id=' + item.id
                    + '&teacherId=' + item.userId
            }
        )">{{ item.isExam }}</el-button>
                    </div>
                </div>
            </el-col>
        </el-row>
        <div style="margin-bottom: 70px">
            <el-pagination @current-change="currentChange" @size-change="sizeChange" :page-size="pageInfo.pageSize"
                :current-page="pageInfo.pageNum" background layout="prev, sizes,pager, next"
                :page-sizes="[10, 20, 30, 40, 50, 100]" :total="total">
            </el-pagination>
        </div>
    </div>
</template>
<script>
export default {
    name: "",
    data() {
        return {
            total: 0,
            listData: [],
            pageInfo: {
                pageNum: 1,
                pageSize: 10
            }
        }
    },
    created() {
        this.getPageList()
    },
    methods: {
        //获取分页数据 定义方法
        getPageList() {
            //分页条件 查询条件
            this.$axios.get("/exam/page", {
                params: this.pageInfo
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
    }
}
</script>