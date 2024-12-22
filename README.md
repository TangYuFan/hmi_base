# hmi_base

底座（Java+Chrom内核）+ UI（VUE）开发桌面窗体应用。  
思路来源于 Uniapp + Android 底座，前端开发着重交互，后端开发着重数据处理。  
相较于 Electron 等框架可以前后端分离加快开发周期，提供了更多跨语言支持方便和更底层的硬件交互。  
相较于 QT、Swing 等框架可以快速开发更加友好美观的 UI 界面、3D 渲染等。
目前此方案在多个硬件交互（Serial、Can、CanOpen等）的上位机项目中运行良好。  

原理：  
1.Java + JXBrowser 启动窗体，使用 Chrom 内核处理 WEB 渲染。
2.Java 开启后台 HTTP 服务，提供接口。
3.Java 开启后台静态资源 WEB 服务，部署前端代码，比如 VUE 入口页面 index.html。
4.前后端接口交互来处理逻辑。
