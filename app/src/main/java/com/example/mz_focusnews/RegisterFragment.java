package com.example.mz_focusnews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.request.SignupRequest;
import com.example.mz_focusnews.request.ValidateRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterFragment extends Fragment {

    private EditText signup_username, signup_userID, signup_userPw, signup_userPwCheck;
    private Button idCheckBtn, signupbtn, nameCheckBtn;
    private boolean validate = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        signup_username = view.findViewById(R.id.et_name);
        signup_userID = view.findViewById(R.id.et_id);
        signup_userPw = view.findViewById(R.id.et_pw);
        signup_userPwCheck = view.findViewById(R.id.et_pw_check);

        nameCheckBtn = view.findViewById(R.id.btn_namecheck);
        idCheckBtn = view.findViewById(R.id.btn_idcheck);
        signupbtn = view.findViewById(R.id.btn_complete_signup);

        // 이름 중복 확인 버튼 클릭 이벤트
        nameCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = signup_username.getText().toString();

                // 이름이 입력되지 않았다면
                if (user_name.equals("")) {
                    Toast.makeText(getActivity(), "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 서버로부터의 응답 처리
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean n_success = jsonResponse.getBoolean("name_success");

                            if (n_success) {
                                Toast.makeText(getActivity(), "사용 가능한 이름입니다.", Toast.LENGTH_SHORT).show();
                                signup_username.setEnabled(false); // 이름 입력란을 비활성화 (이름 값 고정)
                                validate = true; // 이름이 중복되지 않았으므로 validate를 true로 설정
                            } else {
                                Toast.makeText(getActivity(), "이미 존재하는 이름입니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // ValidateRequest: 서버에 이름 중복 확인 요청 보내는 객체
                ValidateRequest validateNameRequest = new ValidateRequest("", user_name, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(validateNameRequest);
            }
        });

        // 아이디 중복 확인 버튼 클릭 이벤트
        idCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = signup_userID.getText().toString();
                if (validate) { // 검증이 완료된 경우 추가 검증 없이 함수 종료
                    return;
                }

                // 아이디가 입력되지 않았다면
                if (user_id.equals("")) {
                    Toast.makeText(getActivity(), "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 아이디 형식 체크
                if (!user_id.matches("[a-zA-Z0-9]{6,12}")) {
                    Toast.makeText(getActivity(), "아이디는 6~12자 이내의 영문/숫자 조합이어야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }


                // 서버로부터의 응답 처리
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean i_success = jsonResponse.getBoolean("id_success");

                            if (i_success) {
                                Toast.makeText(getActivity(), "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                                signup_userID.setEnabled(false);// 아이디 입력란을 비활성화 (아이디 값 고정)
                                validate = true; //검증 완료
                            } else {
                                Toast.makeText(getActivity(), "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // ValidateRequest: 서버에 아이디 중복 확인 요청 보내는 객체
                ValidateRequest validateIdRequest = new ValidateRequest(user_id, "", responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(validateIdRequest);
            }
        });

        // 회원가입 버튼 클릭 이벤트
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user_id = signup_userID.getText().toString();
                final String user_pw = signup_userPw.getText().toString();
                final String user_pwcheck = signup_userPwCheck.getText().toString();
                final String user_name = signup_username.getText().toString();

                // 아이디 중복체크 했는지 확인
                if (!validate) {
                    Toast.makeText(getActivity(), "아이디 중복 확인을 해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 한 칸이라도 입력 안했을 경우
                if (user_id.equals("") || user_pw.equals("") || user_name.equals("")) {
                    Toast.makeText(getActivity(), "모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 비밀번호 형식 체크
                if (!user_pw.matches("(?=.*[0-9])(?=.*[a-zA-Z]).{8,}")) {
                    Toast.makeText(getActivity(), "비밀번호는 8자 이상의 영문/숫자 조합이어야 합니다.", Toast.LENGTH_SHORT).show();
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
                            if (user_pw.equals(user_pwcheck)) {
                                if (success) {
                                    Toast.makeText(getActivity(), "회원 등록에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                                    NavHostFragment.findNavController(RegisterFragment.this)
                                            .navigate(R.id.action_registerFragment_to_loginFragment);
                                } else { // 회원가입 실패시
                                    Toast.makeText(getActivity(), "회원 등록에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "비밀번호가 동일하지 않습니다.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                // 서버로 Volley를 이용해서 요청
                SignupRequest registerRequest = new SignupRequest(user_id, user_pw, user_name, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(registerRequest);
            }
        });

        return view;
    }
}
