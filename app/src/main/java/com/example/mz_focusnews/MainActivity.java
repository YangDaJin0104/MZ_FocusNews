package com.example.mz_focusnews;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.VolleyError;
//import com.example.mz_focusnews.NewsCrawling.NewsArticle;
//import com.example.mz_focusnews.NewsCrawling.NewsScraper;
//import com.example.mz_focusnews.NewsCrawling.NewsScraperCallback;
import com.example.mz_focusnews.NewsSummary.Summary;
import com.example.mz_focusnews.NewsSummary.SummaryUtils;
import com.example.mz_focusnews.RelatedNews.FetchCompleteListener;
import com.example.mz_focusnews.RelatedNews.NewsDataFetcher;
import com.example.mz_focusnews.RelatedNews.NewsDataStore;
import com.example.mz_focusnews.RelatedNews.RelatedNewsUtils;
import com.example.mz_focusnews.RelatedNews.NewsData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private NewsDataStore newsDataStore;

//    private NewsScraper newsScraper;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        newsScraper = new NewsScraper(this);
//        fetchNewsAndProcess();

        performSummaryAndUpload();

        newsDataStore = new NewsDataStore();
        NewsDataFetcher fetcher = new NewsDataFetcher(newsDataStore);
        fetcher.fetchAllNews(this, new FetchCompleteListener() {
            @Override
            public void onFetchComplete() {
                executorService.execute(() -> {
                    try {
                        newsDataStore.calculateAndSetRelatedArticles();
                        updateDatabaseWithRelatedNews();  // 데이터베이스 업데이트 호출
                    } catch (IOException e) {
                        Log.e("MainActivity", "Error calculating related articles", e);
                    }
                });

            }

            @Override
            public void onFetchFailed(VolleyError error) {
                Log.e("MainActivity", "News fetch failed", error);
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // 앱 종료 시 스레드 풀 종료
    }

//    private void fetchNewsAndProcess() {
//        newsScraper.fetchNews(new NewsScraperCallback() {
//            @Override
//            public void onSuccess(List<NewsArticle> articles) {
//                for (NewsArticle article : articles) {
//                    performSummaryAndUpload(article);
//                }
//            }
//            @Override
//            public void onFailure(Exception e) {
//                Log.e("MainActivity", "Failed to fetch news", e);
//            }
//        });
//    }
    private void updateDatabaseWithRelatedNews() {
        for (NewsData item : newsDataStore.getAllNewsItems()) {
            RelatedNewsUtils.updateRelatedNews(this, item.getId(), item.getRelated1(), item.getRelated2());
        }
    }

    private void performSummaryAndUpload() {         //    private void performSummaryAndUpload(NewsArticle article) {
//        String content = article.getBody(); // 기사 본문 내용
//        String link = article.getLink(); // 기사 링크
        String content = "오세훈 서울시장은 20일 여권에서 정부의 ‘해외 직구 금지’ 방침에 대한 비판이 나오는 것과 관련해 “함께 세심하게 명찰추호(明察秋毫) 해야 할 때에 마치 정부 정책 전체에 큰 문제가 있는 것처럼 지적하는 것은 여당 중진으로서의 처신에 아쉬움이 남는다”고 했다. " +
                "오 시장이 언급한 ‘여당 중진’은 국민의힘 한동훈 전 비상대책위원장과 유승민 전 의원, 나경원 당선자 등을 지칭한 것으로 알려졌다. " +
                "오 시장은 이날 페이스북에서 “모든 정책에는 순기능과 역기능이 있고 정부와 여당은 늘 책임 있는 자세로 함께 풀어나가야 한다”면서 " +
                "이렇게 말했다.오 시장은 “최근 해외 직구와 관련해선 시민 안전위해성, 국내기업 고사 우려라는 두 가지 문제점이 있다”며 “안전과 기업 보호는 " +
                "직구 이용자들의 일부 불편을 감안해도 포기할 수 없는 가치다. 후자가 편·불편의 문제라면 전자는 생존의 문제”라고 했다.이어 “국내기업의 산업경쟁력을 " +
                "높이는 게 근본적인 숙제이기는 하지만 갑자기 밀어닥친 홍수는 먼저 막아야 할 것 아니겠느냐”며 “강물이 범람하는데 제방 공사를 논하는 건 탁상공론이다. " +
                "우선은 모래주머니라도 급하게 쌓는 게 오히려 상책”이라고 했다. 오 시장은 그러면서 “유해물질 범벅 어린이용품이 넘쳐나고 500원 숄더백, 600원 목걸이가 " +
                "나와 기업 고사가 현실이 된 상황에서 정부가 손 놓고 있다면 그것이야말로 문제”라며 “서울시는 4월 초 해외 직구 상품과 관련해 안전성 확보 대책을 발표했고, " +
                "4월 말부터 매주 유해물질 제품을 발표하고 있다. 시민 안전과 기업 보호에 있어선 그 무엇과도 타협하지 않겠다”고 했다. 앞서 한동훈 전 비대위원장은 지난 18일 밤 " +
                "페이스북에서 “개인 해외 직구 시 KC 인증 의무화 규제는 소비자의 선택권을 지나치게 제한하므로 재고돼야 한다”고 했다. 같은 날 국민의힘 유승민 전 의원과 나경원 당선자도 " +
                "소셜미디어에서 직구 금지 반대 입장을 밝혔다. 정부는 지난 16일 KC 미인증 제품 직구 금지 방침을 발표한 이후 비판 여론이 확산하자, 사흘 만인 지난 19일 이를 철회했다. " +
                "오 시장 측 인사는 이날 본지 통화에서 ‘오 시장이 언급한 여당 중진에 한 전 위원장도 포함되는 것이냐’는 질문에 “다선 의원뿐만 아니라 전직 여당 대표도 중진 아니냐”고 했다.";
        String link = "https://news.google.com/rss/articles/CBMiRmh0dHBzOi8vd3d3LmNob3N1bi5jb20vcG9saXRpY3MvMjAyNC8wNS8yMC9WNldQRUtMR05KSDdYTE9CVkxPSzVCR1dTWS_SAVVodHRwczovL3d3dy5jaG9zdW4uY29tL3BvbGl0aWNzLzIwMjQvMDUvMjAvVjZXUEVLTEdOSkg3WExPQlZMT0s1QkdXU1kvP291dHB1dFR5cGU9YW1w?oc=5";

        executorService.execute(() -> {
            String summary = Summary.chatGPT_summary(content);
            if (summary != null) {
                SummaryUtils summaryUtils = new SummaryUtils();
                summaryUtils.sendSummaryToServer(MainActivity.this, link, summary);
            }
        });
    }
}



