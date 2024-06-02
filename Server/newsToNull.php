<?php
    // newsData 테이블에서 -> 요약문이 생성 완료된 원문 뉴스를 null로 업데이트
    // news 테이블에서 -> 요약문이 생성되지 않는 bad 뉴스를 삭제

    // 가상서버 - /var/www/html/newsToNull.php

    // MySQL 데이터베이스와 연결
    include('DBConnection.php');
    $conn = new mysqli($host, $username, $password, $dbname);

    // 연결 확인
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // $sql = "DELETE news FROM news JOIN newsData ON news.news_id = newsData.news_id WHERE news.summary IS NULL";
    // $stmt = $conn->prepare($sql);

    // if ($stmt->execute()) {
    //     echo "Record deleted successfully ";
    // } else {
    //     echo "Error deleting record: " . $stmt->error;
    // }

    $sql2 = "UPDATE newsData JOIN news ON newsData.news_id = news.news_id SET newsData.content = NULL WHERE news.summary IS NOT NULL";
    $stmt2 = $conn->prepare($sql2);

    // 쿼리 실행
    if ($stmt2->execute()) {
        echo "Record updated successfully ";
    } else {
        echo "Error updating record: " . $stmt2->error;
    }

    $sql3 = "UPDATE newsData LEFT JOIN news ON newsData.news_id = news.news_id SET newsData.content = NULL WHERE news.news_id IS NULL";
    $stmt3 = $conn->prepare($sql3);

      // 쿼리 실행
    if ($stmt3->execute()) {
        echo "Record deleted successfully ";
    } else {
        echo "Error updating record: " . $stmt3->error;
    }

    // $sql4 = "DELETE news FROM news LEFT JOIN newsData ON news.news_id = newsData.news_id WHERE newsData.news_id IS NULL AND news.summary IS NOT NULL";
    // $stmt4 = $conn->prepare($sql4);

    //   // 쿼리 실행
    // if ($stmt4->execute()) {
    //     echo "Record deleted successfully ";
    // } else {
    //     echo "Error updating record: " . $stmt4->error;
    // }

    // 연결 종료
    // 스테이트먼트와 연결 종료
    // $stmt->close();
    $stmt2->close();
    $stmt3->close();
    // $stmt4->close();
    $conn->close();
?>
