package com.bluedatax.wdpms.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by xuyuanqiang on 7/9/16.
 */
public class GetAppVersion {
    Context mContext;
    public GetAppVersion(Context c){
        mContext = c;
    }
    public int getVersionCode(){
        return getPackageInfo().versionCode;
    }
    private PackageInfo getPackageInfo(){
        PackageInfo pi = null;
        PackageManager manager = mContext.getPackageManager();
        try {
            pi = manager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (PackageManager.NameNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pi;
    }
    public String getVersion(){

        PackageManager manager = mContext.getPackageManager();

        try {
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
