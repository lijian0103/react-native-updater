package cn.cnlee.commons.updater.update;

import java.io.Serializable;

import cn.cnlee.commons.updater.download.IDownloadManager;

/**
 * 版本信息
 */
public class UpdateAppBean implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * update : true
     * constraint: false
     * new_version : xxxxx
     * apk_url : http://cdn.the.url.of.apk/or/patch
     * update_log : xxxx
     * delta : false
     * new_md5 : xxxxxxxxxxxxxx
     * target_size : 601132
     */
    //是否有新版本
    private boolean update;
    //新版本号
    private String versionName;
    //新app下载地址
    private String updateUrl;
    //更新日志
    private String updateContent;
    //配置默认更新dialog 的title
    private String updateDialogTitle;
    //新app大小
    private String size;
    //是否强制更新
    private boolean forced;
    //是否显示忽略按钮
    private boolean ignore;
    //md5
    private String md5;
    //apk保存路径
    private String path;
    //显示提示框，true时，强制显示，false不强制
    private boolean forceDialog;
    //静默下载，true 无论wifi还是流量都静默下载
    private boolean silence;

    /**********以下是内部使用的数据**********/

    //下载工具，内部使用
    private IDownloadManager downloadManager;
    private String targetPath;
    private boolean mHideDialog;
    private boolean mShowIgnoreVersion;
    private boolean mDismissNotificationProgress;
    private boolean mOnlyWifi;

    //是否隐藏对话框下载进度条,内部使用
    public boolean isHideDialog() {
        return mHideDialog;
    }

    public void setHideDialog(boolean hideDialog) {
        mHideDialog = hideDialog;
    }

    public IDownloadManager getDownloadManager() {
        return downloadManager;
    }

    public void setDownloadManager(IDownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public boolean isForced() {
        return forced;
    }

    public UpdateAppBean setForced(boolean forced) {
        this.forced = forced;
        return this;
    }

    public boolean isSilence() {
        return silence;
    }

    public void setSilence(boolean silence) {
        this.silence = silence;
    }

    public boolean isUpdate() {
        return this.update;
    }

    public UpdateAppBean setUpdate(boolean update) {
        this.update = update;
        return this;
    }

    public String getVersionName() {
        return versionName;
    }

    public UpdateAppBean setVersionName(String versionName) {
        this.versionName = versionName;
        return this;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }


    public UpdateAppBean setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
        return this;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public UpdateAppBean setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
        return this;
    }

    public String getUpdateDialogTitle() {
        return updateDialogTitle;
    }

    public UpdateAppBean setUpdateDialogTitle(String updateDefDialogTitle) {
        this.updateDialogTitle = updateDialogTitle;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public UpdateAppBean setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public String getSize() {
        return size;
    }

    public UpdateAppBean setSize(String size) {
        this.size = size;
        return this;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public UpdateAppBean setIgnore(boolean ignore) {
        this.ignore = ignore;
        return this;
    }

    public String getPath() {
        return path;
    }

    public UpdateAppBean setPath(String path) {
        this.path = path;
        return this;
    }

    public boolean isForceDialog() {
        return forceDialog;
    }

    public void setForceDialog(boolean forceDialog) {
        this.forceDialog = forceDialog;
    }

    public boolean isShowIgnoreVersion() {
        return mShowIgnoreVersion;
    }

    public void showIgnoreVersion(boolean showIgnoreVersion) {
        mShowIgnoreVersion = showIgnoreVersion;
    }

    public void dismissNotificationProgress(boolean dismissNotificationProgress) {
        mDismissNotificationProgress = dismissNotificationProgress;
    }

    public boolean isDismissNotificationProgress() {
        return mDismissNotificationProgress;
    }

    public boolean isOnlyWifi() {
        return mOnlyWifi;
    }

    public void setOnlyWifi(boolean onlyWifi) {
        mOnlyWifi = onlyWifi;
    }

}
