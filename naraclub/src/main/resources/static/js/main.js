// main.js
import VideoService from './video-service.js';
import { optionalAuthFetch } from './commonFetch.js';

document.addEventListener('DOMContentLoaded', async function () {
  // 1) 최신 동영상
  await loadLatestVideo();

  // 2) 자유게시판 최신 4개 글
  await loadLatestPosts();

  // 3) '더보기' 클릭 이벤트
  initMoreLink();
});

/**
 * 최신 동영상을 로드하여 화면에 표시
 */
async function loadLatestVideo() {
  try {
    const hotTopicSection = document.querySelector('.hot-topic');
    hotTopicSection.innerHTML = '<div class="loading">동영상을 불러오는 중...</div>';

    const latestVideo = await VideoService.getLatestVideo();
    if (!latestVideo) {
      hotTopicSection.innerHTML = '<div class="no-content">현재 표시할 동영상이 없습니다.</div>';
      return;
    }

    updateHotTopicSection(latestVideo);
  } catch (error) {
    console.error('동영상 로드 중 오류 발생:', error);
    document.querySelector('.hot-topic')
      .innerHTML = '<div class="error">동영상을 불러오는 중 오류가 발생했습니다.</div>';
  }
}

/**
 * 최신 4개 게시글을 불러와 자유게시판 섹션을 업데이트
 */
async function loadLatestPosts() {
  const newsList = document.querySelectorAll('.news-section')[1]?.querySelector('.news-list');
  if (!newsList) return;

  // 로딩 표시 (선택 사항)
  newsList.innerHTML = '<p class="loading-text">게시글을 불러오는 중...</p>';

  try {
    const res = await optionalAuthFetch('/api/board/posts?page=0&size=4');
    const { response } = await res.json();
    const items = response.data || [];

    if (items.length === 0) {
      newsList.innerHTML = '<p class="empty-title">게시글이 없습니다</p>';
      return;
    }

    newsList.innerHTML = items.map(item => `
      <div class="news-item" data-id="${item.boardId}">
        <div class="news-title">${item.title}</div>
        <div class="news-comment">
          <i class="far fa-comment comment-icon"></i>
          <span class="comment-count">${item.commentCount}</span>
        </div>
      </div>
    `).join('');

    // 각 글 클릭 시 상세로 이동
    newsList.querySelectorAll('.news-item').forEach(el => {
      el.addEventListener('click', () => {
        window.location.href = `boardDetail.html?id=${el.dataset.id}`;
      });
    });
  } catch (err) {
    console.error('게시글 로드 오류:', err);
    newsList.innerHTML = '<p class="error">게시글을 불러오는 중 오류가 발생했습니다.</p>';
  }
}

/**
 * 자유게시판 ‘더보기’ 링크 클릭 시 목록 페이지로 이동
 */
function initMoreLink() {
  const section = document.querySelectorAll('.news-section')[1];
  const moreLink = section?.querySelector('.more-link');
  if (moreLink) {
    moreLink.addEventListener('click', e => {
      e.preventDefault();
      window.location.href = 'boardList.html';  // 실제 목록 페이지 경로에 맞춰주세요
    });
  }
}

/**
 * 핫토픽 섹션 업데이트 (기존 코드)
 */
function updateHotTopicSection(video) {
  const hotTopicSection = document.querySelector('.hot-topic');
  // ... (여기에 기존 updateHotTopicSection 로직 그대로)
}
