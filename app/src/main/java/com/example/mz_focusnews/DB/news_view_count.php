<?php
    // 가상서버 - /var/www/html/news_view_count.php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    $php_connect = 'DBConnection.php';      // Connection 파일명
    $username = 'coddl';    // MySQL 계정 아이디

    include($php_connect);

    // 클라이언트로부터 전달받은 뉴스 ID
    $news_id = isset($_POST["news_id"]) ? $_POST["news_id"] : "";

    // 뉴스 조회수 증가
    $stmt = $con->prepare("UPDATE news SET view = view + 1 WHERE news_id = :news_id");
    $stmt->bindParam(':news_id', $news_id, PDO::PARAM_STR);
    $stmt->execute();

    $response = array();
    $response["success"] = false;

    if ($stmt->rowCount() > 0) {
        // 뉴스 조회수 증가 성공
        $response["success"] = true;
    } else {
        // 뉴스 조회수 증가 실패
        $response["success"] = false;
    }

    header('Content-Type: application/json; charset=utf8');
    $json = json_encode($response, JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;
?>
