import {
  optionalAuthFetch,
  handleFetchError,
  authFetch, FetchError
} from '../commonFetch.js';

/**
 * 게시판 목록을 API에서 불러와 동적으로 구성하는 함수
 * @param {number} page - 페이지 번호 (기본값: 1)
 * @param {number} size - 한 페이지당 게시글 수 (기본값: 10)
 * @param {string} keyword - 검색어 (기본값: 빈 문자열)
 */
async function loadBoardList(page = 0, size = 10, keyword = '') {
  // 로딩 상태 표시
  const boardListContainer = document.querySelector('.board-list');
  if (!boardListContainer) {
    return;
  }

  // 로딩 상태 표시
  boardListContainer.innerHTML = `
  <div class="loading-state">
    <div class="loading-animation">
      <svg width="50" height="50" viewBox="0 0 50 50">
        <circle cx="25" cy="25" r="20" fill="none" stroke="#e0e0e0" stroke-width="4"></circle>
        <circle cx="25" cy="25" r="20" fill="none" stroke="#4263eb" stroke-width="4" stroke-dasharray="125" stroke-dashoffset="125" stroke-linecap="round">
          <animate attributeName="stroke-dashoffset" values="125;0" dur="1.5s" repeatCount="indefinite" />
        </circle>
      </svg>
    </div>
    <p class="loading-text">게시글을 불러오고 있습니다</p>
    <p class="loading-subtext">잠시만 기다려주세요...</p>
  </div>`;

  const apiUrl = `/api/board/posts?page=${page}&size=${size}` +
    (keyword ? `&searchType=BOARD_TITLE_CONTENT&searchText=${encodeURIComponent(keyword)}` : '');

  try {
    const res = await optionalAuthFetch(apiUrl);
    const data = await res.json();

    // 페이지네이션 정보 업데이트
    updatePagination(data.response.pagination);

    // 게시글 목록이 없는 경우
    if (!data.response.data || data.response.data.length === 0) {
      // 게시글이 없는 경우
      boardListContainer.innerHTML = `
        <div class="empty-state">
          <div class="empty-icon">
            <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M19 5V19H5V5H19ZM21 3H3V21H21V3Z" fill="#adb5bd"/>
              <path d="M14 17H6V15H14V17Z" fill="#adb5bd"/>
              <path d="M14 13H6V11H14V13Z" fill="#adb5bd"/>
              <path d="M18 9H6V7H18V9Z" fill="#adb5bd"/>
            </svg>
          </div>
          <p class="empty-title">게시글이 없습니다</p>
          <p class="empty-message">첫 번째 게시글을 작성해 보세요</p>
          <button class="write-post-button" onclick="window.location.href='boardWrite.html'">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M19 13H13V19H11V13H5V11H11V5H13V11H19V13Z" fill="white"/>
            </svg>
            새 글 작성하기
          </button>
        </div>`;
      return;
    }

    // 게시글 목록 HTML 생성
    const boardListHTML = data.response.data
    .map(item => createBoardItemHTML(item))
    .join('');

    // 생성된 HTML을 컨테이너에 삽입
    boardListContainer.innerHTML = boardListHTML;

    // 게시글 클릭 이벤트 연결
    initBoardItemClick();

  } catch (err) {
    handleFetchError(err);

    // 오류 메시지 표시
    boardListContainer.innerHTML = `
        <div class="error-state">
          <div class="error-icon">
            <svg width="60" height="60" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 4C7.58172 4 4 7.58172 4 12C4 16.4183 7.58172 20 12 20C16.4183 20 20 16.4183 20 12C20 7.58172 16.4183 4 12 4Z" stroke="#ff6b6b" stroke-width="2"/>
              <path d="M12 8V13" stroke="#ff6b6b" stroke-width="2" stroke-linecap="round"/>
              <circle cx="12" cy="16" r="1" fill="#ff6b6b"/>
            </svg>
          </div>
          <p class="error-title">게시글을 불러오는 데 실패했습니다</p>
          <p class="error-message">네트워크 연결을 확인하시거나 잠시 후 다시 시도해 주세요</p>
          <button class="retry-button">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4C7.58 4 4.01 7.58 4.01 12C4.01 16.42 7.58 20 12 20C15.73 20 18.84 17.45 19.73 14H17.65C16.83 16.33 14.61 18 12 18C8.69 18 6 15.31 6 12C6 8.69 8.69 6 12 6C13.66 6 15.14 6.69 16.22 7.78L13 11H20V4L17.65 6.35Z" fill="#4263eb"/>
            </svg>
            다시 시도하기
          </button>
        </div>
      `;

    const retryButton = boardListContainer.querySelector('.retry-button');
    if (retryButton) {
      retryButton.addEventListener('click',
        () => loadBoardList(page, size, keyword));        // 🚀 수정됨: 파라미터 이름統一(page, size, keyword)
    }
  }
}

/**
 * 게시글 아이템 HTML 생성 함수
 * @param {Object} item - 게시글 데이터
 * @returns {string} 게시글 HTML
 */
function createBoardItemHTML(item) {
  // 배지 HTML 생성 함수
  const getBadgeHTML = () => {
    let badges = '';
    if (item.new) {
      badges += `<span class="badge new">N</span>`;
    }
    if (item.hot) {
      badges += `<span class="badge hot">H</span>`;
    }
    if (item.best) {
      badges += `<span class="badge best">B</span>`;
    }
    return badges;
  };

  // 게시글 HTML 생성 (백틱 사용)
  return `
    <div class="board-item" data-id="${item.boardId}">
      <div class="board-content">
        <div class="board-title">${item.title}</div>
        <div class="board-info">
          ${getBadgeHTML()}
          <span class="board-comments">${item.commentCount}</span>
          <span class="board-views">${item.views}</span>
        </div>
      </div>
    </div>
  `;
}

/**
 * 페이지네이션 업데이트 함수
 * @param {Object} pagination - 페이지네이션 데이터
 */
function updatePagination(pagination) {
  const paginationContainer = document.querySelector('.pagination');
  if (!paginationContainer) {
    return;
  }

  // 현재 페이지
  const currentPage = pagination.currentPage;

  // 전체 페이지 수
  const totalPages = pagination.totalPages;

  // 표시할 페이지 버튼 개수
  const displayPageCount = 5;

  // 시작 페이지와 끝 페이지 계산
  let startPage = Math.max(1, currentPage - Math.floor(displayPageCount / 2));
  let endPage = Math.min(totalPages, startPage + displayPageCount - 1);

  // 시작 페이지 재조정
  if (endPage - startPage < displayPageCount - 1) {
    startPage = Math.max(1, endPage - displayPageCount + 1);
  }

  // 페이지네이션 HTML 생성
  let paginationHTML = `
    <span class="pagination-item prev ${currentPage === 1 ? 'disabled'
      : ''}" data-page="${currentPage - 1}">
      <i class="fas fa-chevron-left"></i>
    </span>
  `;

  // 첫 페이지가 1이 아닌 경우, 1 페이지 버튼 추가
  if (startPage > 1) {
    paginationHTML += `
      <span class="pagination-item" data-page="1">1</span>
      ${startPage > 2 ? '<span class="pagination-item dots">...</span>' : ''}
    `;
  }

  // 페이지 버튼 생성
  for (let i = startPage; i <= endPage; i++) {
    paginationHTML += `
      <span class="pagination-item ${i === currentPage ? 'active'
        : ''}" data-page="${i}">${i}</span>
    `;
  }

  // 마지막 페이지가 totalPages가 아닌 경우, 마지막 페이지 버튼 추가
  if (endPage < totalPages) {
    paginationHTML += `
      ${endPage < totalPages - 1
        ? '<span class="pagination-item dots">...</span>' : ''}
      <span class="pagination-item" data-page="${totalPages}">${totalPages}</span>
    `;
  }

  // 다음 페이지 버튼
  paginationHTML += `
    <span class="pagination-item next ${currentPage === totalPages ? 'disabled'
      : ''}" data-page="${currentPage + 1}">
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

      // 게시글 목록 다시 로드
      loadBoardList(page - 1, 10, keyword);
    });
  });
}

/**
 * 게시글 아이템 클릭 이벤트 초기화
 */
function initBoardItemClick() {
  const boardItems = document.querySelectorAll('.board-item');

  boardItems.forEach(item => {
    item.addEventListener('click', function () {
      const boardId = this.getAttribute('data-id');
      if (!boardId) {
        return;
      }

      // 상세 페이지로 이동
      window.location.href = `/board/boardDetail.html?id=${boardId}`;
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
      loadBoardList(0, 10, keyword);
    });
  }

  if (searchInput) {
    // 검색어 입력 후 엔터 키 이벤트
    searchInput.addEventListener('keypress', function (e) {
      if (e.key === 'Enter') {
        e.preventDefault();
        const keyword = this.value.trim();
        loadBoardList(0, 10, keyword);
      }
    });
  }
}

async function loadBoardWrite(){
  try {
    // 인증 상태 확인 API 호출 (authFetch 사용)
    await authFetch('/api/auth/validate', {method: 'GET'});
    window.location.href = 'boardWrite.html';
  } catch (err) {
    if (err instanceof FetchError && err.httpStatus === 401) {
      alert('로그인이 필요합니다.');
      window.location.href = '/login/login.html';
    } else {
      handleFetchError(err);
    }
  }
}

// 페이지 로드 시 게시글 목록 초기화
document.addEventListener('DOMContentLoaded', function () {
  // 게시글 목록 로드
  loadBoardList();

  // 검색 이벤트 초기화
  initSearch();

  // 글쓰기 버튼 이벤트 초기화
  const writeButton = document.querySelector('.write-button');
  if (writeButton) {
    writeButton.addEventListener('click', function () {

      loadBoardWrite();
    });
  }
});