package com.example.mz_focusnews.Quiz;

import android.content.Context;
import android.util.Log;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class CSVFileReader {

    private static final String TAG = "CSVFileReader";

    public List<String[]> readQuizCSVFile(Context context, String fileName) {
        List<String[]> lines = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            CSVReader csvReader = new CSVReader(bufferedReader);
            lines = csvReader.readAll();
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading CSV file: " + e.getMessage());
        } catch (CsvException e) {
            Log.e(TAG, "Error parsing CSV file: " + e.getMessage());
        }
        return lines;
    }

    // 파일에 저장된 내용 확인용 함수
    public List<String[]> readQuizSolvedCSVFile(Context context, String fileName) {
        List<String[]> lines = new ArrayList<>();

        try {
            // 파일에 저장된 데이터를 읽어오기 위해 FileInputStream 열기
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);

            String line;
            // 파일의 모든 내용을 읽어서 리스트에 추가
            while ((line = reader.readLine()) != null) {
                // 쉼표를 기준으로 문자열을 분할하여 배열로 변환
                String[] parts = line.split(",");
                lines.add(parts);

                // 각 줄의 데이터를 출력
                StringBuilder lineData = new StringBuilder();
                for (String part : parts) {
                    lineData.append(part).append(", ");
                }
                Log.d(TAG, "Data in " + fileName + " file: " + lineData.toString());
            }

            // 스트림 닫기
            reader.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error reading CSV file: " + e.getMessage());
        }

        return lines;
    }
}
