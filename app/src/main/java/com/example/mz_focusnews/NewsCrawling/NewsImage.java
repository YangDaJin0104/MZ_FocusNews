package com.example.mz_focusnews.NewsCrawling;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.util.concurrent.CompletableFuture;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.RequiresApi;

public class NewsImage {
    private static final String TAG = "NewsImage";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static CompletableFuture<String> fetchImageUrl(Context context, String articleUrl) {
        CompletableFuture<String> imageUrlFuture = new CompletableFuture<>();
        Handler handler = new Handler(Looper.getMainLooper());

        if (articleUrl == null || articleUrl.isEmpty()) {
            //Log.e(TAG, "Invalid URL: " + articleUrl);
            imageUrlFuture.complete("");
            return imageUrlFuture;
        }

        handler.post(() -> {
            LoadWebView webView = new LoadWebView(context);
            webView.setWebViewCallback(content -> {
                String imageUrl = extractImageUrlFromHtml(content);
                imageUrlFuture.complete(imageUrl);
                //Log.d(TAG, "Extracted image URL: " + imageUrl);
            });

            webView.loadUrlInView(articleUrl);
        });

        return imageUrlFuture;
    }

    private static String extractImageUrlFromHtml(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }

        int ogImageIndex = html.indexOf("property=\"og:image\"");
        if (ogImageIndex != -1) {
            int contentIndex = html.indexOf("content=\"", ogImageIndex);
            if (contentIndex != -1) {
                int startIndex = contentIndex + 9;
                int endIndex = html.indexOf("\"", startIndex);
                if (endIndex != -1) {
                    return html.substring(startIndex, endIndex);
                }
            }
        }

        int imgIndex = html.indexOf("<img");
        if (imgIndex != -1) {
            int srcIndex = html.indexOf("src=\"", imgIndex);
            if (srcIndex != -1) {
                int startIndex = srcIndex + 5;
                int endIndex = html.indexOf("\"", startIndex);
                if (endIndex != -1) {
                    return html.substring(startIndex, endIndex);
                }
            }
        }

        return "";
    }
}
