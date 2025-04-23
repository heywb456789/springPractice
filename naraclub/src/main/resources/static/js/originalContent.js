import { optionalAuthFetch } from './commonFetch.js';

// 현재 활성화된 탭
let currentTab = 'video';
// 페이지당 아이템 수
const PAGE_SIZE = 10;

/**
 * 콘텐츠 목록을 API에서 불러와 동적으로 구성하는 함수
 * @param {string} type - 콘텐츠 타입 (video, shorts, article)
 * @param {number} page - 페이지 번호 (기본값: 0)
 * @param {number} size - 한 페이지당 아이템 수 (기본값: 10)
 * @param {string} keyword - 검색어 (기본값: 빈 문자열)
 */
async function loadContent(type = 'video', page = 0, size = 10, keyword = '') {
  // 컨텐츠 리스트 컨테이너 확인
  const contentListElement = document.getElementById(`${type}List`);
  if (!contentListElement) {
    return;
  }

  // 콘텐츠 리스트 활성화 상태 변경
  const contentLists = document.querySelectorAll('.content-list');
  contentLists.forEach(list => list.classList.remove('active'));
  contentListElement.classList.add('active');

  // 현재 탭 업데이트
  currentTab = type;

  // 로딩 상태 표시
  showLoading(true);

  // API URL 구성
  const apiUrl = `/api/content/${type}?page=${page}&size=${size}` +
    (keyword ? `&keyword=${encodeURIComponent(keyword)}` : '');

  try {
    // API 호출
    const response = await optionalAuthFetch(apiUrl);
    const data = await response.json();

    // 페이지네이션 정보 업데이트
    updatePagination(data.response.pagination);

    // 콘텐츠 업데이트
    updateContent(type, data.response);

  } catch (error) {
    console.error(`${type} 콘텐츠 로드 오류:`, error);
    
    // 오류 메시지 표시
    contentListElement.innerHTML = `
      <div class="error-state">
        <div class="error-icon">
          <svg width="60" height="60" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 4C7.58172 4 4 7.58172 4 12C4 16.4183 7.58172 20 12 20C16.4183 20 20 16.4183 20 12C20 7.58172 16.4183 4 12 4Z" stroke="#ff6b6b" stroke-width="2"/>
            <path d="M12 8V13" stroke="#ff6b6b" stroke-width="2" stroke-linecap="round"/>
            <circle cx="12" cy="16" r="1" fill="#ff6b6b"/>
          </svg>
        </div>
        <p class="error-title">콘텐츠를 불러오는 데 실패했습니다</p>
        <p class="error-message">네트워크 연결을 확인하시거나 잠시 후 다시 시도해 주세요</p>
        <button class="retry-button" onclick="loadContent('${type}', ${page}, ${size}, '${keyword}')">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4C7.58 4 4.01 7.58 4.01 12C4.01 16.42 7.58 20 12 20C15.73 20 18.84 17.45 19.73 14H17.65C16.83 16.33 14.61 18 12 18C8.69 18 6 15.31 6 12C6 8.69 8.69 6 12 6C13.66 6 15.14 6.69 16.22 7.78L13 11H20V4L17.65 6.35Z" fill="#4263eb"/>
          </svg>
          다시 시도하기
        </button>
      </div>
    `;
  } finally {
    // 로딩 상태 숨김
    showLoading(false);
  }
}

/**
 * 콘텐츠 업데이트 함수
 * @param {string} type - 콘텐츠 타입
 * @param {Object} data - API 응답 데이터
 */
function updateContent(type, data) {
  const contentList = document.getElementById(`${type}List`);

  if (!contentList) return;

  // 데이터가 없는 경우
  if (!data.items || data.items.length === 0) {
    contentList.innerHTML = `
      <div class="empty-state">
        <div class="empty-icon">
          <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M19 5V19H5V5H19ZM21 3H3V21H21V3Z" fill="#adb5bd"/>
            <path d="M14 17H6V15H14V17Z" fill="#adb5bd"/>
            <path d="M14 13H6V11H14V13Z" fill="#adb5bd"/>
            <path d="M18 9H6V7H18V9Z" fill="#adb5bd"/>
          </svg>
        </div>
        <p class="empty-title">콘텐츠가 없습니다</p>
        <p class="empty-message">새로운 콘텐츠가 곧 업데이트될 예정입니다</p>
      </div>
    `;
    return;
  }

  // 컨텐츠 타입에 따라 다른 HTML 생성
  let contentHTML = '';

  if (type === 'video') {
    contentHTML = createVideoListHTML(data.items);
  } else if (type === 'shorts') {
    contentHTML = createShortsListHTML(data.items);
  } else if (type === 'article') {
    contentHTML = createArticleListHTML(data.items);
  }

  // HTML 삽입
  contentList.innerHTML = contentHTML;

  // 아이템 클릭 이벤트 초기화
  initContentItemsClick(type);
}

/**
 * 동영상 리스트 HTML 생성
 * @param {Array} items - 동영상 아이템 배열
 * @returns {string} HTML 문자열
 */
function createVideoListHTML(items) {
  return items.map(item => {
    const isNew = isItemNew(item.createdAt);
    return `
      <div class="video-item" data-id="${item.id}">
        <div class="video-thumbnail-container">
          <img src="${item.thumbnailUrl}" alt="${item.title}" class="video-thumbnail">
          <span class="video-duration">${formatDuration(item.duration)}</span>
          ${isNew ? '<span class="video-badge badge-new">N</span>' : ''}
        </div>
        <div class="video-info">
          <h3 class="video-title">${item.title}</h3>
          <div class="video-meta">
            <span class="video-date">${formatDate(item.createdAt)}</span>
            <span class="video-views">조회수 ${formatViews(item.views)}회</span>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

/**
 * 쇼츠 리스트 HTML 생성
 * @param {Array} items - 쇼츠 아이템 배열
 * @returns {string} HTML 문자열
 */
function createShortsListHTML(items) {
  return items.map(item => {
    const isNew = isItemNew(item.createdAt);
    return `
      <div class="shorts-item" data-id="${item.id}">
        <img src="${item.thumbnailUrl}" alt="${item.title}" class="shorts-thumbnail">
        ${isNew ? '<span class="shorts-badge badge-new">N</span>' : ''}
        <div class="shorts-overlay">
          <h3 class="shorts-title">${item.title}</h3>
          <span class="shorts-views">조회수 ${formatViews(item.views)}회</span>
        </div>
      </div>
    `;
  }).join('');
}

/**
 * 기사 리스트 HTML 생성
 * @param {Array} items - 기사 아이템 배열
 * @returns {string} HTML 문자열
 */
function createArticleListHTML(items) {
  return items.map(item => {
    const isNew = isItemNew(item.createdAt);
    return `
      <div class="article-item" data-id="${item.id}">
        <img src="${item.thumbnailUrl}" alt="${item.title}" class="article-thumbnail">
        <div class="article-info">
          <h3 class="article-title">
            ${isNew ? '<span class="article-badge badge-new">N</span>' : ''}
            ${item.title}
          </h3>
          <div class="article-meta">
            <span class="article-date">${formatDate(item.createdAt)}</span>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

/**
 * 콘텐츠 아이템 클릭 이벤트 초기화
 * @param {string} type - 콘텐츠 타입
 */
function initContentItemsClick(type) {
  const items = document.querySelectorAll(`.${type}-item`);

  items.forEach(item => {
    item.addEventListener('click', function() {
      const contentId = this.getAttribute('data-id');
      if (!contentId) return;

      // 콘텐츠 상세 페이지로 이동
      window.location.href = `/${type}Detail.html?id=${contentId}`;
    });
  });
}

/**
 * 페이지네이션 업데이트 함수
 * @param {Object} pagination - 페이지네이션 데이터
 */
function updatePagination(pagination) {
  const paginationContainer = document.getElementById('pagination');
  if (!paginationContainer) return;

  // 현재 페이지
  const currentPage = pagination.currentPage || 0;

  // 전체 페이지 수
  const totalPages = pagination.totalPages || 0;

  // 표시할 페이지 버튼 개수
  const displayPageCount = 5;

  // 시작 페이지와 끝 페이지 계산
  let startPage = Math.max(0, currentPage - Math.floor(displayPageCount / 2));
  let endPage = Math.min(totalPages - 1, startPage + displayPageCount - 1);

  // 시작 페이지 재조정
  if (endPage - startPage < displayPageCount - 1) {
    startPage = Math.max(0, endPage - displayPageCount + 1);
  }

  // 페이지네이션 HTML 생성
  let paginationHTML = `
    <span class="pagination-item prev ${currentPage === 0 ? 'disabled' : ''}" data-page="${currentPage - 1}">
      <i class="fas fa-chevron-left"></i>
    </span>
  `;

  // 첫 페이지가 0이 아닌 경우, 첫 페이지 버튼 추가
  if (startPage > 0) {
    paginationHTML += `
      <span class="pagination-item" data-page="0">1</span>
      ${startPage > 1 ? '<span class="pagination-item dots">...</span>' : ''}
    `;
  }

  // 페이지 버튼 생성
  for (let i = startPage; i <= endPage; i++) {
    paginationHTML += `
      <span class="pagination-item ${i === currentPage ? 'active' : ''}" data-page="${i}">
        ${i + 1}
      </span>
    `;
  }

  // 마지막 페이지가 totalPages가 아닌 경우, 마지막 페이지 버튼 추가
  if (endPage < totalPages - 1) {
    paginationHTML += `
      ${endPage < totalPages - 2 ? '<span class="pagination-item dots">...</span>' : ''}
      <span class="pagination-item" data-page="${totalPages - 1}">${totalPages}</span>
    `;
  }

  // 다음 페이지 버튼
  paginationHTML += `
    <span class="pagination-item next ${currentPage === totalPages - 1 ? 'disabled' : ''}" data-page="${currentPage + 1}">
      <i class="fas fa-chevron-right"></i>
    </span>
  `;

  // HTML 삽입
  paginationContainer.innerHTML = paginationHTML;

  // 페이지네이션 클릭 이벤트 추가
  initPaginationClick();
}

/**
 * 페이지네이션 클릭 이벤트 초기화
 */
function initPaginationClick() {
  const paginationItems = document.querySelectorAll(
    '.pagination-item:not(.dots):not(.disabled)');

  paginationItems.forEach(item => {
    item.addEventListener('click', function () {
      const page = parseInt(this.getAttribute('data-page'));
      if (isNaN(page)) {
        return;
      }

      // 현재 검색어 유지
      const searchInput = document.querySelector('.search-input');
      const keyword = searchInput ? searchInput.value.trim() : '';

      // 콘텐츠 목록 다시 로드
      loadContent(currentTab, page, PAGE_SIZE, keyword);
    });
  });
}

/**
 * 콘텐츠 탭 클릭 이벤트 초기화
 */
function initTabsEvent() {
  const tabItems = document.querySelectorAll('.content-tabs .tab-item');

  tabItems.forEach(tab => {
    tab.addEventListener('click', function() {
      const tabType = this.getAttribute('data-tab');

      // 이미 활성화된 탭이면 아무것도 하지 않음
      if (currentTab === tabType) return;

      // 탭 활성화 상태 변경
      tabItems.forEach(item => item.classList.remove('active'));
      this.classList.add('active');

      // 새 콘텐츠 로드
      loadContent(tabType, 0, PAGE_SIZE);
    });
  });
}

/**
 * 공통 탭 메뉴 이벤트 초기화
 */
function initCommonTabMenu() {
  // 공통 탭 메뉴의 탭 아이템들
  const commonTabItems = document.querySelectorAll('#tab-menu-container .tab-item');
  
  if (commonTabItems.length === 0) {
    console.warn('공통 탭 메뉴 아이템을 찾을 수 없습니다.');
    return;
  }
  
  commonTabItems.forEach(tab => {
    tab.addEventListener('click', function() {
      // 이미 active 클래스가 있는 경우에는 아무 작업도 하지 않음
      if (this.classList.contains('active')) return;
      
      const page = this.getAttribute('data-page') || '';
      
      // 페이지별 URL 매핑
      const pageUrls = {
        'home': '/main/main.html',
        'original': '/original/originalContent.html',
        'board': '/board/boardList.html',
        'vote': '/vote/voteList.html',
        'column': '/column/columnList.html',
        'notice': '/notice/noticeList.html'
      };
      
      // 해당 페이지로 이동
      if (pageUrls[page]) {
        window.location.href = pageUrls[page];
      } else {
        console.warn(`알 수 없는 탭 페이지: ${page}`);
      }
    });
  });
}

/**
 * 검색 이벤트 초기화
 */
function initSearch() {
  const searchInput = document.querySelector('.search-input');
  const searchForm = document.querySelector('.search-form');

  if (searchForm) {
    searchForm.addEventListener('submit', function (e) {
      e.preventDefault();
      const keyword = searchInput.value.trim();
      loadContent(currentTab, 0, PAGE_SIZE, keyword);
    });
  }

  if (searchInput) {
    // 검색어 입력 후 엔터 키 이벤트
    searchInput.addEventListener('keypress', function (e) {
      if (e.key === 'Enter') {
        e.preventDefault();
        const keyword = this.value.trim();
        loadContent(currentTab, 0, PAGE_SIZE, keyword);
      }
    });
  }
}

/**
 * 로딩 상태 표시 함수
 * @param {boolean} isLoading - 로딩 중 여부
 */
function showLoading(isLoading) {
  const loadingState = document.getElementById('loadingState');
  if (loadingState) {
    loadingState.style.display = isLoading ? 'flex' : 'none';
  }
}

/**
 * 아이템이 새로운지 확인 (48시간 이내 생성)
 * @param {string} dateString - 날짜 문자열
 * @returns {boolean} 새로운 아이템 여부
 */
function isItemNew(dateString) {
  const itemDate = new Date(dateString);
  const now = new Date();
  const diffTime = now.getTime() - itemDate.getTime();
  const diffHours = diffTime / (1000 * 60 * 60);

  return diffHours <= 48;
}

/**
 * 날짜 포맷 함수
 * @param {string} dateString - 날짜 문자열
 * @returns {string} 포맷된 날짜
 */
function formatDate(dateString) {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');

  return `${year}.${month}.${day} ${hours}:${minutes}`;
}

/**
 * 조회수 포맷 함수
 * @param {number} views - 조회수
 * @returns {string} 포맷된 조회수
 */
function formatViews(views) {
  if (views >= 10000) {
    return `${Math.floor(views / 10000)}만`;
  } else if (views >= 1000) {
    return `${Math.floor(views / 1000)}천`;
  } else {
    return views.toString();
  }
}

/**
 * 영상 길이 포맷 함수
 * @param {number} seconds - 초 단위 길이
 * @returns {string} 포맷된 길이 (00:00)
 */
function formatDuration(seconds) {
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = Math.floor(seconds % 60);

  return `${String(minutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`;
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function () {
  // 공통 요소 로드가 완료된 후 실행할 이벤트 등록
  if (window.loadCommonComponents) {
    window.loadCommonComponents().then(() => {
      // 공통 탭 메뉴 이벤트 초기화
      initCommonTabMenu();
      
      // 콘텐츠 탭 클릭 이벤트 초기화
      initTabsEvent();
      
      // 검색 이벤트 초기화
      initSearch();
      
      // 초기 콘텐츠 로드 (기본: 동영상 탭)
      loadContent('video', 0, PAGE_SIZE);
    });
  } else {
    // fallback: 공통 컴포넌트 로더가 없는 경우 직접 실행
    initCommonTabMenu();
    initTabsEvent();
    initSearch();
    loadContent('video', 0, PAGE_SIZE);
  }
});