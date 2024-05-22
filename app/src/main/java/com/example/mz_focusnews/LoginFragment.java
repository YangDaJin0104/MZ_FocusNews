package com.example.mz_focusnews;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.request.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment {

    private EditText loginID, loginPw;
    private Button loginBtn;
    private TextView forgetPw, goToSignup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginID = view.findViewById(R.id.et_id);
        loginPw = view.findViewById(R.id.et_pw);
        loginBtn = view.findViewById(R.id.btn_login);
        forgetPw = view.findViewById(R.id.tv_forgetPw);
        goToSignup = view.findViewById(R.id.tv_goToSignup);

        // 회원가입 페이지로 이동
        goToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_loginFragment_to_registerFragment);

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
                                String user_id = jsonObject.getString("user_id");
                                String user_pw = jsonObject.getString("user_pw");
                                String user_name = jsonObject.getString("user_name");

                                Toast.makeText(getActivity(), "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                                // UserData라는 이름으로 식별되는 공유 환경설정 파일, 비공개 모드
                                // 데이터를 읽어올 때는 똑같이 sp 선언 하고 getString(key, value)로 호출
                                SharedPreferences sp = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("user_id", user_id);
                                editor.putString("user_name", user_name);
                                editor.apply();

                                // HomeFragment로 이동
                                NavHostFragment.findNavController(LoginFragment.this)
                                        .navigate(R.id.action_loginFragment_to_homeFragment);

                            } else {    // 로그인 실패
                                Toast.makeText(getActivity(), "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // 로그인 요청을 보내는 객체를 생성하고, 서버 요청을 보낸다
                LoginRequest loginRequest = new LoginRequest(UserID, UserPw, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(loginRequest);
            }
        });

        return view;
    }
}
