<template>
    <div>
        <el-row>
            <el-col :span="16">
                <div style="width: 95%">
                    <el-carousel height="350px" :interval="10000" class="swiper">
                        <el-carousel-item v-for="item in bannerList" :key="item.id">
                            <a :href="item.link" target="_blank">
                                <img :src="item.img" alt="" style="width: 100%">
                            </a>
                        </el-carousel-item>
                    </el-carousel>
                </div>
            </el-col>
            <el-col :span="8">
                <el-divider>系统通知</el-divider>
                <el-collapse v-model="activeNames" style="height: 300px;overflow-y: auto">
                    <el-collapse-item :title="item.name" :name="item.id" v-for="item in noticeList" :key="item.id">
                        <div>发布时间：{{ item.createTime }}</div>
                        <div>发布内容：{{ item.content }}</div>
                    </el-collapse-item>
                </el-collapse>
            </el-col>
        </el-row>
        <div style="margin: 10px 0;padding-bottom: 50px">
            <el-row :gutter="10">
                <el-divider>在线考试系统</el-divider>
                <el-col :span="19">
                    <div>
                        <el-divider content-position="left">最新资讯</el-divider>
                        <el-card v-for="item in listData" :key="item.id"
                                 style="margin-bottom: 10px;cursor: pointer">
                            <div style="display: flex"  @click="$router.push('/front/detail?id=' + item.id)">
                                <div style="width: 170px;margin-right: 20px">
                                    <img :src="item.img" alt="" style="width: 170px; height: 110px;display: block">
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
        </div>
    </div>
</template>

<script>
import utils from "@/utils/utils";

export default {
    name: "FrontHome",
    data() {
        return {
            bannerList: [],
            listData: [],
            activeNames: ['1'],
            noticeList: [],
            linkList:[]
        }
    },
    created() {
        this.$axios.get("/banner/list").then(res => {
            console.log(res.data)
            this.bannerList = res.data
        })
        this.getNoticeList()
        this.getNewsList()
    },
    methods: {
        getNoticeList() {
            this.$axios.get("/notice/list").then(res => {
                this.noticeList = res.data
            })
            this.$axios.get("/link/list").then(res => {
                this.linkList = res.data
            })
        },
        getNewsList() {
            this.$axios.get("/news/page", {
                params: {
                    pageNum:1,
                    pageSize:5
                }
            }).then(res => {
                this.listData = res.data.list
                this.listData.forEach(v => {
                    v.removeTag = utils.stripHTMLTagsWithRegex(v.content)
                })
                this.total = res.data.total
            })
        },
    }
}
</script>

<style>
.swiper img {
    width: 100%;
    height: 100%;
}
</style>
