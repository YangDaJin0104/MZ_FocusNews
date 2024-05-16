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
import android.widget.TextView;

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

    // 프론트
    private Button btn_quiz_submit;
    private NavController navController;
    private TextView question;
    private TextView score;
    private Button option1;
    private Button option2;
    private Button option3;
    private Button option4;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        question = view.findViewById(R.id.question);
        score = view.findViewById(R.id.score);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);
        btn_quiz_submit = view.findViewById(R.id.next);

        quiz(view);
        // 게임 시작 버튼 클릭 시 퀴즈 화면으로 이동


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        quiz(view);
    }

    public void quiz(View view){
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            int score = 0;                 // 사용자가 얻은 점수

            CSVFileReader csvFileReader = new CSVFileReader();
            CSVFileWriter csvFileWriter = new CSVFileWriter();
            Context context = requireContext();         // 컨텍스트 가져오기

            // quiz_solved.csv 파일 초기화 (테스트용)
            csvFileWriter.clearQuizSolvedCSVFile(context, QUIZ_SOLVED_FILE_NAME);

            // CSV 파일을 읽어옴
            csvFileReader.readQuizCSVFile(context, QUIZ_FILE_NAME);

            // 퀴즈 출제
            // 2~5번째 문제: 문제은행 퀴즈 - quiz.csv 파일에 저장된 퀴즈 리스트 중 4문제
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

            todayQuiz(view, quizQuestions.get(0));

            // 문제 출제 및 채점
            AnswerChecker answerChecker = new AnswerChecker(quizQuestions);
            USER_ANSWER.put(quizQuestions.get(0).getId(), quizQuestions.get(0).getCorrectAnswer());        // 테스트용
            answerChecker.isCorrectAnswer(USER_ANSWER);
        }
    }

    // 1번째 문제: 오늘의 퀴즈 - 오늘의 뉴스 기반의 문제 출제
    public void todayQuiz(View view, Question nextQuiz){
        Question todayQuiz = null;      // 오늘의 퀴즈 생성 결과 (JSON 형태)
        String response;                // 오늘의 퀴즈 생성 여부

        // 오늘의 퀴즈(ChatGPT 기반 퀴즈)가 제대로 만들어질 때까지 반복
        while(todayQuiz == null){
            response = ChatGPTAPI.chatGPT(SUMMARIZE1, SUMMARIZE2, SUMMARIZE3);
            todayQuiz = QuestionGenerator.generateTodayQuiz(response);
        }

        setView(view, todayQuiz, 0);

        clickOption(view, todayQuiz);

        AnswerChecker answerChecker = new AnswerChecker(todayQuiz);

        btn_quiz_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(view);
                //navController.navigate(R.id.action_rankingFragment_to_quizFragment);  // 수정 필요
                if(answerChecker.isCorrectAnswer(USER_ANSWER)){
                    setView(view, nextQuiz, 1);
                } else{
                    Log.d(TAG, "틀렸습니다!");
                    System.exit(1);
                }
            }
        });
    }

    private void setView(View view, Question quiz, int count){
        String str;

        // 문제 내용 설정
        str = "Q. " + quiz.getQuestion();
        question.setText(str);

        // 점수 설정
        switch (count){     // count = 몇 번째 문제인지 (0~3)
            case 0:
                str = "(1점)";
                break;
            case 1:
                str = "(3점)";
                break;
            case 2:
                str = "(5점)";
                break;
            case 3:
                str = "(7점)";
                break;
            case 4:
                str = "(10점)";
                break;
            default:
                break;
        }
        score.setText(str);

        // 보기 설정
        option1.setText(quiz.getOption1());
        option2.setText(quiz.getOption2());
        option3.setText(quiz.getOption3());
        option4.setText(quiz.getOption4());
    }

    private void clickOption(View view, Question todayQuiz){
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "option1 click");
                USER_ANSWER.put(todayQuiz.getId(), todayQuiz.getOption1());
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "option2 click");
                USER_ANSWER.put(todayQuiz.getId(), todayQuiz.getOption2());
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "option3 click");
                USER_ANSWER.put(todayQuiz.getId(), todayQuiz.getOption3());
            }
        });

        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "option4 click");
                USER_ANSWER.put(todayQuiz.getId(), todayQuiz.getOption4());
            }
        });
    }
}