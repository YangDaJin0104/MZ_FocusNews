package com.example.mz_focusnews;

import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.provider.Settings;
import android.content.ContentResolver;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mz_focusnews.Ranking.PopupManager;
import com.example.mz_focusnews.Ranking.Ranking;
import com.example.mz_focusnews.Ranking.RankingParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RankingFragment extends Fragment {
    private NavController navController;
    private static final String TAG = "RankingFragment";
    private static final String GET_QUIZ_SCORE_URL = "http://43.201.173.245/getQuizScore.php";
    private static final String GET_QUIZ_COMPLETED_URL = "http://43.201.173.245/getQuizCompleted.php";
    private static final String UPDATE_QUIZ_COMPLETED_URL = "http://43.201.173.245/updateQuizCompleted.php";
    private String USER_ID;     // 사용자 ID
    private int quiz_completed;      // 사용자가 퀴즈 푼 여부 (DB 상으로 TINYINT로 저장되어서 int 타입)
    private static final int POPUP_WIDTH = 700;
    private static final int POPUP_HEIGHT = 600;

    private Button btn_quiz_start;
    private TextView score1;
    private TextView score2;
    private TextView score3;
    private TextView score_user;
    private TextView rank_user;
    private ImageView dot;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 로그인할 때, SharedPreferences로 저장된 USER_ID 가져오기
        SharedPreferences preferences = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        USER_ID = preferences.getString("user_id", "null");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        score1 = view.findViewById(R.id.score1);
        score2 = view.findViewById(R.id.score2);
        score3 = view.findViewById(R.id.score3);
        score_user = view.findViewById(R.id.score_user);
        rank_user = view.findViewById(R.id.rank_user);
        dot = view.findViewById(R.id.dot);

        // 퀴즈 랭킹 보여주기
        showRanking(view);

        btn_quiz_start = view.findViewById(R.id.quizStart);

        // 게임 시작 버튼 클릭 시 퀴즈 화면으로 이동
        btn_quiz_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSolvedQuiz(quiz_completed)) {        // quiz_completed == 0 (오늘 퀴즈를 풀지 않은 경우)
                    Log.d(TAG, "System: 초기화 됐습니다. 문제 출제 중!");

                    setPopupGenerateQuiz(v);    // 문제 생성 중이라는 팝업창 출력
                    quiz_completed = 1;

                    ExecutorService executor = Executors.newSingleThreadExecutor();     // 백그라운드 실행을 위함

                    executor.execute(() -> {
                        updateQuizCompleted();
                    });

                    navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_rankingFragment_to_quizFragment);
                } else {        // quiz_completed == 1 (이미 오늘 퀴즈를 푼 경우)
                    Log.d(TAG, "System: 이미 오늘의 퀴즈를 풀었습니다! 내일 다시 도전하세요.");

                    setPopupAlreadySolvedQuiz(v);   // 이미 풀었다는 팝업창 출력
                }
            }
        });

        return view;
    }

    private Boolean isSolvedQuiz(int quiz_completed) {
        if(quiz_completed == 0){
            return true;
        } else{
            return false;
        }
    }

    private String getQuizCompleted() {        // users 테이블의 quiz_completed(TINYINT)를 가져옴 (Boolean)
        try {
            URL url = new URL(GET_QUIZ_COMPLETED_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // POST 데이터 작성
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            writer.write("user_id=" + USER_ID);
            writer.flush();
            writer.close();

            // 응답 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // users 테이블의 quiz_completed 가져오기 (0 또는 1)
            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                quiz_completed = jsonObject.getInt("quiz_completed");
                Log.d(TAG, "quiz_completed: " + quiz_completed);
            } catch (JSONException e) {
                Log.e(TAG, "getQuizCompleted() Error: ", e);
            }

            return response.toString();
        } catch (Exception e) {
            Log.e(TAG, "getQuizCompleted() Error: ", e);
            return null;
        }
    }

    private String updateQuizCompleted() {        // users 테이블의 quiz_completed(TINYINT) 값을 1로 업데이트 (false)
        try {
            // 요청을 보낼 URL 설정
            URL url = new URL(UPDATE_QUIZ_COMPLETED_URL);

            // 연결 설정
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);

            // POST 데이터 설정
            String postData = "userId=" + URLEncoder.encode(USER_ID, "UTF-8");

            // USER_ID 데이터 전송
            try (OutputStream os = connect.getOutputStream()) {
                byte[] input = postData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 연결 종료
            connect.disconnect();

        } catch (IOException e) {
            Log.e(TAG, "Exception occurred: " + e.getMessage());
        }

        return null;
    }

    private void setView(View view, List<Ranking> rankingList) {
        String str;

        // rankingList가 null이 아니고 최소 세 개의 요소가 있는지 확인
        if (rankingList != null && rankingList.size() >= 3) {
            str = rankingList.get(0).getUserName() + ": " + rankingList.get(0).getScore() + "점";
            score1.setText(str);
            str = rankingList.get(1).getUserName() + ": " + rankingList.get(1).getScore() + "점";
            score2.setText(str);
            str = rankingList.get(2).getUserName() + ": " + rankingList.get(2).getScore() + "점";
            score3.setText(str);

            if (rankingList.size() == 4) {
                str = rankingList.get(3).getUserName() + ": " + rankingList.get(3).getScore() + "점";
                Log.d(TAG, "STR = " + str);
                score_user.setText(str);

                String rank = Integer.toString(rankingList.get(3).getRank());
                rank_user.setText(rank);

                if (rankingList.get(3).getRank() == 4) {
                    dot.setVisibility((View.GONE));
                }

            } else {
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
            
            // quiz_completed 값 가져오기
            getQuizCompleted();
            //updateQuizCompleted();

            handler.post(() -> {
                //UI Thread work - 백그라운드 작업 결과를 메인 스레드로 전달
                if (response != null) {
                    List<Ranking> rankings = RankingParser.parseRanking(response, USER_ID);
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
            URL obj = new URL(GET_QUIZ_SCORE_URL);

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

    // 문제 생성 시 나타나는 팝업 창 설정
    private void setPopupGenerateQuiz(View view) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_ranking, null);

        // ANR 타임아웃 시간 설정 - 팝업이 오래 떠있으면 에러가 발생하기 때문. (기본 5초)
        ContentResolver contentResolver = requireActivity().getContentResolver();
        try {
            Settings.Global.putInt(contentResolver, "ANR_TIMEOUT", 10000); // 10s
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e(TAG, "ANR_TIMEOUT");
        }

        // PopupWindow 생성
        final PopupWindow popupWindow = new PopupWindow(popupView, POPUP_WIDTH, POPUP_HEIGHT, true);

        // PopupWindow 저장 (QuizFragment에서 PopupWindow 객체 사용하기 위함)
        PopupManager.getInstance().setPopupWindow(popupWindow);

        // 팝업창 메시지 설정
        TextView popupMessage = popupView.findViewById(R.id.popup_message);
        popupMessage.setText("초기화 됐습니다.\n문제 출제 중!");

        // CLOSE 버튼 숨김
        Button closeButton = popupView.findViewById(R.id.popup_close);
        closeButton.setVisibility(View.GONE);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    // 이미 오늘의 퀴즈를 풀었을 시 나타나는 팝업 창 설정
    private void setPopupAlreadySolvedQuiz(View view) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_ranking, null);

        // PopupWindow 생성
        final PopupWindow popupWindow = new PopupWindow(popupView, POPUP_WIDTH, POPUP_HEIGHT, true);

        // 팝업창 메시지 설정
        TextView popupMessage = popupView.findViewById(R.id.popup_message);
        popupMessage.setText("이미 오늘의 퀴즈를 풀었습니다!\n내일 다시 도전하세요.");


        ProgressBar popupProgressBar = popupView.findViewById(R.id.popup_progress_bar);
        popupProgressBar.setVisibility(View.GONE);

        // CLOSE 버튼
        Button closeButton = popupView.findViewById(R.id.popup_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
}
