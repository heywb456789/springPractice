/**
 * 자유게시판 목록 페이지 스크립트
 */

document.addEventListener('DOMContentLoaded', function() {
  // 탭 메뉴 클릭 이벤트
  initTabMenu();

  // 게시글 목록 아이템 클릭 이벤트
  initBoardItemClick();

  // 글쓰기 버튼 클릭 이벤트
  initWriteButton();

  // 페이지네이션 클릭 이벤트
  initPagination();

  // 검색 이벤트
  initSearch();
});

/**
 * 탭 메뉴 초기화
 */
function initTabMenu() {
  const tabItems = document.querySelectorAll('.tab-item');

  tabItems.forEach(tab => {
    tab.addEventListener('click', function() {
      // 모든 탭 비활성화
      tabItems.forEach(item => {
        item.classList.remove('active');
      });

      // 클릭한 탭 활성화
      this.classList.add('active');

      // 여기에 탭에 따른 콘텐츠 변경 로직 추가
      console.log('탭 선택:', this.textContent);
    });
  });
}

/**
 * 게시글 목록 아이템 클릭 이벤트 초기화
 */
function initBoardItemClick() {
  const boardItems = document.querySelectorAll('.board-item');

  boardItems.forEach(item => {
    item.addEventListener('click', function() {
      const title = this.querySelector('.board-title').textContent;
      console.log('게시글 클릭:', title);

      // 상세 페이지로 이동
      window.location.href = 'board-detail.html';
    });
  });
}

/**
 * 글쓰기 버튼 클릭 이벤트 초기화
 */
function initWriteButton() {
  const writeButton = document.querySelector('.write-button');

  if (writeButton) {
    writeButton.addEventListener('click', function() {
      console.log('글쓰기 버튼 클릭');

      // 글쓰기 페이지로 이동
      window.location.href = 'board-write.html';
    });
  }
}

/**
 * 페이지네이션 클릭 이벤트 초기화
 */
function initPagination() {
  const paginationItems = document.querySelectorAll('.pagination-item');

  paginationItems.forEach(item => {
    item.addEventListener('click', function() {
      // 특수 항목(이전, 다음, ...) 처리
      if (this.classList.contains('dots')) {
        return; // 점(...)은 클릭 불가
      }

      // 현재 활성화된 페이지 아이템
      const currentActive = document.querySelector('.pagination-item.active');
      if (currentActive) {
        currentActive.classList.remove('active');
      }

      // 이전 버튼 클릭
      if (this.classList.contains('prev')) {
        const currentPage = parseInt(currentActive.textContent);
        if (currentPage > 1) {
          // 이전 페이지 활성화
          const prevPage = document.querySelector(`.pagination-item:not(.prev):not(.next):not(.dots):nth-child(${currentPage})`);
          if (prevPage) {
            prevPage.classList.add('active');
          }
        }
        return;
      }

      // 다음 버튼 클릭
      if (this.classList.contains('next')) {
        const currentPage = parseInt(currentActive.textContent);
        // 다음 페이지 활성화
        const nextPage = document.querySelector(`.pagination-item:not(.prev):not(.next):not(.dots):nth-child(${currentPage + 2})`);
        if (nextPage) {
          nextPage.classList.add('active');
        }
        return;
      }

      // 일반 페이지 번호 클릭
      this.classList.add('active');

      // 여기에 페이지 로드 로직 추가
      console.log('페이지 선택:', this.textContent);
    });
  });
}

/**
 * 검색 이벤트 초기화
 */
function initSearch() {
  const searchInput = document.querySelector('.search-input');

  if (searchInput) {
    // 검색어 입력 후 엔터 키 이벤트
    searchInput.addEventListener('keypress', function(e) {
      if (e.key === 'Enter') {
        e.preventDefault();
        const searchTerm = this.value.trim();
        if (searchTerm) {
          console.log('검색어:', searchTerm);
          performSearch(searchTerm);
        }
      }
    });
  }
}

/**
 * 검색 수행 함수
 * @param {string} searchTerm - 검색어
 */
function performSearch(searchTerm) {
  // 여기에 실제 검색 로직 구현
  console.log(`"${searchTerm}" 검색 중...`);

  // 예시: 검색 결과를 가져오는 API 호출
  // fetchSearchResults(searchTerm).then(results => {
  //   updateBoardList(results);
  // });

  // 임시 알림 (실제 구현 시 제거)
  alert(`"${searchTerm}" 검색 결과입니다.`);
}