# hmi_base

底座（Java + Chrom内核）+ UI（VUE）开发桌面窗体应用。  
思路来源于 Uniapp + Android 底座，前端开发着重交互，后端开发着重数据处理。  
相较于 Electron 等框架可以前后端分离加快开发周期，提供了更多跨语言支持方便和更底层的硬件交互。  
相较于 QT、Swing 等框架可以快速开发更加友好美观的 UI 界面、3D 渲染等。   
目前此方案在多个硬件交互（Serial、Can、CanOpen等）的上位机项目中运行良好。   

特点：       
1.Java 开启后台 HTTP 服务，提供后台接口，开启静态 WEB 服务部署前端项目，使用 http、ws 交互。  
2.JXBrowser 启动窗体，使用 Chrom 内核加载前端入口页。  

## 前端代码

可以直接将 vue 项目打包后复制到资源路径下，默认 index.html 作为入口页面：  
resourse/dist   
