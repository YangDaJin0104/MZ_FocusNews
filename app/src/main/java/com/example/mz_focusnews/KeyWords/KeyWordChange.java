package com.example.mz_focusnews.KeyWords;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.R;

import org.json.JSONException;
import org.json.JSONObject;

public class KeyWordChange extends Fragment {

    private EditText keyword1EditText;
    private EditText keyword2EditText;
    private EditText keyword3EditText;
    private TextView keyword1TextView;
    private TextView keyword2TextView;
    private TextView keyword3TextView;
    private String userId;
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_key_word_change, container, false);

        keyword1EditText = view.findViewById(R.id.change_keyword1);
        keyword2EditText = view.findViewById(R.id.change_keyword2);
        keyword3EditText = view.findViewById(R.id.change_keyword3);
        keyword1TextView = view.findViewById(R.id.user_keyword1);
        keyword2TextView = view.findViewById(R.id.user_keyword2);
        keyword3TextView = view.findViewById(R.id.user_keyword3);

        SharedPreferences sp = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        userId = sp.getString("user_id", null);

        requestQueue = Volley.newRequestQueue(getContext());

        loadUserKeywords();

        Button changeKeyword1Button = view.findViewById(R.id.btn_change_keyword1);
        changeKeyword1Button.setOnClickListener(v -> updateKeyword(keyword1EditText.getText().toString(), 1));

        Button changeKeyword2Button = view.findViewById(R.id.btn_change_keyword2);
        changeKeyword2Button.setOnClickListener(v -> updateKeyword(keyword2EditText.getText().toString(), 2));

        Button changeKeyword3Button = view.findViewById(R.id.btn_change_keyword3);
        changeKeyword3Button.setOnClickListener(v -> updateKeyword(keyword3EditText.getText().toString(), 3));

        ImageView backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> getActivity().onBackPressed());

        return view;
    }

    private void loadUserKeywords() {
        String url = "http://10.0.2.2:8081/api/users/" + userId + "/keywords";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String keyword1 = response.optString("keyword1", "Add Keyword");
                    String keyword2 = response.optString("keyword2", "Add Keyword");
                    String keyword3 = response.optString("keyword3", "Add Keyword");

                    keyword1TextView.setText(keyword1.isEmpty() ? "Add Keyword" : keyword1);
                    keyword2TextView.setText(keyword2.isEmpty() ? "Add Keyword" : keyword2);
                    keyword3TextView.setText(keyword3.isEmpty() ? "Add Keyword" : keyword3);
                },
                error -> Toast.makeText(getContext(), "Failed to load keywords", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void updateKeyword(String keyword, int keywordPosition) {
        if (keyword.length() > 10) {
            Toast.makeText(getContext(), "Keyword must be less than 10 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8081/api/users/saveKeyword";
        JSONObject postData = new JSONObject();
        try {
            postData.put("userId", userId);
            postData.put("keyword", keyword);
            postData.put("keywordPosition", keywordPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    Toast.makeText(getContext(), "Keyword updated successfully", Toast.LENGTH_SHORT).show();
                    loadUserKeywords();
                },
                error -> Toast.makeText(getContext(), "Failed to update keyword", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(jsonObjectRequest);
    }
}
