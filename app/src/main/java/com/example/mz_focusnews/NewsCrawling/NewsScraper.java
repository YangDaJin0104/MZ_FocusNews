package com.example.mz_focusnews.NewsCrawling;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewsScraper {
    private ExecutorService executor;
    private Context context;

    public NewsScraper(Context context) {
        this.context = context;
        // 스레드 풀의 크기를 4로 설정 하여 동시에 실행할 수 있는 스레드 수를 제한
        this.executor = Executors.newFixedThreadPool(4);
    }

    public void fetchNews(NewsScraperCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isNetworkAvailable()) {
                callback.onFailure(new Exception("No Internet Connection")); // 인터넷 연결이 안됐을 때
                return;
            }
        }

        executor.submit(() -> {
            try {
                Document doc = Jsoup.connect("https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko").get(); // rss주소
                Elements items = doc.select("item");
                ArrayList<NewsArticle> articles = new ArrayList<>();

                for (Element item : items) {
                    String title = item.select("title").text(); // 제목
                    String pubDate = item.select("pubDate").text(); // 기사 작성 시간
                    String url = item.select("link").text(); // 기사 url

                    NewsArticle article = new NewsArticle(title, pubDate, url);
                    articles.add(article);
                }

                callback.onSuccess(articles);
            } catch (Exception e) {
                callback.onFailure(e);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        if (network != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        return false;
    }

    public void shutdown() {
        executor.shutdown();
    }
}
