package com.example.mz_focusnews;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.BroadcastReceiver;

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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mz_focusnews.Quiz.QuizTimeReset;
import com.example.mz_focusnews.Ranking.PopupManager;
import com.example.mz_focusnews.Ranking.Ranking;
import com.example.mz_focusnews.Ranking.RankingParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RankingFragment extends Fragment {
    private NavController navController;
    private static final String TAG = "RankingFragment";
    private static final String URL = "http://43.201.173.245/getQuizScoreJson.php";
    private static final String PREFS_NAME = "QuizPrefs";
    private String USER_ID;     // 사용자 ID
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

        Intent serviceIntent = new Intent(getActivity(), QuizTimeReset.class);
        getActivity().startService(serviceIntent);


        //scheduleQuizTimeReset();
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

        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        //setAllSolvedQuizFlagToFalse();      // SharedPreferences 변수 모두 초기화용 (테스트)

        showRanking(view);

        btn_quiz_start = view.findViewById(R.id.quizStart);

        // 게임 시작 버튼 클릭 시 퀴즈 화면으로 이동
        btn_quiz_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSolvedQuiz(preferences)) {
                    Log.d(TAG, "System: 이미 오늘의 퀴즈를 풀었습니다! 내일 다시 도전하세요.");
                    setPopupAlreadySolvedQuiz(v);   // 이미 오늘 퀴즈를 푼 경우, 팝업창 출력
                } else {
                    Log.d(TAG, "System: 초기화 됐습니다. 문제 출제 중!");
                    setPopupGenerateQuiz(v);        // 오늘 퀴즈를 풀지 않은 경우, 문제 생성 중이라는 팝업창 출력
                    setSolvedQuizFlag(true);
                    navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_rankingFragment_to_quizFragment);
                }
            }
        });

        return view;
    }

    private Boolean isSolvedQuiz(SharedPreferences preferences) {
        boolean isSolvedQuiz = false;
        Map<String, ?> mapData = preferences.getAll();
        boolean found = false;      // USER_ID가 있는지 여부

        int index = 0;    // Log 출력을 위한 카운터 변수
        for (Map.Entry<String, ?> entry : mapData.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Log.d(TAG, "isSolvedQuiz() - " + index + ". key = " + key + "/ value = " + value);

            if (key.equals(USER_ID) && value instanceof Boolean) {
                isSolvedQuiz = (boolean) value;
                found = true; // USER_ID를 찾았음을 표시
                break;
            }

            index++;
        }

        if (!found) {
            // USER_ID를 찾지 못했을 경우 기본값으로 false인 데이터를 넣어줍니다.
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(USER_ID, false);
            editor.apply();
            isSolvedQuiz = false;
        }

        return isSolvedQuiz;
    }

    // TODO: 한국 시간 오전 12시 이후 유저 정보 초기화 됐는지 확인 -> 앱이 실행 중이 아닐 때는 초기화가 되지 않음.
    private void scheduleQuizTimeReset() {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable resetQuizFlagTask = new Runnable() {
            @Override
            public void run() {
                setAllSolvedQuizFlagToFalse();
                handler.postDelayed(this, TimeUnit.DAYS.toMillis(1));   // 하루 뒤 같은 작업 예약
            }
        };

        // 매일 오전 12시(한국시간)에 퀴즈 초기화 - 뉴스 업데이트 시간에 맞춤
        Calendar now = Calendar.getInstance();
        Calendar next12AM = Calendar.getInstance();
        next12AM.set(Calendar.HOUR_OF_DAY, 15);      // 기본적으로 UTC이기 때문에, 한국 시간에 맞춰 -9h -> 15h
        next12AM.set(Calendar.MINUTE, 0);
        next12AM.set(Calendar.SECOND, 0);
        next12AM.set(Calendar.MILLISECOND, 0);

        // 이미 오전 12시가 지난 경우, 내일 오전 12시에 초기화
        if (now.after(next12AM)) {
            next12AM.add(Calendar.DAY_OF_MONTH, 1);
        }

        long initialDelay = next12AM.getTimeInMillis() - now.getTimeInMillis();
        handler.postDelayed(resetQuizFlagTask, initialDelay);
    }

    // SharedPreferences 변수 값 true/false(파라메터)로 저장
    private boolean setSolvedQuizFlag(boolean isSolved) {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(USER_ID, isSolved);
        editor.apply();

        return isSolved;
    }

    // QuizTimeReset에서 특정 시간이 됐을 때 broadcast 하면 받아와서 변수 초기화 함수 실행
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_SET_QUIZ_FLAG".equals(intent.getAction())) {
                setAllSolvedQuizFlagToFalse();
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, new IntentFilter("ACTION_SET_QUIZ_FLAG"));
    }
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver);
    }

    
    // 모든 SharedPreferences 변수 값 false로 저장 - 모든 유저에 대한 퀴즈 푼 여부를 false로 바꿈
    public void setAllSolvedQuizFlagToFalse() {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Map<String, ?> mapData = preferences.getAll();

        for (Map.Entry<String, ?> entry : mapData.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Boolean) {
                editor.putBoolean(key, false);
            }

            Log.d(TAG, "모든 SharedPreferences 값 - key = " + key + " / value = " + value);
        }

        editor.apply();
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
