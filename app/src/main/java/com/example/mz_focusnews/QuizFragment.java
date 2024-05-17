package com.example.mz_focusnews;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mz_focusnews.Quiz.AnswerChecker;
import com.example.mz_focusnews.Quiz.CSVFileReader;
import com.example.mz_focusnews.Quiz.CSVFileWriter;
import com.example.mz_focusnews.Quiz.ChatGPTAPI;
import com.example.mz_focusnews.Quiz.Question;
import com.example.mz_focusnews.Quiz.QuestionGenerator;
import com.example.mz_focusnews.Quiz.UpdateDBQuizScore;

import java.util.List;

public class QuizFragment extends Fragment {
    private static final String TAG = "QuizActivity";
    private static final String QUIZ_FILE_NAME = "quiz.csv";
    private static final String QUIZ_SOLVED_FILE_NAME = "quiz_solved.csv";
    private static final int QUESTION_COUNT = 4;        // 문제 갯수 (오늘의 퀴즈 제외)
    private int SCORE = 0;      // 사용자 획득 점수

    // 테스트용 데이터
    private static final String USER_ID = "user3";             // 현재 앱 사용자(테스트용 하드코딩) - 로그인 후 정보 받아옴
    private String USER_ANSWER;    // 사용자가 입력한 정답
    private static final String SUMMARIZE = "우리은행이 한국신용데이터가 주도하는 인터넷전문은행 컨소시엄에 투자 의사를 밝혔다. " +
            "한국신용데이터는 소상공인을 대상으로 하는 인터넷전문은행을 만들 계획이며, 소상공인에 대한 자체적인 신용평가 서비스를 제공한다." +
            "컨소시엄은 예비인가 신청 후 금융감독원 및 금융위의 심사를 거쳐 인가를 받으면 6개월 이내에 영업을 개시할 예정이다.";    // 3문장 요약

    // 프론트
    private NavController navController;
    private TextView text_question, text_score, text_progress;
    private Button btn_option1, btn_option2, btn_option3, btn_option4, btn_stop, btn_next, btn_complete;
    private ImageView img_correct, img_incorrect;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        text_question = view.findViewById(R.id.question);
        text_score = view.findViewById(R.id.score);
        text_progress = view.findViewById(R.id.progress);
        btn_option1 = view.findViewById(R.id.option1);
        btn_option2 = view.findViewById(R.id.option2);
        btn_option3 = view.findViewById(R.id.option3);
        btn_option4 = view.findViewById(R.id.option4);
        btn_stop = view.findViewById(R.id.stop);
        btn_next = view.findViewById(R.id.next);
        btn_complete = view.findViewById(R.id.complete);

        img_correct = view.findViewById(R.id.quiz_correct);
        img_incorrect = view.findViewById(R.id.quiz_incorrect);

        showQuiz(view);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void showQuiz(View view) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            CSVFileReader csvFileReader = new CSVFileReader();
            CSVFileWriter csvFileWriter = new CSVFileWriter();
            Context context = requireContext();         // 컨텍스트 가져오기

            // quiz_solved.csv 파일 초기화 (테스트용)
            csvFileWriter.clearQuizSolvedCSVFile(context, QUIZ_SOLVED_FILE_NAME);

            // CSV 파일을 읽어옴
            csvFileReader.readQuizCSVFile(context, QUIZ_FILE_NAME);

            // 2~5번째 퀴즈 출제: 문제은행 퀴즈 - quiz.csv 파일에 저장된 퀴즈 리스트 중 4문제
            List<Question> quizQuestions = QuestionGenerator.generateQuestions(context, QUIZ_FILE_NAME, QUIZ_SOLVED_FILE_NAME, USER_ID, QUESTION_COUNT);

            quiz(view, quizQuestions);
        }
    }

    public void quiz(View view, List<Question> quizList) {
        Question todayQuiz = null;      // 오늘의 퀴즈 생성 결과 (JSON 형태)
        String response;                // 오늘의 퀴즈 생성 여부

        // 오늘의 퀴즈(ChatGPT 기반 퀴즈)가 제대로 만들어질 때까지 반복
        while (todayQuiz == null) {
            response = ChatGPTAPI.chatGPT(SUMMARIZE);
            todayQuiz = QuestionGenerator.generateTodayQuiz(response);
        }

        // 1번째 문제: 오늘의 퀴즈 - 오늘의 뉴스 기반의 문제 출제
        setQuizView(view, todayQuiz, 1);

        clickOption(view, todayQuiz);

        AnswerChecker answerChecker = new AnswerChecker(todayQuiz);

        final Question static_question = todayQuiz;
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(view);
                //navController.navigate(R.id.action_rankingFragment_to_quizFragment);  // 수정 필요

                // 사용자가 선택한 답이 정답인지 확인
                if (answerChecker.isCorrectAnswer(USER_ANSWER, static_question)) {
                    SCORE = 1;
                    setResultView(true);
                    csvQuiz1(view, quizList);   // 정답일 경우 다음 문제로 넘어감
                } else {
                    Log.d(TAG, "틀렸습니다!");
                    setResultView(false);
                    updateDBQuizScore();
                    // TODO: '그만' 버튼 클릭 시 fragment_ranking 화면으로 넘어가야 함.
                }
            }
        });
    }

    private void csvQuiz1(View view, List<Question> quizList) {
        Question current_quiz = quizList.get(0);

        setQuizView(view, current_quiz, 2);
        clickOption(view, current_quiz);

        AnswerChecker answerChecker = new AnswerChecker(current_quiz);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(view);
                //navController.navigate(R.id.action_rankingFragment_to_quizFragment);  // 수정 필요

                // 사용자가 선택한 답이 정답인지 확인
                if (answerChecker.isCorrectAnswer(USER_ANSWER, current_quiz)) {
                    SCORE = 3;
                    setResultView(true);
                    csvQuiz2(view, quizList);   // 정답일 경우 다음 문제로 넘어감
                } else {
                    Log.d(TAG, "틀렸습니다!");
                    setResultView(false);
                    updateDBQuizScore();
                    // TODO: '그만' 버튼 클릭 시 fragment_ranking 화면으로 넘어가야 함.
                }
            }
        });
    }

    private void csvQuiz2(View view, List<Question> quizList) {
        Question current_quiz;

        current_quiz = quizList.get(1);
        setQuizView(view, current_quiz, 3);
        clickOption(view, current_quiz);

        AnswerChecker answerChecker = new AnswerChecker(current_quiz);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(view);
                //navController.navigate(R.id.action_rankingFragment_to_quizFragment);  // 수정 필요

                // 사용자가 선택한 답이 정답인지 확인
                if (answerChecker.isCorrectAnswer(USER_ANSWER, current_quiz)) {
                    SCORE = 5;
                    setResultView(true);
                    csvQuiz3(view, quizList);   // 정답일 경우 다음 문제로 넘어감
                } else {
                    Log.d(TAG, "틀렸습니다!");
                    setResultView(false);
                    updateDBQuizScore();
                    // TODO: '그만' 버튼 클릭 시 fragment_ranking 화면으로 넘어가야 함.
                }
            }
        });
    }

    private void csvQuiz3(View view, List<Question> quizList) {
        Question current_quiz;

        current_quiz = quizList.get(2);
        setQuizView(view, current_quiz, 4);
        clickOption(view, current_quiz);

        AnswerChecker answerChecker = new AnswerChecker(current_quiz);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(view);
                //navController.navigate(R.id.action_rankingFragment_to_quizFragment);  // 수정 필요

                // 사용자가 선택한 답이 정답인지 확인
                if (answerChecker.isCorrectAnswer(USER_ANSWER, current_quiz)) {
                    SCORE = 7;
                    setResultView(true);
                    csvQuiz4(view, quizList);   // 정답일 경우 다음 문제로 넘어감
                } else {
                    Log.d(TAG, "틀렸습니다!");
                    setResultView(false);
                    updateDBQuizScore();
                    // TODO: '그만' 버튼 클릭 시 fragment_ranking 화면으로 넘어가야 함.
                }
            }
        });
    }

    private void csvQuiz4(View view, List<Question> quizList) {
        Question current_quiz;

        current_quiz = quizList.get(3);
        setQuizView(view, current_quiz, 5);
        clickOption(view, current_quiz);

        AnswerChecker answerChecker = new AnswerChecker(current_quiz);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(view);
                //navController.navigate(R.id.action_rankingFragment_to_quizFragment);  // 수정 필요

                // 사용자가 선택한 답이 정답인지 확인
                if (answerChecker.isCorrectAnswer(USER_ANSWER, current_quiz)) {
                    SCORE = 10;
                    setResultView(true);
                    setCompleteView();      // 퀴즈 종료 시 설정
                } else {
                    Log.d(TAG, "틀렸습니다!");
                    setResultView(false);
                    // TODO: '그만' 버튼 클릭 시 fragment_ranking 화면으로 넘어가야 함.
                }
                updateDBQuizScore();
            }
        });
    }

    private void setQuizView(View view, Question quiz, int count) {
        String str_score, str_progress;

        // 문제 내용 설정
        str_score = "Q. " + quiz.getQuestion();
        text_question.setText(str_score);

        // 퀴즈 진행도 설정
        str_progress = "(" + count + "/5)";
        text_progress.setText(str_progress);

        // 점수 설정
        switch (count) {     // count = 몇 번째 문제인지
            case 1:
                str_score = "(1점)";
                break;
            case 2:
                str_score = "(3점)";
                break;
            case 3:
                str_score = "(5점)";
                break;
            case 4:
                str_score = "(7점)";
                break;
            case 5:
                str_score = "(10점)";
                break;
            default:
                break;
        }
        text_score.setText(str_score);

        // 보기 설정
        btn_option1.setText(quiz.getOption1());
        btn_option2.setText(quiz.getOption2());
        btn_option3.setText(quiz.getOption3());
        btn_option4.setText(quiz.getOption4());

        int blueColorValue = Color.parseColor("#1876E3");
        ColorStateList blue = ColorStateList.valueOf(blueColorValue);      // 기본

        btn_option1.setBackgroundTintList(blue);
        btn_option2.setBackgroundTintList(blue);
        btn_option3.setBackgroundTintList(blue);
        btn_option4.setBackgroundTintList(blue);
    }

    private void setResultView(boolean iscorrect) {
        ObjectAnimator fadeOut;

        if (iscorrect) {
            img_correct.setVisibility(View.VISIBLE);
            fadeOut = ObjectAnimator.ofFloat(img_correct, "alpha", 1f, 0f);
        } else {
            img_incorrect.setVisibility(View.VISIBLE);
            fadeOut = ObjectAnimator.ofFloat(img_incorrect, "alpha", 1f, 0f);
        }

        // 2초 후에 흐릿하게 사라지는 애니메이션을 실행하는 Handler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeOut.setDuration(1000);      // 1초간 FadeOut
                fadeOut.start();

                // 애니메이션이 끝난 후 INVISIBLE로 설정
                fadeOut.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        img_correct.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
            }
        }, 0);

        // 틀렸을 경우, '그만','다음' 버튼 비활성화
        if (!iscorrect) {
            setCompleteView();
        }
    }

    // 퀴즈 종료 시 설정
    private void setCompleteView() {
        btn_complete.setVisibility(View.VISIBLE);
        btn_stop.setVisibility(View.INVISIBLE);
        btn_next.setVisibility(View.INVISIBLE);
    }

    // 보기1,2,3,4 버튼 클릭 시 설정
    private void clickOption(View view, Question quiz) {
        int greenColorValue = Color.parseColor("#00ff00");
        int blueColorValue = Color.parseColor("#1876E3");

        ColorStateList green = ColorStateList.valueOf(greenColorValue);
        ColorStateList blue = ColorStateList.valueOf(blueColorValue);      // 기본

        btn_option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "option1 click");
                btn_option1.setBackgroundTintList(green);
                btn_option2.setBackgroundTintList(blue);
                btn_option3.setBackgroundTintList(blue);
                btn_option4.setBackgroundTintList(blue);
                USER_ANSWER = quiz.getOption1();
            }
        });

        btn_option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "option2 click");
                btn_option1.setBackgroundTintList(blue);
                btn_option2.setBackgroundTintList(green);
                btn_option3.setBackgroundTintList(blue);
                btn_option4.setBackgroundTintList(blue);
                USER_ANSWER = quiz.getOption2();
            }
        });

        btn_option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "option3 click");
                btn_option1.setBackgroundTintList(blue);
                btn_option2.setBackgroundTintList(blue);
                btn_option3.setBackgroundTintList(green);
                btn_option4.setBackgroundTintList(blue);
                USER_ANSWER = quiz.getOption3();
            }
        });

        btn_option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "option4 click");
                btn_option1.setBackgroundTintList(blue);
                btn_option2.setBackgroundTintList(blue);
                btn_option3.setBackgroundTintList(blue);
                btn_option4.setBackgroundTintList(green);
                USER_ANSWER = quiz.getOption4();
            }
        });
    }

    private void updateDBQuizScore(){
        UpdateDBQuizScore updateQuizScore = new UpdateDBQuizScore(USER_ID, SCORE);
        updateQuizScore.execute();
    }
}