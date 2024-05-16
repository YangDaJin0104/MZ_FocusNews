package com.example.mz_focusnews.NewsCrawling;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.util.Log;

public class LoadWebView extends WebView {

    private static final String TAG = "LoadWebView";
    private WebViewCallback callback;

    public LoadWebView(Context context) {
        super(context);
        init();
    }

    public LoadWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // WebView 설정 초기화
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false; // WebView에서 URL 로딩을 처리하도록 함
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //Log.d(TAG, "Page loaded: " + url);
                if (callback != null) {
                    view.evaluateJavascript(
                            "(function() { return document.documentElement.outerHTML; })();",
                            value -> {
                                //Log.d(TAG, "Extracted content: " + value);
                                callback.onContentExtracted(value);
                            }
                    );
                }
            }
        });

        setWebChromeClient(new WebChromeClient());
    }

    public void setWebViewCallback(WebViewCallback callback) {
        this.callback = callback;
    }

    // URL을 로드하는 메소드
    public void loadUrlInView(String url) {
        if (url != null && !url.isEmpty()) {
            loadUrl(url);
        } else {
            Log.e(TAG, "Invalid URL: " + url);
        }
    }

    public interface WebViewCallback {
        void onContentExtracted(String content);
    }
}
