import schedule
import time
import mysql.connector
from newspaper import Article
from newspaper.article import ArticleException
from bs4 import BeautifulSoup
import signal
import sys

# 사용자 중단 신호 처리
def signal_handler(sig, frame):
    print('\n사용자에 의해 스크립트가 중단되었습니다!')
    sys.exit(0)

signal.signal(signal.SIGINT, signal_handler)

def get_urls_from_db(db_config):
    try:
        print("데이터베이스에 연결 중")
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()

        print("데이터베이스에서 URL을 가져오는 중")
        cursor.execute('SELECT news_id, link FROM news')
        urls = cursor.fetchall()

        print("데이터베이스 연결 종료 중")
        conn.close()

        print(f"데이터베이스에서 {len(urls)}개의 URL을 가져왔습니다.")
        return urls
    except mysql.connector.Error as err:
        print(f"데이터베이스 오류: {err}")
        return []

def check_existing_article(db_config, news_id):
    try:
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()
        cursor.execute("SELECT content FROM newsData WHERE news_id = %s", (news_id,))
        result = cursor.fetchone()
        conn.close()
        return result is not None
    except mysql.connector.Error as err:
        print(f"데이터베이스 오류: {err}")
        log_error(news_id, f"MySQL Error: {err}")
        return False

def extract_article_body(url, retries=2):
    for attempt in range(retries):
        try:
            print(f"URL에서 기사를 다운로드하고 파싱 중: {url} (시도 {attempt + 1}회)")
            article = Article(url)
            article.download()
            article.parse()
            html = article.html
            soup = BeautifulSoup(html, 'lxml')
            
            paragraphs = soup.find_all('p')
            p_text = ' '.join([p.get_text() for p in paragraphs])
            
            breaks = soup.find_all('br')
            br_texts = []
            for br in breaks:
                next_sibling = br.next_sibling
                while next_sibling and not isinstance(next_sibling, str):
                    next_sibling = next_sibling.next_sibling
                if next_sibling:
                    br_texts.append(next_sibling.strip())
            br_text = ' '.join(br_texts)

            full_text = p_text + ' ' + br_text

            return full_text.strip()
        except ArticleException as e:
            log_error(url, f"처리 중 오류: {e}")
        except Exception as e:
            log_error(url, f"예기치 않은 오류: {e}")
        if attempt < retries - 1:
            print("다시 시도 중...")
    return None

def save_article_body_to_db(db_config, news_id, content):
    try:
        print(f"news_id: {news_id}인 기사를 데이터베이스에 저장 중")
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()

        if content:
            cursor.execute("INSERT INTO newsData (news_id, content) VALUES (%s, %s)", (news_id, content))
        else:
            cursor.execute("INSERT INTO newsData (news_id, content) VALUES (%s, NULL)", (news_id,))

        conn.commit()
        
        print(f"news_id: {news_id}인 기사가 데이터베이스에 성공적으로 저장되었습니다. 데이터 크기: {len(content) if content else 'NULL'} 문자")
        conn.close()
    except mysql.connector.Error as err:
        print(f"데이터베이스 오류: {err}")
        log_error(news_id, f"MySQL Error: {err}")

def log_error(identifier, message):
    with open("error_log.txt", "a") as f:
        f.write(f"Identifier: {identifier}\nError: {message}\n\n")

# DB 설정
db_config = {
    'user': 'coddl',
    'password': '1234',
    'host': '43.201.173.245',
    'database': 'news'
}

def update_articles():
    # DB에서 URL 가져오기
    urls = get_urls_from_db(db_config)

    # 기사 본문 추출 및 데이터베이스에 저장
    for idx, (news_id, url) in enumerate(urls):
        if check_existing_article(db_config, news_id):
            print(f"news_id: {news_id}의 기사가 이미 존재합니다. 다음으로 넘어갑니다.")
            continue

        print(f"URL {idx + 1} 처리 중: {url}")
        body = extract_article_body(url)
        if body and "크리에이터들이 유튜브 상에 게시, 태그 또는 추천한 상품들은 판매자들의 약관에 따라 판매됩니다. 유튜브는 이러한 제품들을 판매하지 않으며, 그에 대한 책임을 지지 않습니다." in body:
            print(f"기사 {idx + 1}에 해당 문구가 포함되어 있습니다. NULL 값으로 저장합니다.")
            save_article_body_to_db(db_config, news_id, None)
        elif body:
            print(f"기사 {idx + 1} 본문:")
            print(body)
            save_article_body_to_db(db_config, news_id, body)
        else:
            print(f"기사 {idx + 1}을(를) 처리할 수 없습니다.")
            save_article_body_to_db(db_config, news_id, None)

# 스케줄 설정
schedule.every(30).minutes.do(update_articles)

print("스케줄러가 설정되었습니다. 서버 시작 시 한 번 실행하고 이후 30분마다 업데이트가 실행됩니다.")

# 서버 시작 시 한 번 실행
update_articles()

# 스케줄 실행
while True:
    schedule.run_pending()
    time.sleep(1)
