package com.example.mz_focusnews.RelatedNews;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.mz_focusnews.ApiKeyManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class NewsDataStore {
    private static final String ENGINE = "text-embedding-3-small";
    private Map<Integer, NewsData> newsMap = new HashMap<>();

    public void addNewsItem(int id, String title, String summary, int related1, int related2) {
        newsMap.put(id, new NewsData(id, title, summary, related1, related2));
    }

    public Collection<NewsData> getAllNewsItems() {
        return newsMap.values();
    }

    public NewsData getNewsItem(int id) {
        return newsMap.get(id);
    }

    public void calculateAndSetRelatedArticles(Context context) throws IOException {
        List<String> summaries = new ArrayList<>();
        List<Integer> articleIds = new ArrayList<>(newsMap.keySet());

        for (NewsData item : newsMap.values()) {
            summaries.add(item.getSummary());
        }

        RealMatrix embeddings = getEmbeddings(context, summaries);

        for (int i = 0; i < summaries.size(); i++) {
            int selectedArticleId = articleIds.get(i);
            RealMatrix cosineSimilarities = embeddings.getRowMatrix(i).multiply(embeddings.transpose());
            double[] scores = cosineSimilarities.getRow(0);
            List<Integer> topIndices = findTopIndices(scores, i, 2);

            NewsData currentItem = newsMap.get(selectedArticleId);
            currentItem.setRelatedArticles(articleIds.get(topIndices.get(0)), articleIds.get(topIndices.get(1)));
        }
    }



    private List<Integer> findTopIndices(double[] scores, int excludeIndex, int topN) {
        List<Integer> indices = new ArrayList<>();
        double firstMax = -1, secondMax = -1;
        int firstIndex = -1, secondIndex = -1;

        for (int i = 0; i < scores.length; i++) {
            if (i == excludeIndex) continue;
            if (scores[i] > firstMax) {
                secondMax = firstMax;
                secondIndex = firstIndex;
                firstMax = scores[i];
                firstIndex = i;
            } else if (scores[i] > secondMax) {
                secondMax = scores[i];
                secondIndex = i;
            }
        }

        if (firstIndex != -1) indices.add(firstIndex);
        if (secondIndex != -1) indices.add(secondIndex);
        return indices;
    }

    private RealMatrix getEmbeddings(Context context, List<String> texts) throws IOException {
        // ApiKeyManager로 ChatGPT API Key 받아오기 (해당 클래스에서 key를 이 함수에만 쓰기 때문에 지역 변수로 수정)
        ApiKeyManager apiKeyManager = ApiKeyManager.getInstance(context);
        String apiKey = apiKeyManager.getApiKey();

        URL url = new URL("https://api.openai.com/v1/embeddings");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        Map<String, Object> map_data = new HashMap<>();
        map_data.put("input", texts);
        map_data.put("model", ENGINE);

        Gson gson = new Gson();
        String jsonInputString = gson.toJson(map_data);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {

            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        } catch (IOException e) {
            Log.e("HTTP Error", "Error reading from the HTTP connection", e);
            return null;
        }


        try {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> responseMap = gson.fromJson(response.toString(), type);
            List<Map<String, Object>> data = (List<Map<String, Object>>) responseMap.get("data");
            if (data == null) {
                Log.e("JSON Parsing Error", "Data key is missing or null in the response");
                return null; // data 키가 없거나 null 일 때의 처리
            }
            double[][] matrixData = data.stream()
                    .filter(e -> e != null && e.containsKey("embedding"))
                    .map(e -> ((List<Double>) e.get("embedding")).stream().mapToDouble(Double::doubleValue).toArray())
                    .toArray(double[][]::new);
            return new Array2DRowRealMatrix(matrixData, false);
        } catch (Exception e) {  // 모든 예외를 처리
            Log.e("JSON Parsing Error", "Error parsing JSON response: " + response.toString(), e);
            return null;  // JSON 파싱 실패 처리
        }
    }
}
