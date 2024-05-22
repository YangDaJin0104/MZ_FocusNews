package com.example.mz_focusnews.NewsCrawling;

import java.util.ArrayList;
import java.util.List;

public interface NewsScraperCallback {
    void onSuccess(List<NewsArticle> articles);
    void onFailure(Exception exception);
}