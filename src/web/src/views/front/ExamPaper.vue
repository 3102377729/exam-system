<template>
    <div>
        <h1 style="text-align: center;font-size: 30px;line-height: 35px;">{{ $route.query.examName }}</h1>
        <h3 v-if="type === '考试'" style="text-align: center">{{ examTime }}</h3>
        <div v-for="(item, i) in list" :key="item.id">
            <div class="exam" v-if="item.type === '单选题'">
                <div>
                    {{ i + 1 }}、[单选题]{{ item.name }}[分值：{{ item.score }}]
                </div>
                <el-radio-group v-model="item.userAnswers">
                    <div>
                        <el-radio label="A" :disabled="type === '评分'">{{ item.options[0] }}</el-radio>
                    </div>
                    <div>
                        <el-radio label="B" :disabled="type === '评分'">{{ item.options[1] }}</el-radio>
                    </div>
                    <div>
                        <el-radio label="C" :disabled="type === '评分'">{{ item.options[2] }}</el-radio>
                    </div>
                    <div>
                        <el-radio label="D" :disabled="type === '评分'">{{ item.options[3] }}</el-radio>
                    </div>
                </el-radio-group>
                <div v-if="type === '评分'">
                    <div>正确答案：{{ item.answer }}</div>
                    <div>用户答案：{{ item.userAnswers }}</div>
                    <div>结果：{{ item.answer === item.userAnswers ? '正确' : '错误' }}</div>
                    <el-input v-model="item.userScore" :disabled="type === '评分'" placeholder="请输入内容"></el-input>
                </div>
            </div>
            <div class="exam" v-if="item.type === '多选题'">
                <div>
                    {{ i + 1 }}、[多选题]{{ item.name }}[分值：{{ item.score }}]
                </div>
                <el-checkbox-group v-model="item.userAnswers" @change="update">
                    <div>
                        <el-checkbox label="A" :disabled="type === '评分'">{{ item.options[0] }}</el-checkbox>
                    </div>
                    <div>
                        <el-checkbox label="B" :disabled="type === '评分'">{{ item.options[1] }}</el-checkbox>
                    </div>
                    <div>
                        <el-checkbox label="C" :disabled="type === '评分'">{{ item.options[2] }}</el-checkbox>
                    </div>
                    <div>
                        <el-checkbox label="D" :disabled="type === '评分'">{{ item.options[3] }}</el-checkbox>
                    </div>
                </el-checkbox-group>
                <div v-if="type === '评分'">
                    <div>正确答案：{{ item.answer }}</div>
                    <div>用户答案：{{ item.userAnswers }}</div>
                    <div>结果：{{
            checkCharactersInString(item.userAnswers, item.answer) ? '正确' : '错误'
        }}
                    </div>
                    <el-input v-model="item.userScore" :disabled="type === '评分'" placeholder="评分"></el-input>
                </div>
            </div>
            <div class="exam" v-if="item.type === '判断题'">
                <div>
                    {{ i + 1 }}、[判断题]{{ item.name }}[分值：{{ item.score }}]
                </div>
                <el-radio-group v-model="item.userAnswers">
                    <div>
                        <el-radio label="对" :disabled="type === '评分'">对</el-radio>
                    </div>
                    <div>
                        <el-radio label="错" :disabled="type === '评分'">错</el-radio>
                    </div>
                </el-radio-group>
                <div v-if="type === '评分'">
                    <div>正确答案：{{ item.answer }}</div>
                    <div>用户答案：{{ item.userAnswers }}</div>
                    <div>结果：{{ item.answer === item.userAnswers ? '正确' : '错误' }}</div>
                    <el-input v-model="item.userScore" :disabled="type === '评分'" placeholder="请输入内容"></el-input>
                </div>
            </div>
            <div class="exam" v-if="item.type === '填空题'">
                <div>
                    {{ i + 1 }}、[填空题]{{ item.name }}[分值：{{ item.score }}]
                </div>
                <div>
                    <el-input style="width: 500px" v-model="item.userAnswers" :disabled="type === '评分'"
                        placeholder="请输入内容" @input="update"></el-input>
                </div>
                <div v-if="type === '评分'">
                    <div>参考答案：{{ item.answer }}</div>
                    <div>用户答案：{{ item.userAnswers }}</div>
                    <el-input v-model="item.userScore" placeholder="请输入评分" :disabled="type === '评分'"></el-input>
                    <div>结果：{{ item.answer === item.userAnswers ? '正确' : '错误' }}</div>
                </div>
            </div>
            <div class="exam" v-if="item.type === '主观题'">
                <div>
                    {{ i + 1 }}、[主观题]{{ item.name }}[分值：{{ item.score }}]
                </div>
                <div>
                    <el-input type="textarea" :rows="10" style="width: 500px" v-model="item.userAnswers"
                        :disabled="type === '评分'" placeholder="请输入内容" @input="update"></el-input>
                </div>
                <div v-if="type === '评分'">
                    <div>参考答案：{{ item.answer }}</div>
                    <div>用户答案：{{ item.userAnswers }}</div>
                    <el-input v-model="item.userScore" placeholder="请输入评分"
                        :disabled="!(userInfo.roleType === 'TEACHER' && type === '评分')"></el-input>

                </div>
            </div>
        </div>
        <div style="padding-bottom: 100px">
            <h3 v-if="type === '评分'">当前总评分为：{{ totalScore }}</h3>
            <div style="text-align: center">
                <el-button type="primary" @click="save" v-if="type !== '评分'">确定交卷</el-button>
                <el-button type="primary" @click="save" v-if="userInfo.roleType !== 'USER'">评分</el-button>
            </div>
        </div>
    </div>
</template>

<script>
import utils from "@/utils/utils";

export default {
    name: "Exam",
    data() {
        return {
            type: this.$route.query.type,
            list: [],
            userInfo: utils.getUserInfo(),
            examTime: "",
            name: "",
            timer: null
        }
    },
    //生命周期钩子函数 页面加载时调用
    created() {
        this.load()
    },
    methods: {
        update() {
            // console.log("多选选中无效果需要强制更新")
            this.$forceUpdate();
        },
        getSort() {

            //试卷按照 选择 多选 填空 主观
            let result = []
            this.list.forEach(item => {
                if (item.type === "单选题") {
                    result.push(item)
                }
            })
            this.list.forEach(item => {
                if (item.type === "多选题") {
                    result.push(item)
                }
            })
            this.list.forEach(item => {
                if (item.type === "判断题") {
                    result.push(item)
                }
            })
            this.list.forEach(item => {
                if (item.type === "填空题") {
                    result.push(item)
                }
            })
            this.list.forEach(item => {
                if (item.type === "主观题") {
                    result.push(item)
                }
            })

            this.list = result
        },
        getExamTime() {
            // 指定时间的字符串表示
            const specifiedDateString = this.$route.query.endTime;
            // 将指定时间解析为 Date 对象
            const specifiedDate = new Date(decodeURIComponent(specifiedDateString.replace(/\+/g, ' ')));

            // 设置定时器，每秒更新显示剩余时间
            this.timer = setInterval(() => {
                const currentTime = new Date();
                const remainingTime = specifiedDate.getTime() - currentTime.getTime();

                // 计算剩余时间的小时、分钟和秒数
                const hours = Math.floor(remainingTime / (1000 * 60 * 60));
                const minutes = Math.floor((remainingTime % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((remainingTime % (1000 * 60)) / 1000);

                // 显示剩余时间
                // console.log(`剩余时间：${hours} 小时 ${minutes} 分钟 ${seconds} 秒`);
                this.examTime = `剩余时间：${hours} 小时 ${minutes} 分钟 ${seconds} 秒`
                // 如果剩余时间小于等于 0，清除定时器
                if (remainingTime <= 0) {
                    clearInterval(this.timer);
                    console.log('指定时间已过');
                    //自动交卷
                    this.save()
                }
            }, 1000);
        },
        load() {
            if (this.type === '考试') {
                this.getExamTime()
                this.$axios.get("/examQuestion/getQuestion?ids=" + this.$route.query.ids, {}).then(res => {
                    this.list = res.data

                    this.list.forEach((item, i) => {
                        if (item.type === '单选题' || item.type === '多选题') {
                            this.list[i].options = JSON.parse(this.list[i].options);
                        }
                        if (item.type === '多选题') {
                            this.list[i].userAnswers = []
                        }
                        if (item.type === '填空题') {
                            this.list[i].userAnswers = ""
                        }

                        if (item.type === '主观题') {
                            this.list[i].userAnswers = ""
                        }
                        // 用户提交之后就可以计算 单选和多选分数
                        this.list[i].userScore = 0
                    });
                    this.getSort()
                })
            } else {
                this.$axios.get("/score/getById?id=" + this.$route.query.id, {}).then(res => {
                    this.list = JSON.parse(res.data.userAnswers)
                    console.log(this.list)
                    this.getSort()
                })
            }
        },
        //arr 考生答案 str正确答案
        checkCharactersInString(arr, str) {
            //标准答案 ABCD ['A','B','C','D']
            let arrAnswer = str.split("")
            //sort 按照相同的规则排序
            return arr.sort().join(",") === arrAnswer.sort().join(",")
        },
        save() {
            this.list.forEach((item, i) => {
                // 用户提交之后就可以计算 单选和多选分数
                //单选
                if (item.type === '单选题') {
                    if (item.answer === item.userAnswers) {
                        this.list[i].userScore = item.score
                    }
                }
                //多选
                if (item.type === '多选题') {
                    if (this.checkCharactersInString(item.userAnswers, item.answer)) {
                        this.list[i].userScore = item.score
                    }
                }
                //判断
                if (item.type === '判断题') {
                    if (item.answer === item.userAnswers) {
                        this.list[i].userScore = item.score
                    }
                }
                //填空
                if (item.type === '填空题') {
                    if (item.answer === item.userAnswers) {
                        this.list[i].userScore = item.score
                    }
                }
                //主观题
                if (item.type === '主观题') {
                    if (item.answer === item.userAnswers) {
                        this.list[i].userScore = item.score
                    }
                }
            });
            let obj = {}
            if (this.type === '考试') {
                obj = {
                    examId: this.$route.query.id,
                    ids: this.$route.query.ids,
                    name: this.$route.query.examName,
                    userId: this.userInfo.id,
                    teacherId: this.$route.query.teacherId,
                    // JSON.stringify 将json数组转换为字符串
                    userAnswers: JSON.stringify(this.list),
                    score: this.totalScore,
                }
            } else {
                obj = {
                    id: this.$route.query.id,
                    userAnswers: JSON.stringify(this.list)
                }
                obj.score = this.totalScore
            }
            if (this.type === '考试') {
                // 有主观题需要老师来手动给分
                if (this.list.some(item => item.type === '主观题')) {
                    obj.isScore = "未评分"
                } else {
                    obj.isScore = "已评分"
                }
                this.$axios.post("/score/add", obj).then(res => {
                    if (res.code === 200) {
                        this.$message.success(res.msg)
                        if ("USER" === this.userInfo.roleType) {
                            this.$router.push("/front/score")
                        } else {
                            this.$router.push("/scoreManage")
                        }
                    }
                })
            } else {
                obj.isScore = "已评分"
                this.$axios.put("/score/update", obj).then(res => {
                    if (res.code === 200) {
                        this.$message.success(res.msg)
                        if ("USER" === this.userInfo.roleType) {
                            this.$router.push("/front/score")
                        } else {
                            this.$router.push("/scoreManage")
                        }
                    }
                })
            }



        },
    },
    computed: {
        totalScore() {
            //将数组列表中的值进行累加
            return this.list.reduce((sum, exam) => parseInt(sum) + parseInt(exam.userScore), 0);
        }
    }
}
</script>


<style>
.exam div {
    margin: 15px auto;
}

.headerBg {
    background: #eee !important;
}
</style>
