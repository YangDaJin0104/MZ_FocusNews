package com.example.mz_focusnews.NewsRecommend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PythonScriptRunner {

    public static void main(String[] args) {
        String pythonScriptPath = "../../../../../python/recommend.py";  // 파이썬 스크립트 파일 경로
        List<String> results = runPythonScript(pythonScriptPath);
        results.forEach(System.out::println);
    }

    public static List<String> runPythonScript(String scriptPath) {
        List<String> results = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    results.add(line);
                }
                process.waitFor();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return results;
    }
}
