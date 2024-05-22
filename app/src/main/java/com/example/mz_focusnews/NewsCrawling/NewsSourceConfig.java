package com.example.mz_focusnews.NewsCrawling;
public class NewsSourceConfig {
    private String location;
    private String category;

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNewsSourceUrl() {
        switch (category) {
            case "politics":
                return "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNRFZ4ZERBU0FtdHZLQUFQAQ?hl=ko&gl=KR&ceid=KR%3Ako";
            case "economy":
                return "https://news.google.com/topics/CAAqIggKIhxDQkFTRHdvSkwyMHZNR2RtY0hNekVnSnJieWdBUAE?hl=ko&gl=KR&ceid=KR:ko";
            case "society":
                return "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNRFp4WkRNU0FtdHZLQUFQAQ?hl=ko&gl=KR&ceid=KR%3Ako";
            case "area":
                return "https://news.google.com/rss/search?q=(용인)&hl=ko&gl=KR&ceid=KR%3Ako";
            case "recruitment":
                return "https://news.google.com/rss/topics/CAAqJAgKIh5DQkFTRUFvS0wyMHZNRFF4TVRWME1oSUNhMjhvQUFQAQ?hl=ko&gl=KR&ceid=KR%3Ako";
            case "science_technology":
                return "https://news.google.com/topics/CAAqKAgKIiJDQkFTRXdvSkwyMHZNR1ptZHpWbUVnSnJieG9DUzFJb0FBUAE?hl=ko&gl=KR&ceid=KR:ko";
            case "entertainment":
                return "https://news.google.com/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNREZ5Wm5vU0FtdHZLQUFQAQ?hl=ko&gl=KR&ceid=KR:ko";
            case "sports":
                return "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRFp1ZEdvU0FtdHZHZ0pMVWlnQVAB?hl=ko&gl=KR&ceid=KR%3Ako";
            default:
                return "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNRFZ4ZERBU0FtdHZLQUFQAQ?hl=ko&gl=KR&ceid=KR%3Ako";
            // 기본값 혹은 유효하지 않은 카테고리 선택 시
        }
    }
}
