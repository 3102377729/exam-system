import Vue from 'vue'
import App from './App.vue'
import router from './router'

Vue.config.productionTip = false
import Element from 'element-ui'
import "element-ui/lib/theme-chalk/index.css"

//设置项目样式为mini
Vue.use(Element,{size:"mini"});

import axios from '@/axios'
Vue.prototype.$axios = axios
Vue.prototype.$httpUrl=process.env.VUE_APP_SERVER
Vue.prototype.$httpUploadFile=process.env.VUE_APP_SERVER+"/file/upload/exam"
// console.log("环境",process.env.NODE_ENV)
// console.log("服务器",process.env.VUE_APP_SERVER)
// console.log(process.env)

import CustomPages from "@/components/CustomPages";
import CustomDrawer from "@/components/CustomDrawer";
import CustomEditor from "@/components/CustomEditor";
//全局引入
Vue.component("CustomDrawer",CustomDrawer)
Vue.component("CustomPages",CustomPages)
Vue.component("CustomEditor",CustomEditor)

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
