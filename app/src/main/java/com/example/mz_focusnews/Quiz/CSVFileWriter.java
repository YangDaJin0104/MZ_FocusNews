package com.example.mz_focusnews.Quiz;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class CSVFileWriter {
    private static final String TAG = "CSVFileWriter";
    private static final String FILENAME = "quiz_solved.csv";

    public void writeCSVFile(Context context, int userId, int questionID) {
        String struserId = String.valueOf(userId);
        String strQuestionID = String.valueOf(questionID);

        try {
            // 파일 출력 스트림 열기 (내부 저장소)
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter writer = new BufferedWriter(osw);

            // CSV 형식으로 데이터 작성
            String csvLine = struserId + "," + strQuestionID + "\n";

            // 파일에 데이터 쓰기
            writer.write(csvLine);

            // 스트림 닫기
            writer.close();
            osw.close();
            fos.close();

            Log.d(TAG, "CSV file write successful");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error writing to CSV file: " + e.getMessage());
        }
    }

    // quiz_solved.csv 파일 초기화 (테스트용)
    public void clearQuizSolvedCSVFile(Context context, String fileName) {
        try {
            // 파일 출력 스트림 열기 (내부 저장소)
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.close();
            Log.d(TAG, "CSV file cleared successfully");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error clearing CSV file: " + e.getMessage());
        }
    }
}
