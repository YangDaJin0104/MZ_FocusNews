package com.example.mz_focusnews;

import android.content.Context;
import android.content.SharedPreferences;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.request.RegisterRequest;
import com.example.mz_focusnews.request.ValidateRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterFragment extends Fragment {

    private EditText register_username, register_userID, register_userPw, register_userPwCheck;
    private Button idCheckBtn, registerBtn, nameCheckBtn;
    private ImageButton backBtn;
    private ImageView iv_personal, iv_alarm;
    private TextView tv_personal, tv_alarm;
    private CheckBox full_permission, personal_permission, location_permission, alarm_permission; // 전체 동의, 개인 정보 동의, 위치 정보 동의, 속보 알림 동의
    private boolean validate = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        register_username = view.findViewById(R.id.et_name);
        register_userID = view.findViewById(R.id.et_id);
        register_userPw = view.findViewById(R.id.et_pw);
        register_userPwCheck = view.findViewById(R.id.et_pw_check);

        full_permission = view.findViewById(R.id.full_permission);
        personal_permission = view.findViewById(R.id.personal_permission);
        alarm_permission = view.findViewById(R.id.alarm_permission);

        iv_personal = view.findViewById(R.id.personal_info);
        iv_alarm = view.findViewById(R.id.alarm_info);
        tv_personal = view.findViewById(R.id.tv_personal);
        tv_alarm = view.findViewById(R.id.tv_alarm);

        nameCheckBtn = view.findViewById(R.id.btn_namecheck);
        idCheckBtn = view.findViewById(R.id.btn_idcheck);
        registerBtn = view.findViewById(R.id.btn_complete_register);

        backBtn = view.findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onBack(); // 이전 화면으로 돌아가기
            }
        });

        // Set up full_permission checkbox listener
        full_permission.setOnCheckedChangeListener((buttonView, isChecked) -> {
            personal_permission.setChecked(isChecked);
            alarm_permission.setChecked(isChecked);
        });

        iv_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = tv_personal.getVisibility();
                if (visibility == GONE) {
                    tv_personal.setVisibility(VISIBLE);
                } else {
                    tv_personal.setVisibility(GONE);
                }
            }
        });

        iv_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = tv_alarm.getVisibility();
                if (visibility == GONE) {
                    tv_alarm.setVisibility(VISIBLE);
                } else {
                    tv_alarm.setVisibility(GONE);
                }
            }
        });

        // 이름 중복 확인 버튼 클릭 이벤트
        nameCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = register_username.getText().toString();

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
                                register_username.setEnabled(false); // 이름 입력란을 비활성화 (이름 값 고정)
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
                String user_id = register_userID.getText().toString();

                // 아이디가 입력되지 않았다면
                if (user_id.equals("")) {
                    Toast.makeText(getActivity(), "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //                // 테스트 후 주석 제거
//                // 아이디 형식 체크ㅁ


                // 서버로부터의 응답 처리
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean i_success = jsonResponse.getBoolean("id_success");

                            if (i_success) {
                                Toast.makeText(getActivity(), "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                                register_userID.setEnabled(false);// 아이디 입력란을 비활성화 (아이디 값 고정)
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
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user_id = register_userID.getText().toString();
                final String user_pw = register_userPw.getText().toString();
                final String user_pwcheck = register_userPwCheck.getText().toString();
                final String user_name = register_username.getText().toString();
                final int alarm_perm = alarm_permission.isChecked() ? 1 : 0;


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


//                // 테스트 후 주석 제거
//                // 비밀번호 형식 체크
//                if (!user_pw.matches("(?=.*[0-9])(?=.*[a-zA-Z]).{8,}")) {
//                    Toast.makeText(getActivity(), "비밀번호는 8자 이상의 영문/숫자 조합이어야 합니다.", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                // 개인 정보 동의 체크 여부 확인
                if (!personal_permission.isChecked()) {
                    Toast.makeText(getActivity(), "개인 정보 동의는 필수입니다.", Toast.LENGTH_SHORT).show();
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

                                    SharedPreferences sp = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("user_id", user_id);
                                    editor.putString("user_name", user_name);
                                    editor.apply();

                                    NavHostFragment.findNavController(RegisterFragment.this)
                                            .navigate(R.id.action_registerFragment_to_keywordFragment);
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
                RegisterRequest registerRequest = new RegisterRequest(user_id, user_pw, user_name, alarm_perm, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(registerRequest);
            }
        });

        return view;
    }
}
