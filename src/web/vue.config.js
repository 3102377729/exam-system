module.exports = {
    chainWebpack: config =>{
        config.plugin('html')
            .tap(args => {
                args[0].title = "在线考试管理系统";
                args[0].keywords = "在线考试管理系统";
                args[0].description = "在线考试管理系统";
                return args;
            })
    },
     devServer: {
        port: 80, // 设置端口为 80
        // 其他 devServer 配置项
    }
};
