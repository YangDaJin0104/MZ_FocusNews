package com.example.mz_focusnews.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateCategoryRequest extends StringRequest {

    private static final String URL = "http://43.201.173.245/update_interest_category.php";
    private Map<String, String> params;

    public UpdateCategoryRequest(String user_id, String interest_category, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("interest_category", interest_category);
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }
}