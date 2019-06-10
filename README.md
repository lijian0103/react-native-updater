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
```javascript
import RNUpdater from 'react-native-updater';

// TODO: What to do with the module?
RNUpdater;
```
  