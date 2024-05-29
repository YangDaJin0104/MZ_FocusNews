<?php
    // 가상서버 - /var/www/html/updateImgUrl.php

    $host = '43.201.173.245';   // IP 주소
    $username = 'coddl';    // MySQL 계정 아이디
    $password = '1234';     // MySQL 계정 패스워드
    $dbname = 'news';     // DATABASE 이름

    // POST 요청에서 사용자 ID와 새로운 퀴즈 점수 가져오기
    $newsId = $_POST['newsId'];
    $imgUrl = $_POST['imgUrl'];

    // MySQL 데이터베이스에 연결
    $conn = new mysqli($servername, $username, $password, $dbname);

    // 연결 확인
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // 준비된 문장을 사용하여 데이터 업데이트
    $stmt = $conn->prepare("UPDATE news SET img_url = ? WHERE news_id = ?");        // SQL Injection 방지
    $stmt->bind_param("si", $imgUrl, $newsId);

    if ($stmt->execute() === TRUE) {
        echo "Record updated successfully";
    } else {
        echo "Error updating record: " . $stmt->error;
    }

    $stmt->close();
    $conn->close();
?>
