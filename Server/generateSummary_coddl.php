<?php
    // 가상서버 - /var/www/html/generateSummary_coddl.php

    $apiKey = '//';     // ChatGPT API Key
    $updateUrl = 'http://43.201.173.245/SummaryInsert.php';      // summary를 생성한 경우, news 테이블에 update
    $getNewsData = 'http://43.201.173.245/getNewsData_coddl.php';      // news_id, summary 가져옴

    // crontab 설정 (실행될 시간 설정)
    // */10 * * * * /usr/bin/php /var/www/html/generateSummary_coddl.php
    // */40 * * * * /usr/bin/php /var/www/html/generateSummary_coddl.php

    function chatGPTSummary($newsId, $article) {
        global $apiKey;

        $url = 'https://api.openai.com/v1/chat/completions';
        $model = 'gpt-3.5-turbo';
    
        // prompt 생성
        $prompt = $article . " 이 뉴스 기사의 핵심 내용을 3문장으로 요약해. 각 문장은 다.로 끝나도록 하고 공백 포함 70글자 내외로 써줘. 문장번호나 글자수는 쓰지 마.";
    
        // request body
        $body = json_encode([
            'model' => $model,
            'messages' => [
                [
                    'role' => 'user',
                    'content' => $prompt
                ]
            ]
        ]);
    
        $headers = [
            'Authorization: Bearer ' . $apiKey,
            'Content-Type: application/json'
        ];
    
        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $body);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    
        $response = curl_exec($ch);
    
        if (curl_errno($ch)) {
            error_log('Error in ChatGPT request: ' . curl_error($ch));
            return null;
        }
        
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        if ($httpCode != 200) {
            error_log('Error in ChatGPT request: HTTP code ' . $httpCode);
            return null;
        }
    
        curl_close($ch);
    
        // 파싱 후 content만 추출
        $jsonResponse = json_decode($response, true);
        if (isset($jsonResponse['choices']) && count($jsonResponse['choices']) > 0) {
            $firstChoice = $jsonResponse['choices'][0];
            if (isset($firstChoice['message']['content'])) {
                $content = $firstChoice['message']['content'];
                return $content;
            } else {
                error_log('No "content" in message');
            }
        } else {
            error_log('No "message" in first choice');
        }
    
        return null;
    }

    // news 테이블의 summary 값 업데이트
    function updateSummary($newsId, $summary) {
        global $updateUrl;

        $data = http_build_query(['newsId' => $newsId, 'summary' => $summary]);
        $options = [
            'http' => [
                'header'  => "Content-Type: application/x-www-form-urlencoded;charset=UTF-8\r\n",
                'method'  => 'POST',
                'content' => $data,
            ],
        ];

        $context = stream_context_create($options);
        $result = file_get_contents($updateUrl, false, $context);
        if ($result === FALSE) {
            echo "Error updating summary\n";
        } else {
            echo "Summary updated successfully\n";
        }
    }

    // /var/log/syslog에 로그 출력
    function logToSyslog($message) {
        openlog('generateSummary', LOG_PID | LOG_PERROR, LOG_LOCAL0);
        syslog(LOG_INFO, $message);
        closelog();
    }

    // news 데이터 가져오기
    $response = file_get_contents($getNewsData);
    $newsData = json_decode($response, true);

    foreach ($newsData as $news) {
        $newsId = $news['news_id'];
        $article = $news['content'];

        $summary = chatGPTSummary($newsId, $article);
        
        if ($summary) {
            $logMessage = "newsId: " . $newsId . " / summary: " . $summary;
            logToSyslog($logMessage);

            updateSummary($newsId, $summary);
        } else{
            logToSyslog("fail to generate summary");
        }

        sleep(1);
    }

    // 순차적으로 실행
    exec('php /var/www/html/deleteNews.php');       // summary 생성 후 summary is null인 데이터 삭제
    exec('php /var/www/html/generateImages.php');   // img_url is null인 뉴스 이미지 생성
?>
