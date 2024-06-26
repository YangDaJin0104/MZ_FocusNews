package com.example.mz_focusnews;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.request.UpdateAlarmRequest;
import com.example.mz_focusnews.request.UserInfoRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class MyPageFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final int REQUEST_CODE_PICK_IMAGE = 2;

    private TextView tv_name, tv_keyword1, tv_keyword2, tv_keyword3;
    private ImageView iv_profile, iv_edit, iv_keyword;
    private Button btn_change;
    private Switch switch_alarm;

    private String user_id;
    private String user_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        tv_name = view.findViewById(R.id.my_name);
        tv_keyword1 = view.findViewById(R.id.tv_keyword1);
        tv_keyword2 = view.findViewById(R.id.tv_keyword2);
        tv_keyword3 = view.findViewById(R.id.tv_keyword3);
        iv_keyword = view.findViewById(R.id.iv_keyword); // 키워드 변경
        iv_profile = view.findViewById(R.id.iv_profile);
        iv_edit = view.findViewById(R.id.iv_edit);
        btn_change = view.findViewById(R.id.btn_change_pw);
        switch_alarm = view.findViewById(R.id.switch_alarm);

        // Load user data from SharedPreferences
        SharedPreferences sp = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        user_id = sp.getString("user_id", null);
        user_name = sp.getString("user_name", null);
        tv_name.setText(user_name);

        fetchUserData(user_id);

        // Set up listeners
        btn_change.setOnClickListener(v -> NavHostFragment.findNavController(MyPageFragment.this)
                .navigate(R.id.action_myPageFragment_to_changePasswordFragment));

        // 이미지 접근 권한 설정 (프로필 이미지 변경)
        iv_edit.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_PERMISSION);
            } else {
                openGallery();
            }
        });

        // 키워드 변경 버튼 리스너
        iv_keyword.setOnClickListener(v -> {
            NavHostFragment.findNavController(MyPageFragment.this)
                    .navigate(R.id.action_myPageFragment_to_keywordChangeFragment);
        });

        // Switch 상태 변경 리스너 설정
        switch_alarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateAlarmSetting(isChecked);
        });

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try (InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImageUri)) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                iv_profile.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Permission needed")
                            .setMessage("This permission is needed to pick images from your gallery")
                            .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_PERMISSION))
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .create().show();
                } else {
                    Toast.makeText(getContext(), "Permission denied. You can enable it in app settings.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void fetchUserData(String userId) {
        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getBoolean("success")) {
                    JSONObject userInfo = jsonResponse.getJSONObject("user");
                    getActivity().runOnUiThread(() -> {
                        tv_keyword1.setText(userInfo.optString("keyword1", "Not set"));
                        tv_keyword2.setText(userInfo.optString("keyword2", "Not set"));
                        tv_keyword3.setText(userInfo.optString("keyword3", "Not set"));
                        switch_alarm.setChecked(userInfo.optInt("alarm_permission", 0) == 1);
                    });
                } else {
                    Toast.makeText(getContext(), jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getContext(), "Error parsing JSON data", Toast.LENGTH_SHORT).show();
            }
        };
        Response.ErrorListener errorListener = error -> Toast.makeText(getContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();

        UserInfoRequest userInfoRequest = new UserInfoRequest(userId, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(userInfoRequest);
    }

    private void updateAlarmSetting(boolean isEnabled) {
        Response.Listener<String> responseListener = response -> Toast.makeText(getContext(), "알림 설정이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
        Response.ErrorListener errorListener = error -> {
            String errorMessage = "Failed to update alarm setting";
            if (error.networkResponse != null) {
                errorMessage += " - Status Code: " + error.networkResponse.statusCode;
            }
            if (error.getCause() != null) {
                errorMessage += " - Cause: " + error.getCause().toString();
            }
            Log.e("UpdateAlarmRequest", errorMessage, error);
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        };

        UpdateAlarmRequest request = new UpdateAlarmRequest(user_id, isEnabled, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}
