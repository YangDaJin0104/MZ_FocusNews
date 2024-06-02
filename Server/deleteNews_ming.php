<?php
    // 가상서버 - /var/www/html/deleteNews2.php

    // MySQL 데이터베이스와 연결
    include('DBConnection.php');
    $conn = new mysqli($host, $username, $password, $dbname);

    // 연결 확인
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // summary가 null인 뉴스 항목 삭제
    $deletedRows = 0;       // log 출력용
    do {
        // summary가 null인 뉴스 항목 삭제
        $sql = "DELETE FROM news WHERE summary IS NULL";
        $result = $conn->query($sql);

        if ($result === TRUE) {
            $deletedRows += $conn->affected_rows;
        } else {
            echo "Error deleting news with null summary: " . $conn->error;
            break; // 오류 발생 시 종료
        }
    } while ($conn->affected_rows > 0);     // 반복하여 모든 null summary 행을 삭제

    // syslog 출력
    openlog('deleteNews2', LOG_PID | LOG_PERROR, LOG_LOCAL0);
    syslog(LOG_INFO, "Total $deletedRows news with null summary deleted successfully");
    closelog();

    // 요약문이 생성된 원문 null로 바꾸기
    $sql2 = "UPDATE newsData JOIN news ON newsData.news_id = news.news_id SET newsData.content = NULL WHERE news.summary IS NOT NULL";
    $stmt2 = $conn->prepare($sql2);

    if ($stmt2->execute()) {
        echo "Record updated successfully ";
    } else {
        echo "Error updating record: " . $stmt2->error;
    }

    // news 테이블엔 없고 newsData 테이블에만 있는 news_id의 원문 null로 바꾸기
    $sql3 = "UPDATE newsData LEFT JOIN news ON newsData.news_id = news.news_id SET newsData.content = NULL WHERE news.news_id IS NULL";
    $stmt3 = $conn->prepare($sql3);

    if ($stmt3->execute()) {
        echo "Record deleted successfully ";
    } else {
        echo "Error updating record: " . $stmt3->error;
    }


    // 스테이트먼트와 연결 종료
    $stmt2->close();
    $stmt3->close();
    $conn->close();
?>
