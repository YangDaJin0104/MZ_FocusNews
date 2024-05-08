package com.example.mz_focusnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.request.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText loginID, loginPw;
    private Button loginBtn;
    private TextView forgetPw, goToSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginID = findViewById(R.id.et_id);
        loginPw = findViewById(R.id.et_pw);
        loginBtn = findViewById(R.id.btn_login);
        forgetPw = findViewById(R.id.tv_forgetPw);
        goToSignup = findViewById(R.id.tv_goToSignup);

        // 회원가입 페이지로 이동
        goToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });


        // 로그인 버튼 클릭 시
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력된 아이디와 비밀번호 가져오기
                String UserID = loginID.getText().toString();
                String UserPw = loginPw.getText().toString();

                // 서버 응답 처리
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {  // 로그인 성공
                                String userID = jsonObject.getString("user_id");
                                String userPw = jsonObject.getString("user_pw");
                                String userName = jsonObject.getString("user_name");

                                Toast.makeText(getApplicationContext(), "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                                Bundle bundle = new Bundle();
                                bundle.putString("user_name", userName); // 사용자 이름을 Bundle에 추가

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userData", bundle); // Bundle을 Intent에 추가
                                startActivity(intent);

                                finish();
                            } else {    // 로그인 실패
                                Toast.makeText(getApplicationContext(), "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // 로그인 요청을 보내는 객체를 생성하고, 서버 요청을 보낸다
                LoginRequest loginRequest = new LoginRequest(UserID, UserPw, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });

    }
}