<?php
    // 가상서버 - /var/www/html/updateQuizCompleted.php

    $host = '43.201.173.245';   // IP 주소
    $username = 'coddl';    // MySQL 계정 아이디
    $password = '1234';     // MySQL 계정 패스워드
    $dbname = 'users';     // DATABASE 이름

    // POST 요청에서 사용자 ID와 새로운 퀴즈 점수 가져오기
    $userId = $_POST['userId'];

    // MySQL 데이터베이스에 연결
    $conn = new mysqli($servername, $username, $password, $dbname);

    // 연결 확인
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // 쿼리 작성 및 실행하여 데이터 업데이트
    $sql = "UPDATE users SET quiz_completed = 1 WHERE user_id = '$userId'";

    if ($conn->query($sql) === TRUE) {
        echo "Record updated successfully";
    } else {
        echo "Error updating record: " . $conn->error;
    }

    $conn->close();
?>
