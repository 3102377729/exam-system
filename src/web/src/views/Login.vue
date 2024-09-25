<template>
    <div class="container">
        <div class="box">
            <div class="login">在线考试后台管理系统</div>
            <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
                <el-form-item label="" prop="username">
                    <el-input v-model="ruleForm.username" autocomplete="off" placeholder="用户名称" clearable>
                        <i slot="prefix" class="el-input__icon el-icon-user"></i>
                    </el-input>
                </el-form-item>

                <el-form-item label="" prop="password">
                    <el-input type="password" v-model="ruleForm.password" autocomplete="off" placeholder="用户密码"
                        clearable>
                        <i slot="prefix" class="el-input__icon el-icon-lock"></i>
                    </el-input>
                </el-form-item>

                <el-form-item prop="captcha">
                    <el-row>
                        <el-col :span="16">
                            <el-input v-model="ruleForm.captcha" autocomplete="off" placeholder="请输入验证码" clearable>
                                <i slot="prefix" class="el-input__icon el-icon-key"></i>
                            </el-input>
                        </el-col>
                        <el-col :span="8">
                            <img :src="captchaUrl" class="captcha-image" alt="验证码" @click="refreshCaptcha" />
                        </el-col>
                    </el-row>
                </el-form-item>

                <el-form-item>
                    <div style="margin-bottom: 15px">
                        <el-button type="primary" @click="submitForm('ruleForm')">立即登录</el-button>
                    </div>
                    <div>
                        <el-button @click="resetForm('ruleForm')">重置</el-button>
                    </div>
                    <router-link tag="span" :to="{ path: 'register' }">
                        <el-button style="margin-top: 10px">跳转到注册</el-button>
                    </router-link>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
import { AddRoutes } from "@/router";
import axios from 'axios';


export default {
    data() {
        return {
            captchaUrl: "",
            ruleForm: {
                username: "admin",
                password: "admin",
                captcha: "" 
            },
            rules: {
                username: [
                    { required: true, message: '请输入用户名称', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' }
                ],
                captcha: [
                    { required: true, message: '请输入验证码', trigger: 'change' }
                ]
            },
            
        };
    },
    
    methods: {
        getCapchUrl() {
            let url = "http://localhost:8080/cap";
            axios.get(url).then(res => {
                if (res.data && res.data.data) {
                    this.captchaUrl = res.data.data;
                } else {
                    console.error("Invalid captcha data", res);
                }
            }).catch(err => {
                console.error("Failed to load captcha image", err);
            });
        },
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    axios.post("/sysUser/login", this.ruleForm).then(res => {
                        if (res.data.code === 200) {
                            this.$message({
                                message: "登录成功，正在跳转",
                                type: "success"
                            });
                            let userInfo = res.data.data;
                            localStorage.setItem("systemUser", JSON.stringify(userInfo));
                            localStorage.setItem("systemRoleMenu", JSON.stringify(userInfo.roleMenu));
                            AddRoutes();
                            if (userInfo.roleType === "USER") {
                                this.$router.push({ path: "/front" });
                            } else {
                                this.$router.push({ path: "/" });
                            }
                        } else {
                            this.$message({
                                message: res.data.message || "登录失败",
                                type: "error"
                            });
                            this.refreshCaptcha(); // Refresh captcha on error
                        }
                    }).catch(err => {
                        console.error("Login request failed", err);
                    });
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        resetForm(formName) {
            this.$refs[formName].resetFields();
        },
        refreshCaptcha() {
            this.getCapchUrl(); // Refresh the captcha URL to get a new image
        }
    },
    created() {
        this.getCapchUrl(); // Initial load of captcha image
    },
}
</script>
<style>
.box .el-form-item__content {
    margin-left: 0 !important;
}

.box .el-button {
    width: 100% !important;
}

</style>
<style scoped>
.container {
    width: 100%;
    height: 100%;
    position: relative;
    background: url("../assets/login.png") 100% 100%;
    background-size: cover;

}

.login {
    text-align: center;
    margin-bottom: 15px;
    font-size: 22px;
    color: #141414;
}

.box {
    overflow: hidden;
    width: 350px;
    padding: 22px 22px 0 22px;
    box-sizing: border-box;
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translateY(-50%) translateX(-50%);
    background-color: whitesmoke;
    border-radius: 5px;
}

.wx {
    width: 150px;
    height: 150px;
    display: block;
    margin: 0 auto;
}
.login-code-img {
    height: 38px;
}
</style>

