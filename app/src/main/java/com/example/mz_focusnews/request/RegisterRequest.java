package com.example.mz_focusnews.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


/**
 * 회원가입 요청 처리
 */
public class RegisterRequest extends StringRequest {

    //서버 URL 설정(php 파일 연동)
    final static private String URL = "http://43.201.173.245/register.php";
    private Map<String, String> map;


    public RegisterRequest(String UserID, String UserPw, String UserName, int LocationPermission, int AlarmPermission, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("user_id", UserID);
        map.put("user_pw", UserPw);
        map.put("user_name", UserName);
        map.put("location_permission", String.valueOf(LocationPermission));
        map.put("alarm_permission", String.valueOf(AlarmPermission));
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}