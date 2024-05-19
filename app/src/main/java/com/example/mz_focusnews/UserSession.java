package com.example.mz_focusnews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 세션을 관리하는 클래스
 */
public class UserSession {
    private List<UserNewsInteraction> interactions;

    public UserSession() {
        interactions = new ArrayList<>();
    }

    // 사용자가 특정 뉴스에 상호작용할 때 호출되어 해당 뉴스를 사용자 세션에 추가
    public void logInteraction(int newsId, String category) {
        interactions.add(new UserNewsInteraction(newsId, category, System.currentTimeMillis()));
    }

    // 사용자가 최근 30일간 상호작용한 뉴스를 기반으로 선호 카테고리를 결정
    public String getPreferredCategory() {
        Map<String, Integer> categoryCount = new HashMap<>();
        long now = System.currentTimeMillis();
        long interval = 30L * 24 * 60 * 60 * 1000; // 30일

        for (UserNewsInteraction interaction : interactions) {
            if (now - interaction.getTimestamp() <= interval) {
                categoryCount.put(interaction.getCategory(),
                        categoryCount.getOrDefault(interaction.getCategory(), 0) + 1);
            }
        }

        return categoryCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("default_category");
    }
}
