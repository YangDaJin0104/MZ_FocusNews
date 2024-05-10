package com.example.mz_focusnews.Quiz;

import android.content.Context;
import android.util.Log;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVFileReader {

    private static final String TAG = "CSVFileReader";

    public List<String[]> readCSVFile(Context context, String fileName) {
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
}
