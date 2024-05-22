<?php
    // 가상서버 - /var/www/html/register.php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    $php_connect = 'UserConnection.php'; // Connection 파일명
    include($php_connect);

    // POST 데이터 수신
    $user_id = isset($_POST["user_id"]) ? $_POST["user_id"] : "";
    $user_pw = isset($_POST["user_pw"]) ? $_POST["user_pw"] : "";
    $user_name = isset($_POST["user_name"]) ? $_POST["user_name"] : "";

    // 사용자 ID가 이미 존재하는지 확인하는 쿼리
    $check_query = $con->prepare("SELECT user_id FROM users WHERE user_id = :user_id");
    $check_query->bindParam(':user_id', $user_id);
    $check_query->execute();

    if ($check_query->rowCount() > 0) {
        // 이미 존재하는 경우
        $response = array();
        $response["success"] = false;
        $response["message"] = "User ID already exists";
        echo json_encode($response);
    } else {
        // 사용자 ID가 존재하지 않는 경우에만 삽입 수행
        $statement = $con->prepare("INSERT INTO users (user_id, user_pw, user_name) VALUES (:user_id, :user_pw, :user_name)");
        $statement->bindParam(':user_id', $user_id);
        $statement->bindParam(':user_pw', $user_pw);
        $statement->bindParam(':user_name', $user_name);

        // 쿼리 실행
        $statement->execute();

        // 성공 응답 반환
        $response = array();
        $response["success"] = true;
        echo json_encode($response);
    }
?>
