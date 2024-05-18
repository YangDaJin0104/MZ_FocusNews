package com.example.mz_focusnews.NewsSummary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PythonCaller {
        public static void main(String[] args) {
                try {
                        // 파이썬 스크립트 경로 설정
                        String pythonScriptPath = "../../../../../python/summary.py";

                        // 파이썬 스크립트 실행 명령 설정
                        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
                        processBuilder.redirectErrorStream(true);

                        // 파이썬 프로세스 시작
                        Process process = processBuilder.start();

                        // 파이썬 프로세스 출력 읽기
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                                System.out.println(line);
                        }

                        // 프로세스 완료 대기
                        int exitCode = process.waitFor();
                        System.out.println("Exited with code : " + exitCode);

                } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                }
        }
}
