// boardDetailComments.js
// 댓글 섹션 로직 (스크롤 페이징, 조회/등록/수정/삭제, 인증 토큰 처리)

import { authFetch, optionalAuthFetch, handleFetchError, FetchError } from '../commonFetch.js';

document.addEventListener('DOMContentLoaded', () => {
  const votePostId = new URLSearchParams(window.location.search).get('id');
  const container = document.querySelector('.comments-wrapper');
  const list = document.querySelector('.comments-container');
  const noCommentsEl = document.querySelector('.no-comments');
  const input = document.getElementById('commentInput');
  const submitBtn = document.getElementById('submitCommentButton');
  const pageSize = 10;
  let page = 0;
  let loading = false;
  let done = false;

  // 초기 로드
  loadComments();

  // 스크롤 이벤트: 상단 근처에서 추가 로드
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

  // 댓글 등록
  submitBtn.addEventListener('click', () => {
    const content = input.value.trim();
    if (!content) return;
    submitComment(content);
    input.value = '';
  });

  // 댓글 목록 로드 (인증 선택)
  async function loadComments() {
    loading = true;
    try {
      const res = await optionalAuthFetch(
        `/api/vote/posts/${votePostId}/comments?page=${page}&size=${pageSize}`
      );
      const data = await res.json();
      const items = data.response.data || [];
      if (items.length === 0) {
        if (page === 0) {
          list.innerHTML = '';
          noCommentsEl.style.display = 'block';
        }
        done = true;
      } else {
        noCommentsEl.style.display = 'none';
        items.forEach(c => list.insertAdjacentHTML('afterbegin', renderComment(c)));
        page++;
      }
    } catch (err) {
      console.error('댓글 로드 오류:', err);
      handleFetchError(err);
      list.innerHTML = '';
      noCommentsEl.style.display = 'block';
      done = true;
    } finally {
      loading = false;
    }
  }

  // 댓글 등록 (인증 필수)
  async function submitComment(content) {
    try {
      const res = await authFetch(
        `/api/vote/posts/${votePostId}/comments`,
        { method: 'POST', body: JSON.stringify({ content }) }
      );
      const { response: comment } = await res.json();
      list.insertAdjacentHTML('beforeend', renderComment(comment));
      noCommentsEl.style.display = 'none';
    } catch (err) {
      console.error('댓글 등록 오류:', err);
      if (err instanceof FetchError && err.httpStatus === 401) {
        alert('댓글 등록을 위해 로그인 후 이용해주세요.');
        return window.location.href = '/login/login.html';
      }
      handleFetchError(err);
    }
  }

  // 수정/삭제 (인증 필수)
  list.addEventListener('click', async e => {
    const item = e.target.closest('.comment-item');
    if (!item) return;
    const id = item.dataset.id;
    if (e.target.classList.contains('delete-btn')) {
      try {
        await authFetch(
          `/api/vote/posts/${votePostId}/comments/${id}`,
          { method: 'DELETE' }
        );
        item.remove();
      } catch (err) {
        console.error('삭제 오류:', err);
        if (err instanceof FetchError && err.httpStatus === 401) {
          alert('삭제를 위해 로그인 후 이용해주세요.');
          return window.location.href = '/login/login.html';
        }
        handleFetchError(err);
      }
    } else if (e.target.classList.contains('edit-btn')) {
      const p = item.querySelector('.content');
      if (p.isContentEditable) {
        p.contentEditable = false;
        try {
          await authFetch(
            `/api/vote/posts/${votePostId}/comments/${id}`,
            { method: 'PUT', body: JSON.stringify({ content: p.textContent.trim() }) }
          );
        } catch (err) {
          if (err instanceof FetchError && err.httpStatus === 401) {
            alert('수정을 위해 로그인 후 이용해주세요.');
            return window.location.href = '/login/login.html';
          }
          handleFetchError(err); // 🚀 공통 에러 핸들러 호출
        }
      } else {
        p.contentEditable = true;
        p.focus();
      }
    }
  });

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
});