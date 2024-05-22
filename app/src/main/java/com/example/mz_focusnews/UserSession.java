package com.example.mz_focusnews;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자의 세션을 관리하는 클래스
 */
public class UserSession {
    private List<UserNewsInteraction> interactions;

    public UserSession() {
        interactions = new ArrayList<>();
    }

    public void logInteraction(int newsId, String category) {
        interactions.add(new UserNewsInteraction(newsId, category, System.currentTimeMillis()));
    }

}
