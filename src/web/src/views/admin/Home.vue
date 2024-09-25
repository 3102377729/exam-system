<template>
    <div>
        <h3>系统通知</h3>
        <el-collapse>
            <el-collapse-item v-for="item in listData" :key="item.id">
                <template slot="title">
                    <span style="font-weight: bold">{{ item.name }}</span><i class="header-icon el-icon-info"></i>
                </template>
                <div>发布时间：{{item.createTime}}</div>
                <div>发布内容：{{ item.content }}</div>
            </el-collapse-item>
        </el-collapse>
    </div>
</template>
<script>
export default {
    data(){
        return {
            listData:[]
        }
    },

    created(){
        this.getPageList()
    },
    methods:{
        getPageList() {
            this.$axios.get("/notice/list").then(res => {
                this.listData = res.data
            })
        },
    }
}
</script>
