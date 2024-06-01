package com.example.mz_focusnews.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateAlarmRequest extends StringRequest {
    private static final String URL = "http://10.0.2.2:8081/update-alarm-permission";
    private final Map<String, String> params;

    public UpdateAlarmRequest(String userId, boolean alarmPermission, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        params = new HashMap<>();
        params.put("userId", userId);
        params.put("alarmPermission", String.valueOf(alarmPermission));
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
