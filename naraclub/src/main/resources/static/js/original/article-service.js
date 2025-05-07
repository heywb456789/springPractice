import { optionalAuthFetch } from '../commonFetch.js';

export default class NewsArticleService{

  static async getLatestArticles(page=0, limit = 4, originalType='NEWS_ARTICLE', keyword='') {
    const url = `/api/articles?page=${page}&size=${limit}&type=${originalType}`+
      (keyword ? `&searchType=ARTICLE_TITLE_CONTENT&searchText=${encodeURIComponent(keyword)}` : '');
    try {
      const response = await optionalAuthFetch(url)
      if (!response.ok) {
        if (response.status === 204) {
          return []; // 콘텐츠 없음
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      // console.log('API 응답 데이터:', data); // 디버깅용
      return data;

    } catch (error) {
      console.error('비디오 목록을 가져오는 중 오류 발생:', error);
      return [];
    }
  }
}