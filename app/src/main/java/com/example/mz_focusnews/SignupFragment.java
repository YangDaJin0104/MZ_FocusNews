package com.example.mz_focusnews;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupFragment extends Fragment {

    private EditText signup_username, signup_userID, signup_userPw, signup_userPwCheck;
    private Button idCheckBtn, completeSignup;
    private AlertDialog dialog;
    private boolean validate = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signup_username = view.findViewById(R.id.et_name);
        signup_userID = view.findViewById(R.id.et_id);
        signup_userPw = view.findViewById(R.id.et_pw);
        signup_userPwCheck = view.findViewById(R.id.et_pw_check);

        completeSignup = view.findViewById(R.id.btn_complete_signup);
        idCheckBtn = view.findViewById(R.id.btn_idcheck);

        /**
         * TODO: 이름 중복 확인
         */

        // 아이디 중복 확인
        idCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserID = signup_userID.getText().toString();
                if (validate) { // 검증이 완료된 경우 추가 검증 없이 함수 종료
                    return;
                }

                // 아이디가 입력되지 않았다면
                if (UserID.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    dialog = builder.setMessage("아이디를 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }

                // 서버로부터의 응답 처리
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            // 성공 여부에 따라 dialog 생성하여 보여줌
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                signup_userID.setEnabled(false);// 아이디 입력란을 비활성화 (아이디 값 고정)
                                validate = true; //검증 완료
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                dialog = builder.setMessage("이미 존재하는 아이디입니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // ValidateRequest: 서버에 아이디 중복 확인 요청 보내는 객체
                ValidateRequest validateRequest = new ValidateRequest(UserID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(validateRequest);
            }
        });

        completeSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String UserID = signup_userID.getText().toString();
                final String UserPw = signup_userPw.getText().toString();
                final String UserPwCk = signup_userPwCheck.getText().toString();
                final String UserName = signup_username.getText().toString();

                // 아이디 중복체크 했는지 확인
                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    dialog = builder.setMessage("중복된 아이디가 있는지 확인하세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                // 한 칸이라도 입력 안했을 경우
                if (UserID.equals("") || UserPw.equals("") || UserName.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                // 응답 처리
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            // 회원가입 성공시 비밀번호 체크
                            if (UserPw.equals(UserPwCk)) {
                                if (success) {
                                    Toast.makeText(getContext(), String.format("%s님 가입을 환영합니다.", UserName), Toast.LENGTH_SHORT).show();

                                    // 회원가입이 성공하면 로그인 화면으로 이동
                                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                                    navController.navigate(R.id.action_signupFragment_to_signupCompleteFragment);
                                } else { // 회원가입 실패시
                                    Toast.makeText(getContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                dialog = builder.setMessage("비밀번호가 동일하지 않습니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                // 서버로 Volley를 이용해서 요청
                SignupRequest registerRequest = new SignupRequest(UserID, UserPw, UserName, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(registerRequest);
            }
        });
    }
}
