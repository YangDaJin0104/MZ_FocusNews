package com.example.mz_focusnews.KeyWords;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.mz_focusnews.UsersDB.UserApi;
import com.example.mz_focusnews.NewsDB.RetrofitClient;

public class UserKeyWord {

    private static final String TAG = "UserKeyWord";

    public static void saveKeywordToServer(Context context, String userId, String keyword, int keywordIndex) {
        // Retrofit 인스턴스 가져오기
        UserApi userApi = RetrofitClient.getInstance().getUserApi();

        // 서버에 키워드를 저장하는 API 호출
        Call<Void> call = userApi.saveKeyword(userId, keyword, keywordIndex);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 성공적으로 저장된 경우
                    Toast.makeText(context, "키워드가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 저장 실패한 경우
                    Log.e(TAG, "키워드 저장 실패: " + response.code());
                    Toast.makeText(context, "키워드 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 네트워크 오류 등으로 저장 실패한 경우
                Log.e(TAG, "키워드 저장 오류: " + t.getMessage());
                Toast.makeText(context, "키워드 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

