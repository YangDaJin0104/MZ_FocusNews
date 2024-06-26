package com.example.mz_focusnews.KeyWords;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mz_focusnews.ContentFragment;
import com.example.mz_focusnews.R;
import com.example.mz_focusnews.NewsDB.RetrofitClient;
import com.example.mz_focusnews.UsersDB.UserApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeywordChangeFragment extends Fragment {

    private EditText keyword1EditText;
    private Button keyword1Button;
    private TextView userKeyword1;

    private EditText keyword2EditText;
    private Button keyword2Button;
    private TextView userKeyword2;

    private EditText keyword3EditText;
    private Button keyword3Button;
    private TextView userKeyword3;

    private ImageButton btn_back;
    private Button completeChangeButton;

    private String userId; // 사용자의 ID를 저장할 변수
    private UserApi userApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyword_change, container, false);

        // UI 요소 초기화
        keyword1EditText = view.findViewById(R.id.change_keyword1);
        keyword1Button = view.findViewById(R.id.btn_change_keyword1);
        userKeyword1 = view.findViewById(R.id.user_keyword1);

        keyword2EditText = view.findViewById(R.id.change_keyword2);
        keyword2Button = view.findViewById(R.id.btn_change_keyword2);
        userKeyword2 = view.findViewById(R.id.user_keyword2);

        keyword3EditText = view.findViewById(R.id.change_keyword3);
        keyword3Button = view.findViewById(R.id.btn_change_keyword3);
        userKeyword3 = view.findViewById(R.id.user_keyword3);

        completeChangeButton = view.findViewById(R.id.btn_complete_change);

        // 뒤로가기
        btn_back = view.findViewById(R.id.backbtn);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(KeywordChangeFragment.this).navigateUp();
            }
        });

        // SharedPreferences를 사용하여 userId를 초기화
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null); // 사용자 ID 불러오기

        // Retrofit 초기화
        userApi = RetrofitClient.getInstance().getUserApi();

        // 서버에서 기존 키워드를 가져와 표시
        userApi.getUserKeywords(userId).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> keywords = response.body();
                    if (keywords != null) {
                        if (keywords.size() > 0) userKeyword1.setText(keywords.get(0));
                        if (keywords.size() > 1) userKeyword2.setText(keywords.get(1));
                        if (keywords.size() > 2) userKeyword3.setText(keywords.get(2));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                // 에러 처리
            }
        });

        // 키워드1 버튼 클릭 리스너 설정
        keyword1Button.setOnClickListener(v -> {
            String keyword = keyword1EditText.getText().toString().trim();
            if (!keyword.isEmpty()) {
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

        // 완료 버튼 클릭 리스너 설정
        completeChangeButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(KeywordChangeFragment.this)
                    .navigate(R.id.action_keywordChangeFragment_to_myPageFragment);
        });

        return view;
    }
}
