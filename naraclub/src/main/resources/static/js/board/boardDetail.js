/**
 * 게시글 상세 페이지 스크립트
 */

import {
  authFetch,
  optionalAuthFetch,
  handleTokenRefresh,
  handleFetchError,
  FetchError
} from '../commonFetch.js';

let shareModal;

document.addEventListener('DOMContentLoaded', function () {
  // 뒤로가기 버튼 이벤트
  initBackButton();
  // 액션 버튼 이벤트 (좋아요, 공유 등)
  initActionButtons();

  initShareFeatures();
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
        // window.history.back();
        window.location.href = '/board/boardList.html';
      } else {
        // 히스토리가 없는 경우 목록 페이지로 이동
        window.location.href = '/board/boardList.html';
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
}


// 2) X(트위터) 공유
  // document.getElementById('shareTwitter').addEventListener('click', () => {
  //   const url = encodeURIComponent(shareUrl);
  //   const text = encodeURIComponent(title);
  //   window.open(
  //       `https://twitter.com/intent/tweet?url=${url}&text=${text}`,
  //       '_blank',
  //       'width=550,height=420'
  //   );
  //   shareModal.hide();
  // });

  // 3) 통통(커스텀) 공유
  // document.getElementById('shareTongtong').addEventListener('click', () => {
  //   window.open(
  //       `https://yourdomain.com/tongtong/share?url=${encodeURIComponent(
  //           shareUrl)}`,
  //       '_blank'
  //   );
  //   shareModal.hide();
  // });


function initShareFeatures() {
  // Bootstrap Modal 인스턴스
  shareModal = new bootstrap.Modal(document.getElementById('shareModal'));

  document.getElementById('openShareModal')?.addEventListener('click', () => {
    shareModal.show();
    setupKakaoShare();
  });
  document.getElementById('copyUrl')?.addEventListener('click', () => {
    copyCurrentUrl();
  });
}

// Kakao.Share.createDefaultButton 방식으로 공유 버튼 생성
function setupKakaoShare() {
  const postId = getPostIdFromUrl();
  const shareUrl = `https://www.xn--w69at2fhshwrs.kr/share/board/${postId}`;
  const title = document.querySelector('.post-title')?.textContent || '게시글 공유';
  const description = document.querySelector('.post-content p')?.textContent || '';
  const imageUrl = 'https://image.newstomato.com/newstomato/club/share/freeboard.png';

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
      }
    });
  } else {
    Kakao.init('a1c1145bbd0ca5e22d5b2c996a8aa32a'); // voteDetail과 동일한 앱 키
    Kakao.Share.createDefaultButton({
      container: '#kakaotalk-sharing-btn',
      objectType: 'feed',
      content: {
        title,
        description,
        imageUrl,
        link: { mobileWebUrl: shareUrl, webUrl: shareUrl }
      }
    });
  }
}

// URL 복사 및 모달 닫기
function copyCurrentUrl() {
  const postId = getPostIdFromUrl();
  const shareUrl = `https://www.xn--w69at2fhshwrs.kr/share/board/${postId}`;
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
  try {
    const res = await optionalAuthFetch(`/api/board/posts/${postId}`);
    const {response: post} = await res.json();
    updatePostUI(post);

    // 좋아요 초기화
    document
    .querySelector('.like-button')
    .classList.toggle('active', post.like);

  } catch (err) {
    handleFetchError(err);
    window.location.href="/board/boardList.html"
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
    // 내용 초기화
    contentElement.innerHTML = '';

    // 본문 텍스트 추가
    if (postData.content) {
      const paragraph = document.createElement('p');
      paragraph.textContent = postData.content;
      contentElement.appendChild(paragraph);
    }

    // 이미지가 있는 경우 추가
    if (postData.imageUrls && postData.imageUrls.length > 0) {
      // 이미지 컨테이너 생성
      const imagesContainer = document.createElement('div');
      imagesContainer.className = 'post-images-container';

      // 각 이미지 URL에 대해 이미지 요소 생성 및 추가
      postData.imageUrls.forEach(imageUrl => {
        const imageContainer = document.createElement('div');
        imageContainer.className = 'post-image-container';

        const image = document.createElement('img');
        // 이미지 URL 설정
        image.src = imageUrl;
        image.className = 'post-image';
        image.alt = '게시글 이미지';

        // 이미지 로딩 이벤트 추가
        image.onload = function () {
          // 이미지 로드 완료 시 페이드인 효과 적용
          image.classList.add('loaded');
        };

        // 이미지 에러 처리
        image.onerror = function () {
          // 이미지 로드 실패 시 대체 이미지 또는 메시지 표시
          imageContainer.innerHTML = '<div class="image-error">이미지를 불러올 수 없습니다</div>';
        };

        imageContainer.appendChild(image);
        imagesContainer.appendChild(imageContainer);
      });

      contentElement.appendChild(imagesContainer);
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
  const id = getPostIdFromUrl();
  const method = button.classList.contains('active') ? 'DELETE' : 'POST';

  try {
    const res = await authFetch(`/api/board/posts/${id}/like`, {method});
    const {response} = await res.json();

    button.querySelector('span').textContent = response;
    button.classList.toggle('active');

  } catch (err) {
    if (err instanceof FetchError && err.httpStatus === 401) {
      alert('로그인이 필요한 기능입니다.');
      window.location.href = '/login/login.html';
    } else {
      handleFetchError(err);
    }

  }
}
