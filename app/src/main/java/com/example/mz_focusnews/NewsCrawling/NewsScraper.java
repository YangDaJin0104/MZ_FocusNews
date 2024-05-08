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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class NewsScraper {
    private ExecutorService executor;
    private Context context;

    public NewsScraper(Context context) {
        this.context = context;
        this.executor = Executors.newFixedThreadPool(4);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void fetchNews(NewsScraperCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isNetworkAvailable()) {
                callback.onFailure(new Exception("No Internet Connection"));
                return;
            }
        }

        executor.submit(() -> {
            try {
                Document doc = Jsoup.connect("https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko").get();
                ArrayList<NewsArticle> articles = doc.select("item").stream()
                        .map(item -> {
                            String title = item.select("title").text();
                            String pubDate = item.select("pubDate").text();
                            String publisher = item.select("source").text();
                            String link = item.select("link").text();

                            try {
                                Document articleDoc = Jsoup.connect(link).get();
                                String imageUrl = extractImageUrl(articleDoc);
                                String body = extractBodyText(articleDoc);

                                return new NewsArticle(title, pubDate, publisher, link, imageUrl, body);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .filter(java.util.Objects::nonNull)
                        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

                callback.onSuccess(articles);
            } catch (Exception e) {
                callback.onFailure(e);
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String extractImageUrl(Document articleDoc) {
        Element imageElement;

        // 첫 번째 시도: 'twitter:image' 속성을 사용
        imageElement = articleDoc.select("meta[name=twitter:image]").first();
        if (imageElement != null && !imageElement.attr("content").isEmpty()) {
            return imageElement.attr("content");
        }

        // 두 번째 시도: HTML 내의 첫 번째 이미지 태그를 사용
        imageElement = articleDoc.select("img").first();
        if (imageElement != null && !imageElement.attr("src").isEmpty()) {
            return imageElement.attr("src");
        }

        // 세 번째 시도: 기사의 본문에서 가장 큰 이미지를 찾기
        imageElement = articleDoc.select("img[src]").stream()
                .max(Comparator.comparingInt(img -> {
                    // 이미지의 크기를 추정하는 데 필요한 데이터(가로 X 세로)를 얻을 수 있으면 그 값을 사용
                    String width = img.attr("width");
                    String height = img.attr("height");
                    try {
                        return Integer.parseInt(width) * Integer.parseInt(height);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })).orElse(null);
        if (imageElement != null && !imageElement.attr("src").isEmpty()) {
            return imageElement.attr("src");
        }

        // 모든 시도에서 실패한 경우 기본 이미지 URL 반환
        return "default_image_url_here";
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private String extractBodyText(Document articleDoc) {
        // 첫 번째 시도: <article> 태그 내부의 텍스트 추출
        String bodyText = articleDoc.select("article").text();

        // 'article' 태그에서 충분한 내용을 얻지 못할 경우, 'content' 클래스를 가진 <div>를 시도
        if (bodyText.isEmpty()) {
            bodyText = articleDoc.select("div.content").text();
        }

        // 그래도 내용이 없다면, <p> 태그를 활용하여 전체 텍스트를 추출
        if (bodyText.isEmpty()) {
            bodyText = articleDoc.select("p").stream()
                    .map(Element::text)
                    .collect(Collectors.joining("\n"));
        }

        // 또 다른 가능성으로 'text-body' 클래스를 가진 <div> 태그 검색
        if (bodyText.isEmpty()) {
            bodyText = articleDoc.select("div.text-body").text();
        }

        // 모든 시도에서 결과가 비어있다면, 더 많은 CSS 선택자를 고려하거나 HTML 구조를 다시 분석해야 함
        return bodyText.trim();
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