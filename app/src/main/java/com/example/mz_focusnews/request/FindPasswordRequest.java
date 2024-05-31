package com.example.mz_focusnews.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class FindPasswordRequest extends StringRequest {

    // Server URL setup (php file connection)
    final static private String URL = "http://43.201.173.245/find_password.php";
    private Map<String, String> map;

    public FindPasswordRequest(String UserID, String UserName, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("user_id", UserID);
        map.put("user_name", UserName);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
