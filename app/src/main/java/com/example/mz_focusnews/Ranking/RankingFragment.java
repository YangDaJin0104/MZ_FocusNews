package com.example.mz_focusnews.Ranking;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.mz_focusnews.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RankingFragment extends Fragment {
    private static final String TAG = "RankingFragment";
    private static final String URL = "http://43.201.173.245/getQuizScoreJson.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 프래그먼트의 UI를 인플레이트합니다.
        return inflater.inflate(R.layout.fragment_quiz_start, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showRanking();
    }

    private void showRanking() {
        ExecutorService executor = Executors.newSingleThreadExecutor();     // 백그라운드 실행을 위함
        Handler handler = new Handler(Looper.getMainLooper());              // 메인 스레드로 전달을 위함

        executor.execute(() -> {
            //Background work - DB 관련 함수이기 때문에 백그라운드 스레드에서 네트워크 요청 수행
            String response = fetchDataFromServer();

            handler.post(() -> {
                //UI Thread work - 백그라운드 작업 결과를 메인 스레드로 전달
                if(response!=null){
                    List<Ranking> rankings = RankingParser.parseRanking(response);
                    for(int i=0;i<rankings.size();i++){
                        System.out.println(rankings.get(i).getRank() + "등: " + rankings.get(i).getUserId() + " - " + rankings.get(i).getScore() + "점");
                    }
                } else{
                    Log.e(TAG, "response == null");
                }
            });
        });

    }

    private String fetchDataFromServer() {
        try {
            URL obj = new URL(URL);

            // 웹 서버와 연결
            HttpURLConnection connect = (HttpURLConnection) obj.openConnection();
            connect.setRequestMethod("GET");        // request 메소드 설정 (GET 방식)
            connect.getResponseCode();

            // response 내용 읽기
            BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            //Log.d(TAG, "DB data: " + response);
            in.close();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
