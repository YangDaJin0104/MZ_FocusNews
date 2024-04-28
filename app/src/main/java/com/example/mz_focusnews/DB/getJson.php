<?php 
    // 가상서버 - /var/www/html/getJson.php

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    $php_connect = 'DBConnection.php';      // Connection 파일명
    $username = 'coddl';    // MySQL 계정 아이디

    include($php_connect);
        
    $table = 'news';      // DB table 이름

    $stmt = $con->prepare("select * from {$table}");
    $stmt->execute();

    if ($stmt->rowCount() > 0)
    {
        $data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC))
        {
            extract($row);
    
            array_push($data, array(
                'news_id' => $news_id,
                'view' => $view,
                'image' => $image,
                'link' => $link,
                'summary1' => $summary1,
                'summary2' => $summary2,
                'summary3' => $summary3
            ));
        }

        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array($username=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }

?>