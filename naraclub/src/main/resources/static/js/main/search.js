/**
 * 검색 기능 관련 JavaScript
 */
import { optionalAuthFetch } from '../commonFetch.js';

// 검색 관련 변수
let searchTimeout = null;
let currentBoardType = 'all';
let currentSearchScope = 'all';
let currentQuery = '';
let isSearching = false;

// 검색 오버레이 초기화
function initSearchOverlay() {
  const searchIcon = document.querySelector('.search-icon');
  const searchOverlay = document.getElementById('searchOverlay');
  const searchCloseBtn = document.getElementById('searchCloseBtn');
  const searchInput = document.getElementById('searchInput');
  const searchSubmitBtn = document.getElementById('searchSubmitBtn');
  const boardTypeFilters = document.querySelectorAll('.board-type-filter .filter-btn');
  const searchScopeFilters = document.querySelectorAll('.search-scope-filter .filter-btn');

  // 검색 아이콘 클릭 시 오버레이 표시
  if (searchIcon) {
    searchIcon.addEventListener('click', function() {
      searchOverlay.classList.add('active');
      searchInput.focus();
    });
  }

  // 닫기 버튼 클릭 시 오버레이 숨김
  if (searchCloseBtn) {
    searchCloseBtn.addEventListener('click', function() {
      searchOverlay.classList.remove('active');
      searchInput.value = '';
      resetSearchResults();
    });
  }

  // 검색어 입력 시 자동 검색 (Debounce 적용)
  if (searchInput) {
    searchInput.addEventListener('input', function() {
      const query = this.value.trim();

      // 이전 타이머 취소
      if (searchTimeout) {
        clearTimeout(searchTimeout);
      }

      // 검색어가 없으면 결과 초기화
      if (!query) {
        resetSearchResults();
        return;
      }

      // 500ms 후에 검색 실행 (타이핑 중에는 검색하지 않음)
      searchTimeout = setTimeout(() => {
        currentQuery = query;
        performSearch(query, currentBoardType, currentSearchScope);
      }, 500);
    });
  }

  // 검색 버튼 클릭 시 검색 실행
  if (searchSubmitBtn) {
    searchSubmitBtn.addEventListener('click', function() {
      const query = searchInput.value.trim();
      if (query) {
        currentQuery = query;
        performSearch(query, currentBoardType, currentSearchScope);
      }
    });
  }

  // 게시판 유형 필터 클릭 이벤트
  boardTypeFilters.forEach(btn => {
    btn.addEventListener('click', function() {
      // 활성화 상태 변경
      boardTypeFilters.forEach(b => b.classList.remove('active'));
      this.classList.add('active');

      // 필터 값 적용
      currentBoardType = this.dataset.filter;

      // 현재 검색어가 있으면 재검색
      if (currentQuery) {
        performSearch(currentQuery, currentBoardType, currentSearchScope);
      }
    });
  });

  // 검색 범위 필터 클릭 이벤트
  searchScopeFilters.forEach(btn => {
    btn.addEventListener('click', function() {
      // 활성화 상태 변경
      searchScopeFilters.forEach(b => b.classList.remove('active'));
      this.classList.add('active');

      // 필터 값 적용
      currentSearchScope = this.dataset.scope;

      // 현재 검색어가 있으면 재검색
      if (currentQuery) {
        performSearch(currentQuery, currentBoardType, currentSearchScope);
      }
    });
  });

  // 검색어 입력 시 엔터키 이벤트
  searchInput.addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
      const query = this.value.trim();
      if (query) {
        currentQuery = query;
        performSearch(query, currentBoardType, currentSearchScope);
      }
    }
  });
}

/**
 * 검색 실행 함수
 * @param {string} query - 검색어
 * @param {string} boardType - 게시판 유형 (all, original, board, vote)
 * @param {string} searchScope - 검색 범위 (all, title, content, author)
 */
async function performSearch(query, boardType, searchScope) {
  const resultsContainer = document.getElementById('searchResults');

  // 이미 검색 중이면 중단
  if (isSearching) return;

  // 검색 시작
  isSearching = true;

  // 로딩 표시
  resultsContainer.innerHTML = `
    <div class="search-loading">
      <i class="fas fa-spinner fa-spin"></i>
      <p>검색 중...</p>
    </div>
  `;

  try {
    // 검색 API 파라미터 구성
    const params = new URLSearchParams({
      query: query,
      page: 0,
      size: 20
    });

    // 게시판 유형 파라미터 추가
    if (boardType !== 'all') {
      params.append('boardType', boardType.toUpperCase());
    }

    // 검색 범위 파라미터 추가
    if (searchScope !== 'all') {
      params.append('searchType', searchScope.toUpperCase());
    }

    // API 호출
    const response = await optionalAuthFetch(`/api/search?${params.toString()}`);
    const data = await response.json();

    // 검색 결과 표시
    displaySearchResults(data.response, resultsContainer);

  } catch (error) {
    console.error('검색 오류:', error);
    resultsContainer.innerHTML = `
      <div class="no-results">
        <p>검색 중 오류가 발생했습니다</p>
        <p>다시 시도해 주세요</p>
      </div>
    `;
  } finally {
    isSearching = false;
  }
}

/**
 * 검색 결과 화면에 표시
 * @param {Object} results - 검색 결과 데이터
 * @param {HTMLElement} container - 결과를 표시할 컨테이너
 */
function displaySearchResults(results, container) {
  // 데이터가 없는 경우
  if (!results || !results.data || results.data.length === 0) {
    container.innerHTML = `
      <div class="no-results">
        <p>검색 결과가 없습니다</p>
      </div>
    `;
    return;
  }

  // 결과 HTML 생성
  let resultsHTML = '';

  results.data.forEach(item => {
    // 게시판 유형에 따른 아이콘 및 스타일 설정
    let iconClass = 'fa-file-alt';
    let boardTypeText = '기타';
    let boardTypeClass = '';

    if (item.boardType === 'YOUTUBE_VIDEO' || item.boardType === 'YOUTUBE_SHORTS') {
      iconClass = 'fa-play-circle';
      boardTypeText = '오리지널';
      boardTypeClass = 'original';
    } else if (item.boardType === 'BOARD') {
      iconClass = 'fa-clipboard';
      boardTypeText = '자유게시판';
      boardTypeClass = 'board';
    } else if (item.boardType === 'VOTE') {
      iconClass = 'fa-chart-bar';
      boardTypeText = '투표';
      boardTypeClass = 'vote';
    }

    // 검색 결과 아이템 HTML
    resultsHTML += `
      <div class="search-result-item" data-id="${item.id}" data-type="${item.boardType.toLowerCase()}">
        <div class="result-icon">
          <i class="far ${iconClass}"></i>
        </div>
        <div class="result-content">
          <div class="result-title">${item.title}</div>
          <div class="result-meta">
            <span class="result-board ${boardTypeClass}">${boardTypeText}</span>
            <span class="result-date">${formatDate(item.createdAt)}</span>
          </div>
        </div>
      </div>
    `;
  });

  // 결과 HTML 삽입
  container.innerHTML = resultsHTML;

  // 결과 아이템 클릭 이벤트 설정
  const resultItems = container.querySelectorAll('.search-result-item');
  resultItems.forEach(item => {
    item.addEventListener('click', function() {
      const id = this.dataset.id;
      const type = this.dataset.type;

      // 게시판 유형에 따라 다른 상세 페이지로 이동
      navigateToDetail(type, id);
    });
  });
}

/**
 * 검색 결과 초기화
 */
function resetSearchResults() {
  const resultsContainer = document.getElementById('searchResults');
  if (resultsContainer) {
    resultsContainer.innerHTML = '';
  }
}

/**
 * 상세 페이지로 이동
 * @param {string} type - 게시판 유형
 * @param {string} id - 게시물 ID
 */
function navigateToDetail(type, id) {
  let detailPage = '';

  // 게시판 유형에 따른 상세 페이지 경로 설정
  if (type === 'youtube_video' || type === 'youtube_shorts') {
    detailPage = '/original/videoDetail.html';
  } else if (type === 'board') {
    detailPage = '/board/boardDetail.html';
  } else if (type === 'vote') {
    detailPage = '/vote/voteDetail.html';
  } else {
    detailPage = '/articleDetail.html';
  }

  // 상세 페이지로 이동
  window.location.href = `${detailPage}?id=${id}`;
}

/**
 * 날짜 형식 변환
 * @param {string} dateString - ISO 형식 날짜 문자열
 * @returns {string} 포맷된 날짜 문자열
 */
function formatDate(dateString) {
  if (!dateString) return '';

  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}.${month}.${day}`;
}

// 페이지 로드 시 검색 오버레이 초기화
document.addEventListener('DOMContentLoaded', function() {
  initSearchOverlay();
});

export { initSearchOverlay };