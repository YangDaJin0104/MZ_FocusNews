<?php
    $apiKey = '//';
    $updateUrl = 'http://43.201.173.245/updateImgUrl.php';
    $getUrl = 'http://43.201.173.245/getNullImgNewsData.php';

    function cleanTitle($title) {       // 제목에서 특수 문자 제거
        return preg_replace('/[^\p{L}\p{N}\s\x20-\x7E]/u', '', $title);
    }

    function chatGPTImageGenerator($newsId, $title) {
        global $apiKey;

        $url = 'https://api.openai.com/v1/images/generations';
        $model = 'dall-e-3';

        if ($title == 'null') {
            return null;
        }

        // 제목에서 특수 문자를 제거
        $cleanTitle = cleanTitle($title);
        echo($cleanTitle);

        $data = [
            'model' => $model,
            'prompt' => $cleanTitle,
            'n' => 1,
            'size' => '1024x1024'
        ];

        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, [
            "Authorization: Bearer $apiKey",
            "Content-Type: application/json"
        ]);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));

        $result = curl_exec($ch);
        $httpcode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);

        if ($httpcode >= 400) {
            echo "HTTP request failed! Status code: $httpcode\n";
            return null;
        }

        $response = json_decode($result, true);

        if (isset($response['error'])) {
            echo "API Error: " . $response['error']['message'] . "\n";
            return null;
        }

        return $response['data'][0]['url'] ?? null;
    }

    // news 테이블의 img_url 값 업데이트
    function updateDBNewsImage($newsId, $imgUrl) {
        global $updateUrl;

        $data = http_build_query(['newsId' => $newsId, 'imgUrl' => $imgUrl]);
        $options = [
            'http' => [
                'header'  => "Content-Type: application/x-www-form-urlencoded\r\n",
                'method'  => 'POST',
                'content' => $data,
            ],
        ];

        $context  = stream_context_create($options);
        $result = file_get_contents($updateUrl, false, $context);
        if ($result === FALSE) {
            echo "Error updating news image URL\n";
        } else {
            echo "News image URL updated successfully\n";
        }
    }

    // /var/log/syslog에 로그 출력
    function logToSyslog($message) {
        openlog('generateImages', LOG_PID | LOG_PERROR, LOG_LOCAL0);
        syslog(LOG_INFO, $message);
        closelog();
    }

    // news 데이터 가져오기
    $response = file_get_contents($getUrl);
    $newsData = json_decode($response, true);

    foreach ($newsData as $news) {
        $newsId = $news['news_id'];
        $title = $news['title'];

        $imgUrl = chatGPTImageGenerator($newsId, $title);

        if ($imgUrl) {
            echo "imgUrl = " . $imgUrl . "\n";
            logToSyslog($imgUrl);

            updateDBNewsImage($newsId, $imgUrl);
        }

        sleep(1);
    }
?>
