import {
  optionalAuthFetch,
  authFetch,
  handleFetchError,
  FetchError, getUserId
} from '../commonFetch.js';

// 전역 변수
let videoData = null;
let videoPlayer = null;
let progressUpdateInterval = null;
let isPlaying = false;
let isMuted = false;
let isFullscreen = false;
let shareModal = null;


// 문서 로드 완료 시 실행
document.addEventListener('DOMContentLoaded', function () {
  // 비디오 플레이어 요소 참조
  videoPlayer = document.getElementById('videoPlayer');
  // 뒤로가기 버튼 초기화
  initBackButton();
  // 비디오 데이터 로드
  loadVideoData();
  // 비디오 플레이어 초기화
  initVideoPlayer();
  // 비디오 컨트롤 초기화
  initVideoControls();
  // 공유 버튼 초기화
  initShareFeatures();
  // 댓글 영역은 별도로 처리
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
        // 히스토리가 없는 경우, videoData를 확인하기 전에 기본값으로 'video' 탭 설정
        let tabType = 'video';

        // videoData가 로드된 경우에만 콘텐츠 타입 확인
        if (videoData && videoData.originalType) {
          tabType = videoData.originalType === 'YOUTUBE_SHORTS' ? 'shorts'
              : 'video';
        }

        // 현재 탭 정보 저장
        sessionStorage.setItem('currentContentTab', tabType);

        // 목록 페이지로 이동
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
  }
}

/**
 * 비디오 UI 업데이트
 */
function updateVideoUI(data) {
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
  const videoElement = document.getElementById('videoPlayer');
  if (videoElement) {
    videoElement.src = data.videoUrl;
    videoElement.poster = data.thumbnailUrl;
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
  const videoWrapper = document.getElementById('videoWrapper');
  const thumbnailContainer = document.getElementById('thumbnailContainer');
  const playButton = document.getElementById('playButton');

  // 썸네일 클릭 시 비디오 재생
  if (thumbnailContainer) {
    thumbnailContainer.addEventListener('click', startPlayback);
  }

  // 재생 버튼 클릭 시 비디오 재생
  if (playButton) {
    playButton.addEventListener('click', startPlayback);
  }

  // 비디오 이벤트 리스너 등록
  if (videoPlayer) {
    // 메타데이터 로드 완료 시
    videoPlayer.addEventListener('loadedmetadata', function () {
      updateTotalTime();
    });

    // 재생 시작 시
    videoPlayer.addEventListener('play', function () {
      isPlaying = true;
      updatePlayPauseButton();
      startProgressInterval();
    });

    // 일시 정지 시
    videoPlayer.addEventListener('pause', function () {
      isPlaying = false;
      updatePlayPauseButton();
      stopProgressInterval();
    });

    // 재생 완료 시
    videoPlayer.addEventListener('ended', function () {
      isPlaying = false;
      updatePlayPauseButton();
      stopProgressInterval();
      showThumbnail();
    });

    // 볼륨 변경 시
    videoPlayer.addEventListener('volumechange', function () {
      updateVolumeUI();
    });
  }
}

/**
 * 재생 시작
 */
function startPlayback(event) {
  event.preventDefault();

  const thumbnailContainer = document.getElementById('thumbnailContainer');
  const videoControls = document.getElementById('videoControls');

  // 썸네일 숨기고 비디오 표시
  if (thumbnailContainer) {
    thumbnailContainer.style.display = 'none';
  }

  // 비디오 표시 및 재생
  if (videoPlayer) {
    videoPlayer.style.display = 'block';
    videoPlayer.play()
    .then(() => {
      // 재생 성공
      isPlaying = true;

      // 컨트롤 표시
      if (videoControls) {
        videoControls.style.display = 'block';
      }

      // 재생/일시정지 버튼 업데이트
      updatePlayPauseButton();

      // 프로그레스 바 업데이트 시작
      startProgressInterval();
    })
    .catch(error => {
      console.error('비디오 재생 실패:', error);
      alert('비디오 재생을 시작할 수 없습니다.');
      showThumbnail();
    });
  }
}

/**
 * 썸네일 표시
 */
function showThumbnail() {
  const thumbnailContainer = document.getElementById('thumbnailContainer');
  const videoControls = document.getElementById('videoControls');

  // 비디오 숨기고 썸네일 표시
  if (videoPlayer) {
    videoPlayer.style.display = 'none';
  }

  // 썸네일 표시
  if (thumbnailContainer) {
    thumbnailContainer.style.display = 'block';
  }

  // 컨트롤 숨김
  if (videoControls) {
    videoControls.style.display = 'none';
  }
}

/**
 * 비디오 컨트롤 초기화
 */
function initVideoControls() {
  const playPauseButton = document.getElementById('playPauseButton');
  const muteButton = document.getElementById('muteButton');
  const volumeSlider = document.getElementById('volumeSlider');
  const progressBar = document.getElementById('progressBar');
  const fullscreenButton = document.getElementById('fullscreenButton');

  // 재생/일시정지 버튼
  if (playPauseButton) {
    playPauseButton.addEventListener('click', togglePlayPause);
  }

  // 음소거 버튼
  if (muteButton) {
    muteButton.addEventListener('click', toggleMute);
  }

  // 볼륨 슬라이더
  if (volumeSlider) {
    volumeSlider.addEventListener('input', function () {
      videoPlayer.volume = this.value;

      // 볼륨이 0이면 음소거 상태로 변경
      if (parseFloat(this.value) === 0) {
        videoPlayer.muted = true;
        isMuted = true;
      } else {
        videoPlayer.muted = false;
        isMuted = false;
      }

      updateVolumeUI();
    });
  }

  // 프로그레스 바
  if (progressBar) {
    progressBar.addEventListener('click', function (e) {
      const rect = this.getBoundingClientRect();
      const pos = (e.clientX - rect.left) / rect.width;

      if (videoPlayer && videoPlayer.duration) {
        // 비디오 시간 업데이트
        videoPlayer.currentTime = pos * videoPlayer.duration;
        updateProgress();
      }
    });
  }

  // 전체화면 버튼
  if (fullscreenButton) {
    fullscreenButton.addEventListener('click', toggleFullscreen);
  }
}

/**
 * 재생/일시정지 토글
 */
function togglePlayPause() {
  if (!videoPlayer) {
    return;
  }

  if (isPlaying) {
    videoPlayer.pause();
  } else {
    videoPlayer.play();
  }
}

/**
 * 재생/일시정지 버튼 업데이트
 */
function updatePlayPauseButton() {
  const playPauseButton = document.getElementById('playPauseButton');

  if (playPauseButton) {
    const icon = playPauseButton.querySelector('i');

    if (icon) {
      if (isPlaying) {
        icon.className = 'fas fa-pause';
      } else {
        icon.className = 'fas fa-play';
      }
    }
  }
}

/**
 * 음소거 토글
 */
function toggleMute() {
  if (!videoPlayer) {
    return;
  }

  videoPlayer.muted = !videoPlayer.muted;
  isMuted = videoPlayer.muted;

  updateVolumeUI();
}

/**
 * 볼륨 UI 업데이트
 */
function updateVolumeUI() {
  const muteButton = document.getElementById('muteButton');
  const volumeSlider = document.getElementById('volumeSlider');

  if (muteButton) {
    const icon = muteButton.querySelector('i');

    if (icon) {
      if (isMuted || videoPlayer.volume === 0) {
        icon.className = 'fas fa-volume-mute';
      } else if (videoPlayer.volume < 0.5) {
        icon.className = 'fas fa-volume-down';
      } else {
        icon.className = 'fas fa-volume-up';
      }
    }
  }

  if (volumeSlider) {
    volumeSlider.value = isMuted ? 0 : videoPlayer.volume;
  }
}

/**
 * 프로그레스 바 업데이트 인터벌 시작
 */
function startProgressInterval() {
  if (progressUpdateInterval) {
    clearInterval(progressUpdateInterval);
  }

  progressUpdateInterval = setInterval(updateProgress, 200);
}

/**
 * 프로그레스 바 업데이트 인터벌 중지
 */
function stopProgressInterval() {
  if (progressUpdateInterval) {
    clearInterval(progressUpdateInterval);
    progressUpdateInterval = null;
  }
}

/**
 * 프로그레스 바 업데이트
 */
function updateProgress() {
  if (!videoPlayer) {
    return;
  }

  const progressFill = document.getElementById('progressFill');
  const currentTimeElement = document.getElementById('currentTime');

  if (progressFill && videoPlayer.duration) {
    const percentage = (videoPlayer.currentTime / videoPlayer.duration) * 100;
    progressFill.style.width = `${percentage}%`;
  }

  if (currentTimeElement) {
    currentTimeElement.textContent = formatTime(videoPlayer.currentTime);
  }
}

/**
 * 총 시간 업데이트
 */
function updateTotalTime() {
  if (!videoPlayer) {
    return;
  }

  const totalTimeElement = document.getElementById('totalTime');

  if (totalTimeElement && videoPlayer.duration) {
    totalTimeElement.textContent = formatTime(videoPlayer.duration);
  }
}

/**
 * 전체화면 토글
 */
function toggleFullscreen() {
  const videoWrapper = document.getElementById('videoWrapper');

  if (!videoWrapper) {
    return;
  }

  if (!isFullscreen) {
    // 전체화면 진입
    videoWrapper.classList.add('fullscreen-video');

    if (videoWrapper.requestFullscreen) {
      videoWrapper.requestFullscreen();
    } else if (videoWrapper.webkitRequestFullscreen) {
      videoWrapper.webkitRequestFullscreen();
    } else if (videoWrapper.msRequestFullscreen) {
      videoWrapper.msRequestFullscreen();
    }

    // 버튼 아이콘 변경
    const fullscreenButton = document.getElementById('fullscreenButton');
    if (fullscreenButton) {
      const icon = fullscreenButton.querySelector('i');
      if (icon) {
        icon.className = 'fas fa-compress';
      }
    }

    isFullscreen = true;
  } else {
    // 전체화면 종료
    videoWrapper.classList.remove('fullscreen-video');

    if (document.exitFullscreen) {
      document.exitFullscreen();
    } else if (document.webkitExitFullscreen) {
      document.webkitExitFullscreen();
    } else if (document.msExitFullscreen) {
      document.msExitFullscreen();
    }

    // 버튼 아이콘 변경
    const fullscreenButton = document.getElementById('fullscreenButton');
    if (fullscreenButton) {
      const icon = fullscreenButton.querySelector('i');
      if (icon) {
        icon.className = 'fas fa-expand';
      }
    }

    isFullscreen = false;
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
    const isOverflowing = descriptionElement.scrollHeight
        > descriptionElement.clientHeight;

    // 내용이 넘치는 경우에만 더보기 버튼 표시
    moreButton.style.display = isOverflowing ? 'block' : 'none';

    moreButton.addEventListener('click', function () {
      // 설명 영역 확장 토글
      descriptionElement.classList.toggle('expanded');

      // 버튼 텍스트 변경
      this.textContent = descriptionElement.classList.contains('expanded')
          ? '접기' : '더보기';
    });
  }
}

/**
 * 공유 버튼 초기화
 */
// 공유 기능 초기화: 모달 열기 & 버튼 연결
function initShareFeatures() {
  shareModal = new bootstrap.Modal(document.getElementById('shareModal'));

  document.getElementById('shareButton')?.addEventListener('click', () => {
    shareModal.show();
    setupKakaoShare();
  });

  document.getElementById('copyUrl')?.addEventListener('click', () => {
    copyCurrentUrl();
  });
}

// 카카오톡 기본공유 버튼 생성
async function setupKakaoShare() {
  const id = getVideoIdFromUrl();
  const userId = await getUserId();
  const shareUrl = window.location.href;
  const title = document.getElementById('videoTitle')?.textContent || '동영상 공유';
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
        description: '',
        imageUrl,
        link: { mobileWebUrl: shareUrl, webUrl: shareUrl }
      },
      serverCallbackArgs: {
        type: 'video',      // 'board' | 'vote' | 'news' | 'video'
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
        description: '',
        imageUrl,
        link: { mobileWebUrl: shareUrl, webUrl: shareUrl }
      },
      serverCallbackArgs: {
        type: 'video',      // 'board' | 'vote' | 'news' | 'video'
        id: id,        // 게시물 PK
        userId: userId // 로그인한 회원 ID
      }
    });
  }
}

// URL 복사 후 모달 닫기
function copyCurrentUrl() {
  const videoId = getVideoIdFromUrl();
  const shareUrl = `https://www.xn--w69at2fhshwrs.kr/share/original/${videoId}`;
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
    if (!videoId || !list) {
      return;
    }

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
        items.forEach(
            c => list.insertAdjacentHTML('afterbegin', renderComment(c)));
        page++;
      }

      // 댓글 수 업데이트
      updateCommentCount(items.length);

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

  // 댓글 등록 (인증 필수)
  async function submitComment(content) {
    if (!videoId) {
      return;
    }

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
        const currentCount = parseInt(commentCountEl.textContent) || 0;
        commentCountEl.textContent = currentCount + 1;
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
              `/api/videos/${videoId}/comments/${id}`,
              {method: 'DELETE'}
          );
          item.remove();

          // 댓글 수 업데이트
          const commentCountEl = document.getElementById('commentCount');
          if (commentCountEl) {
            const currentCount = parseInt(commentCountEl.textContent) || 0;
            if (currentCount > 0) {
              commentCountEl.textContent = currentCount - 1;
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

/**
 * 댓글 수 업데이트
 */
function updateCommentCount(count) {
  const commentCountElement = document.getElementById('commentCount');

  if (commentCountElement) {
    commentCountElement.textContent = formatNumber(count);
  }
}

/**
 * 숫자 포맷 (천 단위 콤마)
 */
function formatNumber(num) {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

/**
 * 날짜 포맷
 */
function formatDate(dateString) {
  if (!dateString) {
    return '';
  }

  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');

  return `${year}.${month}.${day} ${hours}:${minutes}`;
}

/**
 * 시간 포맷 (00:00 형식)
 */
function formatTime(seconds) {
  if (isNaN(seconds) || seconds === Infinity) {
    return '00:00';
  }

  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);

  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
}