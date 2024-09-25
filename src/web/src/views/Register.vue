<template>
    <div class="container">
        <div class="box">
            <div class="login">用户注册</div>
            <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
                <el-form-item label="" prop="username">
                    <el-input v-model="ruleForm.username" autocomplete="off" placeholder="用户名称" clearable></el-input>
                </el-form-item>
                <el-form-item label="" prop="password">
                    <el-input type="password" v-model="ruleForm.password" autocomplete="off" placeholder="用户密码" clearable ></el-input>
                </el-form-item>
                <el-form-item>
                    <div style="margin-bottom: 15px">
                        <el-button type="primary" @click="submitForm('ruleForm')" >立即注册</el-button>
                    </div>
                    <div>
                        <el-button @click="resetForm('ruleForm')" >重置</el-button>
                    </div>
                    <router-link tag="span" :to="{path:'login'}">
                        <el-button style="margin-top: 10px" >跳转到登录</el-button>
                    </router-link>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>
<script>
export default {
    data() {
        return {
            ruleForm: {
                username: "",
                password: ""
            },
            rules: {
                username: [
                    {required: true, message: '请输入用户名称', trigger: 'blur'},
                ],
                password: [
                    {required: true, message: '请输入密码', trigger: 'blur'},
                ],
            }
        };
    },
    methods: {
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    console.log(this.ruleForm)
                    this.$axios.post("/sysUser/register", this.ruleForm).then(res => {
                        console.log(res)
                        if (res.code === 200) {
                            this.$message({
                                message: "注册成功，正在跳转",
                                type: "success"
                            })
                            setTimeout(() => {
                                this.$router.push({path: "login"})
                            }, 1000)
                        }
                    })
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        resetForm(formName) {
            this.$refs[formName].resetFields();
        }
    }
}
</script>
<style>
.box .el-form-item__content{
    margin-left: 0!important;
}

.box .el-button {
    width: 100%!important;
}

</style>
<style scoped>
.container {
    width: 100%;
    height: 100%;
    position: relative;
    background: url("../assets/register.png") 100% 100%;
    background-size: cover;

}
.login {
    text-align: center;
    margin-bottom: 15px;
    font-size: 22px;
    color: #fff;
}

.box {
    overflow: hidden;
    width:350px;
    padding: 22px 22px 0 22px;
    box-sizing: border-box;
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translateY(-50%) translateX(-50%);
    background-color: skyblue;
    border-radius: 5px;
}
.wx {
    width: 150px;
    height: 150px;
    display: block;
    margin: 0 auto;
}
</style>
