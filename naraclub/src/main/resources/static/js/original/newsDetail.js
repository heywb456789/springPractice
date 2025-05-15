import {
  optionalAuthFetch,
  authFetch,
  handleFetchError,
  FetchError,
  getUserId
} from '../commonFetch.js';

// 전역 변수
let newsData = null;
let shareModal = null;

// 문서 로드 완료 시 실행
document.addEventListener('DOMContentLoaded', function () {
  // 뒤로가기 버튼 초기화
  initBackButton();
  // 뉴스 데이터 로드
  loadNewsData();
  // 공유 버튼 초기화
  initShareFeatures();   // ← 공유 기능 초기화
  // 재시도 버튼 이벤트 초기화
  initRetryButton();
  // 댓글 영역 초기화
  initComments();
});

/**
 * 뒤로가기 버튼 초기화
 */
function initBackButton() {
  const backButton = document.getElementById('backButton');
  if (backButton) {
    backButton.addEventListener('click', function () {
      // 이전 페이지로 이동 (히스토리가 있는 경우)
      if (window.history.length > 1) {
        window.history.back();
      } else {
        // 히스토리가 없는 경우 목록 페이지로 이동
        sessionStorage.setItem('currentContentTab', 'article');
        window.location.href = '/original/originalContent.html';
      }
    });
  }
}

/**
 * URL에서 뉴스 ID 추출
 */
function getNewsIdFromUrl() {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('id');
}


/**
 * 재시도 버튼 초기화
 */
function initRetryButton() {
  const retryButton = document.getElementById('retryButton');
  if (retryButton) {
    retryButton.addEventListener('click', loadNewsData);
  }
}

/**
 * 뉴스 데이터 로드
 */
async function loadNewsData() {
  const newsId = getNewsIdFromUrl();
  const loadingElement = document.getElementById('loadingState');
  const errorElement = document.getElementById('errorState');
  const newsDetailContainer = document.getElementById('newsDetailContainer');

  if (!newsId) {
    console.error('뉴스 ID가 없습니다.');
    showError('잘못된 접근입니다.');
    return;
  }

  // 로딩 상태 표시
  loadingElement && (loadingElement.style.display = 'flex');
  errorElement && (errorElement.style.display = 'none');
  newsDetailContainer && (newsDetailContainer.style.display = 'none');

  try {
    const response = await optionalAuthFetch(`/api/news/${newsId}`);
    const data = await response.json();

    if (!data?.response) {
      throw new Error('Invalid news data');
    }

    newsData = data.response;
    updateNewsUI(newsData);

    loadingElement && (loadingElement.style.display = 'none');
    newsDetailContainer && (newsDetailContainer.style.display = 'block');
  } catch (err) {
    console.error('뉴스 데이터 로드 오류:', err);
    handleFetchError(err);
    showError('뉴스 정보를 불러오는 데 실패했습니다.');
  }
}

/**
 * 관련 뉴스 로드
 */
async function loadRelatedNews(newsId) {
  const relatedNewsContainer = document.getElementById('relatedNewsContainer');
  const relatedNewsSection = document.getElementById('relatedNewsSection');

  if (!relatedNewsContainer || !relatedNewsSection) {
    return;
  }

  try {
    const response = await optionalAuthFetch(
        `/api/news/${newsId}/related?limit=5`);  // 🚀 수정됨
    if (response.status === 204) {
      relatedNewsSection.style.display = 'none';
      return;
    }

    const data = await response.json();
    if (!data.response?.data.length) {
      relatedNewsSection.style.display = 'none';
      return;
    }

    relatedNewsContainer.innerHTML = createRelatedNewsHTML(data.response.data);
    initRelatedNewsClick();
  } catch (err) {
    console.error('관련 뉴스 로드 오류:', err);
    handleFetchError(err);
    relatedNewsSection.style.display = 'none';
  }
}

/**
 * 관련 뉴스 HTML 생성
 */
function createRelatedNewsHTML(items) {
  return items.map(item => {
    const hasImage = item.thumbnailUrl && item.thumbnailUrl.trim() !== '';

    return `
      <div class="related-news-item" data-id="${item.id || item.newsId}">
        ${hasImage
        ? `<img src="${item.thumbnailUrl}" alt="${item.title}" class="related-news-image">`
        : ''}
        <div class="related-news-info">
          <h4 class="related-news-title">${item.title}</h4>
          <span class="related-news-date">${formatDate(
        item.publishedAt || item.createdAt)}</span>
        </div>
      </div>
    `;
  }).join('');
}

/**
 * 관련 뉴스 클릭 이벤트 초기화
 */
function initRelatedNewsClick() {
  const relatedNewsItems = document.querySelectorAll('.related-news-item');

  relatedNewsItems.forEach(item => {
    item.addEventListener('click', function () {
      const newsId = this.getAttribute('data-id');
      if (!newsId) {
        return;
      }

      // 현재 페이지 URL 업데이트
      window.location.href = `/news/newsDetail.html?id=${newsId}`;
    });
  });
}

/**
 * 뉴스 UI 업데이트
 */
function updateNewsUI(data) {
  // 페이지 타이틀 업데이트
  document.title = `${data.title} - 뉴스토마토`;

  // 뉴스 카테고리 업데이트
  const categoryElement = document.getElementById('newsCategory');
  if (categoryElement) {
    categoryElement.textContent = getCategoryDisplayName(data.category);
  }

  // 뉴스 제목 업데이트
  const titleElement = document.getElementById('newsTitle');
  if (titleElement) {
    titleElement.textContent = data.title;
  }

  // 작성자 업데이트
  const authorElement = document.getElementById('newsAuthor');
  if (authorElement) {
    authorElement.textContent = data.author || '뉴스토마토';
  }

  // 날짜 업데이트
  const dateElement = document.getElementById('newsDate');
  if (dateElement) {
    dateElement.textContent = formatDate(data.publishedAt || data.createdAt,
        true);
  }

  // 조회수 업데이트
  const viewsElement = document.getElementById('newsViews');
  if (viewsElement) {
    viewsElement.textContent = `조회 ${formatNumber(data.viewCount || 0)}`;
  }

  // 뉴스 내용 업데이트
  const contentElement = document.getElementById('newsContent');
  if (contentElement) {
    if (data.content) {
      // 컨텐츠 HTML을 적용하기 전에 보안 강화 (XSS 방지)
      // 아래는 기본적인 구현으로, 실제 환경에서는 보다 강력한 보안 라이브러리 사용 권장
      contentElement.innerHTML = sanitizeHtml(data.content);
    } else {
      contentElement.innerHTML = '<p>내용이 없습니다.</p>';
    }

    // 이미지 로딩 에러 처리
    const images = contentElement.querySelectorAll('img');
    images.forEach(img => {
      img.onerror = function () {
        this.style.display = 'none';
      };

      // 이미지에 loading="lazy" 속성 추가
      img.setAttribute('loading', 'lazy');
    });

    // 외부 링크에 target="_blank" 및 rel 속성 추가
    const links = contentElement.querySelectorAll('a');
    links.forEach(link => {
      if (link.host !== window.location.host) {
        link.setAttribute('target', '_blank');
        link.setAttribute('rel', 'noopener noreferrer');
      }
    });
  }
}

/**
 * HTML 내용에서 위험한 스크립트 등을 제거
 */
function sanitizeHtml(html) {
  // 아주 기본적인 구현 예시입니다. 실제로는 DOMPurify 같은 라이브러리 사용을 권장합니다.
  const temp = document.createElement('div');
  temp.innerHTML = html;

  // 스크립트 태그 제거
  const scripts = temp.querySelectorAll('script');
  scripts.forEach(script => script.remove());

  // 위험한 이벤트 속성(on*)들 제거
  const elements = temp.querySelectorAll('*');
  elements.forEach(el => {
    Array.from(el.attributes).forEach(attr => {
      if (attr.name.startsWith('on')) {
        el.removeAttribute(attr.name);
      }
    });
  });

  return temp.innerHTML;
}

/**
 * 에러 메시지 표시
 */
function showError(message) {
  const loadingElement = document.getElementById('loadingState');
  const errorElement = document.getElementById('errorState');
  const errorMessageElement = errorElement?.querySelector('.error-message');
  const newsDetailContainer = document.getElementById('newsDetailContainer');

  if (loadingElement) {
    loadingElement.style.display = 'none';
  }
  if (newsDetailContainer) {
    newsDetailContainer.style.display = 'none';
  }

  if (errorElement) {
    errorElement.style.display = 'flex';
    if (errorMessageElement && message) {
      errorMessageElement.textContent = message;
    }
  }
}

/**
 * 공유 기능 초기화: 모달 열기 & 버튼 연결
 */
function initShareFeatures() {
  // Bootstrap Modal 인스턴스
  shareModal = new bootstrap.Modal(document.getElementById('shareModal'));

  document.getElementById('shareButton')?.addEventListener('click', () => {
    shareModal.show();
    setupKakaoShare();
  });
  document.getElementById('copyUrl')?.addEventListener('click', () => {
    copyCurrentUrl();
  });
  // 통통 공유 버튼 이벤트 추가
  document.getElementById('shareTongtong')?.addEventListener('click', () => {
    shareTongtongApp();
  });

  document.getElementById('shareX')?.addEventListener('click', () => {
    shareToX();
  });
}

/**
 * 통통 앱 공유 함수
 */
function shareTongtongApp() {
  const newsId = getNewsIdFromUrl();
  const shareUrl = `https://www.xn--w69at2fhshwrs.kr/share/news/${newsId}`;

  // 모바일 기기 확인
  const userAgent = navigator.userAgent.toLowerCase();
  let appScheme = '';
  let storeUrl = '';

  // iOS 기기 확인
  if (/iphone|ipad|ipod/.test(userAgent)) {
    appScheme = `tongtongios://tongtongiOS?url=${encodeURIComponent(shareUrl)}`;
    storeUrl = 'https://apps.apple.com/kr/app/통통-암호화-메신저/id982895719';
  }
  // 안드로이드 기기 확인
  else if (/android/.test(userAgent)) {
    appScheme = `tongtong://m.etomato.com?url=${encodeURIComponent(shareUrl)}`;
    storeUrl = 'https://play.google.com/store/apps/details?id=tomato.solution.tongtong';
  }
  // 데스크톱 또는 기타 기기
  else {
    navigator.clipboard.writeText(shareUrl)
      .then(() => {
        alert('URL이 클립보드에 복사되었습니다. 통통 앱에서 공유해주세요.');
      })
      .catch(err => {
        console.error('클립보드 복사 오류:', err);
        alert('복사에 실패했습니다.');
      });
    shareModal.hide();
    return;
  }

  // URL 자동 복사 (앱으로 전환되기 전에)
  navigator.clipboard.writeText(shareUrl)
    .then(() => {
      console.log('URL이 클립보드에 복사되었습니다.');
    })
    .catch(err => {
      console.error('클립보드 복사 오류:', err);
    });

  // 앱 실행 시도 시간
  const appCheckTimeout = 1500;
  const now = Date.now();

  // 앱 스킴 호출
  window.location.href = appScheme;

  // 앱 실행 확인
  setTimeout(function() {
    // 페이지가 숨겨지지 않았다면 (앱이 실행되지 않았다면)
    if (document.hidden === false && Date.now() - now > appCheckTimeout) {
      if (confirm('통통 앱이 설치되어 있지 않은 것 같습니다. 앱 스토어로 이동하시겠습니까?')) {
        window.location.href = storeUrl;
      }
    }
    shareModal.hide();
  }, appCheckTimeout + 500);
}

/**
 * 카카오톡 기본공유 버튼 생성
 */
async function setupKakaoShare() {
  const id = getNewsIdFromUrl();
  const userId = await getUserId();
  const title = document.getElementById('newsTitle')?.textContent || '뉴스 공유';
  const description = '';
  const shareUrl = `https://www.xn--w69at2fhshwrs.kr/share/news/${id}`;
  const imageUrl = document.querySelector('.news-thumbnail img')?.src || 'https://image.newstomato.com/newstomato/club/share/news.png';

  const container = document.getElementById('kakaotalk-sharing-btn');
  container.innerHTML = `
    <img src="/images/kakao.svg" alt="카카오톡" width="32"/>
    <span class="share-label">카카오톡</span>
  `;

  if (window.Kakao && Kakao.isInitialized()) {
    Kakao.Share.createDefaultButton({
      container: '#kakaotalk-sharing-btn',
      objectType: 'feed',
      content: {
        title,
        description,
        imageUrl,
        link: { mobileWebUrl: shareUrl, webUrl: shareUrl }
      },
      serverCallbackArgs: {
        type: 'news',      // 'board' | 'vote' | 'news' | 'video'
        id: id,        // 게시물 PK
        userId: userId // 로그인한 회원 ID
      }
    });
  } else {
    Kakao.init('a1c1145bbd0ca5e22d5b2c996a8aa32a');
    Kakao.Share.createDefaultButton({
      container: '#kakaotalk-sharing-btn',
      objectType: 'feed',
      content: {
        title,
        description,
        imageUrl,
        link: { mobileWebUrl: shareUrl, webUrl: shareUrl }
      },
      serverCallbackArgs: {
        type: 'news',      // 'board' | 'vote' | 'news' | 'video'
        id: id,        // 게시물 PK
        userId: userId // 로그인한 회원 ID
      }
    });
  }
}

/**
 * URL 복사 후 모달 닫기
 */
function copyCurrentUrl() {
  const newsId = getNewsIdFromUrl();
  const shareUrl = `https://www.xn--w69at2fhshwrs.kr/share/news/${newsId}`;
  navigator.clipboard.writeText(shareUrl)
  .then(() => {
    alert('URL이 클립보드에 복사되었습니다.');
    shareModal.hide();
  })
  .catch(err => {
    console.error('클립보드 복사 오류:', err);
    alert('복사에 실패했습니다.');
  });
}

/**
 * X(구 Twitter) 공유 함수
 */
async function shareToX() {
  const title = document.querySelector('.post-title')?.textContent.trim() || '';
  const postId = getNewsIdFromUrl();
  const shareUrl = `https://www.xn--w69at2fhshwrs.kr/share/news/${postId}`;

  try {
    const res = await authFetch('/twitter/share', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title: title,
        shareUrl: shareUrl,
        type: 'NEWS',
        targetId: postId
      })
    });
    await res.json(); // 서버 메시지 무시해도 OK
    alert('X에 성공적으로 공유되었습니다!');
  } catch (err) {
    if (err instanceof FetchError && err.httpStatus === 401) {
      alert('트위터 연동이 필요합니다.');
      window.location.href = '/mypage/mypage.html';
    } else {
      console.error('트위터 공유 실패:', err);
      handleFetchError(err);
    }
  } finally {
    shareModal.hide();
  }
}

/**
 * 카테고리 코드를 표시용 이름으로 변환
 */
function getCategoryDisplayName(category) {
  const categoryMap = {
    'ECONOMY': '경제',
    'STOCK': '주식',
    'POLITICS': '정치',
    'SOCIETY': '사회',
    'GLOBAL': '국제',
    'TECH': 'IT/과학',
    'CULTURE': '문화',
    'SPORTS': '스포츠'
  };

  return categoryMap[category] || '뉴스';
}

/**
 * 날짜 포맷 함수
 * @param {string} dateString - 날짜 문자열
 * @param {boolean} detail - 상세 시간 표시 여부
 * @returns {string} 포맷된 날짜
 */
function formatDate(dateString, detail = false) {
  if (!dateString) {
    return '';
  }

  const date = new Date(dateString);
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  const diffMin = Math.floor(diffMs / (1000 * 60));
  const diffHour = Math.floor(diffMs / (1000 * 60 * 60));
  const diffDay = Math.floor(diffMs / (1000 * 60 * 60 * 24));

  if (!detail) {
    // 간단한 형식 (관련 뉴스용)
    if (diffMin < 60) {
      return `${diffMin}분 전`;
    } else if (diffHour < 24) {
      return `${diffHour}시간 전`;
    } else if (diffDay < 7) {
      return `${diffDay}일 전`;
    } else {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}.${month}.${day}`;
    }
  } else {
    // 상세 형식 (뉴스 상세페이지용)
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    return `${year}.${month}.${day} ${hour}:${minute}`;
  }
}

/**
 * 숫자 포맷 (천 단위 콤마)
 */
function formatNumber(num) {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

/**
 * 댓글 영역 초기화
 */
function initComments() {
  const newsId = getNewsIdFromUrl();
  const container = document.querySelector('.comments-section');
  const list = document.getElementById('commentsContainer');
  const noCommentsEl = document.getElementById('noComments');
  const input = document.getElementById('commentInput');
  const submitBtn = document.getElementById('commentSubmitButton');
  const pageSize = 10;
  let page = 0;
  let loading = false;
  let done = false;

  // 초기 로드
  loadComments();

  // 스크롤 이벤트: 상단 근처에서 추가 로드
  if (container) {
    container.addEventListener('scroll', () => {
      if (!loading && !done && container.scrollTop < 30) {
        const prevHeight = container.scrollHeight;
        loadComments().then(() => {
          // 로드 후 스크롤 위치 보정
          const addedHeight = container.scrollHeight - prevHeight;
          container.scrollTop += addedHeight;
        });
      }
    });
  }

  // 댓글 등록
  if (submitBtn && input) {
    submitBtn.addEventListener('click', () => {
      const content = input.value.trim();
      if (!content) {
        return;
      }
      submitComment(content);
      input.value = '';
    });

    // 엔터 키 제출
    input.addEventListener('keypress', function (e) {
      if (e.key === 'Enter') {
        e.preventDefault();
        const content = this.value.trim();
        if (!content) {
          return;
        }
        submitComment(content);
        this.value = '';
      }
    });
  }

  // 댓글 목록 로드 (인증 선택)
  async function loadComments() {
    if (!newsId || !list) {
      return;
    }

    loading = true;
    try {
      const res = await optionalAuthFetch(
          `/api/news/${newsId}/comments?page=${page}&size=${pageSize}`
      );

      if (!res.ok) {
        if (res.status === 204) {
          if (page === 0 && noCommentsEl) {
            noCommentsEl.style.display = 'block';
          }
          done = true;
          return;
        }
        throw new Error(`HTTP error! status: ${res.status}`);
      }

      const data = await res.json();
      const items = data.response.data || [];

      if (items.length === 0) {
        if (page === 0) {
          list.innerHTML = '';
          if (noCommentsEl) {
            noCommentsEl.style.display = 'block';
          }
        }
        done = true;
      } else {
        if (noCommentsEl) {
          noCommentsEl.style.display = 'none';
        }
        items.forEach(
            c => list.insertAdjacentHTML('afterbegin', renderComment(c)));
        page++;
      }

    } catch (error) {
      // console.error('댓글 로드 오류:', error);
      // handleFetchError(err);
      if (list) {
        list.innerHTML = '<p class="error-message">댓글을 불러오는 데 실패했습니다.</p>';
      }
      if (noCommentsEl) {
        noCommentsEl.style.display = 'none';
      }
      done = true;
    } finally {
      loading = false;
    }
  }

  // 댓글 등록 (인증 필수)
  async function submitComment(content) {
    const newsId = getNewsIdFromUrl();
    try {
      const res = await authFetch(
          `/api/news/${newsId}/comments`,
          {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({content})
          }
      );
      const {response: comment} = await res.json();
      list.insertAdjacentHTML('beforeend', renderComment(comment));
      noCommentsEl.style.display = 'none';
    } catch (err) {
      if (err instanceof FetchError && err.httpStatus === 401) {
        alert('댓글 등록을 위해 로그인 후 이용해주세요.');
        return window.location.href = '/login/login.html';
      }
      handleFetchError(err);
    }
  }

  // 수정/삭제 (인증 필수)
  if (list) {
    list.addEventListener('click', async e => {
      const item = e.target.closest('.comment-item');
      if (!item) {
        return;
      }

      const id = item.dataset.id;

      if (e.target.classList.contains('delete-btn')) {
        try {
          await authFetch(
              `/api/news/${newsId}/comments/${id}`,
              {method: 'DELETE'}
          );
          item.remove();

        } catch (err) {
          if (err instanceof FetchError && err.httpStatus === 401) {
            alert('로그인이 필요합니다.');
            location.href = '/login/login.html';
          } else {
            handleFetchError(err);
          }
        }
      } else if (e.target.classList.contains('edit-btn')) {
        const p = item.querySelector('.content');
        if (p.isContentEditable) {
          p.contentEditable = false;
          try {
            await authFetch(
                `/api/news/${newsId}/comments/${id}`,
                {
                  method: 'PUT',
                  headers: {
                    'Content-Type': 'application/json'
                  },
                  body: JSON.stringify({content: p.textContent.trim()})
                }
            );
          } catch (error) {
            if (err instanceof FetchError && err.httpStatus === 401) {
              alert('로그인이 필요합니다.');
              location.href = '/login/login.html';
            } else {
              handleFetchError(err);
            }
          }
        } else {
          p.contentEditable = true;
          p.focus();
        }
      }
    });
  }

  // 댓글 HTML 생성
  function renderComment(c) {
    return `
      <div class="comment-item" data-id="${c.commentId}">
        <div>
          <strong>${c.authorName}</strong>
          <small>${new Date(c.createdAt).toLocaleString('ko-KR')}</small>
          <p class="content">${c.content}</p>
        </div>
        ${c.mine ? `
          <div class="comment-actions">
            <i class="fas fa-pen edit-btn"></i>
            <i class="fas fa-times delete-btn"></i>
          </div>
        ` : ''}
      </div>
    `;
  }
}