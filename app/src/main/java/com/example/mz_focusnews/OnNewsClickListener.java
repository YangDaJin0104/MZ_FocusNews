package com.example.mz_focusnews;


import com.example.mz_focusnews.NewsDB.News;

/**
 * NewsAdapter에서 이벤트 발생했을 때 실횡될 콜백 함수 (CategoryFragment에서 사용)
 */
public interface OnNewsClickListener {
    void onNewsClick(News news);
}
