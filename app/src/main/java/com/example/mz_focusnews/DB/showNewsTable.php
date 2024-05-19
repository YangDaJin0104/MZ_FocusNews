<?php
    // 가상서버 - /var/www/html/showNewsTable.php

    // 데이터베이스 연결 설정
    $host = '43.201.173.245';   // IP 주소
    $username = 'coddl';    // MySQL 계정 아이디
    $password = '1234';     // MySQL 계정 패스워드
    $dbname = 'news';     // DATABASE 이름

    try {
        // 데이터베이스에 연결
        $con = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
        $con->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        // 뉴스 데이터를 가져오는 쿼리 실행
        $stmt = $con->prepare("SELECT * FROM news");
        $stmt->execute();

    } catch(PDOException $e) {
        // 에러 발생 시 예외 처리
        echo "Error: " . $e->getMessage();
    }

?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>News Table</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
    <h2>News Table</h2>
    <table>
        <thead>
            <tr>
                <th>News ID</th>
                <th>View</th>
                <th>Image</th>
                <th>Link</th>
                <th>Summary 1</th>
                <th>Summary 2</th>
                <th>Summary 3</th>
            </tr>
        </thead>
        <tbody>
            <?php while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) { ?>
                <tr>
                    <td><?php echo $row['news_id']; ?></td>
                    <td><?php echo $row['view']; ?></td>
                    <td><?php echo $row['image'] ? 'Yes' : 'No'; ?></td>
                    <td><a href="<?php echo $row['link']; ?>" target="_blank"><?php echo $row['link']; ?></a></td>
                    <td><?php echo $row['summary1']; ?></td>
                    <td><?php echo $row['summary2']; ?></td>
                    <td><?php echo $row['summary3']; ?></td>
                </tr>
            <?php } ?>
        </tbody>
    </table>
</body>
</html>
