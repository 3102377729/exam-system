<template>
    <div>
        <el-card>
            <h1 style="text-align: center;margin: 15px auto">{{ detail.name }}</h1>
            <el-divider></el-divider>
            <div style="margin-top:35px;display: flex;justify-content: space-between">
                <span><i class="el-icon-view"></i> {{ detail.views }}人看过</span>
                <span><i class="el-icon-timer"></i> {{ detail.createTime }}</span>
                <span><i class="el-icon-user-solid"></i> {{ detail.user }}</span>
            </div>
            <el-divider></el-divider>

            <div v-html="detail.content" style="text-indent: 2em;line-height: 35px"></div>

        </el-card>
    </div>
</template>
<script>
export default {
    data() {
        return {
            id: this.$route.query.id,
            detail: {}
        }
    },
    created() {
        this.$axios.get("/news/getById?id=" + this.id).then(res => {
            this.detail = res.data
        })
    }
}
</script>