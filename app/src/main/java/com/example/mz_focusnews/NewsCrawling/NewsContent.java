package com.example.mz_focusnews.NewsCrawling;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.CompletableFuture;
import java.util.HashMap;
import java.util.Map;

public class NewsContent {
    private static final String TAG = "NewsContent";

    // 사이트별 CSS 선택자 맵
    private static final Map<String, String> siteSelectors = new HashMap<>();

    static {
        // 예시로 사이트별 CSS 선택자 추가
        siteSelectors.put("hani.co.kr", "div.article-text p"); // 한겨레 사이트 CSS 선택자 추가
        siteSelectors.put("donga.com", "div.article_txt p"); // 동아닷컴 사이트 CSS 선택자 추가
        siteSelectors.put("imbc.com", "div.news_txt p"); // MBC 뉴스 사이트 CSS 선택자 추가
        siteSelectors.put("yna.co.kr", "div.story-news p"); // 연합뉴스 사이트 CSS 선택자 추가
        siteSelectors.put("chosun.com", "div#fusion-app div[article-body-content] p"); // 조선일보 사이트 CSS 선택자 추가
        siteSelectors.put("hankyung.com", "div.article p"); // 한국경제 사이트 CSS 선택자 추가
        siteSelectors.put("munhwa.com", "div#NewsAdContent p"); // 문호일보 사이트 CSS 선택자 추가
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static CompletableFuture<String> fetchArticleContent(Context context, String articleUrl) {
        CompletableFuture<String> contentFuture = new CompletableFuture<>();
        Handler handler = new Handler(Looper.getMainLooper());

        if (articleUrl == null || articleUrl.isEmpty()) {
            contentFuture.complete("");
            return contentFuture;
        }

        handler.post(() -> {
            LoadWebView webView = new LoadWebView(context);
            webView.setWebViewCallback(content -> {
                String articleContent = parseHtml(content, articleUrl);
                contentFuture.complete(articleContent);

                // 기사 본문을 JSON 파일로 저장
                saveContentToJsonFile(context, articleUrl, articleContent);
            });

            webView.loadUrlInView(articleUrl);
        });

        return contentFuture;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String parseHtml(String html, String url) {
        Document document = Jsoup.parse(html);

        // URL을 기반으로 도메인을 추출하여 해당 도메인의 CSS 선택자를 가져옴
        String domain = getDomainName(url);
        String selector = siteSelectors.getOrDefault(domain, "p"); // 기본값으로 모든 <p> 태그 선택

        // 기사 본문을 선택하는 CSS 쿼리
        Elements articleElements = document.select(selector);

        // 기사 본문 텍스트 추출
        StringBuilder articleContent = new StringBuilder();
        for (Element element : articleElements) {
            articleContent.append(element.text()).append("\n");
        }

        return articleContent.toString().trim();
    }

    private static String getDomainName(String url) {
        try {
            String domain = url.split("/")[2];
            domain = domain.startsWith("www.") ? domain.substring(4) : domain;
            return domain;
        } catch (Exception e) {
            Log.e(TAG, "URL에서 도메인 추출 실패: " + url, e);
            return "";
        }
    }
    private static void saveContentToJsonFile(Context context, String articleUrl, String content) {
        try {
            // 기사 내용을 포함하는 JSON 객체 생성
            Map<String, String> articleData = new HashMap<>();
            articleData.put("content", content);

            // 맵을 JSON 문자열로 변환
            Gson gson = new Gson();
            String jsonString = gson.toJson(articleData);

            // URL을 사용하여 고유한 파일 이름 생성
            String fileName = "article_" + articleUrl.hashCode() + ".json";

            // JSON 문자열을 파일에 작성
            try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                 OutputStreamWriter writer = new OutputStreamWriter(fos)) {
                writer.write(jsonString);
            }

            Log.d(TAG, "기사 본문이 JSON 파일로 저장되었습니다: " + fileName);
        } catch (Exception e) {
            Log.e(TAG, "기사 본문을 JSON 파일로 저장하는 중 오류 발생", e);
        }
    }

}
