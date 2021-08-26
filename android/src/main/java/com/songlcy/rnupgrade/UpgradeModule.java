package com.songlcy.rnupgrade;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.songlcy.rnupgrade.Constants.Constants;
import com.songlcy.rnupgrade.utils.ApkInstallUtils;
import com.songlcy.rnupgrade.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.lang.String;

/**
 * Created by Song on 2017/7/10.
 * Update by Song on 2021/01/21.19:58
 */
public class UpgradeModule extends ReactContextBaseJavaModule {
    private final static String PLAY_STORE_PACKAGE_NAME = "com.android.vending";
    private ReactApplicationContext context;
    private String versionName = "1.0.0";
    private int versionCode = 1;

    public UpgradeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
        try {
            PackageInfo pInfo = reactContext.getPackageManager().getPackageInfo(reactContext.getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "upgrade";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        String apkFilePath = FileUtils.getCacheDirectory(this.context).getAbsolutePath() + Constants.APK_FILE_DIR_NAME;
        constants.put("versionName", versionName);
        constants.put("versionCode", versionCode);
        constants.put("downloadApkFilePath", apkFilePath);
        return constants;
    }

    /**
     * RN调用安装apk
     * @param filePath
     */
    @ReactMethod
    public void installApk(String filePath) {
        ApkInstallUtils.install(this.context, filePath);
    }

    @ReactMethod
    public void openPlayStore() {
        String packageName = this.context.getPackageName();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,  Uri.parse(String.format("market://details?id=%s", packageName)));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://play.google.com/store/apps/details?id=%s", packageName)));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.context.startActivity(intent);
        }
    }

    @ReactMethod
    public void checkPlayStore(Promise promise) {
        try {
            promise.resolve(this.context.getPackageManager().getApplicationInfo(PLAY_STORE_PACKAGE_NAME, 0).enabled);
        } catch (PackageManager.NameNotFoundException e) {
            promise.resolve(false);
        }
    }
}
