// assets/admin/js/videoList.js
import { adminAuthFetch } from '../../../js/commonFetch.js';

document.addEventListener('DOMContentLoaded', () => {
  const table       = document.getElementById('videoTable');
  const checkAll    = document.getElementById('checkAll');
  const deleteBtn   = document.getElementById('btnDelete');
  const confirmModal= new bootstrap.Modal(document.getElementById('confirmModal'));
  let selectedIds   = [];

  // 1) 전체 선택 / 해제
  checkAll?.addEventListener('change', e => {
    const checked = e.target.checked;
    document.querySelectorAll('.row-check').forEach(cb => {
      cb.checked = checked;
      toggleRowHighlight(cb.closest('tr'), checked);
    });
    updateDeleteBtn();
  });

  // 2) 각 행 체크박스 클릭
  document.querySelectorAll('.row-check').forEach(cb => {
    cb.addEventListener('change', () => {
      toggleRowHighlight(cb.closest('tr'), cb.checked);
      updateDeleteBtn();
      syncCheckAll();
    });

    // 체크박스 클릭 시 이벤트 전파 중지 (행 클릭 이벤트와 겹치지 않도록)
    cb.addEventListener('click', (e) => {
      e.stopPropagation();
    });
  });

  // 3) 행 클릭 시 체크박스 토글
  table?.querySelectorAll('tbody tr').forEach(tr => {
    tr.addEventListener('click', (e) => {
      // 링크나 이미지 클릭 시에는 동작하지 않음
      if (e.target.tagName === 'A' || e.target.closest('a') ||
          e.target.tagName === 'IMG' || e.target.closest('img')) {
        return;
      }

      const checkbox = tr.querySelector('.row-check');
      if (checkbox) {
        checkbox.checked = !checkbox.checked;
        toggleRowHighlight(tr, checkbox.checked);
        updateDeleteBtn();
        syncCheckAll();
      }
    });
  });

  // 4) 더블클릭 시 상세 페이지로 이동
  table?.querySelectorAll('tbody tr').forEach(tr => {
    tr.addEventListener('dblclick', () => {
      const id = tr.getAttribute('data-id');
      if (id) window.location.href = `/admin/video/${id}`;
    });
  });

  // 5) 삭제 버튼 클릭 → 모달 띄우기
  deleteBtn?.addEventListener('click', () => {
    selectedIds = Array.from(
      document.querySelectorAll('.row-check:checked'),
      cb => cb.value
    );
    if (selectedIds.length) confirmModal.show();
  });

  // 6) 모달에서 확인 클릭 → 삭제 API 호출 후 새로고침
  document.getElementById('btnConfirmDelete')?.addEventListener('click', async () => {
    try {
      await adminAuthFetch('/admin/video/delete', {
        method: 'POST',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify({ ids: selectedIds })
      });
      location.reload();
    } catch (err) {
      console.error(err);
      alert('삭제 중 오류가 발생했습니다.');
    }
  });

  // 7) 검색 기능
  const searchTypeSelect = document.getElementById('searchType');
  const searchKeywordInput = document.getElementById('searchKeyword');
  const searchButton = document.getElementById('btnSearch');

  // URL 파라미터에서 검색 조건 가져오기
  const urlParams = new URLSearchParams(window.location.search);
  const searchType = urlParams.get('searchType');
  const keyword = urlParams.get('searchText');

  // 검색 조건 설정
  if (searchType && searchTypeSelect) {
    searchTypeSelect.value = searchType;
  }

  if (keyword && searchKeywordInput) {
    searchKeywordInput.value = keyword;
  }

  // 검색 버튼 클릭 이벤트
  searchButton?.addEventListener('click', () => {
    performSearch();
  });

  // 검색어 입력 필드에서 엔터 키 이벤트
  searchKeywordInput?.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      performSearch();
    }
  });

  // Helper: 행 강조
  function toggleRowHighlight(tr, on) {
    if (tr) tr.classList.toggle('table-active', on);
  }

  // Helper: 삭제 버튼 활성화
  function updateDeleteBtn() {
    const any = !!document.querySelector('.row-check:checked');
    deleteBtn.disabled = !any;
  }

  // Helper: 전체 체크박스 상태 동기화
  function syncCheckAll() {
    const all = Array.from(document.querySelectorAll('.row-check'));
    const anyChecked = all.some(cb => cb.checked);
    const allChecked = all.length > 0 && all.every(cb => cb.checked);

    checkAll.checked = allChecked;
    checkAll.indeterminate = anyChecked && !allChecked;
  }

  // Helper: 검색 실행
  function performSearch() {
    const type = searchTypeSelect ? searchTypeSelect.value : 'title';
    const keyword = searchKeywordInput ? searchKeywordInput.value.trim() : '';

    if (keyword) {
      window.location.href = `/admin/video/list?searchType=${type}&searchText=${encodeURIComponent(keyword)}`;
    } else {
      window.location.href = '/admin/video/list';
    }
  }
});