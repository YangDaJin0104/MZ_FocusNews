<?php
    // 가상서버 - /var/www/html/getQuizScore.php

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

    // 랭킹 구하는 쿼리
    $sql = "SET @rank=0;";
    $conn->query($sql);

    $sql = "SELECT @rank:=@rank+1 AS `rank`, user_id, score FROM ranking ORDER BY score DESC;";
    $result = $conn->query($sql);


    // 결과를 담을 배열 초기화
    $response = array();

    // 결과가 있는지 확인
    if ($result->num_rows > 0) {
        // 결과를 루프하여 배열에 추가
        while($row = $result->fetch_assoc()) {
            $response[] = $row;
        }
    } else {
        // 결과가 없는 경우 빈 배열 반환
        $response = array();
    }

    // JSON 형식으로 반환
    header('Content-Type: application/json');
    echo json_encode($response);

    // 연결 종료
    $conn->close();
?>
