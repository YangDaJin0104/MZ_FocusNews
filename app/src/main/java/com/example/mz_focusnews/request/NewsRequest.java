package com.example.mz_focusnews.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class NewsRequest extends StringRequest {

    private static final String URL = "http://43.201.173.245/getNewsView.php";
    private final Map<String, String> params;

    public NewsRequest(String date, String type, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        params = new HashMap<>();
        params.put("date", date);
        params.put("type", type);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}