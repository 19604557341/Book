const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  devServer: {
    hot: true, //热加载
    host: 'localhost',
    port: 9090, //端口
    https: false, //false关闭https，true为开启
    open: false, //自动打开浏览器
    client: {
      overlay: false
    },
  },
 
  //chainWebpack配置对象
  chainWebpack: config =>{
    //配置title
    config.plugin('html').tap(args => {
      args[0].title = "图书管理系统";
      return args;
    })
    config.plugin('define').tap((definitions) => {
      Object.assign(definitions[0], {
        __VUE_OPTIONS_API__: 'true',
        __VUE_PROD_DEVTOOLS__: 'false',
        __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: 'false'
      })
      return definitions
    })
  },
}) 
