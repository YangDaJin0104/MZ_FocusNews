package com.example.mz_focusnews.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 서버로 아이디와 이름의 중복 확인 요청을 보냄
 */
public class ValidateRequest extends StringRequest {

    // 서버 URL 설정(php 파일 연동)
    final static private String URL = "http://43.201.173.245/validate.php"; // 임시
    private Map<String, String> map;

    public ValidateRequest(String user_id, String user_name, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("user_name", user_name);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
