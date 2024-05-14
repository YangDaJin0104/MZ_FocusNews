package com.example.mz_focusnews.DB;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mz_focusnews.R;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;


/*
        < AWS DB 서버 사용 방법 >

        ★ AWS EC2 "DB" 인스턴스 Elastic IP 주소: 43.201.173.245

        ※ 서버가 중지된 상태 or 서버 CPU 사용률이 100%가 되어 죽은 경우는 접속이 안될 수 있음
          > AWS 계정 공유 혹은 사용자 추가?해서 인스턴스 종료 후 재시작하면 정상 작동


        0. Elastic IP 주소로의 웹 연결 확인
        > 43.201.173.245/
        > 위 주소로 입력해서 들어갔을 때 "Ubuntu Apache2 Default Page + It works!" 가 안 뜨면 연결 실패



        1. 웹 서버 연결 확인 (=웹 서버와 DB 연결이 성공적으로 이루어짐)
            > 43.201.173.245/DBConnection.php
            > 위 주소 입력해서 들어갔을 때 아무것도 안 뜨면 연결 성공

        2. DB 테이블의 전체 데이터 확인 (SELECT)
            > 43.201.173.245/getJson.php
            > 위 주소 입력해서 들어가면 아래와 같은 결과가 출력됨 (무한로딩, 혹은 404 버그 뜨면 연결 실패)
        {
            "coddl": [
                {
                    "id": 1,
                    "name": "test",
                    "country": "test"
                },
                {
                    "id": 2,
                    "name": "123",
                    "country": "dgasdf"
                },

        3. DB 테이블에 데이터 삽입 (INSERT) - 연결 테스트용
            > 43.201.173.245/DBInsert.php
            > 위 주소 입력해서 들어가면 데이터 삽입 가능(임시 UI 제작해둠) (무한로딩, 혹은 404 버그 뜨면 연결 실패)


        2, 3번 테이블명 및 컬럼 수정 방법
            1. 가상 서버 접속
            2. $ cd /var/www/html		(VS Code로 접속 시 이미 파일이 들어가 있으니 테이블명만 수정하면 됨)
            3. $ sudo vim getJson.php(혹은 DBInsert.php)
            4. vim 안에서 내용 수정
            5. 웹 새로고침하면 변경 사항이 적용될 것임~

        MZ_FocusNews\app\src\main\java\com\example\mz_focusnews\DB\에 참고용으로 .php 파일 올려뒀으니 거기서 수정하고 복붙해도 됩니당~
*/


public class DBInsertActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "43.201.173.245";
    private static String TAG = "phptest";

    private EditText mEditTextName;
    private EditText mEditTextCountry;
    private TextView mTextViewResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_insert_test);

        mEditTextName = (EditText)findViewById(R.id.editText_main_name);
        mEditTextCountry = (EditText)findViewById(R.id.editText_main_country);
        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());


        Button buttonInsert = (Button)findViewById(R.id.button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mEditTextName.getText().toString();
                String country = mEditTextCountry.getText().toString();

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/DBInsert.php", name,country);


                mEditTextName.setText("");
                mEditTextCountry.setText("");

            }
        });

    }



    class InsertData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(DBInsertActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String name = (String)params[1];
            String country = (String)params[2];

            String serverURL = (String)params[0];
            String postParameters = "name=" + name + "&country=" + country;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}
