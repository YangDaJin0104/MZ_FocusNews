package com.example.mz_focusnews;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


/**
 * 회원가입 요청 처리
 */
public class SignupRequest extends StringRequest {

    //서버 URL 설정(php 파일 연동)
    final static private String URL = "http://tofha1108.ivyro.net/signup.php"; // 임시
    private Map<String, String> map;

    public SignupRequest(String UserID, String UserPw, String UserName, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID", UserID);
        map.put("userPw", UserPw);
        map.put("userName", UserName);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }
}
