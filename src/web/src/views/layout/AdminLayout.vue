<template>
    <el-container style="height: 100%; ">
        <el-aside width="200px" style="background-color: #000000;overflow-x:hidden">
            <h3 class="title">在线考试后台管理系统</h3>
            <!-- 图标更换    https://element.eleme.cn/#/zh-CN/component/icon-->
            <el-menu
                background-color="#000000"
                text-color="#fff"
                :default-openeds="['user']" router style="border: none" :default-active="$route.path">
                <el-menu-item v-for="(item,i) in roleMenu" v-show="!item.isPage" :key="i" :index="item.path"><i :class="item.iconClass"></i><span slot="title">{{item.title}}</span></el-menu-item>
                <el-submenu index="user"  v-for="item in roleMenu" v-show="item.isPage" :key="item.name">
                    <template slot="title">
                        <i class="el-icon-s-home"></i><span>{{ item.title }}</span>
                    </template>
                    <el-menu-item v-for="(current,index) in item.list" :key="index" :index="current.path"> <i :class="current.iconClass"></i><span slot="title">{{current.title}}</span></el-menu-item>
                </el-submenu>
            </el-menu>
        </el-aside>
        <el-container>
            <el-header style="text-align: right; font-size: 12px">
                <el-avatar style="width: 30px;height: 30px;position: relative;top: 10px;right: 10px" shape="square" :size="30" :src="userInfo.img"></el-avatar>
                <el-dropdown>
                    <span class="el-dropdown-link">
                        {{ userInfo.username }}<i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                        <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item><span @click="editUserInfo">个人信息</span></el-dropdown-item>
                        <el-dropdown-item><span @click="editPassword">修改密码</span></el-dropdown-item>
                        <el-dropdown-item divided><span @click="logout">退出登录</span></el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </el-header>
            <el-main>
                <router-view/>
            </el-main>
        </el-container>
    </el-container>
</template>
<script>
import tools from "@/utils/utils";

export default {
    data() {
        return {
            userInfo:tools.getUserInfo(),
            roleMenu:[]
        }
    },
    created() {
    },
    mounted() {
        this.roleMenu=tools.getRoleMenu()
    },
    methods: {
        logout() {
            this.$message({
                message: "退出登录成功，正在跳转",
                type: "success"
            })
            localStorage.removeItem("systemUser")
            localStorage.removeItem("systemRoleMenu")
            setTimeout(() => {
                this.$router.push({path: "/login"})
            }, 1000)
        },
        editUserInfo() {
            this.$router.push({path: "/editUserInfo"})
        },
        editPassword() {
            this.$router.push({path: "/editPassword"})
        },
    }
};
</script>

<style>
.el-header {
    background-color: #B3C0D1;
    /*color: #333;*/
    line-height: 60px;
}

/*更改下拉框的样式*/
.el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
}

.el-icon-arrow-down {
    font-size: 12px;
}

.title {
    text-align: center;
    color: #fff;
    height: 30px;
    line-height: 30px;
    margin-top: 11px;
}
/*搜索框大小定义*/
.input {
    width: 200px !important;
    margin-right: 10px
}

.mr10px {
    margin-right: 10px;
}

.pb10px {
    padding-bottom: 10px;
}
</style>

