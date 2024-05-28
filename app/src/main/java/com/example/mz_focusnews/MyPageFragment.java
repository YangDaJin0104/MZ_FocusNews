package com.example.mz_focusnews;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.request.UserInfoRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MyPageFragment extends Fragment {

    private TextView tv_name, tv_keyword1, tv_keyword2, tv_keyword3;
    private ImageView iv_edit;
    private Button btn_change;
    private Switch switch_alarm;

    private String user_id;
    private String user_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        tv_name = view.findViewById(R.id.my_name);

        tv_keyword1 = view.findViewById(R.id.tv_keyword1);
        tv_keyword2 = view.findViewById(R.id.tv_keyword2);
        tv_keyword3 = view.findViewById(R.id.tv_keyword3);

        iv_edit = view.findViewById(R.id.edit_keyword);

        btn_change = view.findViewById(R.id.btn_change_pw);

        switch_alarm = view.findViewById(R.id.switch_alarm);

        SharedPreferences sp = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        user_id = sp.getString("user_id", null);
        user_name = sp.getString("user_name", null);

        tv_name.setText(user_name);

        fetchUserData(user_id);


        return view;

    }

    private void fetchUserData(String userId) {
        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getBoolean("success")) {
                    JSONObject userInfo = jsonResponse.getJSONObject("user");
                    getActivity().runOnUiThread(() -> {
                        tv_keyword1.setText(userInfo.optString("keyword1", "Not set"));
                        tv_keyword2.setText(userInfo.optString("keyword2", "Not set"));
                        tv_keyword3.setText(userInfo.optString("keyword3", "Not set"));
                        int alarmPermission = userInfo.optInt("alarm_permission", 0); // 기본값으로 0 사용
                        switch_alarm.setChecked(alarmPermission == 1);
                    });
                } else {
                    Toast.makeText(getContext(), jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getContext(), "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
            }
        };

        Response.ErrorListener errorListener = error -> Toast.makeText(getContext(), "Failed to connect to server.", Toast.LENGTH_SHORT).show();

        UserInfoRequest userInfoRequest = new UserInfoRequest(userId, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(userInfoRequest);
    }


}