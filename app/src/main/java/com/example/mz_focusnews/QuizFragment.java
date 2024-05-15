package com.example.mz_focusnews;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mz_focusnews.Quiz.AnswerChecker;
import com.example.mz_focusnews.Quiz.CSVFileReader;
import com.example.mz_focusnews.Quiz.CSVFileWriter;
import com.example.mz_focusnews.Quiz.ChatGPTAPI;
import com.example.mz_focusnews.Quiz.Question;
import com.example.mz_focusnews.Quiz.QuestionGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizFragment extends Fragment {
    private static final String TAG = "QuizActivity";
    private static final String QUIZ_FILE_NAME = "quiz.csv";
    private static final String QUIZ_SOLVED_FILE_NAME = "quiz_solved.csv";
    private static final int QUESTION_COUNT = 4;        // 문제 갯수 (오늘의 퀴즈 제외)

    // 테스트용 데이터
    private static final int USER_ID = 456;             // 현재 앱 사용자(테스트용 하드코딩) - 로그인 후 정보 받아옴
    Map<Integer, String> USER_ANSWER = new HashMap<>();    // 현재 앱 사용자가 입력한 정답
    private static final String SUMMARIZE1 = "우리은행이 한국신용데이터가 주도하는 인터넷전문은행 컨소시엄에 투자 의사를 밝혔다.";   // 요약1
    private static final String SUMMARIZE2 = "한국신용데이터는 소상공인을 대상으로 하는 인터넷전문은행을 만들 계획이며, 소상공인에 대한 자체적인 신용평가 서비스를 제공한다.";  // 요약2
    private static final String SUMMARIZE3 = "컨소시엄은 예비인가 신청 후 금융감독원 및 금융위의 심사를 거쳐 인가를 받으면 6개월 이내에 영업을 개시할 예정이다.";   // 요약3

    private Button btn_quiz_submit;
    private NavController navController;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        btn_quiz_submit = view.findViewById(R.id.quiz_submit);

        // 게임 시작 버튼 클릭 시 퀴즈 화면으로 이동
        btn_quiz_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(view);
                //navController.navigate(R.id.action_rankingFragment_to_quizFragment);  // 수정 필요
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        quiz();
    }

    public void quiz(){
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            CSVFileReader csvFileReader = new CSVFileReader();
            CSVFileWriter csvFileWriter = new CSVFileWriter();
            Context context = requireContext(); // 컨텍스트 가져오기

            // quiz_solved.csv 파일 초기화 (테스트용)
            csvFileWriter.clearQuizSolvedCSVFile(context, QUIZ_SOLVED_FILE_NAME);

            // CSV 파일을 읽어옴
            List<String[]> csvData = csvFileReader.readQuizCSVFile(context, QUIZ_FILE_NAME);

            // 읽어온 데이터 출력
            for (String[] line : csvData) {
                StringBuilder lineBuilder = new StringBuilder();
                for (String value : line) {
                    lineBuilder.append(value).append(", ");
                }
                Log.d(TAG, "CSV Read: " + lineBuilder.toString());
            }

            // 퀴즈 출제
            List<Question> quizQuestions = QuestionGenerator.generateQuestions(context, QUIZ_FILE_NAME, QUIZ_SOLVED_FILE_NAME, USER_ID, QUESTION_COUNT);

            // 퀴즈 문제 출력
            for (Question question : quizQuestions) {
                Log.d(TAG, "Question ID: " + question.getId());
                Log.d(TAG, "Question: " + question.getQuestion());
                Log.d(TAG, "Correct Answer: " + question.getCorrectAnswer());
                Log.d(TAG, "Option 1: " + question.getOption1());
                Log.d(TAG, "Option 2: " + question.getOption2());
                Log.d(TAG, "Option 3: " + question.getOption3());
                Log.d(TAG, "Option 4: " + question.getOption4());
            }

            csvFileReader.readQuizSolvedCSVFile(context, QUIZ_SOLVED_FILE_NAME);

            Question todayQuiz = null;      // 오늘의 퀴즈 생성 결과 (JSON 형태)
            String response;                // 오늘의 퀴즈 생성 여부
            // 오늘의 퀴즈(ChatGPT 기반 퀴즈)가 제대로 만들어질 때까지 반복
            while(todayQuiz == null){
                response = ChatGPTAPI.chatGPT(SUMMARIZE1, SUMMARIZE2, SUMMARIZE3);
                todayQuiz = QuestionGenerator.generateTodayQuiz(response);
            }

            // 문제 출제 및 채점
            AnswerChecker answerChecker = new AnswerChecker(quizQuestions);
            USER_ANSWER.put(quizQuestions.get(0).getId(), quizQuestions.get(0).getCorrectAnswer());        // 테스트용
            USER_ANSWER.put(quizQuestions.get(1).getId(), quizQuestions.get(1).getCorrectAnswer());        // 테스트용
            USER_ANSWER.put(quizQuestions.get(2).getId(), quizQuestions.get(2).getCorrectAnswer());        // 테스트용
            USER_ANSWER.put(quizQuestions.get(3).getId(), quizQuestions.get(3).getCorrectAnswer());        // 테스트용
            int score = answerChecker.checkAnswers(USER_ANSWER);
            Log.d(TAG, "Score: " + score);
        }

    }
}