package cn.cnlee.commons.updater;

import android.os.Environment;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import org.json.JSONObject;

import java.io.File;
import java.util.Map;

import cn.cnlee.commons.updater.update.UpdateAppBean;
import cn.cnlee.commons.updater.update.UpdateAppManager;
import cn.cnlee.commons.updater.utils.AppUpdateUtils;
import cn.cnlee.commons.updater.utils.ConvertUtil;
import cn.cnlee.commons.updater.utils.Strings;

public class RNUpdaterModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public RNUpdaterModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNUpdater";
    }

    @ReactMethod
    public void checkUpdate(ReadableMap options) {
        Map map = options.toHashMap();
        UpdateAppBean updateAppBean = new UpdateAppBean();
        try {
            updateAppBean = (UpdateAppBean) ConvertUtil.mapToBean(map, UpdateAppBean.class);
            Log.e(RNUpdaterModule.class.getSimpleName(),new JSONObject(map).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        UpdateAppManager.Builder builder = new UpdateAppManager.Builder();
        if (!Strings.isBlank(updateAppBean.getPath())) {
            builder.setTargetPath(Environment.getExternalStorageDirectory() + File.separator + updateAppBean.getPath());
        }
        if (updateAppBean.isIgnore()) {
            builder.showIgnoreVersion();
        }
        builder.setActivity(this.getCurrentActivity());
        if (updateAppBean.isForceDialog()) { //强制弹出提示框，主要用于设置里，手动版本检测更新
            builder.build().update(updateAppBean);
        } else if (updateAppBean.isSilence()) { //强制静默下载更新，无论wifi还是移动数据
            builder.build().silenceUpdate(updateAppBean);
        } else if (AppUpdateUtils.isWifi(this.reactContext)) {// wifi时静默
            builder.setOnlyWifi().build().silenceUpdate(updateAppBean);
        } else {//普通弹框
            builder.build().update(updateAppBean);
        }
    }
}