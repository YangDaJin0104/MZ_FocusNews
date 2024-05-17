package com.example.mz_focusnews;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mz_focusnews.Ranking.Ranking;
import com.example.mz_focusnews.Ranking.RankingParser;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RankingFragment extends Fragment {

    private Button btn_quiz_start;
    private NavController navController;
    private static final String TAG = "RankingFragment";
    private static final String URL = "http://43.201.173.245/getQuizScoreJson.php";

    TextView score1;
    TextView score2;
    TextView score3;
    TextView score_user;
    TextView rank_user;
    ImageView dot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        score1 = view.findViewById(R.id.score1);
        score2 = view.findViewById(R.id.score2);
        score3 = view.findViewById(R.id.score3);
        score_user = view.findViewById(R.id.score_user);
        rank_user = view.findViewById(R.id.rank_user);
        dot = view.findViewById(R.id.dot);

        showRanking(view);

        btn_quiz_start = view.findViewById(R.id.quizStart);

        // 게임 시작 버튼 클릭 시 퀴즈 화면으로 이동
        btn_quiz_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_rankingFragment_to_quizFragment);
            }
        });

        return view;
    }

    private void setView(View view, List<Ranking> rankingList){
        String str;

        // rankingList가 null이 아니고 최소 세 개의 요소가 있는지 확인
        if (rankingList != null && rankingList.size() >= 3) {
            str = rankingList.get(0).getUserName() + ": " + rankingList.get(0).getScore() + "점";
            score1.setText(str);
            str = rankingList.get(1).getUserName() + ": " + rankingList.get(1).getScore() + "점";
            score2.setText(str);
            str = rankingList.get(2).getUserName() + ": " + rankingList.get(2).getScore() + "점";
            score3.setText(str);

            if(rankingList.size() == 4){
                str = rankingList.get(3).getUserName() + ": " + rankingList.get(3).getScore() + "점";
                Log.d(TAG, "STR = " + str);
                score_user.setText(str);

                String rank = Integer.toString(rankingList.get(3).getRank());
                rank_user.setText(rank);

                if(rankingList.get(3).getRank() == 4){
                    dot.setVisibility((View.GONE));
                }

            } else{
                score_user.setVisibility(View.GONE);
                rank_user.setVisibility(View.GONE);
            }

        }
    }

    private void showRanking(View view) {
        ExecutorService executor = Executors.newSingleThreadExecutor();     // 백그라운드 실행을 위함
        Handler handler = new Handler(Looper.getMainLooper());              // 메인 스레드로 전달을 위함

        executor.execute(() -> {
            //Background work - DB 관련 함수이기 때문에 백그라운드 스레드에서 네트워크 요청 수행
            String response = fetchDataFromServer();

            handler.post(() -> {
                //UI Thread work - 백그라운드 작업 결과를 메인 스레드로 전달
                if (response != null) {
                    List<Ranking> rankings = RankingParser.parseRanking(response);
                    setView(view, rankings); // setView 호출
                    for (int i = 0; i < rankings.size(); i++) {
                        System.out.println(rankings.get(i).getRank() + "등: " + rankings.get(i).getUserId() + " - " + rankings.get(i).getScore() + "점");
                    }
                } else {
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
            in.close();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}