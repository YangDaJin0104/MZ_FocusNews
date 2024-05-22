package com.example.mz_focusnews.RelatedNews;

import com.android.volley.VolleyError;

// FetchCompleteListener 인터페이스 정의
public interface FetchCompleteListener {
    void onFetchComplete();

    void onFetchFailed(VolleyError error);
}
