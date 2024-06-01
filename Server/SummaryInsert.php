<?php
    // 가상서버 - /var/www/html/SummaryInsert.php (ming의 코드)

    error_reporting(E_ALL);
    ini_set('display_errors', 1);

    include('DBConnection.php');  // 데이터베이스 연결 설정
    $conn = new mysqli($host, $username, $password, $dbname);

    // 연결 확인
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        $newsId = $_POST['newsId'];
        $summary = $_POST['summary'];


        if(empty($summary) || empty($newsId)) {
            echo json_encode(array("status" => "error", "message" => "내용이 비어있습니다."));
            exit();
        }

        try {
            $stmt = $con->prepare("UPDATE news SET summary = ? WHERE news_id = ?");
            // $stmt->bind_param("si", $summary, $newsId);
            $stmt->bindParam(1, $summary, PDO::PARAM_STR);
            $stmt->bindParam(2, $newsId, PDO::PARAM_INT);

            if ($stmt->execute()) {
                echo json_encode(array("status" => "success", "message" => "요약 내용 저장 성공"));
            } else {
                echo json_encode(array("status" => "error", "message" => "데이터 저장 실패"));
            }
        } catch (Exception $e) {
            echo json_encode(array("status" => "error", "message" => "Database error: " . $e->getMessage()));
        }
    } else {
        echo json_encode(array("status" => "error", "message" => "Invalid request method"));
    }

    // 스테이트먼트와 연결 종료
    $stmt = null;
    $conn->close();
?>
