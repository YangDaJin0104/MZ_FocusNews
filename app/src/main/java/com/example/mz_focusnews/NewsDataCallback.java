package com.example.mz_focusnews;

public interface NewsDataCallback {
    void onDataFetched(int newsId, String content);
    void onError(String error);
}
