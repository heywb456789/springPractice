// assets/admin/js/boardList.js
import {
  adminAuthFetch,
  authFetch,
  optionalAuthFetch
} from '../../../js/commonFetch.js';

(async () => {
  'use strict';

  const boardContainer = document.getElementById('boardContainer');
  const paginationEl   = document.querySelector('.pagination');
  const searchTypeEl   = document.getElementById('searchType');
  const searchInputEl  = document.getElementById('searchKeyword');
  const deleteBtn      = document.getElementById('btnDelete');
  const confirmModal   = new bootstrap.Modal(document.getElementById('confirmModal'));

  let selectedIds = [];

  // ——— 1) 페이지 로딩 시, 검색 폼/삭제 버튼/페이징 초기화
  document.addEventListener('DOMContentLoaded', () => {
    initSearchForm();
    initDeleteFlow();
    loadBoardList();
  });

  // ——— 2) 비동기 게시글 목록 로딩
  async function loadBoardList(page = 0, size = 10, type = '', keyword = '') {
    // 로딩 스피너
    boardContainer.innerHTML = `<div class="text-center py-5">⏳ 불러오는 중…</div>`;

    const url = `/api/board/posts?page=${page}&size=${size}`
              + (type && keyword ? `&searchType=${type}&keyword=${encodeURIComponent(keyword)}` : '');

    try {
      const res  = await adminAuthFetch(url);
      const json = await res.json();
      renderBoardTable(json.response);
      updatePagination(json.response.pagination);
    } catch (err) {
      boardContainer.innerHTML = `<div class="text-danger text-center py-5">
        게시글을 불러오는 중 오류가 발생했습니다.
      </div>`;
      console.error(err);
    }
  }

  // ——— 3) HTML 그리기
  function renderBoardTable({ data }) {
    if (!data.length) {
      boardContainer.innerHTML = `<div class="text-center py-5">등록된 게시글이 없습니다.</div>`;
      return;
    }
    const rows = data.map(item => `
      <tr data-id="${item.boardId}">
        <td><input type="checkbox" class="form-check-input row-check" value="${item.boardId}"></td>
        <td>${item.boardId}</td>
        <td>
          <a href="#" data-id="${item.boardId}" class="board-link">${item.title}</a>
          ${item.commentCount > 0
            ? `<span class="badge bg-secondary ms-1">${item.commentCount}</span>` : ''}
        </td>
        <td>${item.authorName}</td>
        <td>${item.createdAt.substring(0,10)}</td>
        <td>${item.views}</td>
      </tr>
    `).join('');
    boardContainer.innerHTML = `
      <div class="table-responsive">
        <table class="table table-striped">
          <thead>…(헤더 동일)…</thead>
          <tbody>${rows}</tbody>
        </table>
      </div>`;
    initRowSelection();
  }

  // ——— 4) 페이징
  function updatePagination({ currentPage, totalPages }) {
    const display = 5;
    let start = Math.max(0, currentPage - Math.floor(display/2));
    let end   = Math.min(totalPages-1, start + display -1);
    if (end - start < display -1) start = Math.max(0, end - display +1);

    let html = `<li class="page-item${currentPage===0?' disabled':''}">
                  <a class="page-link" data-page="0">&laquo;&laquo;</a>
                </li>
                <li class="page-item${currentPage===0?' disabled':''}">
                  <a class="page-link" data-page="${currentPage-1}">&laquo;</a>
                </li>`;

    for (let i=start; i<=end; i++) {
      html += `<li class="page-item${i===currentPage?' active':''}">
                 <a class="page-link" data-page="${i}">${i+1}</a>
               </li>`;
    }

    html += `<li class="page-item${currentPage===totalPages-1?' disabled':''}">
               <a class="page-link" data-page="${currentPage+1}">&raquo;</a>
             </li>
             <li class="page-item${currentPage===totalPages-1?' disabled':''}">
               <a class="page-link" data-page="${totalPages-1}">&raquo;&raquo;</a>
             </li>`;

    paginationEl.innerHTML = html;
    document.querySelectorAll('.pagination .page-link').forEach(a => {
      if (!a.parentElement.classList.contains('disabled')) {
        a.addEventListener('click', () => loadBoardList(
          parseInt(a.dataset.page,10),
          10,
          searchTypeEl.value,
          searchInputEl.value.trim()
        ));
      }
    });
  }

  // ——— 5) 검색 폼
  function initSearchForm() {
    document.getElementById('btnSearch').addEventListener('click', () => {
      loadBoardList(0, 10,
        searchTypeEl.value,
        searchInputEl.value.trim()
      );
    });
    searchInputEl.addEventListener('keypress', e => {
      if (e.key==='Enter') {
        e.preventDefault();
        loadBoardList(0, 10,
          searchTypeEl.value,
          searchInputEl.value.trim()
        );
      }
    });
  }

  // ——— 6) 행 선택 & 삭제 흐름
  function initRowSelection() {
    selectedIds = [];
    document.querySelectorAll('.row-check').forEach(cb =>
      cb.addEventListener('change', () => {
        const tr = cb.closest('tr');
        cb.checked ? tr.classList.add('table-active')
                   : tr.classList.remove('table-active');

        selectedIds = Array.from(
          document.querySelectorAll('.row-check:checked'),
          x => x.value
        );
        deleteBtn.disabled = !selectedIds.length;
      })
    );
  }

  function initDeleteFlow() {
    deleteBtn.addEventListener('click', () => confirmModal.show());
    document.getElementById('btnConfirmDelete').addEventListener('click', async () => {
      await adminAuthFetch('/admin/board/delete', {
        method:'POST',
        headers:{'Content-Type':'application/json'},
        body: JSON.stringify({ ids: selectedIds })
      });
      confirmModal.hide();
      loadBoardList(); // 목록 갱신
    });
  }
})();
