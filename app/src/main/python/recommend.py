import openai
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np

# OpenAI API 키 설정
openai.api_key = ''

# 샘플 뉴스 기사 데이터 및 각 기사에 대한 ID
news_data = [
    {"id": 1, "text": "동양하루살이가 따뜻한 날씨로 예년보다 이른 시기에 출몰하고 있으며, 성수동 일대에 대량으로 나타나 불편 민원이 증가하고 있다. 동양하루살이는 해충이 아니며, 감염병을 옮기지 않고 생태계 순환에 도움을 준다. 이를 피하기 위해서는 조명을 줄이거나 백색등을 황색등으로 교체하는 것이 효과적이다."},
    {"id": 2, "text": "따뜻한 날씨로 인해 동양하루살이가 이른 출몰을 보이며 대중의 관심을 끌고 있습니다. 이로 인해 지하철 내부에서도 동양하루살이가 발견되는 사례가 보고되고 있습니다. 이에 성동구청은 동양하루살이에 대한 대처 방법을 안내하고 있습니다. 방법은 주로 밝은 조명을 최소화하고 모기장을 설치하는 등의 예방책을 제시하고 있습니다."},
    {"id": 3, "text": "뽕나무깍지벌레는 어른벌레가 되면 방제가 어려우므로 5월 16일과 5월 30일에 방제해야 효과적이다. 농촌진흥청은 주요 생산지 농업기술센터와 협력해 방제 정보를 공유하고, 정확한 방제 시기를 알리기로 했다. 기온 차이로 인한 지역별 애벌레 발생 차이는 적어 전국적으로 동일한 방제 적기를 적용할 계획이다."},
    {"id": 4, "text": "서울 서대문구에서 경찰이 20대 대학생 A씨의 여자친구를 살해한 혐의로 A씨를 구속영장 발부 후 송치할 예정이라고 밝혔습니다. A씨는 이별 통보 후 흉기로 여자친구를 살해한 것으로, 경찰은 계획적 범행으로 분류하며 사이코패스 진단 검사는 검찰 송치 이후에 실시할 계획입니다. A씨는 범행 전 흉기를 구매하고 범행 후 옷을 갈아입는 등 계획적 행동을 보였습니다."},
    {"id": 5, "text": "한국인 범죄 일당이 태국에서 한국인을 살해하고 도주한 사건으로, 피의자 A는 혐의를 부인하며 ""아무것도 몰랐다""고 주장했다. 나머지 공범 중 1명은 캄보디아에서 붙잡혔으며, 도주 중인 다른 공범은 우리나라 경찰이 추적 중이다. 경찰은 협박 전화와 문자 메시지 등을 조사하며 범행 동기를 파악 중이며, 시신은 저수지에서 발견되었다."},
    {"id": 6, "text": "제주시 한 빌라에서 20대 여성이 감금된 사건에서 경찰은 20대 남성을 긴급체포했습니다. 피해 여성은 상대방의 폭행과 위협에 겁에 질려 사흘 동안 감금당한 후 지인을 통해 도움을 요청하며 경찰에 구조됐습니다. 가해 남성은 사건을 부인하고 있지만, 경찰은 특수감금 혐의로 수사를 진행 중입니다."},
    {"id": 7, "text": "Local governments prepare for increased climate change-related disasters."}
]

def get_embeddings(texts):
    response = openai.Embedding.create(
        input=texts,
        engine="text-embedding-3-small"
    )
    return np.array([embedding['embedding'] for embedding in response['data']])

def find_similar_articles(selected_article_id, news_data):
    # 문서와 ID 추출
    article_texts = [article['text'] for article in news_data]
    article_ids = [article['id'] for article in news_data]

    # 임베딩 추출
    embeddings = get_embeddings(article_texts)
    
    # 선택한 기사의 인덱스 찾기
    selected_index = article_ids.index(selected_article_id)
    
    # 선택한 기사와 다른 모든 기사의 유사도 계산
    cosine_similarities = cosine_similarity([embeddings[selected_index]], embeddings)
    
    # 유사도 기준으로 기사 정렬 및 상위 2개 선택 (자기 자신 제외)
    similar_indices = cosine_similarities.argsort()[0][-3:-1]
    return [article_ids[i] for i in similar_indices]

# 모든 기사에 대해 유사한 기사 찾기
results = {}
for article in news_data:
    selected_article_id = article['id']
    similar_article_ids = find_similar_articles(selected_article_id, news_data)
    results[selected_article_id] = similar_article_ids

# 결과 출력
for article_id, similar_ids in results.items():
    print("Selected Article ID:", article_id)
    print("Similar Article IDs:", similar_ids)
    print()
# selected_article_id = 5
# similar_article_ids = find_similar_articles(selected_article_id, news_data)
#
# print("Selected Article ID:", selected_article_id)
# print("Similar Article IDs:", similar_article_ids)