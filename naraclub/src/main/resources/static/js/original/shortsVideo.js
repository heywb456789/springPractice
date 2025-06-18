import {
  optionalAuthFetch,
  authFetch,
  handleFetchError,
  FetchError,
  getUserId
} from '../commonFetch.js';

// 전역 변수
let videoData = null;
let videoPlayer = null;
let isPlaying = false;
let shareModal = null;
let commentsModal = null;
let videoType = null;
let isLiked = false;
let isBookmarked = false;
let isDescriptionExpanded = false;

// 문서 로드 완료 시 실행
document.addEventListener('DOMContentLoaded', function () {
  // 요소 참조
  videoPlayer = document.getElementById('videoPlayer');
  shareModal = new bootstrap.Modal(document.getElementById('shareModal'));
  commentsModal = new bootstrap.Modal(document.getElementById('commentsModal'));

  // 초기화
  initBackButton();
  loadVideoData();
  initVideoPlayer();
  initActionButtons();
  initMuteToggle(); // 음소거 토글 버튼 초기화
  initShareFeatures();
  initComments();
});

/**
 * 뒤로가기 버튼 초기화
 */
function initBackButton() {
  const backButton = document.getElementById('backButton');
  if (backButton) {
    backButton.addEventListener('click', function () {
      if (window.history.length > 1) {
        window.history.back();
      } else {
        let tabType = 'video';
        if (videoData && videoData.originalType) {
          tabType = videoData.originalType === 'YOUTUBE_SHORTS' ? 'shorts' : 'video';
        }
        sessionStorage.setItem('currentContentTab', tabType);
        window.location.href = '/original/originalContent.html';
      }
    });
  }
}

/**
 * URL에서 비디오 ID 추출
 */
function getVideoIdFromUrl() {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('id');
}

/**
 * 비디오 데이터 로드
 */
async function loadVideoData() {
  const videoId = getVideoIdFromUrl();
  if (!videoId) {
    console.error('비디오 ID가 없습니다.');
    alert('잘못된 접근입니다.');
    return window.location.href = '/original/originalContent.html';
  }

  try {
    showLoading();
    const response = await optionalAuthFetch(`/api/videos/${videoId}`);
    const data = await response.json();
    if (!data?.response) {
      throw new Error('Invalid video data');
    }
    videoData = data.response;
    updateVideoUI(videoData);
  } catch (error) {
    console.error('비디오 데이터 로드 오류:', error);
    handleFetchError(error);
  } finally {
    hideLoading();
  }
}

/**
 * 비디오 UI 업데이트
 */
function updateVideoUI(data) {
  videoType = data.type === 'YOUTUBE_VIDEO' ? 'video_long' : 'video_short';

  // 타이틀 업데이트
  const titleElement = document.getElementById('videoTitle');
  if (titleElement) {
    titleElement.textContent = data.title;
  }

  // 썸네일 업데이트
  const thumbnailElement = document.getElementById('videoThumbnail');
  if (thumbnailElement) {
    thumbnailElement.src = data.thumbnailUrl;
    thumbnailElement.alt = data.title;
  }

  // 비디오 소스 업데이트
  if (videoPlayer) {
    videoPlayer.src = data.videoUrl;
    videoPlayer.poster = data.thumbnailUrl;
  }

  // 날짜 업데이트
  const dateElement = document.getElementById('videoDate');
  if (dateElement) {
    dateElement.textContent = formatDate(data.publishedAt || data.createdAt);
  }

  // 조회수 업데이트
  const viewCountElement = document.getElementById('viewCount');
  if (viewCountElement) {
    viewCountElement.textContent = formatNumber(data.viewCount || 0);
  }

  // 댓글 수 업데이트
  const commentCountElement = document.getElementById('commentCount');
  if (commentCountElement) {
    commentCountElement.textContent = formatNumber(data.commentCount || 0);
  }

  // 좋아요 수 업데이트
  const likeCountElement = document.getElementById('likeCount');
  if (likeCountElement) {
    likeCountElement.textContent = formatNumber(data.likeCount || 0);
  }

  // 설명 업데이트
  const descriptionElement = document.getElementById('videoDescription');
  if (descriptionElement) {
    descriptionElement.textContent = data.description || '설명이 없습니다.';
  }

  // 더보기 버튼 초기화
  initMoreButton();
}

/**
 * 비디오 플레이어 초기화
 */
function initVideoPlayer() {
  const thumbnailContainer = document.getElementById('thumbnailContainer');
  const playOverlay = document.getElementById('playOverlay');

  // 썸네일 클릭 시 비디오 재생
  if (thumbnailContainer) {
    thumbnailContainer.addEventListener('click', startPlayback);
  }

  // 재생 버튼 클릭 시 비디오 재생
  if (playOverlay) {
    playOverlay.addEventListener('click', startPlayback);
  }

  // 비디오 이벤트 리스너 등록
  if (videoPlayer) {
    // 자동 재생을 위한 속성 설정
    videoPlayer.muted = true; // 자동 재생을 위해 음소거 시작
    videoPlayer.loop = true; // 반복 재생 설정
    videoPlayer.autoplay = true; // 자동 재생 설정

    // 비디오 클릭 시 재생/일시정지
    videoPlayer.addEventListener('click', togglePlayPause);

    // 재생 시작 시
    videoPlayer.addEventListener('play', function () {
      isPlaying = true;
      hidePlayOverlay();
    });

    // 일시 정지 시
    videoPlayer.addEventListener('pause', function () {
      isPlaying = false;
      showPlayOverlay();
    });

    // 재생 완료 시 (loop가 있어도 혹시 모를 상황 대비)
    videoPlayer.addEventListener('ended', function () {
      if (!videoPlayer.loop) {
        // 루프가 꺼져있으면 다시 재생
        videoPlayer.currentTime = 0;
        videoPlayer.play();
      }
    });

    // 로딩 시작
    videoPlayer.addEventListener('loadstart', showLoading);

    // 재생 가능할 때 - 자동 재생 시도
    videoPlayer.addEventListener('canplay', function() {
      hideLoading();
      // 자동 재생 시도
      autoPlayVideo();
    });

    // 메타데이터 로드 완료 시에도 자동 재생 시도
    videoPlayer.addEventListener('loadedmetadata', function() {
      autoPlayVideo();
    });
  }
}

/**
 * 자동 재생 시도
 */
function autoPlayVideo() {
  if (!videoPlayer || isPlaying) return;

  // 썸네일 숨기고 비디오 표시
  const thumbnailContainer = document.getElementById('thumbnailContainer');
  if (thumbnailContainer) {
    thumbnailContainer.style.display = 'none';
  }

  if (videoPlayer) {
    videoPlayer.style.display = 'block';

    // 자동 재생 시도
    const playPromise = videoPlayer.play();

    if (playPromise !== undefined) {
      playPromise
        .then(() => {
          // 자동 재생 성공
          console.log('자동 재생 시작됨');
          isPlaying = true;
          hidePlayOverlay();

          // 음소거 토글 버튼 업데이트
          updateMuteToggleButton();

          // 음소거 해제 버튼 표시 (사용자가 탭하면 음소거 해제) - 잠시만 표시
          // showUnmuteButton();
        })
        .catch(error => {
          console.log('자동 재생 실패, 사용자 상호작용 필요:', error);
          // 자동 재생 실패 시 썸네일과 재생 버튼 표시
          showThumbnail();
          showPlayOverlay();
        });
    }
  }
}

/**
 * 음소거 해제 임시 버튼 표시
 */
function showUnmuteButton() {
  // 기존 음소거 해제 버튼이 있으면 제거
  const existingButton = document.getElementById('unmuteButton');
  if (existingButton) {
    existingButton.remove();
  }

  // 음소거 해제 버튼 생성 (자동재생 후 잠시만 표시)
  const unmuteButton = document.createElement('button');
  unmuteButton.id = 'unmuteButton';
  unmuteButton.className = 'unmute-button';
  unmuteButton.innerHTML = '<i class="fas fa-volume-mute"></i>';

  // 버튼 클릭 시 음소거 해제
  unmuteButton.addEventListener('click', function() {
    if (videoPlayer) {
      videoPlayer.muted = false;
      updateMuteToggleButton(); // 상단 토글 버튼도 업데이트
      this.innerHTML = '<i class="fas fa-volume-up"></i>';

      // 즉시 버튼 숨김
      this.style.opacity = '0';
      setTimeout(() => {
        if (this.parentNode) {
          this.parentNode.removeChild(this);
        }
      }, 300);
    }
  });

  // 비디오 컨테이너에 버튼 추가
  const videoWrapper = document.querySelector('.video-wrapper');
  if (videoWrapper) {
    videoWrapper.appendChild(unmuteButton);

    // 5초 후 자동으로 버튼 숨김
    setTimeout(() => {
      if (unmuteButton.parentNode) {
        unmuteButton.style.opacity = '0';
        setTimeout(() => {
          if (unmuteButton.parentNode) {
            unmuteButton.parentNode.removeChild(unmuteButton);
          }
        }, 300);
      }
    }, 5000);
  }
}

/**
 * 재생 시작 (썸네일 클릭 시)
 */
function startPlayback(event) {
  event.preventDefault();
  event.stopPropagation();

  const thumbnailContainer = document.getElementById('thumbnailContainer');

  // 썸네일 숨기고 비디오 표시
  if (thumbnailContainer) {
    thumbnailContainer.style.display = 'none';
  }

  // 비디오 표시 및 재생
  if (videoPlayer) {
    videoPlayer.style.display = 'block';

    // 사용자가 직접 재생하는 경우 음소거 해제
    videoPlayer.muted = false;
    updateMuteToggleButton(); // 음소거 토글 버튼 업데이트

    videoPlayer.play()
      .then(() => {
        isPlaying = true;
        hidePlayOverlay();
      })
      .catch(error => {
        console.error('비디오 재생 실패:', error);
        alert('비디오 재생을 시작할 수 없습니다.');
        showThumbnail();
      });
  }
}

/**
 * 재생/일시정지 토글
 */
function togglePlayPause(event) {
  event.preventDefault();
  event.stopPropagation();

  if (!videoPlayer) return;

  if (isPlaying) {
    videoPlayer.pause();
  } else {
    videoPlayer.play();
  }
}

/**
 * 썸네일 표시
 */
function showThumbnail() {
  const thumbnailContainer = document.getElementById('thumbnailContainer');

  if (videoPlayer) {
    videoPlayer.style.display = 'none';
  }

  if (thumbnailContainer) {
    thumbnailContainer.style.display = 'flex';
  }
}

/**
 * 재생 오버레이 표시/숨김
 */
function showPlayOverlay() {
  const playOverlay = document.getElementById('playOverlay');
  if (playOverlay) {
    playOverlay.classList.remove('hidden');
  }
}

function hidePlayOverlay() {
  const playOverlay = document.getElementById('playOverlay');
  if (playOverlay) {
    playOverlay.classList.add('hidden');
  }
}

/**
 * 로딩 스피너 표시/숨김
 */
function showLoading() {
  const loadingSpinner = document.getElementById('loadingSpinner');
  if (loadingSpinner) {
    loadingSpinner.style.display = 'block';
  }
}

function hideLoading() {
  const loadingSpinner = document.getElementById('loadingSpinner');
  if (loadingSpinner) {
    loadingSpinner.style.display = 'none';
  }
}

/**
 * 음소거 토글 버튼 초기화
 */
function initMuteToggle() {
  const muteToggleButton = document.getElementById('muteToggleButton');

  if (muteToggleButton) {
    muteToggleButton.addEventListener('click', function() {
      if (!videoPlayer) return;

      videoPlayer.muted = !videoPlayer.muted;
      updateMuteToggleButton();
    });

    // 초기 상태 설정
    updateMuteToggleButton();
  }
}

/**
 * 음소거 토글 버튼 UI 업데이트
 */
function updateMuteToggleButton() {
  const muteToggleButton = document.getElementById('muteToggleButton');

  if (muteToggleButton && videoPlayer) {
    const icon = muteToggleButton.querySelector('i');

    if (videoPlayer.muted) {
      muteToggleButton.classList.add('muted');
      icon.className = 'fas fa-volume-mute';
    } else {
      muteToggleButton.classList.remove('muted');
      icon.className = 'fas fa-volume-up';
    }
  }
}

/**
 * 액션 버튼들 초기화
 */
function initActionButtons() {
  const likeButton = document.getElementById('likeButton');
  const commentButton = document.getElementById('commentButton');
  const shareButton = document.getElementById('shareButton');
  const bookmarkButton = document.getElementById('bookmarkButton');

  if (likeButton) {
    likeButton.addEventListener('click', toggleLike);
  }

  if (commentButton) {
    commentButton.addEventListener('click', openCommentsModal);
  }

  if (shareButton) {
    shareButton.addEventListener('click', openShareModal);
  }

  if (bookmarkButton) {
    bookmarkButton.addEventListener('click', toggleBookmark);
  }
}

/**
 * 좋아요 토글
 */
function toggleLike() {
  isLiked = !isLiked;
  const likeButton = document.getElementById('likeButton');
  const likeCountElement = document.getElementById('likeCount');

  if (likeButton) {
    const icon = likeButton.querySelector('i');
    if (isLiked) {
      likeButton.classList.add('liked');
      icon.className = 'fas fa-heart';
    } else {
      likeButton.classList.remove('liked');
      icon.className = 'far fa-heart';
    }
  }

  // 좋아요 수 업데이트 (실제로는 서버 API 호출)
  if (likeCountElement) {
    const currentCount = parseInt(likeCountElement.textContent.replace(/,/g, '')) || 0;
    const newCount = isLiked ? currentCount + 1 : Math.max(0, currentCount - 1);
    likeCountElement.textContent = formatNumber(newCount);
  }
}

/**
 * 북마크 토글
 */
function toggleBookmark() {
  isBookmarked = !isBookmarked;
  const bookmarkButton = document.getElementById('bookmarkButton');

  if (bookmarkButton) {
    const icon = bookmarkButton.querySelector('i');
    if (isBookmarked) {
      bookmarkButton.classList.add('bookmarked');
      icon.className = 'fas fa-bookmark';
    } else {
      bookmarkButton.classList.remove('bookmarked');
      icon.className = 'far fa-bookmark';
    }
  }
}

/**
 * 댓글 모달 열기
 */
function openCommentsModal() {
  if (commentsModal) {
    commentsModal.show();
  }
}

/**
 * 공유 모달 열기
 */
function openShareModal() {
  if (shareModal) {
    shareModal.show();
    setupKakaoShare();
  }
}

/**
 * 더보기 버튼 초기화
 */
function initMoreButton() {
  const moreButton = document.getElementById('moreButton');
  const descriptionElement = document.getElementById('videoDescription');

  if (moreButton && descriptionElement) {
    // 내용이 줄임 표시가 필요한지 확인
    const isOverflowing = descriptionElement.scrollHeight > descriptionElement.clientHeight;

    // 내용이 넘치는 경우에만 더보기 버튼 표시
    moreButton.style.display = isOverflowing ? 'block' : 'none';

    moreButton.addEventListener('click', function () {
      isDescriptionExpanded = !isDescriptionExpanded;

      if (isDescriptionExpanded) {
        descriptionElement.classList.add('expanded');
        this.textContent = '접기';
      } else {
        descriptionElement.classList.remove('expanded');
        this.textContent = '더보기';
      }
    });
  }
}

/**
 * 공유 기능 초기화
 */
function initShareFeatures() {
  document.getElementById('copyUrl')?.addEventListener('click', () => {
    copyCurrentUrl();
  });

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
  const videoId = getVideoIdFromUrl();
  const shareUrl = `https://club1.newstomato.com/share/original/${videoId}`;

  const userAgent = navigator.userAgent.toLowerCase();
  let appScheme = '';
  let storeUrl = '';

  if (/iphone|ipad|ipod/.test(userAgent)) {
    appScheme = `tongtongios://tongtongiOS?url=${encodeURIComponent(shareUrl)}`;
    storeUrl = 'https://apps.apple.com/kr/app/통통-암호화-메신저/id982895719';
  } else if (/android/.test(userAgent)) {
    appScheme = `tongtong://m.etomato.com?url=${encodeURIComponent(shareUrl)}`;
    storeUrl = 'https://play.google.com/store/apps/details?id=tomato.solution.tongtong';
  } else {
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

  navigator.clipboard.writeText(shareUrl)
    .then(() => {
      console.log('URL이 클립보드에 복사되었습니다.');
    })
    .catch(err => {
      console.error('클립보드 복사 오류:', err);
    });

  const appCheckTimeout = 1500;
  const now = Date.now();

  window.location.href = appScheme;

  setTimeout(function() {
    if (document.hidden === false && Date.now() - now > appCheckTimeout) {
      if (confirm('통통 앱이 설치되어 있지 않은 것 같습니다. 앱 스토어로 이동하시겠습니까?')) {
        window.location.href = storeUrl;
      }
    }
    shareModal.hide();
  }, appCheckTimeout + 500);
}

/**
 * 카카오톡 공유 설정
 */
async function setupKakaoShare() {
  const id = getVideoIdFromUrl();
  const userId = await getUserId();
  const title = document.getElementById('videoTitle')?.textContent || '동영상 공유';
  const description = document.getElementById('videoDescription')?.textContent?.slice(0, 100) || '';
  const shareUrl = `https://club1.newstomato.com/share/original/${id}`;
  const imageUrl = document.getElementById('videoThumbnail')?.src || '';

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
        type: videoType,
        id: id,
        userId: userId
      }
    });
  }
}

/**
 * X(구 Twitter) 공유 함수
 */
async function shareToX() {
  const title = document.getElementById('videoTitle')?.textContent.trim() || '';
  const postId = getVideoIdFromUrl();
  const shareUrl = `https://club1.newstomato.com/share/original/${postId}`;

  try {
    const res = await authFetch('/twitter/share', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title: title,
        shareUrl: shareUrl,
        type: videoType.toUpperCase(),
        targetId: postId
      })
    });
    await res.json();
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
 * URL 복사
 */
function copyCurrentUrl() {
  const videoId = getVideoIdFromUrl();
  const shareUrl = `https://club1.newstomato.com/share/original/${videoId}`;
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
 * 댓글 영역 초기화
 */
function initComments() {
  const videoId = getVideoIdFromUrl();
  const list = document.getElementById('commentsContainer');
  const noCommentsEl = document.getElementById('noComments');
  const input = document.getElementById('commentInput');
  const submitBtn = document.getElementById('commentSubmitButton');
  const modalInput = document.getElementById('modalCommentInput');
  const modalSubmitBtn = document.getElementById('modalCommentSubmit');
  const pageSize = 10;
  let page = 0;
  let loading = false;
  let done = false;

  // 초기 로드
  loadComments();

  // 댓글 등록 (하단 입력창)
  if (submitBtn && input) {
    // 입력창 상태 관리
    function updateSubmitButton() {
      const hasContent = input.value.trim().length > 0;
      submitBtn.disabled = !hasContent;
      submitBtn.textContent = hasContent ? '게시' : '게시';
    }

    // 입력할 때마다 버튼 상태 업데이트
    input.addEventListener('input', updateSubmitButton);
    updateSubmitButton(); // 초기 상태 설정

    submitBtn.addEventListener('click', () => {
      const content = input.value.trim();
      if (!content) return;
      submitComment(content);
      input.value = '';
      updateSubmitButton();
    });

    input.addEventListener('keypress', function (e) {
      if (e.key === 'Enter') {
        e.preventDefault();
        const content = this.value.trim();
        if (!content) return;
        submitComment(content);
        this.value = '';
        updateSubmitButton();
      }
    });
  }

  // 댓글 등록 (모달 입력창)
  if (modalSubmitBtn && modalInput) {
    // 모달 입력창 상태 관리
    function updateModalSubmitButton() {
      const hasContent = modalInput.value.trim().length > 0;
      modalSubmitBtn.disabled = !hasContent;
      modalSubmitBtn.textContent = hasContent ? '게시' : '게시';
    }

    modalInput.addEventListener('input', updateModalSubmitButton);
    updateModalSubmitButton();

    modalSubmitBtn.addEventListener('click', () => {
      const content = modalInput.value.trim();
      if (!content) return;
      submitComment(content);
      modalInput.value = '';
      updateModalSubmitButton();
    });

    modalInput.addEventListener('keypress', function (e) {
      if (e.key === 'Enter') {
        e.preventDefault();
        const content = this.value.trim();
        if (!content) return;
        submitComment(content);
        this.value = '';
        updateModalSubmitButton();
      }
    });
  }

  // 댓글 목록 로드
  async function loadComments() {
    if (!videoId || !list) return;

    loading = true;
    try {
      const res = await optionalAuthFetch(
        `/api/videos/${videoId}/comments?page=${page}&size=${pageSize}`
      );
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
        items.forEach(c => list.insertAdjacentHTML('beforeend', renderComment(c)));
        page++;
      }
    } catch (error) {
      console.error('댓글 로드 오류:', error);
      if (list) {
        list.innerHTML = '<p class="error-message">댓글을 불러오는 데 실패했습니다.</p>';
      }
      if (noCommentsEl) {
        noCommentsEl.style.display = 'block';
      }
      done = true;
    } finally {
      loading = false;
    }
  }

  // 댓글 등록
  async function submitComment(content) {
    if (!videoId) return;

    try {
      const res = await authFetch(
        `/api/videos/${videoId}/comments`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({content})
        }
      );

      const {response: comment} = await res.json();
      if (list) {
        list.insertAdjacentHTML('beforeend', renderComment(comment));
      }
      if (noCommentsEl) {
        noCommentsEl.style.display = 'none';
      }

      // 댓글 수 업데이트
      const commentCountEl = document.getElementById('commentCount');
      if (commentCountEl) {
        const currentCount = parseInt(commentCountEl.textContent.replace(/,/g, '')) || 0;
        commentCountEl.textContent = formatNumber(currentCount + 1);
      }

    } catch (error) {
      console.error('댓글 등록 오류:', error);
      if (error instanceof FetchError && error.httpStatus === 401) {
        alert('댓글 등록을 위해 로그인 후 이용해주세요.');
        return window.location.href = '/login/login.html';
      }
      handleFetchError(error);
    }
  }

  // 수정/삭제
  if (list) {
    list.addEventListener('click', async e => {
      const item = e.target.closest('.comment-item');
      if (!item) return;

      const id = item.dataset.id;

      if (e.target.classList.contains('delete-btn')) {
        if (!confirm('댓글을 삭제하시겠습니까?')) return;

        try {
          await authFetch(
            `/api/videos/${videoId}/comments/${id}`,
            {method: 'DELETE'}
          );
          item.remove();

          // 댓글 수 업데이트
          const commentCountEl = document.getElementById('commentCount');
          if (commentCountEl) {
            const currentCount = parseInt(commentCountEl.textContent.replace(/,/g, '')) || 0;
            if (currentCount > 0) {
              commentCountEl.textContent = formatNumber(currentCount - 1);
            }
          }

        } catch (error) {
          if (error instanceof FetchError && error.httpStatus === 401) {
            alert('삭제를 위해 로그인 후 이용해주세요.');
            return window.location.href = '/login/login.html';
          }
          handleFetchError(error);
          console.error('삭제 오류:', error);
        }
      } else if (e.target.classList.contains('edit-btn')) {
        const p = item.querySelector('.content');
        if (p.isContentEditable) {
          p.contentEditable = false;
          try {
            await authFetch(
              `/api/videos/${videoId}/comments/${id}`,
              {
                method: 'PUT',
                headers: {
                  'Content-Type': 'application/json'
                },
                body: JSON.stringify({content: p.textContent.trim()})
              }
            );
          } catch (error) {
            if (error instanceof FetchError && error.httpStatus === 401) {
              alert('수정을 위해 로그인 후 이용해주세요.');
              return window.location.href = '/login/login.html';
            }
            handleFetchError(error);
            console.error('수정 오류:', error);
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
          <small>${formatDate(c.createdAt)}</small>
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

/**
 * 숫자 포맷 (천 단위 콤마)
 */
function formatNumber(num) {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '만';
  }
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

/**
 * 날짜 포맷
 */
function formatDate(dateString) {
  if (!dateString) return '';

  const now = new Date();
  const date = new Date(dateString);
  const diffMs = now.getTime() - date.getTime();
  const diffMins = Math.floor(diffMs / (1000 * 60));
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

  if (diffMins < 1) {
    return '방금 전';
  } else if (diffMins < 60) {
    return `${diffMins}분 전`;
  } else if (diffHours < 24) {
    return `${diffHours}시간 전`;
  } else if (diffDays < 7) {
    return `${diffDays}일 전`;
  } else {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
  }
}