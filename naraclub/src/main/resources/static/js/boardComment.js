// boardDetailComments.js
// 댓글 섹션 로직 (스크롤 페이징, 조회/등록/수정/삭제, 인증 토큰 처리)

import {handleTokenRefresh} from './common.js';

document.addEventListener('DOMContentLoaded', () => {
  const postId = new URLSearchParams(window.location.search).get('id');
  const container = document.querySelector('.comments-wrapper');
  const list = document.querySelector('.comments-container');
  const noCommentsEl = document.querySelector('.no-comments');
  const input = document.getElementById('commentInput');
  const submitBtn = document.getElementById('submitCommentButton');
  const commentSize = 10;
  let commentPage = 0;
  let loading = false;
  let done = false;
  const currentUserId = Number(localStorage.getItem('memberId'));

  // 초기 댓글 로드
  loadComments();

  // 스크롤 이벤트: 상단 근처에서 추가 로드 (수정)
  container.addEventListener('scroll', () => {
    // 스크롤이 거의 맨 위에 도달했을 때만 추가 로드
    if (!loading && !done && container.scrollTop < 30) {
      // 현재 스크롤 위치와 콘텐츠 높이 기록
      const oldScrollHeight = container.scrollHeight;

      loadComments().then(() => {
        // 새 콘텐츠가 로드된 후 스크롤 위치 조정
        if (container.scrollHeight > oldScrollHeight) {
          container.scrollTop = container.scrollHeight - oldScrollHeight
              + container.scrollTop;
        }
      });
    }
  });

  // 댓글 등록 버튼 이벤트
  submitBtn.addEventListener('click', () => {
    const content = input.value.trim();
    if (!content) {
      return;
    }
    submitComment(content);
    input.value = '';
  });

  // 댓글 목록 로드 함수
  async function loadComments() {
    loading = true;
    try {
      const res = await tryAuthorizedFetch(
          `/api/board/posts/${postId}/comments?page=${commentPage}&size=${commentSize}`
      );
      const data = await res.json();
      const items = data.response.data;
      // 초기 로드 또는 추가 로드 시 댓글 없을 경우 처리
      if (items.length === 0) {
        if (commentPage === 0) {
          // 첫 페이지에서 댓글 없으면 메시지 표시
          list.innerHTML = '';
          noCommentsEl.style.display = 'block';
        }
        done = true;
      } else {
        // 댓글이 있으면 목록에 추가 및 메시지 숨김
        noCommentsEl.style.display = 'none';
        items.forEach(
            c => list.insertAdjacentHTML('afterbegin', renderComment(c)));
        commentPage++;
      }
    } catch (err) {
      console.error('댓글 로드 오류:', err);
      // 조회 실패 시에도 '댓글이 없습니다' 메시지 표시
      list.innerHTML = '';
      noCommentsEl.style.display = 'block';
      done = true;
    } finally {
      loading = false;
    }
  }

  // 댓글 등록 함수 (인증 필요)
  async function submitComment(content) {
    try {
      const res = await authorizedFetch(
          `/api/board/posts/${postId}/comments`,
          {method: 'POST', body: JSON.stringify({content})}
      );
      const data = await res.json();
      list.insertAdjacentHTML('beforeend', renderComment(data.response));
      noCommentsEl.style.display = 'none';
    } catch (err) {
      console.error('댓글 등록 오류:', err);
      alert('댓글 등록을 위해 로그인 후 이용해주세요.');
    }
  }

  // 수정/삭제 이벤트 위임 (인증 필요)
  list.addEventListener('click', async e => {
    const item = e.target.closest('.comment-item');
    if (!item) {
      return;
    }
    const commentId = item.dataset.id;
    if (e.target.classList.contains('delete-btn')) {
      try {
        await authorizedFetch(
            `/api/board/posts/${postId}/comments/${commentId}`,
            {method: 'DELETE'}
        );
        item.remove();
      } catch (err) {
        console.error('삭제 오류:', err);
        alert('삭제를 위해 로그인 후 이용해주세요.');
      }
    } else if (e.target.classList.contains('edit-btn')) {
      const p = item.querySelector('.content');
      if (p.isContentEditable) {
        p.contentEditable = false;
        try {
          await authorizedFetch(
              `/api/board/posts/${postId}/comments/${commentId}`,
              {
                method: 'PUT',
                body: JSON.stringify({content: p.textContent.trim()})
              }
          );
        } catch (err) {
          console.error('수정 오류:', err);
          alert('수정을 위해 로그인 후 이용해주세요.');
        }
      } else {
        p.contentEditable = true;
        p.focus();
      }
    }
  });

  // 일반 조회 시도, 토큰 있으면 갱신 후 인증 요청, 실패 시 일반 조회
  async function tryAuthorizedFetch(url) {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      return fetch(url);
    }
    try {
      return await authorizedFetch(url);
    } catch (err) {
      console.warn('토큰 갱신 실패, 일반 조회로 대체:', err);
      return fetch(url);
    }
  }

  // 인증 요청 유틸
  async function authorizedFetch(url, options = {}) {
    let res = await fetch(url, {
      method: options.method || 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
      },
      credentials: 'include',
      body: options.body
    });
    if (res.status === 401) {
      await handleTokenRefresh();
      res = await fetch(url, {
        ...options,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        },
        credentials: 'include'
      });
    }
    if (!res.ok) {
      throw new Error(`status ${res.status}`);
    }
    return res;
  }

  // 댓글 렌더링 함수
  function renderComment(c) {
    return `
      <div class="comment-item" data-id="${c.commentId}">
        <div>
          <strong>${c.authorName}</strong> <small>${new Date(
        c.createdAt).toLocaleString('ko-KR')}</small>
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
