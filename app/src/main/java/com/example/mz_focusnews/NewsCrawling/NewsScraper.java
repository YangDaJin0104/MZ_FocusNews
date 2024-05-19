package com.example.mz_focusnews.NewsCrawling;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

                    // 크롤링된 데이터 로그 출력
                    Log.d(TAG, "Title: " + title);
                    Log.d(TAG, "Publication Date: " + pubDate);
                    Log.d(TAG, "Link: " + link);

                    CompletableFuture<String> imageUrlFuture = NewsImage.fetchImageUrl(context, link);
                    CompletableFuture<String> contentFuture = NewsContent.fetchArticleContent(context, link);

                    CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(imageUrlFuture, contentFuture).thenAccept(result -> {
                        NewsArticle article = new NewsArticle(title, pubDate, publisher, link, imageUrlFuture.join(), contentFuture.join());
                        articles.add(article);
                        insertLinkTitleAndPubDateToDatabase(article.getTitle(), article.getLink(), article.getPublicationDate()); // 크롤링한 링크, 제목, 발행 날짜를 데이터베이스에 삽입
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

    private void insertLinkTitleAndPubDateToDatabase(String title, String link, String pubDate) {
        executor.submit(() -> {
            try {
                URL url = new URL("http://43.201.173.245/NewsInsert.php"); // URL 확인
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                String postParameters = "title=" + title + "&link=" + link + "&pubDate=" + pubDate;

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    Log.d(TAG, "insertLinkTitleAndPubDateToDatabase response - " + sb.toString());
                } else {
                    Log.e(TAG, "Error in insertLinkTitleAndPubDateToDatabase - " + responseStatusCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception in insertLinkTitleAndPubDateToDatabase", e);
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}
