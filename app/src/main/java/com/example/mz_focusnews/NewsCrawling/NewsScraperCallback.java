package com.example.mz_focusnews.NewsCrawling;

import java.util.ArrayList;

public interface NewsScraperCallback {
    void onSuccess(ArrayList<NewsArticle> articles);
    void onFailure(Exception e);
}