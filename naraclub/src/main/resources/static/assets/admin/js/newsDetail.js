/**
 * 뉴스 상세 페이지 JavaScript
 */
import {adminAuthFetch} from '/js/commonFetch.js';

document.addEventListener('DOMContentLoaded', function () {
  'use strict';

  // DOM 요소
  const btnDelete = document.getElementById('btnDelete');
  const btnConfirmDelete = document.getElementById('btnConfirmDelete');
  const deleteConfirmModal = new bootstrap.Modal(
      document.getElementById('deleteConfirmModal'));
  const btnOpenPreview = document.getElementById('btnOpenPreview');
  const previewFrame = document.getElementById('previewFrame');

  // 뉴스 ID 가져오기 (URL에서 추출)
  const newsId = getNewsIdFromUrl();

  // 초기화
  initPage();

  /**
   * 페이지 초기화 함수
   */
  function initPage() {
    // 이벤트 리스너 등록
    initEventListeners();
  }

  /**
   * 이벤트 리스너 초기화
   */
  function initEventListeners() {
    // 삭제 버튼 이벤트
    if (btnDelete) {
      btnDelete.addEventListener('click', function() {
        deleteConfirmModal.show();
      });
    }

    // 삭제 확인 버튼 이벤트
    if (btnConfirmDelete) {
      btnConfirmDelete.addEventListener('click', confirmDelete);
    }

    // 새 창에서 미리보기 버튼 이벤트
    if (btnOpenPreview && previewFrame) {
      btnOpenPreview.addEventListener('click', function() {
        const previewUrl = previewFrame.getAttribute('src');
        if (previewUrl) {
          window.open(previewUrl, '_blank', 'width=375,height=667');
        }
      });
    }
  }

  /**
   * URL에서 뉴스 ID 추출
   * @returns {string|null} 뉴스 ID 또는 null
   */
  function getNewsIdFromUrl() {
    const pathSegments = window.location.pathname.split('/');
    const idIndex = pathSegments.findIndex(segment => segment === 'news') + 1;
    return idIndex > 0 && idIndex < pathSegments.length ? pathSegments[idIndex] : null;
  }

  /**
   * 삭제 확인 처리
   */
  async function confirmDelete() {
    if (!newsId) {
      showAlert('삭제할 뉴스 ID가 없습니다.', 'warning');
      deleteConfirmModal.hide();
      return;
    }

    btnConfirmDelete.disabled = true;
    btnConfirmDelete.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 삭제 중...';

    try {
      const res = await adminAuthFetch(`/admin/news/delete/${newsId}`, {
        method: 'DELETE'
      });

      if (!res.ok) {
        throw new Error('서버 응답 오류: ' + res.status);
      }

      const result = await res.json();

      if (result.status.code === 'OK_0000') {
        showAlert('뉴스가 성공적으로 삭제되었습니다.', 'success');

        // 삭제 성공 후 목록 페이지로 이동
        setTimeout(() => {
          window.location.href = '/admin/news/list';
        }, 1000);
      } else {
        throw new Error(result.message || '삭제 중 오류가 발생했습니다.');
      }
    } catch (err) {
      console.error(err);
      showAlert('삭제 중 오류가 발생했습니다: ' + err.message, 'danger');
    } finally {
      btnConfirmDelete.innerHTML = '삭제';
      btnConfirmDelete.disabled = false;
      deleteConfirmModal.hide();
    }
  }

  /**
   * 알림 표시 함수
   * @param {string} message - 알림 메시지
   * @param {string} type - 알림 타입 (success, danger, warning, info)
   */
  function showAlert(message, type = 'info') {
    // 커스텀 알림 시스템 사용
    if (window.CustomNotification) {
      window.CustomNotification.show(message, type);
      return;
    }

    // 기본 alert 사용
    alert(message);
  }
});