<?php
    error_reporting(E_ALL);
    ini_set('display_errors', 1);

    // MySQL 데이터베이스와 연결
    include('DBConnection.php');
    $conn = new mysqli($host, $username, $password, $dbname);

    // POST 데이터에서 변수 추출
    $newsId = $_POST['news_id'];
    $related1 = $_POST['related_news1'];
    $related2 = $_POST['related_news2'];

    // SQL 업데이트 쿼리
    $sql = "UPDATE news SET related_news1 = ?, related_news2 = ? WHERE news_id = ?";

    // 쿼리를 준비하고 매개변수를 바인딩
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("iii", $related1, $related2, $newsId);

    // 쿼리 실행
    if ($stmt->execute()) {
        echo "Record updated successfully";
    } else {
        echo "Error updating record: " . $stmt->error;
    }

    // 스테이트먼트와 연결 종료
    $stmt->close();
    $conn->close();
?>
