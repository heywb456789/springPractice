/**
 * 게시글 상세 페이지 스크립트
 */

document.addEventListener('DOMContentLoaded', function() {
  // 뒤로가기 버튼 이벤트
  initBackButton();

  // 액션 버튼 이벤트 (좋아요, 공유 등)
  initActionButtons();

  // 댓글 입력 이벤트
  initCommentInput();

  // 게시글 데이터 로드 (실제 구현 시 활성화)
  // loadPostData();
});

/**
 * 뒤로가기 버튼 초기화
 */
function initBackButton() {
  const backButton = document.getElementById('backButton');

  if (backButton) {
    backButton.addEventListener('click', function() {
      // 이전 페이지로 이동 (히스토리가 있는 경우)
      if (window.history.length > 1) {
        window.history.back();
      } else {
        // 히스토리가 없는 경우 목록 페이지로 이동
        window.location.href = 'board-list.html';
      }
    });
  }
}

/**
 * 액션 버튼 이벤트 초기화 (좋아요, 공유 등)
 */
function initActionButtons() {
  // 좋아요 버튼
  const likeButton = document.querySelector('.action-button:nth-child(3)');
  if (likeButton) {
    likeButton.addEventListener('click', function() {
      toggleLike(this);
    });
  }

  // 공유 버튼
  const shareButton = document.querySelector('.share-button');
  if (shareButton) {
    shareButton.addEventListener('click', function() {
      sharePost();
    });
  }
}

/**
 * 좋아요 토글 함수
 * @param {HTMLElement} button - 좋아요 버튼 요소
 */
function toggleLike(button) {
  // 현재 상태 확인
  const isActive = button.classList.contains('active');
  const likeCountElement = button.querySelector('span');
  const currentLikes = parseInt(likeCountElement.textContent);

  // 토글 상태 변경
  if (isActive) {
    button.classList.remove('active');
    likeCountElement.textContent = currentLikes - 1;
  } else {
    button.classList.add('active');
    likeCountElement.textContent = currentLikes + 1;
  }

  // API 호출 (실제 구현 시 활성화)
  // const postId = getPostIdFromUrl();
  // const action = isActive ? 'unlike' : 'like';
  // togglePostLike(postId, action);
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
 * 댓글 입력 초기화
 */
function initCommentInput() {
  const commentInput = document.getElementById('commentInput');

  if (commentInput) {
    commentInput.addEventListener('keypress', function(e) {
      // 엔터 키 입력 시 댓글 등록
      if (e.key === 'Enter') {
        e.preventDefault();
        const comment = this.value.trim();

        if (comment) {
          submitComment(comment);
          this.value = '';
        }
      }
    });
  }
}

/**
 * 댓글 등록 함수
 * @param {string} comment - 댓글 내용
 */
function submitComment(comment) {
  // 임시 댓글 아이템 생성 (실제 구현 시 변경)
  const commentsContainer = document.querySelector('.comments-container');
  const noComments = document.querySelector('.no-comments');

  // '댓글 없음' 메시지 숨기기
  if (noComments) {
    noComments.style.display = 'none';
  }

  // 현재 시간 포맷팅
  const now = new Date();
  const formattedDate = `${now.getFullYear()}.${String(now.getMonth() + 1).padStart(2, '0')}.${String(now.getDate()).padStart(2, '0')}`;

  // 댓글 아이템 HTML
  const commentHTML = `
    <div class="comment-item">
      <div class="comment-content">${comment}</div>
      <div class="comment-info">
        <span class="comment-author">내 댓글</span>
        <span class="comment-date">${formattedDate}</span>
      </div>
    </div>
  `;

  // DOM에 추가
  commentsContainer.insertAdjacentHTML('afterbegin', commentHTML);

  // API 호출 (실제 구현 시 활성화)
  /*
  const postId = getPostIdFromUrl();

  fetch('/api/comments', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      postId: postId,
      content: comment
    })
  })
  .then(response => response.json())
  .then(data => {
    console.log('댓글 등록 성공:', data);
    // 필요 시 응답 데이터로 UI 업데이트
  })
  .catch(error => {
    console.error('댓글 등록 오류:', error);
    alert('댓글 등록 중 오류가 발생했습니다.');
  });
  */
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
function loadPostData() {
  const postId = getPostIdFromUrl();

  if (!postId) {
    console.error('게시글 ID를 찾을 수 없습니다.');
    return;
  }

  // 게시글 데이터 가져오기 (API 요청)
  fetch(`/api/posts/${postId}`)
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      // 게시글 데이터로 UI 업데이트
      updatePostUI(data);
    })
    .catch(error => {
      console.error('게시글 로드 오류:', error);
      alert('게시글을 불러오는 중 오류가 발생했습니다.');
    });
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
    postMetaElement.innerHTML = `<span class="post-author">${postData.author} | ${formattedDate}</span>`;
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
    if (postData.images && postData.images.length > 0) {
      postData.images.forEach(imageUrl => {
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
  const likeCountElement = document.querySelector('.action-button:nth-child(3) span');
  if (likeCountElement) {
    likeCountElement.textContent = postData.likes || 0;
  }

  // 조회수 업데이트
  const viewCountElement = document.querySelector('.action-button:nth-child(2) span');
  if (viewCountElement) {
    viewCountElement.textContent = postData.views || 0;
  }

  // 댓글 수 업데이트
  const commentCountElement = document.querySelector('.action-button:nth-child(1) span');
  if (commentCountElement) {
    commentCountElement.textContent = postData.comments?.length || 0;
  }

  // 댓글 목록 업데이트
  const commentsContainer = document.querySelector('.comments-container');
  const noComments = document.querySelector('.no-comments');

  if (commentsContainer) {
    // 기존 댓글 제거 (no-comments 요소 제외)
    Array.from(commentsContainer.querySelectorAll('.comment-item')).forEach(item => {
      item.remove();
    });

    // 댓글이 있는 경우
    if (postData.comments && postData.comments.length > 0) {
      // '댓글 없음' 메시지 숨기기
      if (noComments) {
        noComments.style.display = 'none';
      }

      // 댓글 추가
      postData.comments.forEach(comment => {
        const formattedDate = new Date(comment.createdAt).toLocaleDateString('ko-KR');

        const commentHTML = `
          <div class="comment-item">
            <div class="comment-content">${comment.content}</div>
            <div class="comment-info">
              <span class="comment-author">${comment.author}</span>
              <span class="comment-date">${formattedDate}</span>
            </div>
          </div>
        `;

        commentsContainer.insertAdjacentHTML('beforeend', commentHTML);
      });
    } else {
      // 댓글이 없는 경우 '댓글 없음' 메시지 표시
      if (noComments) {
        noComments.style.display = 'block';
      }
    }
  }
}