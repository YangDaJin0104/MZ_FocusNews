package com.example.mz_focusnews.Quiz;

import android.util.Log;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CSVFileWriter {
    private static final String TAG = "CSVFileWriter";
    private static final String FILENAME = "quiz_solved.csv";

    public void writeCSVFile(int userID, int questionID) {
        String strUserID = String.valueOf(userID);
        String strQuestionID = String.valueOf(questionID);

        try (CSVWriter writer = new CSVWriter(new FileWriter(FILENAME, true))) {
            writer.writeNext(new String[]{strUserID, strQuestionID});
            writer.flush();
            Log.d(TAG, "userID: " + strUserID + ", " + "questionID: " + strQuestionID);
        } catch (IOException e) {
            Log.e(TAG, "Error parsing CSV file: " + e.getMessage());
        }
    }
}