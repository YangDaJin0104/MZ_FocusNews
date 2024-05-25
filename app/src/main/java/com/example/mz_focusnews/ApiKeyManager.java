package com.example.mz_focusnews;

import android.content.Context;
import android.content.res.Resources;

public class ApiKeyManager {
    private final Resources resources;

    private ApiKeyManager(Context context) {
        resources = context.getResources();
    }

    public static ApiKeyManager getInstance(Context context) {
        return new ApiKeyManager(context.getApplicationContext());
    }

    public String getApiKey() {
        // api_key.xml 파일에서 API 키 가져오기 - .gitignore 설정하여 git에 올라가지 않도록 하기!!!
        return resources.getString(R.string.api_key);
    }
}
