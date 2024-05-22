package com.example.mz_focusnews;
/*
위치정보 동의
*/
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class RequestPermissionUtil {
    private Context context;
    private static final int REQUEST_LOCATION = 1;

    // 위치 권한 SDK 버전 29 이상
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private String[] permissionsLocationUpApi29Impl = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };

    // 위치 권한 SDK 버전 29 이하
    private String[] permissionsLocationDownApi29Impl = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public RequestPermissionUtil(Context context) {
        this.context = context;
    }

    // 위치정보 권한 요청
    public void requestLocation() {
        if (Build.VERSION.SDK_INT >= 29) {
            if (ActivityCompat.checkSelfPermission(context, permissionsLocationUpApi29Impl[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, permissionsLocationUpApi29Impl[1]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, permissionsLocationUpApi29Impl[2]) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        (Activity) context,
                        permissionsLocationUpApi29Impl,
                        REQUEST_LOCATION
                );
            }
        } else {
            if (ActivityCompat.checkSelfPermission(context, permissionsLocationDownApi29Impl[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, permissionsLocationDownApi29Impl[1]) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        (Activity) context,
                        permissionsLocationDownApi29Impl,
                        REQUEST_LOCATION
                );
            }
        }
    }
}