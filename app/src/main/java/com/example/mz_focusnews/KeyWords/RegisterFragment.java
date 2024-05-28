package com.example.mz_focusnews.KeyWords;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mz_focusnews.KeyWords.UserKeyWord;
import com.example.mz_focusnews.R;

public class RegisterFragment extends Fragment {

    private EditText keyword1EditText;
    private Button keyword1Button;

    private EditText keyword2EditText;
    private Button keyword2Button;

    private EditText keyword3EditText;
    private Button keyword3Button;

    private Button completeRegisterButton;

    private String userId; // 사용자의 ID를 저장할 변수

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_key_word, container, false);

        // UI 요소 초기화
        keyword1EditText = view.findViewById(R.id.keyword1);
        keyword1Button = view.findViewById(R.id.btn_keyword1);

        keyword2EditText = view.findViewById(R.id.keyword2);
        keyword2Button = view.findViewById(R.id.btn_keyword2);

        keyword3EditText = view.findViewById(R.id.keyword3);
        keyword3Button = view.findViewById(R.id.btn_keyword3);

        completeRegisterButton = view.findViewById(R.id.btn_complete_register);

        // SharedPreferences를 사용하여 userId를 초기화
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null); // 사용자 ID 불러오기

        // 키워드1 버튼 클릭 리스너 설정
        keyword1Button.setOnClickListener(v -> {
            String keyword = keyword1EditText.getText().toString().trim();
            if (!keyword.isEmpty()) {
                // UserKeyWord 클래스를 사용하여 키워드를 서버에 저장
                UserKeyWord.saveKeywordToServer(getContext(), userId, keyword, 1);
            }
        });

        // 키워드2 버튼 클릭 리스너 설정
        keyword2Button.setOnClickListener(v -> {
            String keyword = keyword2EditText.getText().toString().trim();
            if (!keyword.isEmpty()) {
                UserKeyWord.saveKeywordToServer(getContext(), userId, keyword, 2);
            }
        });

        // 키워드3 버튼 클릭 리스너 설정
        keyword3Button.setOnClickListener(v -> {
            String keyword = keyword3EditText.getText().toString().trim();
            if (!keyword.isEmpty()) {
                UserKeyWord.saveKeywordToServer(getContext(), userId, keyword, 3);
            }
        });


        return view;
    }
}
