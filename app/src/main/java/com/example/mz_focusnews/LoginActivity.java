package com.example.mz_focusnews;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    EditText edt_id;
    EditText edt_pw;
    TextView tv_goToFindPw;
    TextView tv_goToSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edt_id = (EditText) findViewById(R.id.et_id);
        edt_pw = (EditText) findViewById(R.id.et_pw);

        // 비밀번호 찾기 페이지로 이동
        tv_goToFindPw = (TextView) findViewById(R.id.goToFindPwd);
        // ...

        // 회원가입 페이지로 이동
        tv_goToSignup = (TextView) findViewById(R.id.goToSignup);
        tv_goToSignup.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        });

    }
}