// assets/admin/js/boardList.js
import { adminAuthFetch } from '../../../js/commonFetch.js';

document.addEventListener('DOMContentLoaded', () => {
  const table       = document.getElementById('boardTable');
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
  });

  // 3) 더블클릭 시 상세 페이지로 이동
  table?.querySelectorAll('tbody tr').forEach(tr => {
    tr.addEventListener('dblclick', () => {
      const id = tr.getAttribute('data-id');
      if (id) window.location.href = `/admin/board/${id}`;
    });
  });

  // 4) 삭제 버튼 클릭 → 모달 띄우기
  deleteBtn?.addEventListener('click', () => {
    selectedIds = Array.from(
      document.querySelectorAll('.row-check:checked'),
      cb => cb.value
    );
    if (selectedIds.length) confirmModal.show();
  });

  // 5) 모달에서 확인 클릭 → 삭제 API 호출 후 새로고침
  document.getElementById('btnConfirmDelete')?.addEventListener('click', async () => {
    try {
      await adminAuthFetch('/admin/board/delete', {
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
    checkAll.checked = all.every(cb => cb.checked);
    checkAll.indeterminate = all.some(cb => cb.checked) && !all.every(cb => cb.checked);
  }
});
