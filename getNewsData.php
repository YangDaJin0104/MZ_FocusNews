<?php
    // 가상서버 - /var/www/html/getNewsData.php

    // MySQL 데이터베이스와 연결
    include('DBConnection.php');
    $conn = new mysqli($host, $username, $password, $dbname);

    // 연결 확인
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // $_GET 배열을 사용하여 URL 파라미터에서 뉴스 ID를 가져옴
    $news_id = isset($_GET['news_id']) ? $_GET['news_id'] : null;

    header('Content-Type: application/json');


    if ($news_id === 'all') {
        $sql = "SELECT newsData.news_id, newsData.content FROM newsData INNER JOIN news ON newsData.news_id = news.news_id WHERE newsData.content IS NOT NULL AND news.summary IS NULL";
        $stmt = $conn->prepare($sql);
    } else {
        echo json_encode(["error" => "News ID is required"]);
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
        $response['error'] = "No content available for the specified news ID: $news_id";
    }

    // JSON 형식으로 반환
    echo json_encode($response);
    

    // 데이터베이스 연결 종료
    $conn->close();
?>