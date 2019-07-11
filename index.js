import {NativeModules, Platform, AlertIOS, Linking} from 'react-native';

const {RNUpdater, RNTools} = NativeModules;

const showUpdate =
    (isUpdate = false, isForceUpdate = false, url, versionName, updateContent) => {
        let title = `发现新版本，是否升级到${versionName}版本？`;
        if (isForceUpdate) {
            AlertIOS.alert(
                title,
                updateContent,
                [
                    {
                        text: "升级",
                        onPress: () => Linking.openURL(url).catch(err => {
                            console.log(err);
                        })
                    }
                ]
            );
            return;
        }
        if (isUpdate) {
            AlertIOS.alert(
                title,
                updateContent,
                [
                    {
                        text: '取消',
                        style: "cancel"
                    },
                    {
                        text: "升级",
                        onPress: () => Linking.openURL(url).catch(err => {
                            console.log(err);
                        })
                    }
                ]
            );
        }
    };

export const CheckUpdate = (options) => {
    if (options && Platform.OS === 'ios') {
        showUpdate(options.update, options.forced, options.updateUrl, options.versionName, options.updateContent);
    } else {
        RNUpdater.checkUpdate(options);
    }
};

export const Tools = {
    checkNotifyEnabled: () => {
        return RNTools.checkNotifyEnabled();
    },
    openNotifySetting: () => {
        RNTools.openNotifySetting();
    },
};