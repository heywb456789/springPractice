/**
 * 뉴스 작성/수정 페이지 JavaScript
 */
import {adminAuthFetch} from '/js/commonFetch.js';

document.addEventListener('DOMContentLoaded', function () {
  'use strict';

  // DOM 요소
  const newsForm = document.getElementById('newsForm');
  const titleInput = document.getElementById('title');
  const authorInput = document.getElementById('author');
  const categorySelect = document.getElementById('category');
  const thumbnailUrlInput = document.getElementById('thumbnailUrl');
  const thumbnailPreview = document.getElementById('thumbnailPreview');
  const thumbnailPlaceholder = document.getElementById('thumbnailPlaceholder');
  const contentEditor = document.getElementById('content');

  const btnSaveDraft = document.getElementById('btnSaveDraft');
  const btnPublish = document.getElementById('btnPublish');
  const btnPreview = document.getElementById('btnPreview');
  const btnUploadThumbnail = document.getElementById('btnUploadThumbnail');

  const btnDesktopView = document.getElementById('btnDesktopView');
  const btnMobileView = document.getElementById('btnMobileView');
  const mobilePreview = document.getElementById('mobilePreview');
  const desktopPreview = document.getElementById('desktopPreview');

  const previewTitle = document.getElementById('previewTitle');
  const previewAuthor = document.getElementById('previewAuthor');
  const previewDate = document.getElementById('previewDate');
  const previewImage = document.getElementById('previewImage');
  const previewImageContainer = document.getElementById('previewImageContainer');
  const previewContent = document.getElementById('previewContent');
  const previewLoading = document.getElementById('previewLoading');

  const desktopPreviewTitle = document.getElementById('desktopPreviewTitle');
  const desktopPreviewAuthor = document.getElementById('desktopPreviewAuthor');
  const desktopPreviewDate = document.getElementById('desktopPreviewDate');
  const desktopPreviewImage = document.getElementById('desktopPreviewImage');
  const desktopPreviewImageContainer = document.getElementById('desktopPreviewImageContainer');
  const desktopPreviewContent = document.getElementById('desktopPreviewContent');

  const btnConfirmUpload = document.getElementById('btnConfirmUpload');
  const thumbnailFile = document.getElementById('thumbnailFile');
  const uploadPreview = document.getElementById('uploadPreview');
  const uploadPlaceholder = document.getElementById('uploadPlaceholder');

  const uploadThumbnailModal = new bootstrap.Modal(document.getElementById('uploadThumbnailModal'));
  const confirmModal = new bootstrap.Modal(document.getElementById('confirmModal'));
  const confirmModalMessage = document.getElementById('confirmModalMessage');
  const btnConfirmAction = document.getElementById('btnConfirmAction');

  // 뉴스 ID와 모드 저장
  const newsId = document.getElementById('newsId').value;
  const mode = document.getElementById('mode').value;

  // 현재 작업 상태
  let isSubmitting = false;
  let isDirty = false;
  let currentAction = null;

  // 초기화
  initPage();

  /**
   * 페이지 초기화 함수
   */
  function initPage() {
    // Summernote 에디터 초기화
    initSummernote();

    // 현재 날짜로 미리보기 날짜 설정
    updatePreviewDate();

    // 이벤트 리스너 등록
    initEventListeners();

    // 페이지 이탈 감지
    window.addEventListener('beforeunload', handleBeforeUnload);

    // 초기 미리보기 업데이트
    setTimeout(updatePreview, 500);
  }

  /**
   * Summernote 에디터 초기화
   */
  function initSummernote() {
    if (!contentEditor || typeof $.summernote === 'undefined') {
      console.error('Summernote를 로드할 수 없습니다.');
      return;
    }

    $(contentEditor).summernote({
      lang: 'ko-KR',
      height: 500,
      focus: true,
      placeholder: '내용을 입력해주세요.',
      toolbar: [
        ['style', ['style']],
        ['font', ['bold', 'underline', 'clear']],
        ['color', ['color']],
        ['para', ['ul', 'ol', 'paragraph']],
        ['table', ['table']],
        ['insert', ['link', 'picture']],
        ['view', ['fullscreen', 'codeview', 'help']]
      ],
      callbacks: {
        onChange: function() {
          isDirty = true;
          debounce(updatePreview, 500)();
        },
        onImageUpload: function(files) {
          uploadSummernoteImage(files[0], this);
        }
      }
    });
  }

  /**
   * 이벤트 리스너 초기화
   */
  function initEventListeners() {
    // 폼 입력 변경 감지
    if (titleInput) {
      titleInput.addEventListener('input', function() {
        isDirty = true;
        debounce(updatePreview, 300)();
      });
    }

    if (authorInput) {
      authorInput.addEventListener('input', function() {
        isDirty = true;
        debounce(updatePreview, 300)();
      });
    }

    // 버튼 이벤트
    if (btnSaveDraft) {
      btnSaveDraft.addEventListener('click', function() {
        saveNews(false);
      });
    }

    if (btnPublish) {
      btnPublish.addEventListener('click', function() {
        if (validateForm()) {
          currentAction = 'publish';
          confirmModalMessage.textContent = '뉴스를 발행하시겠습니까? 발행 후에는 즉시 사용자에게 공개됩니다.';
          btnConfirmAction.textContent = '발행';
          btnConfirmAction.className = 'btn btn-primary';
          confirmModal.show();
        }
      });
    }

    if (btnConfirmAction) {
      btnConfirmAction.addEventListener('click', function() {
        confirmModal.hide();

        if (currentAction === 'publish') {
          saveNews(true);
        }
      });
    }

    // 미리보기 모드 전환
    if (btnDesktopView) {
      btnDesktopView.addEventListener('click', function() {
        setPreviewMode('desktop');
      });
    }

    if (btnMobileView) {
      btnMobileView.addEventListener('click', function() {
        setPreviewMode('mobile');
      });
    }

    // 썸네일 업로드 관련
    if (btnUploadThumbnail) {
      btnUploadThumbnail.addEventListener('click', function() {
        uploadThumbnailModal.show();
      });
    }

    if (thumbnailFile) {
      thumbnailFile.addEventListener('change', function(e) {
        previewThumbnail(e.target);
      });
    }

    if (btnConfirmUpload) {
      btnConfirmUpload.addEventListener('click', uploadThumbnail);
    }

    // 전체 페이지 미리보기
    if (btnPreview) {
      btnPreview.addEventListener('click', function() {
        const previewUrl = `/preview/news/temp?title=${encodeURIComponent(titleInput.value)}&content=${encodeURIComponent($(contentEditor).summernote('code'))}&thumbnail=${encodeURIComponent(thumbnailUrlInput.value)}&author=${encodeURIComponent(authorInput.value)}`;
        window.open(previewUrl, '_blank');
      });
    }
  }

  /**
   * 폼 유효성 검사
   * @returns {boolean} 유효성 검사 통과 여부
   */
  function validateForm() {
    let isValid = true;

    // 제목 검사
    if (!titleInput.value.trim()) {
      titleInput.classList.add('is-invalid');
      isValid = false;
    } else {
      titleInput.classList.remove('is-invalid');
    }

    // 카테고리 검사
    if (!categorySelect.value) {
      categorySelect.classList.add('is-invalid');
      isValid = false;
    } else {
      categorySelect.classList.remove('is-invalid');
    }

    // 작성자 검사
    if (!authorInput.value.trim()) {
      authorInput.classList.add('is-invalid');
      isValid = false;
    } else {
      authorInput.classList.remove('is-invalid');
    }

    // 내용 검사
    const contentValue = $(contentEditor).summernote('code');
    if (!contentValue || contentValue === '<p><br></p>') {
      $(contentEditor).next().addClass('is-invalid');
      isValid = false;
    } else {
      $(contentEditor).next().removeClass('is-invalid');
    }

    if (!isValid) {
      showAlert('필수 항목을 모두 입력해주세요.', 'warning');
    }

    return isValid;
  }

  /**
   * 미리보기 업데이트
   */
  function updatePreview() {
    // 로딩 표시
    previewLoading.style.display = 'flex';

    // 제목 업데이트
    const title = titleInput.value || '뉴스 제목';
    previewTitle.textContent = title;
    desktopPreviewTitle.textContent = title;

    // 작성자 업데이트
    const author = authorInput.value || '작성자';
    previewAuthor.textContent = author;
    desktopPreviewAuthor.textContent = author;

    // 썸네일 업데이트
    const thumbnailUrl = thumbnailUrlInput.value;
    if (thumbnailUrl) {
      previewImage.src = thumbnailUrl;
      previewImageContainer.style.display = 'block';
      desktopPreviewImage.src = thumbnailUrl;
      desktopPreviewImageContainer.style.display = 'block';
    } else {
      previewImageContainer.style.display = 'none';
      desktopPreviewImageContainer.style.display = 'none';
    }

    // 내용 업데이트
    const content = $(contentEditor).summernote('code') || '<p>내용이 없습니다.</p>';
    previewContent.innerHTML = content;
    desktopPreviewContent.innerHTML = content;

    // 로딩 숨김
    setTimeout(() => {
      previewLoading.style.display = 'none';
    }, 300);
  }

  /**
   * 현재 날짜로 미리보기 날짜 업데이트
   */
  function updatePreviewDate() {
    const now = new Date();
    const formattedDate = now.getFullYear() + '-' +
      String(now.getMonth() + 1).padStart(2, '0') + '-' +
      String(now.getDate()).padStart(2, '0');

    previewDate.textContent = formattedDate;
    desktopPreviewDate.textContent = formattedDate;
  }

  /**
   * 미리보기 모드 설정 (모바일/데스크톱)
   * @param {string} mode - 'mobile' 또는 'desktop'
   */
  function setPreviewMode(mode) {
    if (mode === 'desktop') {
      mobilePreview.classList.add('d-none');
      desktopPreview.classList.remove('d-none');
      btnMobileView.classList.remove('active');
      btnDesktopView.classList.add('active');
    } else {
      mobilePreview.classList.remove('d-none');
      desktopPreview.classList.add('d-none');
      btnMobileView.classList.add('active');
      btnDesktopView.classList.remove('active');
    }
  }

  /**
   * 썸네일 이미지 미리보기
   * @param {HTMLInputElement} input - 파일 입력 요소
   */
  function previewThumbnail(input) {
    if (input.files && input.files[0]) {
      const file = input.files[0];

      // 파일 유형 검사
      if (!file.type.startsWith('image/')) {
        showAlert('이미지 파일만 업로드 가능합니다.', 'warning');
        input.value = '';
        return;
      }

      // 파일 크기 검사 (5MB 제한)
      if (file.size > 5 * 1024 * 1024) {
        showAlert('이미지 크기가 5MB를 초과할 수 없습니다.', 'warning');
        input.value = '';
        return;
      }

      const reader = new FileReader();

      reader.onload = function(e) {
        uploadPreview.src = e.target.result;
        uploadPreview.style.display = 'block';
        uploadPlaceholder.style.display = 'none';
        btnConfirmUpload.disabled = false;
      };

      reader.readAsDataURL(file);
    } else {
      uploadPreview.style.display = 'none';
      uploadPlaceholder.style.display = 'flex';
      btnConfirmUpload.disabled = true;
    }
  }

  /**
   * 썸네일 이미지 업로드
   */
  async function uploadThumbnail() {
    const file = thumbnailFile.files[0];
    if (!file) {
      showAlert('업로드할 이미지를 선택해주세요.', 'warning');
      return;
    }

    btnConfirmUpload.disabled = true;
    btnConfirmUpload.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 업로드 중...';

    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('type', 'news_thumbnail');

      const res = await adminAuthFetch('/admin/upload/image', {
        method: 'POST',
        body: formData,
        // 중요: fetch에 Content-Type 헤더를 설정하지 않음 (FormData가 자동으로 설정)
      });

      if (!res.ok) {
        throw new Error('서버 응답 오류: ' + res.status);
      }

      const result = await res.json();

      if (result.status.code === 'OK_0000') {
        const imageUrl = result.data.url;

        // 폼에 URL 설정
        thumbnailUrlInput.value = imageUrl;

        // 썸네일 미리보기 업데이트
        thumbnailPreview.src = imageUrl;
        thumbnailPreview.style.display = 'block';
        thumbnailPlaceholder.style.display = 'none';

        // 모달 닫기
        uploadThumbnailModal.hide();

        // 미리보기 업데이트
        updatePreview();

        // 변경사항 감지
        isDirty = true;

        showAlert('이미지가 성공적으로 업로드되었습니다.', 'success');
      } else {
        throw new Error(result.message || '이미지 업로드 중 오류가 발생했습니다.');
      }
    } catch (err) {
      console.error(err);
      showAlert('이미지 업로드 중 오류가 발생했습니다: ' + err.message, 'danger');
    } finally {
      btnConfirmUpload.innerHTML = '업로드';
      btnConfirmUpload.disabled = false;
    }
  }

  /**
   * Summernote 에디터 내 이미지 업로드
   * @param {File} file - 업로드할 이미지 파일
   * @param {Object} editor - Summernote 에디터 인스턴스
   */
  async function uploadSummernoteImage(file, editor) {
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('type', 'news_content');

      const res = await adminAuthFetch('/admin/upload/image', {
        method: 'POST',
        body: formData,
      });

      if (!res.ok) {
        throw new Error('서버 응답 오류: ' + res.status);
      }

      const result = await res.json();

      if (result.status.code === 'OK_0000') {
        const imageUrl = result.data.url;
        $(editor).summernote('insertImage', imageUrl);
      } else {
        throw new Error(result.message || '이미지 업로드 중 오류가 발생했습니다.');
      }
    } catch (err) {
      console.error(err);
      showAlert('이미지 업로드 중 오류가 발생했습니다: ' + err.message, 'danger');
    }
  }

  /**
   * 뉴스 저장 (임시저장 또는 발행)
   * @param {boolean} isPublish - 발행 여부
   */
  async function saveNews(isPublish) {
    if (isSubmitting) {
      return;
    }

    if (isPublish && !validateForm()) {
      return;
    }

    // 저장 중복 방지
    isSubmitting = true;

    // 버튼 상태 업데이트
    const saveButton = isPublish ? btnPublish : btnSaveDraft;
    const originalButtonText = saveButton.innerHTML;
    saveButton.disabled = true;
    saveButton.innerHTML = `<i class="fas fa-spinner fa-spin"></i> ${isPublish ? '발행 중...' : '저장 중...'}`;

    try {
      // 폼 데이터 수집
      const formData = {
        title: titleInput.value,
        content: $(contentEditor).summernote('code'),
        category: categorySelect.value,
        author: authorInput.value,
        thumbnailUrl: thumbnailUrlInput.value,
        isPublic: isPublish,
        isHot: document.getElementById('isHot').checked
      };

      // API 경로 결정
      let apiUrl = mode === 'create'
        ? '/admin/news/create'
        : `/admin/news/update/${newsId}`;

      const res = await adminAuthFetch(apiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (!res.ok) {
        throw new Error('서버 응답 오류: ' + res.status);
      }

      const result = await res.json();

      if (result.status.code === 'OK_0000') {
        isDirty = false;

        showAlert(`뉴스가 성공적으로 ${isPublish ? '발행' : '저장'}되었습니다.`, 'success');

        // 새로 생성된 경우, 수정 페이지로 리디렉션
        if (mode === 'create') {
          setTimeout(() => {
            window.location.href = `/admin/news/update/${result.data.newsId}`;
          }, 1000);
        } else {
          // 수정 모드에서는 페이지 새로고침
          setTimeout(() => {
            window.location.reload();
          }, 1000);
        }
      } else {
        throw new Error(result.message || `뉴스 ${isPublish ? '발행' : '저장'} 중 오류가 발생했습니다.`);
      }
    } catch (err) {
      console.error(err);
      showAlert(`뉴스 ${isPublish ? '발행' : '저장'} 중 오류가 발생했습니다: ` + err.message, 'danger');
    } finally {
      saveButton.innerHTML = originalButtonText;
      saveButton.disabled = false;
      isSubmitting = false;
    }
  }

  /**
   * 페이지 이탈 시 처리
   * @param {BeforeUnloadEvent} e - beforeunload 이벤트 객체
   */
  function handleBeforeUnload(e) {
    if (isDirty) {
      // 표준 메시지 (브라우저마다 다를 수 있음)
      const message = '변경사항이 저장되지 않았습니다. 정말로 페이지를 떠나시겠습니까?';
      e.returnValue = message;
      return message;
    }
  }

  /**
   * 디바운스 함수 (연속 호출 방지)
   * @param {Function} func - 실행할 함수
   * @param {number} wait - 대기 시간 (밀리초)
   * @returns {Function} 디바운스된 함수
   */
  function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
      const later = () => {
        clearTimeout(timeout);
        func(...args);
      };
      clearTimeout(timeout);
      timeout = setTimeout(later, wait);
    };
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