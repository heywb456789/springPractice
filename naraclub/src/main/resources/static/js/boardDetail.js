/**
 * 게시글 상세 페이지 스크립트
 */

import {handleTokenRefresh} from './common.js'
document.addEventListener('DOMContentLoaded', function () {
  // 뒤로가기 버튼 이벤트
  initBackButton();

  // 액션 버튼 이벤트 (좋아요, 공유 등)
  initActionButtons();

  // 게시글 데이터 로드
  loadPostData();
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
        window.location.href = 'boardList.html';
      }
    });
  }
}

/**
 * 액션 버튼 이벤트 초기화 (좋아요, 공유 등)
 */
function initActionButtons() {
  // 좋아요 버튼
  const likeButton = document.querySelector('.like-button');
  if (likeButton) {
    likeButton.addEventListener('click', () => toggleLike(likeButton));
  }

  // 공유 버튼
  const shareButton = document.querySelector('.share-button');
  if (shareButton) {
    shareButton.addEventListener('click', sharePost);
  }
}

/**
 * 게시글 공유 함수
 */
function sharePost() {
  // 현재 페이지 URL
  const shareUrl = window.location.href;
  const title = document.querySelector('.post-title')?.textContent || '게시글 공유';

  // 웹 공유 API가 지원되는 경우
  if (navigator.share) {
    navigator.share({
      title: title,
      url: shareUrl
    })
    .then(() => console.log('공유 성공'))
    .catch((error) => console.log('공유 실패:', error));
  } else {
    // 지원되지 않는 경우 URL 복사
    navigator.clipboard.writeText(shareUrl)
    .then(() => {
      alert('게시글 주소가 복사되었습니다.');
    })
    .catch(err => {
      console.error('URL 복사 실패:', err);
      // 대체 방법: 임시 요소 생성하여 복사
      const tempInput = document.createElement('input');
      document.body.appendChild(tempInput);
      tempInput.value = shareUrl;
      tempInput.select();
      document.execCommand('copy');
      document.body.removeChild(tempInput);
      alert('게시글 주소가 복사되었습니다.');
    });
  }
}

/**
 * URL에서 게시글 ID 추출
 * @returns {string} 게시글 ID
 */
function getPostIdFromUrl() {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('id');
}

/**
 * 게시글 데이터 로드
 */

async function loadPostData() {
  const postId = getPostIdFromUrl();

  if (!postId) {
    console.error('게시글 ID를 찾을 수 없습니다.');
    return;
  }

  const url = `/api/board/posts/${postId}`

  // 실제 요청 로직
  async function getPostDetail() {
    return fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
      },
      credentials: 'include' // if your API uses cookies
    });
  }

  try {
    let res = await getPostDetail();

    // 1차 호출에서 401이면, refresh 시도 후 재호출
    if (res.status === 401) {
      try {
        await handleTokenRefresh();
      } catch (refreshErr) {
        throw new Error('세션이 만료되어 다시 로그인해주세요.');
      }
      // 재호출
      res = await getPostDetail();
    }
    if (!res.ok) {
      throw new Error(`status ${res.status}`);
    }
    const data = await res.json();

    updatePostUI(data.response);
    // 좋아요 초기 상태
    const likeButton = document.querySelector('.like-button');
    if (data.response.like) {
      likeButton.classList.add('active');
    } else {
      likeButton.classList.remove('active');
    }
  } catch (err) {
    console.error('게시글 로드 오류:', err);
    alert('게시글을 불러오는 중 오류가 발생했습니다.');
  }
}

/**
 * 게시글 UI 업데이트
 * @param {Object} postData - 게시글 데이터
 */
function updatePostUI(postData) {
  // 제목 업데이트
  const titleElement = document.querySelector('.post-title');
  if (titleElement) {
    titleElement.textContent = postData.title;
  }

  // 작성자 및 날짜 업데이트
  const postMetaElement = document.querySelector('.post-meta');
  if (postMetaElement) {
    const formattedDate = new Date(postData.createdAt).toLocaleString('ko-KR');
    postMetaElement.innerHTML = `<span class="post-author">${postData.authorName} | ${formattedDate}</span>`;
  }

  // 본문 내용 업데이트
  const contentElement = document.querySelector('.post-content');
  if (contentElement) {
    // 텍스트 내용
    contentElement.innerHTML = '';

    // 본문 텍스트 추가
    if (postData.content) {
      const paragraph = document.createElement('p');
      paragraph.textContent = postData.content;
      contentElement.appendChild(paragraph);
    }

    // 이미지가 있는 경우 추가
    if (postData.imageUrls && postData.imageUrls.length > 0) {
      postData.imageUrls.forEach(imageUrl => {
        const imageContainer = document.createElement('div');
        imageContainer.className = 'post-image-container';

        const image = document.createElement('img');
        image.src = imageUrl;
        image.className = 'post-image';
        image.alt = '게시글 이미지';

        imageContainer.appendChild(image);
        contentElement.appendChild(imageContainer);
      });
    }
  }

  // 좋아요 수 업데이트
  const likeCountElement = document.querySelector(
      '.action-button:nth-child(3) span');
  if (likeCountElement) {
    likeCountElement.textContent = postData.likes || 0;
  }

  // 조회수 업데이트
  const viewCountElement = document.querySelector(
      '.action-button:nth-child(2) span');
  if (viewCountElement) {
    viewCountElement.textContent = postData.views || 0;
  }

}

async function toggleLike(button) {
  const postId = getPostIdFromUrl();
  if (!postId) {
    return;
  }

  const isActive = button.classList.contains('active');
  const url = `/api/board/posts/${postId}/like`;
  const method = isActive ? 'DELETE' : 'POST';

  // 실제 요청 로직
  async function doLikeRequest() {
    return fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
      },
      credentials: 'include' // if your API uses cookies
    });
  }

  try {
    let res = await doLikeRequest();

    // 1차 호출에서 401이면, refresh 시도 후 재호출
    if (res.status === 401) {
      try {
        await handleTokenRefresh();
      } catch (refreshErr) {
        throw new Error('세션이 만료되어 다시 로그인해주세요.');
      }
      // 재호출
      res = await doLikeRequest();
    }

    // 최종 응답 확인
    if (!res.ok) {
      throw new Error(`status ${res.status}`);
    }
    const result = await res.json();

    // UI 업데이트
    const newCount = result.response;
    button.querySelector('span').textContent = newCount;
    button.classList.toggle('active', !isActive);

  } catch (err) {
    console.error('좋아요 처리 오류:', err);
    alert(err.message || '좋아요 처리 중 오류가 발생했습니다.');
  }
}
