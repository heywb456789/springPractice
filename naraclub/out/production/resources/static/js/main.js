import VideoService from './video-service.js';

document.addEventListener('DOMContentLoaded', async function() {

  // 최신 동영상 불러오기
  await loadLatestVideo();
});

/**
 * 최신 동영상을 로드하여 화면에 표시
 */
async function loadLatestVideo() {
  try {
    // 로딩 상태 표시
    const hotTopicSection = document.querySelector('.hot-topic');
    if (hotTopicSection) {
      hotTopicSection.innerHTML = '<div class="loading">동영상을 불러오는 중...</div>';
    }

    // API에서 최신 비디오 가져오기
    const latestVideo = await VideoService.getLatestVideo();
    
    // 비디오가 없는 경우 처리
    if (!latestVideo) {
      if (hotTopicSection) {
        hotTopicSection.innerHTML = '<div class="no-content">현재 표시할 동영상이 없습니다.</div>';
      }
      return;
    }
    
    // 비디오 정보로 핫토픽 섹션 업데이트
    updateHotTopicSection(latestVideo);
    
  } catch (error) {
    console.error('동영상 로드 중 오류 발생:', error);
    const hotTopicSection = document.querySelector('.hot-topic');
    if (hotTopicSection) {
      hotTopicSection.innerHTML = '<div class="error">동영상을 불러오는 중 오류가 발생했습니다.</div>';
    }
  }
}

/**
 * 동영상 정보로 핫토픽 섹션 업데이트
 * @param {Object} video - 비디오 정보 객체
 */
function updateHotTopicSection(video) {
  const hotTopicSection = document.querySelector('.hot-topic');
  if (!hotTopicSection) return;
  
  // 유튜브 미리보기 또는 썸네일 이미지로 표시
  let content = '';
  
  // 동영상 재생 가능한 형태로 표시 (선택 사항)
  const useEmbeddedPlayer = false; // 임베디드 플레이어 사용 여부 (true/false)
  
  if (useEmbeddedPlayer) {
    // YouTube iframe으로 표시 (클릭 시 바로 재생)
    content = `
      <div class="video-container">
        <iframe 
          width="100%" 
          height="200" 
          src="${video.youtubeEmbedUrl}?rel=0" 
          title="${video.title}" 
          frameborder="0" 
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
          allowfullscreen>
        </iframe>
      </div>
    `;
  } else {
    // 썸네일 이미지와 재생 버튼으로 표시 (클릭 시 새 탭에서 열기)
    content = `
      <a href="${video.url}" target="_blank" class="video-thumbnail">
        <img src="${video.thumbnailUrl}" alt="${video.title}" />
        <div class="play-button">
          <i class="fas fa-play-circle"></i>
        </div>
        ${video.duration ? `<div class="video-duration">${video.getFormattedDuration()}</div>` : ''}
      </a>
    `;
  }
  
  // 제목과 함께 표시
  content += `
    <div class="hot-topic-title">
      ${video.title}
    </div>
  `;
  
  hotTopicSection.innerHTML = content;
  
  // 이벤트 핸들러 추가 (썸네일 클릭 시 동영상 페이지로 이동 등)
  const videoThumbnail = hotTopicSection.querySelector('.video-thumbnail');
  if (videoThumbnail) {
    videoThumbnail.addEventListener('click', function(e) {
      // 새 탭으로 열기 이외의 동작이 필요하면 여기서 처리
      // e.preventDefault();
      // 예: 모달로 열기 등
    });
  }
}