<?php
    // 가상서버 - /var/www/html/getSummary.php

    // MySQL 데이터베이스와 연결
    include('DBConnection.php');
    $conn = new mysqli($servername, $username, $password, $dbname);

    // 연결 확인
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // $_GET 배열을 사용하여 URL 파라미터에서 뉴스 ID를 가져옴
    $newsId = isset($_GET['news_id']) ? $_GET['news_id'] : null;

    if ($newsId) { // 요청된 뉴스 ID에 해당하는 id, title, summary, date, img_url, related_news1, related_news2, link를 가져옴
        $sql = "SELECT news_id, summary, title, img_url, link, DATE_FORMAT(date, '%Y년 %m월 %d일') AS date, COALESCE(related_news1, 0) AS related_news1, COALESCE(related_news2, 0) AS related_news2 FROM news WHERE news_id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("i", $newsId); // 뉴스 ID를 "i"로 바인드
    } else { // 모든 뉴스의 id, title, summary, related_news1, related_news2, link를 가져옴
        $sql = "SELECT news_id, summary, title, img_url, link, COALESCE(related_news1, 0) AS related_news1, COALESCE(related_news2, 0) AS related_news2 FROM news WHERE summary IS NOT NULL AND title IS NOT NULL";
        $stmt = $conn->prepare($sql);
    }

    $stmt->execute();
    $result = $stmt->get_result();

    // 결과를 담을 배열 초기화
    $response = array();

    // 결과가 있는지 확인
    if ($result->num_rows > 0) {
        // 결과에서 첫 번째 레코드를 가져옴
        while ($row = $result->fetch_assoc()) {
            $response[] = $row;
        }
    } else {
        // 결과가 없는 경우 오류 메시지 포함
        $response = array('error' => 'No summary available for the specified news ID');
    }

    // JSON 형식으로 반환
    header('Content-Type: application/json');
    echo json_encode($response);

    // 연결 종료
    $conn->close();
?>
