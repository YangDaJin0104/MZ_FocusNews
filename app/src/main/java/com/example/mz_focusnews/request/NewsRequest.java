package com.example.mz_focusnews.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewsRequest extends JsonObjectRequest {

    private static final String URL = "http://43.201.173.245/getNewsView.php";

    public NewsRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.GET, URL, null, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        return headers;
    }
}