package cn.cnlee.commons.updater;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import cn.cnlee.commons.updater.utils.NotificationUtil;

public class RNToolsModule extends ReactContextBaseJavaModule {

    private final static String TAG = RNToolsModule.class.getSimpleName();

    private final ReactApplicationContext reactContext;

    public RNToolsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNTools";
    }

    //检测 推送通知是否打开
    @ReactMethod
    public void checkNotifyEnabled(Promise promise) {
        promise.resolve(NotificationUtil.areNotificationsEnabled(this.reactContext));
    }

    @ReactMethod
    public void openNotifySetting() {
        NotificationUtil.openNotifySetting(this.reactContext);
    }
}