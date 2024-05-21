<?php
    // 가상서버 - /var/www/html/NewsInsert.php

    error_reporting(E_ALL);
    ini_set('display_errors', 1);

    $username = 'coddl';    // MySQL 계정 아이디
    $php_connect = 'DBConnection.php'; // Connection 파일명
    include($php_connect);

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        $title = $_POST['title'] ?? '';
        $link = $_POST['link'] ?? '';
        $pubDate = $_POST['pubDate'] ?? '';

        if (empty($title) || empty($link) || empty($pubDate)) {
            echo json_encode(array("status" => "error", "message" => "제목, 링크, 발행 날짜를 입력하세요."));
            exit();
        }

        try {
            $table = 'news'; // 테이블명

            $stmt = $con->prepare("INSERT INTO {$table} (title, link, date, view, image, summary, category) VALUES (:title, :link, :pubDate, NULL, NULL, NULL, NULL)");
            $stmt->bindParam(':title', $title);
            $stmt->bindParam(':link', $link);
            $stmt->bindParam(':pubDate', $pubDate);

            if ($stmt->execute()) {
                echo json_encode(array("status" => "success", "message" => "제목, 링크, 발행 날짜가 성공적으로 추가되었습니다."));
            } else {
                echo json_encode(array("status" => "error", "message" => "추가 에러"));
            }
        } catch (PDOException $e) {
            echo json_encode(array("status" => "error", "message" => "Database error: " . $e->getMessage()));
        }
    } else {
        echo json_encode(array("status" => "error", "message" => "Invalid request method"));
    }
?>

