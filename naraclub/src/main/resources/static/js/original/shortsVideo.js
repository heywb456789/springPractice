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
let videoType = null; // API 호출용 (사용하지 않음)
let shareVideoType = null; // 공유 기능용
let isLiked = false;
let isBookmarked = false;
let isDescriptionExpanded = false;
let isYouTubeVideo = false;

// 자동재생 관련 변수
let userHasInteracted = false;
let autoplayQueue = [];

// URL 정리 함수 - type 파라미터 제거
(function cleanupUrl() {
  console.log('URL 정리 시작:', window.location.href);

  const urlParams = new URLSearchParams(window.location.search);
  let urlChanged = false;

  // type 파라미터 제거
  if (urlParams.has('type')) {
    console.log('⚠️ type 파라미터 발견 및 제거:', urlParams.get('type'));
    urlParams.delete('type');
    urlChanged = true;
  }

  // URL 업데이트
  if (urlChanged) {
    const cleanUrl = urlParams.toString()
      ? `${window.location.pathname}?${urlParams.toString()}`
      : window.location.pathname;

    window.history.replaceState({}, '', cleanUrl);
    console.log('✅ URL 정리 완료:', cleanUrl);
  }
})();

// 문서 로드 완료 시 실행
document.addEventListener('DOMContentLoaded', function () {
  // 요소 참조
  videoPlayer = document.getElementById('videoPlayer');
  shareModal = new bootstrap.Modal(document.getElementById('shareModal'));
  commentsModal = new bootstrap.Modal(document.getElementById('commentsModal'));

  // 초기화
  initBackButton();
  detectUserInteraction();
  loadVideoData();
  initActionButtons();
  initMuteToggle();
  initShareFeatures();
  initComments();
});

/**
 * 사용자 상호작용 감지 및 자동재생 활성화
 */
function detectUserInteraction() {
  const events = ['click', 'touchstart', 'keydown'];

  function handleFirstInteraction() {
    userHasInteracted = true;
    console.log('사용자 상호작용 감지됨 - 자동재생 활성화');

    // 대기 중인 자동재생 실행
    autoplayQueue.forEach(callback => callback());
    autoplayQueue = [];

    // 이벤트 리스너 제거
    events.forEach(event => {
      document.removeEventListener(event, handleFirstInteraction);
    });
  }

  events.forEach(event => {
    document.addEventListener(event, handleFirstInteraction, { once: true });
  });
}

/**
 * Intersection Observer를 이용한 화면 진입 시 자동재생
 */
function setupAutoplayObserver(iframe, thumbnailContainer, embedUrl, youtubeId) {
  if (!('IntersectionObserver' in window)) {
    setTimeout(() => {
      tryAutoPlayYouTube(iframe, thumbnailContainer, embedUrl, youtubeId);
    }, 1000);
    return;
  }

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting && entry.intersectionRatio > 0.3) {
        console.log('비디오가 화면에 보임 - 자동재생 시도');

        if (userHasInteracted) {
          tryAutoPlayYouTube(iframe, thumbnailContainer, embedUrl, youtubeId);
        } else {
          autoplayQueue.push(() => {
            tryAutoPlayYouTube(iframe, thumbnailContainer, embedUrl, youtubeId);
          });

          setTimeout(() => {
            tryAutoPlayYouTube(iframe, thumbnailContainer, embedUrl, youtubeId);
          }, 500);
        }

        observer.unobserve(entry.target);
      }
    });
  }, {
    threshold: 0.3
  });

  const videoWrapper = document.querySelector('.video-wrapper');
  if (videoWrapper) {
    observer.observe(videoWrapper);
  }
}

/**
 * YouTube 자동재생 시도
 */
function tryAutoPlayYouTube(iframe, thumbnailContainer, embedUrl, youtubeId) {
  console.log('YouTube 자동재생 시도');

  const videoWrapper = document.querySelector('.video-wrapper');

  try {
    if (thumbnailContainer) {
      thumbnailContainer.classList.add('auto-playing');
    }

    hidePlayOverlay();

    if (iframe) {
      iframe.style.display = 'block';

      const loadingTimeout = setTimeout(() => {
        console.log('YouTube 로딩 타임아웃 - 로딩 상태 해제');
        if (videoWrapper) {
          videoWrapper.classList.remove('loading');
        }
        hideLoading();
      }, 5000);

      iframe.onload = function() {
        console.log('YouTube iframe 로드 완료');
        clearTimeout(loadingTimeout);

        if (videoWrapper) {
          videoWrapper.classList.remove('loading');
          videoWrapper.classList.add('auto-playing');
        }
        hideLoading();

        if (thumbnailContainer) {
          thumbnailContainer.style.display = 'none';
        }

        isPlaying = true;
      };

      iframe.onerror = function() {
        console.log('YouTube iframe 로드 실패 - 썸네일 표시');
        clearTimeout(loadingTimeout);
        handleAutoplayFailure(thumbnailContainer, videoWrapper);
      };

      console.log('YouTube 자동재생 iframe 설정 완료:', iframe.src);
    }
  } catch (error) {
    console.log('YouTube 자동재생 실패:', error);
    handleAutoplayFailure(thumbnailContainer, videoWrapper);
  }
}

/**
 * 자동재생 실패 시 처리
 */
function handleAutoplayFailure(thumbnailContainer, videoWrapper) {
  if (videoWrapper) {
    videoWrapper.classList.remove('loading', 'auto-playing');
  }
  hideLoading();

  if (thumbnailContainer) {
    thumbnailContainer.classList.remove('auto-playing');
    thumbnailContainer.style.display = 'flex';
  }

  const playOverlay = document.getElementById('playOverlay');
  if (playOverlay) {
    playOverlay.classList.add('autoplay-failed');
    showPlayOverlay();

    setTimeout(() => {
      playOverlay.classList.remove('autoplay-failed');
    }, 3000);
  }
}

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
        let tabType = 'shorts';
        if (videoData && videoData.type) {
          tabType = videoData.type === 'YOUTUBE_SHORTS' ? 'shorts' : 'video';
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
 * 유튜브 동영상 ID 추출 함수
 */
function extractYouTubeId(url) {
  if (!url) return null;

  const cleanInput = url.trim();

  try {
    // 이미 ID만 있는 경우 (11자리)
    if (cleanInput.length === 11 && /^[a-zA-Z0-9_-]{11}$/.test(cleanInput)) {
      return cleanInput;
    }

    // 다양한 YouTube URL 패턴 처리
    const patterns = [
      /(?:youtube\.com\/watch\?v=)([a-zA-Z0-9_-]{11})/,
      /(?:youtu\.be\/)([a-zA-Z0-9_-]{11})/,
      /(?:youtube\.com\/embed\/)([a-zA-Z0-9_-]{11})/,
      /(?:m\.youtube\.com\/watch\?v=)([a-zA-Z0-9_-]{11})/,
      /(?:youtube\.com\/shorts\/)([a-zA-Z0-9_-]{11})/,
      /(?:youtu\.be\/shorts\/)([a-zA-Z0-9_-]{11})/
    ];

    for (const pattern of patterns) {
      const match = cleanInput.match(pattern);
      if (match && match[1]) {
        return match[1];
      }
    }
  } catch (error) {
    console.error('YouTube ID 추출 실패:', error);
  }

  return null;
}

/**
 * YouTube URL이 Shorts인지 확인
 */
function isYouTubeShorts(url) {
  if (!url) return false;
  const cleanUrl = url.trim().toLowerCase();
  return cleanUrl.includes('youtube.com/shorts/') || cleanUrl.includes('youtu.be/shorts/');
}

/**
 * YouTube embed URL 생성
 */
function createEmbedUrl(youtubeId) {
  if (!youtubeId) return null;
  return `https://www.youtube.com/embed/${youtubeId}`;
}

/**
 * YouTube iframe 생성
 */
function createYouTubeIframe(youtubeId, embedUrl, isShorts) {
  const youtubeIframe = document.createElement('iframe');
  youtubeIframe.id = 'youtubePlayer';
  youtubeIframe.className = 'youtube-player';

  const params = new URLSearchParams({
    rel: '0',
    modestbranding: '1',
    controls: '1',
    loop: '1',
    playlist: youtubeId,
    playsinline: '1',
    autoplay: '1',
    mute: '1',
    enablejsapi: '1'
  });

  if (isShorts) {
    params.set('iv_load_policy', '3');
    params.set('cc_load_policy', '0');
  }

  youtubeIframe.src = `${embedUrl}?${params.toString()}`;
  youtubeIframe.frameBorder = '0';
  youtubeIframe.allow = 'accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share';
  youtubeIframe.allowFullscreen = true;
  youtubeIframe.style.display = 'none';
  youtubeIframe.style.position = 'absolute';
  youtubeIframe.style.top = '0';
  youtubeIframe.style.left = '0';
  youtubeIframe.style.width = '100%';
  youtubeIframe.style.height = '100%';

  if (isShorts) {
    youtubeIframe.style.objectFit = 'cover';
    youtubeIframe.classList.add('youtube-shorts');

    const videoWrapper = document.querySelector('.video-wrapper');
    if (videoWrapper) {
      videoWrapper.classList.add('youtube-shorts-mode');
    }
  } else {
    youtubeIframe.style.objectFit = 'contain';
    youtubeIframe.classList.add('youtube-video');

    const videoWrapper = document.querySelector('.video-wrapper');
    if (videoWrapper) {
      videoWrapper.classList.add('youtube-video-mode');
    }
  }

  return youtubeIframe;
}

/**
 * 동영상 타입 판별 함수
 */
function determineVideoType(data) {
  console.log('비디오 타입 판별 시작:', data);

  // type이 YouTube 관련이면서 youtubeId가 존재하는 경우만 YouTube로 판단
  if (data.type && (data.type === 'YOUTUBE_VIDEO' || data.type === 'YOUTUBE_SHORTS')) {
    if (data.youtubeId || data.youtubeVideoId) {
      console.log('✅ YouTube 비디오 (type + youtubeId):', data.type, data.youtubeId);
      return true;
    } else {
      console.log('✅ 직접 업로드 비디오 (type은 YouTube이지만 youtubeId 없음):', data.type);
      return false;
    }
  }

  console.log('✅ 일반 업로드 비디오 (type:', data.type + ')');
  return false;
}

/**
 * Shorts 여부 판별
 */
function determineIfShorts(data) {
  // 1. type 필드 우선 확인
  if (data.type === 'YOUTUBE_SHORTS') {
    return true;
  }

  // 2. URL에서 확인
  const urlToCheck = data.videoUrl;
  if (urlToCheck && isYouTubeShorts(urlToCheck)) {
    return true;
  }

  return false;
}

/**
 * 비디오 데이터 로드
 */
async function loadVideoData() {
  const videoId = getVideoIdFromUrl();
  if (!videoId) {
    console.error('❌ 비디오 ID가 없습니다.');
    alert('잘못된 접근입니다.');
    return window.location.href = '/original/originalContent.html';
  }

  try {
    showLoading();

    // videoID에서 불필요한 파라미터 제거
    const cleanVideoId = videoId.toString().split('?')[0];

    // type 파라미터 없이 깔끔한 API 호출
    const apiUrl = `/api/videos/${cleanVideoId}`;
    console.log('📡 API 호출:', apiUrl);

    const response = await optionalAuthFetch(apiUrl);
    const data = await response.json();

    if (!data?.response) {
      throw new Error('Invalid video data');
    }

    videoData = data.response;
    console.log('✅ 비디오 데이터 로드 성공:', videoData);

    // 유튜브 영상인지 판별
    isYouTubeVideo = determineVideoType(videoData);
    console.log('📱 YouTube 비디오 여부:', isYouTubeVideo);

    updateVideoUI(videoData);

    // 플레이어 초기화
    if (isYouTubeVideo) {
      console.log('🎬 YouTube 플레이어 초기화');
      initYouTubePlayer(videoData);
    } else {
      console.log('🎬 일반 비디오 플레이어 초기화');
      initVideoPlayer();
    }

  } catch (error) {
    console.error('❌ 비디오 데이터 로드 오류:', error);
    handleFetchError(error);
  } finally {
    hideLoading();
  }
}

/**
 * 비디오 UI 업데이트
 */
function updateVideoUI(data) {
  // 공유용 비디오 타입 설정 (공유 API에서 사용)
  shareVideoType = data.type || (isYouTubeVideo ? 'YOUTUBE_VIDEO' : 'UPLOADED');

  // 타이틀 업데이트
  const titleElement = document.getElementById('videoTitle');
  if (titleElement) {
    titleElement.textContent = data.title;
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
 * 유튜브 플레이어 초기화
 */
function initYouTubePlayer(data) {
  const videoWrapper = document.querySelector('.video-wrapper');
  const thumbnailContainer = document.getElementById('thumbnailContainer');
  const videoPlayerElement = document.getElementById('videoPlayer');
  const muteToggleButton = document.getElementById('muteToggleButton');

  // 유튜브 ID 추출
  let youtubeId = null;

  if (data.youtubeId) {
    youtubeId = data.youtubeId;
  } else if (data.youtubeVideoId) {
    youtubeId = data.youtubeVideoId;
  } else if (data.videoUrl) {
    youtubeId = extractYouTubeId(data.videoUrl);
  } else if (data.videoId && data.videoId.length === 11) {
    youtubeId = data.videoId;
  }

  if (!youtubeId) {
    console.error('유튜브 ID를 찾을 수 없습니다. 데이터:', data);
    hideLoading();
    return;
  }

  console.log('YouTube ID 추출 성공:', youtubeId);

  // 기존 video 요소 숨김
  if (videoPlayerElement) {
    videoPlayerElement.style.display = 'none';
  }

  // 음소거 토글 버튼 숨김 (유튜브는 자체 컨트롤 사용)
  if (muteToggleButton) {
    muteToggleButton.style.display = 'none';
  }

  // 썸네일 설정
  const thumbnailElement = document.getElementById('videoThumbnail');
  if (thumbnailElement) {
    const thumbnailUrl = data.thumbnailUrl || `https://img.youtube.com/vi/${youtubeId}/hqdefault.jpg`;
    thumbnailElement.src = thumbnailUrl;
    thumbnailElement.alt = data.title || '유튜브 동영상';
  }

  // Shorts 여부 확인
  const isShorts = determineIfShorts(data);
  console.log('Shorts 여부:', isShorts, 'Type:', data.type);

  // embed URL 생성
  const embedUrl = createEmbedUrl(youtubeId);
  if (!embedUrl) {
    console.error('Embed URL 생성 실패');
    hideLoading();
    return;
  }

  // 유튜브 iframe 생성
  const youtubeIframe = createYouTubeIframe(youtubeId, embedUrl, isShorts);

  // iframe을 wrapper에 추가
  if (videoWrapper) {
    videoWrapper.appendChild(youtubeIframe);
  }

  // Intersection Observer로 자동재생 설정
  setupAutoplayObserver(youtubeIframe, thumbnailContainer, embedUrl, youtubeId);

  // 썸네일 클릭 이벤트
  if (thumbnailContainer) {
    thumbnailContainer.addEventListener('click', function() {
      startYouTubePlayback(youtubeIframe, thumbnailContainer, embedUrl, youtubeId);
    });
  }

  // 재생 버튼 클릭 이벤트
  const playOverlay = document.getElementById('playOverlay');
  if (playOverlay) {
    playOverlay.addEventListener('click', function() {
      startYouTubePlayback(youtubeIframe, thumbnailContainer, embedUrl, youtubeId);
    });
  }

  console.log('YouTube 플레이어 초기화 완료:', {
    youtubeId,
    embedUrl,
    isShorts
  });
}

/**
 * 유튜브 재생 시작
 */
function startYouTubePlayback(iframe, thumbnailContainer, embedUrl, youtubeId) {
  console.log('YouTube 수동 재생 시작:', { embedUrl, youtubeId });

  hideLoading();
  const videoWrapper = document.querySelector('.video-wrapper');
  if (videoWrapper) {
    videoWrapper.classList.remove('loading');
  }

  // 썸네일 숨기기
  if (thumbnailContainer) {
    thumbnailContainer.style.display = 'none';
  }

  // 재생 오버레이 숨기기
  hidePlayOverlay();

  // 유튜브 iframe 표시
  if (iframe) {
    iframe.style.display = 'block';
    console.log('YouTube iframe 표시됨:', iframe.src);
  }

  isPlaying = true;
}

/**
 * 일반 비디오 플레이어 초기화
 */
function initVideoPlayer() {
  const thumbnailContainer = document.getElementById('thumbnailContainer');
  const playOverlay = document.getElementById('playOverlay');

  console.log('일반 비디오 플레이어 초기화 시작:', videoData);

  // 썸네일 설정
  const thumbnailElement = document.getElementById('videoThumbnail');
  if (thumbnailElement && videoData) {
    const thumbnailUrl = videoData.thumbnailUrl || '/images/default-thumbnail.jpg';
    thumbnailElement.src = thumbnailUrl;
    thumbnailElement.alt = videoData.title;
    console.log('썸네일 설정:', thumbnailUrl);
  }

  // 비디오 소스 설정
  if (videoPlayer && videoData) {
    const videoUrl = videoData.videoUrl || videoData.url || videoData.src;
    if (videoUrl) {
      videoPlayer.src = videoUrl;
      videoPlayer.poster = videoData.thumbnailUrl;
      console.log('비디오 소스 설정:', videoUrl);
    } else {
      console.error('비디오 URL을 찾을 수 없습니다:', videoData);
      return;
    }
  }

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
    videoPlayer.muted = true;
    videoPlayer.loop = true;
    videoPlayer.autoplay = true;

    // 비디오 클릭 시 재생/일시정지
    videoPlayer.addEventListener('click', togglePlayPause);

    // 재생 시작 시
    videoPlayer.addEventListener('play', function () {
      console.log('비디오 재생 시작');
      isPlaying = true;
      hidePlayOverlay();
    });

    // 일시 정지 시
    videoPlayer.addEventListener('pause', function () {
      console.log('비디오 일시정지');
      isPlaying = false;
      showPlayOverlay();
    });

    // 재생 완료 시
    videoPlayer.addEventListener('ended', function () {
      console.log('비디오 재생 완료');
      if (!videoPlayer.loop) {
        videoPlayer.currentTime = 0;
        videoPlayer.play();
      }
    });

    // 로딩 시작
    videoPlayer.addEventListener('loadstart', function() {
      console.log('비디오 로딩 시작');
      showLoading();
    });

    // 재생 가능할 때 - 자동 재생 시도
    videoPlayer.addEventListener('canplay', function() {
      console.log('비디오 재생 가능');
      hideLoading();
      autoPlayVideo();
    });

    // 메타데이터 로드 완료 시에도 자동 재생 시도
    videoPlayer.addEventListener('loadedmetadata', function() {
      console.log('비디오 메타데이터 로드 완료');
      autoPlayVideo();
    });

    // 에러 처리
    videoPlayer.addEventListener('error', function(e) {
      console.error('비디오 로드 에러:', e);
      hideLoading();
      alert('동영상을 불러올 수 없습니다.');
    });
  }
}

/**
 * 자동 재생 시도 (직접 업로드 영상만)
 */
function autoPlayVideo() {
  if (!videoPlayer || isPlaying || isYouTubeVideo) return;

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
          console.log('자동 재생 시작됨');
          isPlaying = true;
          hidePlayOverlay();
          updateMuteToggleButton();
        })
        .catch(error => {
          console.log('자동 재생 실패, 사용자 상호작용 필요:', error);
          showThumbnail();
          showPlayOverlay();
        });
    }
  }
}

/**
 * 재생 시작 (썸네일 클릭 시 - 직접 업로드 영상)
 */
function startPlayback(event) {
  event.preventDefault();
  event.stopPropagation();

  if (isYouTubeVideo) return;

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
    updateMuteToggleButton();

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
 * 재생/일시정지 토글 (직접 업로드 영상만)
 */
function togglePlayPause(event) {
  event.preventDefault();
  event.stopPropagation();

  if (!videoPlayer || isYouTubeVideo) return;

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
  const youtubePlayer = document.getElementById('youtubePlayer');

  // 유튜브 플레이어 숨기기
  if (youtubePlayer) {
    youtubePlayer.style.display = 'none';
  }

  // 일반 비디오 플레이어 숨기기
  if (videoPlayer && !isYouTubeVideo) {
    videoPlayer.style.display = 'none';
  }

  // 썸네일 표시
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
 * 음소거 토글 버튼 초기화 (직접 업로드 영상만)
 */
function initMuteToggle() {
  const muteToggleButton = document.getElementById('muteToggleButton');

  if (muteToggleButton) {
    muteToggleButton.addEventListener('click', function() {
      if (!videoPlayer || isYouTubeVideo) return;

      videoPlayer.muted = !videoPlayer.muted;
      updateMuteToggleButton();
    });

    updateMuteToggleButton();
  }
}

/**
 * 음소거 토글 버튼 UI 업데이트 (직접 업로드 영상만)
 */
function updateMuteToggleButton() {
  const muteToggleButton = document.getElementById('muteToggleButton');

  if (muteToggleButton) {
    if (isYouTubeVideo) {
      muteToggleButton.style.display = 'none';
      return;
    }

    muteToggleButton.style.display = 'flex';

    if (videoPlayer) {
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

  // 좋아요 수 업데이트
  if (likeCountElement) {
    const currentCount = parseInt(likeCountElement.textContent.replace(/,/g, '').replace('만', '0000')) || 0;
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
    const isOverflowing = descriptionElement.scrollHeight > descriptionElement.clientHeight;

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
        type: shareVideoType, // 공유용 타입 사용
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
        type: shareVideoType.toUpperCase(), // 공유용 타입 사용
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
    function updateSubmitButton() {
      const hasContent = input.value.trim().length > 0;
      submitBtn.disabled = !hasContent;
      submitBtn.textContent = hasContent ? '게시' : '게시';
    }

    input.addEventListener('input', updateSubmitButton);
    updateSubmitButton();

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
      // videoId가 깨끗한지 확인하고 댓글 API 호출
      const cleanVideoId = videoId.toString().split('?')[0]; // ? 이후 제거
      const commentApiUrl = `/api/videos/${cleanVideoId}/comments?page=${page}&size=${pageSize}`;
      console.log('댓글 API 호출:', commentApiUrl);

      const res = await optionalAuthFetch(commentApiUrl);
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
      // videoId가 깨끗한지 확인
      const cleanVideoId = videoId.toString().split('?')[0]; // ? 이후 제거
      const submitApiUrl = `/api/videos/${cleanVideoId}/comments`;
      console.log('댓글 등록 API 호출:', submitApiUrl);

      const res = await authFetch(
        submitApiUrl,
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
        const currentCount = parseInt(commentCountEl.textContent.replace(/,/g, '').replace('만', '0000')) || 0;
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
          // videoId가 깨끗한지 확인
          const cleanVideoId = videoId.toString().split('?')[0]; // ? 이후 제거

          await authFetch(
            `/api/videos/${cleanVideoId}/comments/${id}`,
            {method: 'DELETE'}
          );
          item.remove();

          // 댓글 수 업데이트
          const commentCountEl = document.getElementById('commentCount');
          if (commentCountEl) {
            const currentCount = parseInt(commentCountEl.textContent.replace(/,/g, '').replace('만', '0000')) || 0;
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
            // videoId가 깨끗한지 확인
            const cleanVideoId = videoId.toString().split('?')[0]; // ? 이후 제거

            await authFetch(
              `/api/videos/${cleanVideoId}/comments/${id}`,
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