<?php
    // 가상서버 - /var/www/html/validate.php

    error_reporting(E_ALL);
    ini_set('display_errors', 1);

    $php_connect = 'UserConnection.php'; // Connection 파일명
    include($php_connect);

    // POST 데이터 수신
    $user_id = isset($_POST["user_id"]) ? $_POST["user_id"] : "";
    $user_name = isset($_POST["user_name"]) ? $_POST["user_name"] : "";

    // 쿼리 준비
    $statement_id = $con->prepare("SELECT user_id FROM users WHERE user_id = :user_id");
    $statement_id->bindParam(':user_id', $user_id);
    $statement_id->execute();

    $statement_name = $con->prepare("SELECT user_name FROM users WHERE user_name = :user_name");
    $statement_name->bindParam(':user_name', $user_name);
    $statement_name->execute();

    // 결과 처리
    $response = array();
    $response["name_success"] = true;
    $response["id_success"] = true;

    while ($row_id = $statement_id->fetch(PDO::FETCH_ASSOC)) {
        $response["id_success"] = false;
        $response["user_id"] = $row_id["user_id"];
    }

    while ($row_name = $statement_name->fetch(PDO::FETCH_ASSOC)) {
        $response["name_success"] = false;
        $response["user_name"] = $row_name["user_name"];
    }

    // JSON 형식으로 결과 반환
    echo json_encode($response);

?>
