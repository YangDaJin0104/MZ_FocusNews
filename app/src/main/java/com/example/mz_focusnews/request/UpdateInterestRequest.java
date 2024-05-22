package com.example.mz_focusnews.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateInterestRequest extends StringRequest {
    private static final String URL = "http://43.201.173.245/update_interest_category.php";
    private Map<String, String> params;

    public UpdateInterestRequest(String userId, String category, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        params = new HashMap<>();
        params.put("user_id", userId);
        params.put("category", category);
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }
}
