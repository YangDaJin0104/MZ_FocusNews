package com.example.mz_focusnews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.request.FindPasswordRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class FindPasswordFragment extends Fragment {

    private EditText et_id, et_name;
    private Button btn_find_pw;
    private TextView tv1, tv_pw, tv_goToLogin;
    private LinearLayout linear_find;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_password, container, false);


        et_id = view.findViewById(R.id.et_id);
        et_name = view.findViewById(R.id.et_name);

        btn_find_pw = view.findViewById(R.id.btn_find_pw);

        tv1 = view.findViewById(R.id.tv1);
        tv_pw = view.findViewById(R.id.tv_pw);
        tv_goToLogin = view.findViewById(R.id.tv_goToLogin);

        linear_find = view.findViewById(R.id.linear_find);

        tv_goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FindPasswordFragment.this)
                        .navigate(R.id.action_findPasswordFragment_to_loginFragment);
            }
        });

        btn_find_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = et_id.getText().toString();
                String userName = et_name.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                String userPw = jsonResponse.getString("user_pw");

                                linear_find.setVisibility(View.VISIBLE);
                                tv1.setVisibility(View.VISIBLE);
                                tv_pw.setVisibility(View.VISIBLE);
                                tv_goToLogin.setVisibility(View.VISIBLE);

                                tv1.setText(userName + "님의 비밀번호는");
                                tv_pw.setText(userPw + "입니다");
                            } else {
                                tv1.setText("Error");
                                tv_pw.setText(jsonResponse.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                FindPasswordRequest findPasswordRequest = new FindPasswordRequest(userId, userName, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(findPasswordRequest);

            }
        });

        return view;

    }
}