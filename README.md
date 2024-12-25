# hmi_base

底座（Java + Chrom内核）+ UI（VUE）开发桌面窗体应用。  
思路来源于 Uniapp + Android 底座，前端开发着重交互，后端开发着重数据处理。  
相较于 Electron 等框架可以前后端分离加快开发周期，提供了更多跨语言支持方便和更底层的硬件交互。  
相较于 QT、Swing 等框架可以快速开发更加友好美观的 UI 界面、3D 渲染等。   
目前此方案在多个硬件交互（Serial、Can、CanOpen等）的上位机项目中运行良好。   
可以直接打包 exe 免安装运行，打包体积取决于 Chrom 内核版本（version95在百兆内）。  

特点：       
1.Java 开启后台 HTTP 服务，提供后台接口，开启静态 WEB 服务部署前端项目，使用 http、ws 交互。  
2.JXBrowser 启动窗体，使用 Chrom 内核加载前端入口页。  

## 前端代码

使用 HttpServer 开发轻量级静态资源服务器，默认 index.html 作为入口页面。  
可以直接将 vue 项目打包后复制到资源路径下：   
resourse/dist       


## 后端代码

使用 HttpServer 开发轻量级 http 服务器。   
使用 Java-WebSocket 开发轻量级 ws 服务器。   

## UI调试

chrom内核提供了 debug 调试工具，在 jxbrowser 中打开即可：    

也可以开启底部工具栏，实现浏览器的前进、后退、刷新按钮。    

## 数据库

本地使用 sqlite 数据库，sql 脚本放到资源理解下启动会自动建库建表。   
resourse/table    

## 日志系统

使用轻量级日志框架 tinylog。  

## 打包 exe

在 pom 中添加 exec-maven-plugin 插件，在 build 完成后调用自定义打包工具。   
打包默认使用 exe4j，会自动创建 exe4j 打包配置并调用 exe4jc 进行打包，脚本见：   
org.util/BuildTool.java    


