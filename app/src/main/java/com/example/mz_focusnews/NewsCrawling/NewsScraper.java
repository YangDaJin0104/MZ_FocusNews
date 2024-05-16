package com.example.mz_focusnews.NewsCrawling;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewsScraper {
    private ExecutorService executor;
    private Context context;
    private Handler handler = new Handler(Looper.getMainLooper());

    public NewsScraper(Context context) {
        this.context = context;
        this.executor = Executors.newFixedThreadPool(4);
    }

    public void fetchNews(NewsScraperCallback callback) {
        executor.submit(() -> {
            try {
                Document doc = Jsoup.connect("https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko").get();
                List<NewsArticle> articles = new ArrayList<>();
                List<CompletableFuture<Void>> futures = new ArrayList<>();

                doc.select("item").forEach(item -> {
                    String title = item.select("title").text();
                    String pubDate = item.select("pubDate").text();
                    String publisher = item.select("source").text();
                    String link = item.select("link").first().textNodes().get(0).text().trim();

                    CompletableFuture<String> imageUrlFuture = NewsImage.fetchImageUrl(context, link);
                    CompletableFuture<String> contentFuture = NewsContent.fetchArticleContent(context, link);

                    CompletableFuture<Void> combinedFuture = CompletableFuture.allOf().thenAccept(result -> {
                        articles.add(new NewsArticle(title, pubDate, publisher, link, imageUrlFuture.join(), contentFuture.join()));
                    }).handle((result, exception) -> {
                        if (exception != null) {
                            Log.e(TAG, "Error fetching article details", exception);
                        }
                        return result;
                    });

                    futures.add(combinedFuture);
                });

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                        .thenRun(() -> handler.post(() -> callback.onSuccess(articles)))
                        .exceptionally(e -> {
                            handler.post(() -> callback.onFailure(new Exception(e)));
                            return null;
                        });

            } catch (Exception e) {
                handler.post(() -> callback.onFailure(e));
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}