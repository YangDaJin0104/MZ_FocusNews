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
            case "politics": // 정치
                return "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNRFZ4ZERBU0FtdHZLQUFQAQ?hl=ko&gl=KR&ceid=KR%3Ako";
            case "economy": // 경제
                return "https://news.google.com/rss/topics/CAAqIggKIhxDQkFTRHdvSkwyMHZNR2RtY0hNekVnSnJieWdBUAE?hl=ko&gl=KR&ceid=KR:ko";
            case "society": // 사회
                return "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNRFp4WkRNU0FtdHZLQUFQAQ?hl=ko&gl=KR&ceid=KR%3Ako";
            case "area": // 지역
                return "https://news.google.com/rss/search?q=(용인)&hl=ko&gl=KR&ceid=KR%3Ako";
            case "recruitment": // 채용정보
                return "https://news.google.com/rss/topics/CAAqJAgKIh5DQkFTRUFvS0wyMHZNRFF4TVRWME1oSUNhMjhvQUFQAQ?hl=ko&gl=KR&ceid=KR%3Ako";
            case "science": // 과학기술
                return "https://news.google.com/rss/topics/CAAqKAgKIiJDQkFTRXdvSkwyMHZNR1ptZHpWbUVnSnJieG9DUzFJb0FBUAE?hl=ko&gl=KR&ceid=KR:ko";
            case "entertainment": // 연예
                return "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNREZ5Wm5vU0FtdHZLQUFQAQ?hl=ko&gl=KR&ceid=KR:ko";
            case "sports": // 스포츠
                return "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRFp1ZEdvU0FtdHZHZ0pMVWlnQVAB?hl=ko&gl=KR&ceid=KR%3Ako";
            default:
                return "";// 기본값 혹은 유효하지 않은 카테고리 선택 시
        }
    }
}
