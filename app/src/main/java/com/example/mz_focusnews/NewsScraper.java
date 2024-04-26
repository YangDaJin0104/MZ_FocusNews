package com.example.mz_focusnews;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

// 뉴스 제목, 내용, 언론사, 기사작성 시간 크롤링
public class NewsScraper {
    private static final String TAG = "MainActivity";

    // 구글 뉴스가 업데이트 됨에 따라 선택자만 수정할 수 있도록
    private String itemSelector = ".xrnccd";
    private String titleSelector = ".DY5T1d";
    private String sourceSelector = ".wEwyrc";
    private String timeSelector = ".WW6dff";

    private NewsScraperCallback callback;

    public NewsScraper(NewsScraperCallback callback) {
        this.callback = callback;
    }

    // 선택자를 없데이트 하는 메소드
    public void updateSelectors(String item, String title, String source, String time) {
        this.itemSelector = item;
        this.titleSelector = title;
        this.sourceSelector = source;
        this.timeSelector = time;
        Log.i(TAG, "Selectors updated");
    }

    public void scrapeNews() {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect("https://news.google.com/").get(); //웹페이지 크롤링
                Elements newsItems = doc.select(itemSelector);
                StringBuilder resultBuilder = new StringBuilder();

                for (Element item : newsItems) {
                    String title = item.select(titleSelector).text();
                    String source = item.select(sourceSelector).text();
                    String time = item.select(timeSelector).text();

                    if (title.isEmpty() || source.isEmpty() || time.isEmpty()) {
                        Log.w(TAG, "Missing data detected");
                        continue;
                    }

                    // version1 반환하지 않고 크롤링 제대로 되는지 확인
                    resultBuilder.append("뉴스 제목: ").append(title).append("\n")
                            .append("언론사: ").append(source).append("\n")
                            .append("뉴스 작성 시간: ").append(time).append("\n\n");
                    // version2 NewsArticle 클래스 활용하여 사용자에게 보여지도록
                    // ArrayList
                }

                if (callback != null) {
                    callback.onNewsScraped(resultBuilder.toString());
                }
            } catch (IOException e) { // 크롤링이 되지않으면 예외처리(Logcat으로 확인)
                e.printStackTrace();
                Log.e(TAG, "Exception occurred: " + e.toString());
            }
        }).start();
    }
    // 기사 내용 반환
    private String scrapeArticleContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).get(); // Jsoup을 이용해 기사 내용 추출
        StringBuilder contentBuilder = new StringBuilder(); // StringBuilding을 이용해 하나의 문자열로 합친다.

        // 기사 내용을 식별할 수 있는 선택자를 사용하여 해당 내용을 가져온다.
        Elements contentElements = doc.select(".article-content p");
        for (Element element : contentElements) {
            // 각각의 내용을 contentBuilder에 추가
            contentBuilder.append(element.text()).append("\n");
        }

        return contentBuilder.toString();
    }
}
