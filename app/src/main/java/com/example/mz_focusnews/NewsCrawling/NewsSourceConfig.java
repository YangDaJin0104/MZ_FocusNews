package com.example.mz_focusnews.NewsCrawling;
public class NewsSourceConfig {
    private String location;

    public void setLocation(String location) {
        this.location = location;
    }

    // 카테고리 별 URL
    public String getNewsSourceUrl() {

        // 정치
        String politics = "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNRFZ4ZERBU0FtdHZLQUFQAQ?hl=ko&gl=KR&ceid=KR%3Ako";
        // 경제
        String economy = "https://news.google.com/topics/CAAqIggKIhxDQkFTRHdvSkwyMHZNR2RtY0hNekVnSnJieWdBUAE?hl=ko&gl=KR&ceid=KR:ko";
        // 사회
        String society = "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNRFp4WkRNU0FtdHZLQUFQAQ?hl=ko&gl=KR&ceid=KR%3Ako";
        // 지역
        String area = "https://news.google.com/rss/search?q=(용인)&hl=ko&gl=KR&ceid=KR%3Ako";
        // 채용정보
        String Recruitment_information = "https://news.google.com/rss/topics/CAAqJAgKIh5DQkFTRUFvS0wyMHZNRFF4TVRWME1oSUNhMjhvQUFQAQ?hl=ko&gl=KR&ceid=KR%3Ako";
        // 과학기술
        String science_technology = "https://news.google.com/topics/CAAqKAgKIiJDQkFTRXdvSkwyMHZNR1ptZHpWbUVnSnJieG9DUzFJb0FBUAE?hl=ko&gl=KR&ceid=KR:ko";
        // 연예
        String entertainment = "https://news.google.com/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNREZ5Wm5vU0FtdHZLQUFQAQ?hl=ko&gl=KR&ceid=KR:ko";
        // 스포츠
        String sports = "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRFp1ZEdvU0FtdHZHZ0pMVWlnQVAB?hl=ko&gl=KR&ceid=KR%3Ako";

        return politics;
    }
}
