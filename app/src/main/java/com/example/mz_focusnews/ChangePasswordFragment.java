package com.example.mz_focusnews;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.request.ChangePasswordRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordFragment extends Fragment {

    private EditText et_now_pw, et_new_pw, et_pw_check;

    private Button btn_change_pw;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        et_now_pw = view.findViewById(R.id.et_pw1);
        et_new_pw = view.findViewById(R.id.et_pw2);
        et_pw_check = view.findViewById(R.id.et_pw3);
        btn_change_pw = view.findViewById(R.id.change_password);

        SharedPreferences sp = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        final String userId = sp.getString("user_id", "");

        btn_change_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPw = et_now_pw.getText().toString();
                String newPw = et_new_pw.getText().toString();
                String confirmPw = et_pw_check.getText().toString();

                // 비밀번호 형식 체크
                if (!newPw.matches("(?=.*[0-9])(?=.*[a-zA-Z]).{8,}")) {
                    Toast.makeText(getActivity(), "비밀번호는 8자 이상의 영문/숫자 조합이어야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!confirmPw.matches("(?=.*[0-9])(?=.*[a-zA-Z]).{8,}")) {
                    Toast.makeText(getActivity(), "비밀번호는 8자 이상의 영문/숫자 조합이어야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPw.equals(confirmPw)) {
                    Toast.makeText(getActivity(), "새 비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                Toast.makeText(getActivity(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getActivity(), jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(userId, currentPw, newPw, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(changePasswordRequest);
            }
        });

        return view;
    }
}
