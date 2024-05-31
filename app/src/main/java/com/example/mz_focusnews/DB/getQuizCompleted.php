<?php
    // 가상서버 - /var/www/html/getQuizCompleted.php

    // MySQL 서버 연결 정보 설정
    $servername = "43.201.173.245";     // MySQL 서버 주소
    $username = "coddl";                // MySQL 사용자 이름
    $password = "1234";                 // MySQL 비밀번호
    $dbname = "users";                  // 사용할 데이터베이스 이름

    // MySQL 데이터베이스와 연결
    $conn = new mysqli($servername, $username, $password, $dbname);

    // 연결 확인
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // 안드로이드에서 전달된 user_id를 가져옴
    $user_id = $_POST['user_id'];

    // 쿼리 실행
    $sql = "SELECT quiz_completed FROM users WHERE user_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        // 결과를 배열로 가져옴
        $row = $result->fetch_assoc();
        echo json_encode(array("status" => "success", "quiz_completed" => $row['quiz_completed']));
    } else {
        echo json_encode(array("status" => "error", "message" => "User not found"));
    }

    // 연결 종료
    $stmt->close();
    $conn->close();
?>
