/**
 * 비디오 관련 API 호출을 처리하는 서비스
 */
export default class VideoService {
  /**
   * 최신 비디오 단일 항목 가져오기
   * @returns {Promise<Object>} 비디오 정보
   */
  static async getLatestVideo() {
    try {
      const response = await fetch('/api/videos/latest');
      if (!response.ok) {
        if (response.status === 204) {
          return null; // 콘텐츠 없음
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('최신 비디오를 가져오는 중 오류 발생:', error);
      return null;
    }
  }

  /**
   * 여러 개의 최신 비디오 가져오기
   * @param {number} limit - 가져올 비디오 수
   * @returns {Promise<Array>} 비디오 목록
   */
  static async getLatestVideos(limit = 5) {
    try {
      const response = await fetch(`/api/videos?limit=${limit}`);
      if (!response.ok) {
        if (response.status === 204) {
          return []; // 콘텐츠 없음
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('비디오 목록을 가져오는 중 오류 발생:', error);
      return [];
    }
  }

  /**
   * 카테고리별 비디오 가져오기
   * @param {string} category - 카테고리 이름
   * @param {number} limit - 가져올 비디오 수
   * @returns {Promise<Array>} 비디오 목록
   */
  static async getVideosByCategory(category, limit = 5) {
    try {
      const response = await fetch(`/api/videos/category?category=${encodeURIComponent(category)}&limit=${limit}`);
      if (!response.ok) {
        if (response.status === 204) {
          return []; // 콘텐츠 없음
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`${category} 카테고리 비디오를 가져오는 중 오류 발생:`, error);
      return [];
    }
  }
}