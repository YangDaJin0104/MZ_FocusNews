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
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // 추가 설정: 하드웨어 가속 비활성화
        setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (callback != null) {
                    view.evaluateJavascript(
                            "(function() { return document.documentElement.outerHTML; })();",
                            value -> {
                                Log.d(TAG, "Extracted content from URL: " + url);
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

