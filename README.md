# react-native-updater

## Getting started

`$ npm install react-native-updater --save`

### Mostly automatic installation

`$ react-native link react-native-updater`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import cn.cnlee.commons.updater.RNUpdaterPackage;` to the imports at the top of the file
  - Add `new RNUpdaterPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-updater'
  	project(':react-native-updater').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-updater/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-updater')
  	```


## Usage
使用说明
```javascript
import {CheckUpdate, Tools} from 'react-native-updater';

// 检测版本更新方法
CheckUpdate(
    {
        updateUrl: "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/apk/sample-debug.apk",
        versionName: '10.00',
        updateContent: "1，添加删除信用卡接口。\r\n2，添加vip认证。\r\n3，区分自定义消费，一个小时不限制。\r\n4，添加放弃任务接口，小时内不生成。\r\n5，消费任务手动生成。",
        size: '10M',
        forceDialog: false,//强制弹窗提醒
        update: true,//是否更新
        forced: false,//强制更新
        ignore: false,//是否显示忽略此版本
        hideProgressBar: true,//下载时是否隐藏进度条
        md5: '60e2c3ab8bb4c078438f654e39c7b6a0'//新apk的MD5
    });

//检测通知是否打开
Tools.checkNotifyEnabled().then((enabled) => {
    //enabled true表示允许通知，false表示通知未打开
});

//跳转到通知设置页面
Tools.openNotifySetting();
```
  