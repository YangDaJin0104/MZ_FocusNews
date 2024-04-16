<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    // Connection 파일명 변경 시 아래 수정 필요
    $php_connect = 'DBConnection.php';

    include($php_connect);
        
    $table = 'test_table';      // DB table 이름

    $stmt = $con->prepare("select * from {$table}");
    $stmt->execute();

    if ($stmt->rowCount() > 0)
    {
        $data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC))
        {
            extract($row);
    
            array_push($data, 
                array('id'=>$id,
                'name'=>$name,
                'country'=>$country
            ));
        }

        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("coddl"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }

?>