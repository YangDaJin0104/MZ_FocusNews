package com.example.mz_focusnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText loginID, loginPw;
    private Button loginBtn;
    private TextView forgetPw, goToSignup;


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

        loginID = (EditText) findViewById(R.id.et_id);
        loginPw = (EditText) findViewById(R.id.et_pw);

        loginBtn = (Button) findViewById(R.id.btn_login);

        forgetPw = (TextView) findViewById(R.id.tv_forgetPw);
        goToSignup = (TextView) findViewById(R.id.tv_goToSighup);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에서 ID, PW get
                String UserID = loginID.getText().toString();
                String UserPw = loginPw.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {//로그인 성공시

                                String UserID = jsonObject.getString("UserID");
                                String UserPwd = jsonObject.getString("UserPw");
                                String UserName = jsonObject.getString("UserName");

                                Toast.makeText(getApplicationContext(), String.format("%s님 환영합니다.", UserName), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                intent.putExtra("UserEmail", UserID);
                                intent.putExtra("UserPwd", UserPwd);
                                intent.putExtra("UserName", UserName);

                                startActivity(intent);

                            } else {//로그인 실패시
                                Toast.makeText(getApplicationContext(), "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(UserID, UserPw, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);

            }
        });


        // 회원가입 텍스트를 눌렀을 떄 수행
        goToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }
}