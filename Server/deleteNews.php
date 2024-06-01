<?php
    // 가상서버 - /var/www/html/deleteNews.php

    $host = '43.201.173.245';   // IP 주소
    $username = 'coddl';        // MySQL 계정 아이디
    $password = '1234';         // MySQL 계정 패스워드
    $dbname = 'news';           // DATABASE 이름

    // POST 요청에서 newsId 가져오기
    if (isset($_POST['newsId'])) {
        $newsId = $_POST['newsId'];
    } else {
        die("newsId is not set");
    }

    // MySQL 데이터베이스에 연결
    $conn = new mysqli($host, $username, $password, $dbname);

    // 연결 확인
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // 준비된 문장을 사용하여 뉴스 항목 삭제
    $stmt = $conn->prepare("DELETE FROM news WHERE news_id = ?");
    $stmt->bind_param("i", $newsId);

    if ($stmt->execute() === TRUE) {
        echo "News deleted successfully";
    } else {
        echo "Error deleting news: " . $stmt->error;
    }

    $stmt->close();
    $conn->close();
?>
