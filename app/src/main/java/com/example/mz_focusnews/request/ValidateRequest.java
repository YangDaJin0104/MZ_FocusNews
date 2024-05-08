package com.example.mz_focusnews.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 서버로 아이디 중복 확인 요청을 보냄
 */
public class ValidateRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static private String URL = "http://tofha1108.ivyro.net/validate.php"; // 임시
    private Map<String, String> map;

    public ValidateRequest(String UserID, Response.Listener<String> listener){
        super(Method.POST, URL, listener,null);

        map = new HashMap<>();
        map.put("userID", UserID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}