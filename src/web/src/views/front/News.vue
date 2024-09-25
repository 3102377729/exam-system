<template>
    <div>
        <div style="margin: 10px 0;padding-bottom: 50px">
            <el-row :gutter="10">
                <el-col :span="19">
                    <div>
                        <el-card v-for="item in listData" :key="item.id" style="margin-bottom: 10px;cursor: pointer">
                            <div style="display: flex" @click="$router.push('/front/detail?id=' + item.id)">
                                <div style="width: 170px;margin-right: 20px">
                                    <img :src="item.img" alt="" style="width: 170px; height: 110px;">
                                </div>
                                <div style="flex: 1">
                                    <div style="font-size: 17px; text-align: center;">{{ item.name }}</div>
                                    <div style=" line-height: 30px">{{ item.removeTag.slice(0, 60) }}……</div>
                                    <div style="margin-top:5px;display: flex;justify-content: space-between">
                                        <span><i class="el-icon-view"></i> {{ item.views }}人看过</span>
                                        <span><i class="el-icon-timer"></i> {{ item.createTime }}</span>
                                        <span><i class="el-icon-user-solid"></i> {{ item.user }}</span>
                                    </div>
                                </div>
                            </div>
                        </el-card>
                    </div>
                </el-col>
                <el-col :span="5">
                    <el-divider content-position="left">友情链接</el-divider>
                    <el-card>
                        <div style="margin-bottom: 15px" v-for="item in linkList" :key="item.id">
                            <el-link :href="item.link" target="_blank">{{ item.name }}</el-link>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
            <div style="margin-top: 20px">
                <el-pagination @current-change="currentChange" @size-change="sizeChange" :page-size="pageInfo.pageSize"
                    :current-page="pageInfo.currentPage" background layout="sizes, prev, pager, next" :total="total">
                </el-pagination>
            </div>
        </div>
    </div>
</template>

<script>
import utils from "@/utils/utils";

export default {
    name: "FrontHome",
    data() {
        return {
            //总条数
            total: 0,
            //分页详情
            pageInfo: {
                //当前页
                pageNum: 1,
                //分页大小
                pageSize: 10
            },
            listData: [],
            linkList: []
        }
    },
    created() {
        this.$axios.get("/banner/list").then(res => {
            console.log(res.data)
            this.bannerList = res.data
        })
        this.getPageList()
    },
    methods: {
        //获取分页数据
        getPageList() {
            this.$axios.get("/news/page", {
                params: this.pageInfo
            }).then(res => {
                this.listData = res.data.list
                this.listData.forEach(v => {
                    v.removeTag = utils.stripHTMLTagsWithRegex(v.content)
                })
                this.total = res.data.total
            })

            this.$axios.get("/link/list").then(res => {
                this.linkList = res.data
            })

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

    }
}
</script>