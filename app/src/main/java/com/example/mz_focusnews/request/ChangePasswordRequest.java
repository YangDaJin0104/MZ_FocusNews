package com.example.mz_focusnews.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordRequest extends StringRequest {

    // Server URL setup (php file connection)
    final static private String URL = "http://43.201.173.245/change_password.php";
    private Map<String, String> map;

    public ChangePasswordRequest(String UserID, String CurrentPw, String NewPw, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("user_id", UserID);
        map.put("current_pw", CurrentPw);
        map.put("new_pw", NewPw);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
