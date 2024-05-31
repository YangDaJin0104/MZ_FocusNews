<?php
    // 가상서버 - /var/www/html/generateImages.php

    $apiKey = '//';
    $updateUrl = 'http://43.201.173.245/updateImgUrl.php';
    $getNewsData = 'http://43.201.173.245/getNullImgNewsData.php';      // news_id, summary 가져옴

    function chatGPTImageGenerator($newsId, $summary) {
        global $apiKey;

        $url = 'https://api.openai.com/v1/images/generations';
        $model = 'dall-e-3';

        if ($summary == 'null') {
            return null;
        }

        $data = [
            'model' => $model,
            'prompt' => $summary,
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
                'header'  => "Content-Type: application/x-www-form-urlencoded;charset=utf-8\r\n",
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
    $response = file_get_contents($getNewsData);
    $newsData = json_decode($response, true);

    foreach ($newsData as $news) {
        $newsId = $news['news_id'];
        $summary = $news['summary'];

        $firstSentence = strtok($summary, ".");     // 첫 번째 문장 추출
        echo "첫 번째 문장: " . $firstSentence . "\n";
        $imgUrl = chatGPTImageGenerator($newsId, $firstSentence);

        if ($imgUrl) {
            echo "imgUrl = " . $imgUrl . "\n";
            logToSyslog($imgUrl);

            updateDBNewsImage($newsId, $imgUrl);
        }

        sleep(1);
    }

    logToSyslog("함수 종료");
?>
