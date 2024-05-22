<?php
    // 가상서버 - /var/www/html/login.php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    $php_connect = 'UserConnection.php';      // Connection 파일명
    $username = 'coddl';    // MySQL 계정 아이디

    include($php_connect);

    // 사용자가 입력한 정보
    $user_id = isset($_POST["user_id"]) ? $_POST["user_id"] : "";
    $user_pw = isset($_POST["user_pw"]) ? $_POST["user_pw"] : "";

    // 사용자 인증
    $stmt = $con->prepare("SELECT * FROM users WHERE user_id = :user_id AND user_pw = :user_pw");
    $stmt->bindParam(':user_id', $user_id, PDO::PARAM_STR);
    $stmt->bindParam(':user_pw', $user_pw, PDO::PARAM_STR);
    $stmt->execute();

    $response = array();
    $response["success"] = false;

    if ($stmt->rowCount() > 0) {
        // 로그인 성공
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $response["success"] = true;
        $response["user_id"] = $row['user_id'];
        $response["user_pw"] = $row['user_pw'];
        $response["user_name"] = $row['user_name'];
    } else {
        // 로그인 실패
        $response["success"] = false;
        $response["message"] = "Invalid credentials";
    }

    header('Content-Type: application/json; charset=utf8');
    $json = json_encode($response, JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;
?>

