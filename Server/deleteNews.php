<?php
    // 가상서버 - /var/www/html/deleteNews2.php

    $host = '43.201.173.245';   // IP 주소
    $username = 'coddl';        // MySQL 계정 아이디
    $password = '1234';         // MySQL 계정 패스워드
    $dbname = 'news';           // DATABASE 이름

    // MySQL 데이터베이스에 연결
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

    $conn->close();
?>
