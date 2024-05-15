package com.example.mz_focusnews;

/**
 * NewsAdapter에서 이벤트 발생했을 때 실횡될 콜백 함수 (CategoryFragment에서 사용)
 */
public interface OnNewsItemClickListener {
    void onNewsItemClick(NewsItem newsItem);
}
