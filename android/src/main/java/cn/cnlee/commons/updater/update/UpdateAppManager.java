package cn.cnlee.commons.updater.update;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import cn.cnlee.commons.updater.download.DefaultDownloadManager;
import cn.cnlee.commons.updater.download.IDownloadManager;
import cn.cnlee.commons.updater.listener.ExceptionHandler;
import cn.cnlee.commons.updater.listener.ExceptionHandlerHelper;
import cn.cnlee.commons.updater.listener.IUpdateDialogFragmentListener;
import cn.cnlee.commons.updater.service.DownloadService;
import cn.cnlee.commons.updater.utils.AppUpdateUtils;

/**
 * 版本更新管理器
 */
public class UpdateAppManager {
    final static String INTENT_KEY = "update_dialog_values";
    final static String THEME_KEY = "theme_color";
    final static String TOP_IMAGE_KEY = "top_resId";
    private static final String TAG = UpdateAppManager.class.getSimpleName();
    private Activity mActivity;
    private IDownloadManager mDownloadManager;
    private int mThemeColor;
    private
    @DrawableRes
    int mTopPic;
    private UpdateAppBean mUpdateApp;
    private String mTargetPath;
    private boolean mHideDialog;
    private boolean mShowIgnoreVersion;
    private boolean mDismissNotificationProgress;
    private boolean mOnlyWifi;
    //自定义参数
    private IUpdateDialogFragmentListener mUpdateDialogFragmentListener;

    private UpdateAppManager(Builder builder) {
        mActivity = builder.getActivity();
        mDownloadManager = builder.getDownloadManager();

        mThemeColor = builder.getThemeColor();
        mTopPic = builder.getTopPic();

        mTargetPath = builder.getTargetPath();
        mHideDialog = builder.isHideDialog();
        mShowIgnoreVersion = builder.isShowIgnoreVersion();
        mDismissNotificationProgress = builder.isDismissNotificationProgress();
        mOnlyWifi = builder.isOnlyWifi();
        mUpdateDialogFragmentListener = builder.getUpdateDialogFragmentListener();
    }

    /**
     * 可以直接利用下载功能，
     *
     * @param context          上下文
     * @param updateAppBean    下载信息配置
     * @param downloadCallback 下载回调
     */
    public static void download(final Context context, @NonNull final UpdateAppBean updateAppBean, @Nullable final DownloadService.DownloadCallback downloadCallback) {

        if (updateAppBean == null) {
            throw new NullPointerException("updateApp 不能为空");
        }

        DownloadService.bindService(context.getApplicationContext(), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ((DownloadService.DownloadBinder) service).start(updateAppBean, downloadCallback);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        });
    }

    public Context getContext() {
        return mActivity;
    }

    /**
     * @return 新版本信息
     */
    public UpdateAppBean fillUpdateAppData() {
        if (mUpdateApp != null) {
            mUpdateApp.setTargetPath(mTargetPath);
            mUpdateApp.setDownloadManager(mDownloadManager);
//            mUpdateApp.setHideProgressBar(mHideDialog);
            mUpdateApp.showIgnoreVersion(mShowIgnoreVersion);
            mUpdateApp.dismissNotificationProgress(mDismissNotificationProgress);
            mUpdateApp.setOnlyWifi(mOnlyWifi);
            return mUpdateApp;
        }

        return null;
    }


    private boolean verify() {
        //版本忽略
        if (mShowIgnoreVersion && AppUpdateUtils.isNeedIgnore(mActivity, mUpdateApp.getVersionName())) {
            return true;
        }

        if (TextUtils.isEmpty(mTargetPath)
                ) {
            Log.e(TAG, "下载路径错误:" + mTargetPath);
            return true;
        }
        return mUpdateApp == null;
    }

    /**
     * 跳转到更新页面
     */
    public void showDialogFragment() {

        //校验
        if (verify()) return;

        if (mActivity != null && !mActivity.isFinishing()) {
            Bundle bundle = new Bundle();
            //添加信息，
            fillUpdateAppData();
            bundle.putSerializable(INTENT_KEY, mUpdateApp);
            if (mThemeColor != 0) {
                bundle.putInt(THEME_KEY, mThemeColor);
            }

            if (mTopPic != 0) {
                bundle.putInt(TOP_IMAGE_KEY, mTopPic);
            }

            UpdateDialogFragment
                    .newInstance(bundle)
                    .setUpdateDialogFragmentListener(mUpdateDialogFragmentListener)
                    .show(((FragmentActivity) mActivity).getSupportFragmentManager(), "dialog");

        }

    }

    /**
     * 静默更新
     */
    public void silenceUpdate(UpdateAppBean updateAppBean) {
        checkNewApp(updateAppBean, new SilenceUpdateCallback());
    }

    /**
     * 最简方式
     */

    public void update(UpdateAppBean updateAppBean) {
        checkNewApp(updateAppBean, new UpdateCallback());
    }

    /**
     * 检测是否有新版本
     *
     * @param callback 更新回调
     */
    public void checkNewApp(UpdateAppBean updateAppBean, UpdateCallback callback) {
        if (callback == null) {
            return;
        }
        if (DownloadService.isRunning || UpdateDialogFragment.isShow) {
//            Toast.makeText(mActivity, "app正在更新", Toast.LENGTH_SHORT).show();
            return;
        }

       //去掉网络请求，这部分放在RN代码里进行
        processData(updateAppBean, callback);
    }

    /**
     * 后台下载
     *
     * @param downloadCallback 后台下载回调
     */
    public void download(@Nullable final DownloadService.DownloadCallback downloadCallback) {
        if (mUpdateApp == null) {
            throw new NullPointerException("updateApp 不能为空");
        }
        mUpdateApp.setTargetPath(mTargetPath);
        mUpdateApp.setDownloadManager(mDownloadManager);
        DownloadService.bindService(mActivity.getApplicationContext(), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ((DownloadService.DownloadBinder) service).start(mUpdateApp, downloadCallback);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        });
    }

    /**
     * 后台下载
     */
    public void download() {
        download(null);
    }

    /**
     * 解析
     *
     * @param callback
     */
    private void processData(UpdateAppBean updateAppBean, @NonNull UpdateCallback callback) {
        try {
            mUpdateApp = updateAppBean;
            if (mUpdateApp.isUpdate()) {
                callback.hasNewApp(mUpdateApp, this);
                //假如是静默下载，可能需要判断，
                //是否wifi,
                //是否已经下载，如果已经下载直接提示安装
                //没有则进行下载，监听下载完成，弹出安装对话框

            } else {
                callback.noNewApp("没有新版本");
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.noNewApp(String.format("解析自定义更新配置消息出错[%s]", e.getMessage()));
        }
    }

    public static class Builder {
        //必须有
        private Activity mActivity;
        //必须有
        private IDownloadManager mDownloadManager;

        //1，设置按钮，进度条的颜色
        private int mThemeColor = 0;
        //2，顶部的图片
        private
        @DrawableRes
        int mTopPic = 0;
        //4,apk的下载路径
        private String mTargetPath;
        //7,是否隐藏对话框下载进度条
        private boolean mHideDialog = false;
        private boolean mShowIgnoreVersion;
        private boolean dismissNotificationProgress;
        private boolean mOnlyWifi;
        private IUpdateDialogFragmentListener mUpdateDialogFragmentListener;

        public String getTargetPath() {
            return mTargetPath;
        }

        /**
         * apk的下载路径，
         *
         * @param targetPath apk的下载路径，
         * @return Builder
         */
        public Builder setTargetPath(String targetPath) {
            mTargetPath = targetPath;
            return this;
        }

        public Activity getActivity() {
            return mActivity;
        }

        /**
         * 是否是post请求，默认是get
         *
         * @param activity 当前提示的Activity
         * @return Builder
         */
        public Builder setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public IDownloadManager getDownloadManager() {
            if (mDownloadManager == null){
                return new DefaultDownloadManager();
            }
            return mDownloadManager;
        }

        /**
         * 设置网络工具
         *
         * @param httpManager 自己实现的网络对象
         * @return Builder
         */
        public Builder setDownloadManager(IDownloadManager httpManager) {
            mDownloadManager = httpManager;
            return this;
        }

        public int getThemeColor() {
            return mThemeColor;
        }

        /**
         * 设置按钮，进度条的颜色
         *
         * @param themeColor 设置按钮，进度条的颜色
         * @return Builder
         */
        public Builder setThemeColor(int themeColor) {
            mThemeColor = themeColor;
            return this;
        }

        public int getTopPic() {
            return mTopPic;
        }

        /**
         * 顶部的图片
         *
         * @param topPic 顶部的图片
         * @return Builder
         */
        public Builder setTopPic(int topPic) {
            mTopPic = topPic;
            return this;
        }

        public IUpdateDialogFragmentListener getUpdateDialogFragmentListener() {
            return mUpdateDialogFragmentListener;
        }

        /**
         *  设置默认的UpdateDialogFragment监听器
         * @param updateDialogFragmentListener updateDialogFragmentListener 更新对话框关闭监听
         * @return Builder
         */
        public Builder setUpdateDialogFragmentListener(IUpdateDialogFragmentListener updateDialogFragmentListener) {
            this.mUpdateDialogFragmentListener = updateDialogFragmentListener;
            return this;
        }

        /**
         * @return 生成app管理器
         */
        public UpdateAppManager build() {
            //校验
            if (getActivity() == null || getDownloadManager() == null) {
                throw new NullPointerException("必要参数不能为空");
            }
            if (TextUtils.isEmpty(getTargetPath())) {
                //sd卡是否存在
                String path = "";
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
                    try {
                        path = getActivity().getExternalCacheDir().getAbsolutePath();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (TextUtils.isEmpty(path)) {
                        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                    }
                } else {
                    path = getActivity().getCacheDir().getAbsolutePath();
                }
                setTargetPath(path);
            }
            return new UpdateAppManager(this);
        }

        /**
         * 是否隐藏对话框下载进度条
         *
         *
         * @return Builder
         */
        public Builder hideDialogOnDownloading() {
            mHideDialog = true;
            return this;
        }

        /**
         * @return 是否影藏对话框
         */
        public boolean isHideDialog() {
            return mHideDialog;
        }

        /**
         * 显示忽略版本
         *
         * @return 是否忽略版本
         */
        public Builder showIgnoreVersion() {
            mShowIgnoreVersion = true;
            return this;
        }

        public boolean isShowIgnoreVersion() {
            return mShowIgnoreVersion;
        }

        /**
         * 不显示通知栏进度条
         *
         * @return 是否显示进度条
         */
        public Builder dismissNotificationProgress() {
            dismissNotificationProgress = true;
            return this;
        }

        public boolean isDismissNotificationProgress() {
            return dismissNotificationProgress;
        }

        public Builder setOnlyWifi() {
            mOnlyWifi = true;
            return this;
        }

        public boolean isOnlyWifi() {
            return mOnlyWifi;
        }

        public Builder handleException(ExceptionHandler exceptionHandler) {
            ExceptionHandlerHelper.init(exceptionHandler);
            return this;
        }

    }

}

