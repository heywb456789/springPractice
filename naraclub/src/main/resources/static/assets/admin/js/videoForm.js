/**
 * 동영상 업로드/수정 페이지 JavaScript
 */
import {adminAuthFetch} from '../../../js/commonFetch.js';

document.addEventListener('DOMContentLoaded', function () {
  'use strict';

  // DOM 요소
  const videoForm = document.getElementById('videoForm');
  const btnSubmit = document.getElementById('btnSubmit');

  // 파일 업로드 관련 요소
  const videoUploader = document.getElementById('videoUploader');
  const uploadPlaceholder = document.getElementById('uploadPlaceholder');
  const uploadPreview = document.getElementById('uploadPreview');
  const videoPreview = document.getElementById('videoPreview');
  const videoFile = document.getElementById('videoFile');
  const videoFileName = document.getElementById('videoFileName');
  const videoFileSize = document.getElementById('videoFileSize');
  const removeVideo = document.getElementById('removeVideo');

  // 썸네일 관련 요소
  const thumbnailUploader = document.getElementById('thumbnailUploader');
  const thumbnailPlaceholder = document.getElementById('thumbnailPlaceholder');
  const thumbnailPreview = document.getElementById('thumbnailPreview');
  const thumbnailImage = document.getElementById('thumbnailImage');
  const thumbnailFile = document.getElementById('thumbnailFile');
  const thumbnailFileName = document.getElementById('thumbnailFileName');
  const thumbnailFileSize = document.getElementById('thumbnailFileSize');
  const removeThumbnail = document.getElementById('removeThumbnail');

  // 유튜브 관련 요소
  const youtubeId = document.getElementById('youtubeId');
  const previewYoutube = document.getElementById('previewYoutube');
  const youtubePreviewContainer = document.getElementById(
      'youtubePreviewContainer');
  const youtubePreviewFrame = document.getElementById('youtubePreviewFrame');

  // 업로드 타입 탭 관련 요소
  const fileTabs = document.getElementById('uploadTypeTabs');
  const durationSec = document.getElementById('durationSec');

  // Flatpickr 초기화 (날짜/시간 선택기)
  initDateTimePicker();

  // 수정 모드인지 확인
  const isEditing = !!document.getElementById('videoId');

  // 이벤트 리스너 등록
  initEventListeners();

  /**
   * 이벤트 리스너 초기화
   */
  function initEventListeners() {
    // 동영상 파일 업로드 이벤트
    if (videoUploader) {
      videoUploader.addEventListener('click', function () {
        videoFile.click();
      });

      videoUploader.addEventListener('dragover', function (e) {
        e.preventDefault();
        videoUploader.classList.add('dragover');
      });

      videoUploader.addEventListener('dragleave', function () {
        videoUploader.classList.remove('dragover');
      });

      videoUploader.addEventListener('drop', function (e) {
        e.preventDefault();
        videoUploader.classList.remove('dragover');

        if (e.dataTransfer.files.length > 0) {
          handleVideoFileSelect(e.dataTransfer.files[0]);
        }
      });

      if (videoFile) {
        videoFile.addEventListener('change', function () {
          if (this.files.length > 0) {
            handleVideoFileSelect(this.files[0]);
          }
        });
      }

      if (removeVideo) {
        removeVideo.addEventListener('click', function (e) {
          e.stopPropagation();
          resetVideoUploader();
        });
      }
    }

    // 썸네일 이미지 업로드 이벤트
    if (thumbnailUploader) {
      thumbnailUploader.addEventListener('click', function () {
        thumbnailFile.click();
      });

      thumbnailUploader.addEventListener('dragover', function (e) {
        e.preventDefault();
        thumbnailUploader.classList.add('dragover');
      });

      thumbnailUploader.addEventListener('dragleave', function () {
        thumbnailUploader.classList.remove('dragover');
      });

      thumbnailUploader.addEventListener('drop', function (e) {
        e.preventDefault();
        thumbnailUploader.classList.remove('dragover');

        if (e.dataTransfer.files.length > 0) {
          handleThumbnailFileSelect(e.dataTransfer.files[0]);
        }
      });

      if (thumbnailFile) {
        thumbnailFile.addEventListener('change', function () {
          if (this.files.length > 0) {
            handleThumbnailFileSelect(this.files[0]);
          }
        });
      }

      if (removeThumbnail) {
        removeThumbnail.addEventListener('click', function (e) {
          e.stopPropagation();
          resetThumbnailUploader();
        });
      }
    }

    // 유튜브 미리보기 버튼
    if (previewYoutube) {
      previewYoutube.addEventListener('click', previewYoutubeVideo);
    }

    // 유튜브 ID 입력 시 미리보기 준비
    if (youtubeId) {
      youtubeId.addEventListener('input', function () {
        if (this.value.trim()) {
          previewYoutube.disabled = false;
        } else {
          previewYoutube.disabled = true;
          hideYoutubePreview();
        }
      });

      // 페이지 로드 시 기존 유튜브 영상 있으면 미리보기 설정
      if (youtubeId.value.trim()) {
        previewYoutube.disabled = false;
        previewYoutubeVideo();
      } else {
        previewYoutube.disabled = true;
      }
    }

    // 업로드 타입 탭 전환 이벤트
    if (fileTabs) {
      const tabButtons = fileTabs.querySelectorAll('.nav-link');
      tabButtons.forEach(button => {
        button.addEventListener('click', function () {
          // 이미 활성화된 탭이면 무시
          if (this.classList.contains('active')) {
            return;
          }

          // 다른 탭 비활성화
          tabButtons.forEach(btn => btn.classList.remove('active'));

          // 현재 탭 활성화
          this.classList.add('active');

          // 탭 내용 전환
          const targetId = this.getAttribute('data-bs-target');
          const targetContent = document.querySelector(targetId);

          document.querySelectorAll('.tab-pane').forEach(pane => {
            pane.classList.remove('show', 'active');
          });

          targetContent.classList.add('show', 'active');
        });
      });
    }

    // 제출 버튼 이벤트
    if (btnSubmit) {
      btnSubmit.addEventListener('click', validateAndSubmit);
    }
  }

  /**
   * 동영상 파일 선택 처리
   * @param {File} file - 선택된 동영상 파일
   */
  function handleVideoFileSelect(file) {
    // 파일 유형 검증 - 확장된 비디오 형식 지원
    const validTypes = [
      'video/mp4',            // MP4
      'video/quicktime',      // MOV
      'video/x-msvideo',      // AVI
      'video/x-ms-wmv',       // WMV
      'video/x-matroska',     // MKV
      'video/webm',           // WEBM
      'video/x-flv',          // FLV
      'video/mpeg',           // MPG
      'video/x-m4v'           // M4V
    ];

    // 파일 확장자로도 체크 (MIME 타입이 제대로 인식되지 않는 경우를 위해)
    const fileName = file.name.toLowerCase();
    const isValidExtension =
      fileName.endsWith('.mp4') ||
      fileName.endsWith('.mov') ||
      fileName.endsWith('.avi') ||
      fileName.endsWith('.wmv') ||
      fileName.endsWith('.mkv') ||
      fileName.endsWith('.webm') ||
      fileName.endsWith('.flv') ||
      fileName.endsWith('.mpg') ||
      fileName.endsWith('.mpeg') ||
      fileName.endsWith('.m4v');

    if (!validTypes.includes(file.type) && !isValidExtension) {
      showAlert('지원하지 않는 파일 형식입니다. MP4, MOV, AVI, WMV, MKV, WEBM, FLV, MPG, M4V 파일만 허용됩니다.', 'danger');
      return;
    }

    // 파일 크기 검증 (500MB)
    const maxSize = 500 * 1024 * 1024; // 500MB
    if (file.size > maxSize) {
      showAlert('파일 크기가 너무 큽니다. 최대 500MB까지 허용됩니다.', 'danger');
      return;
    }

    // 파일 정보 설정
    videoFileName.textContent = file.name;
    videoFileSize.textContent = formatFileSize(file.size);

    // 비디오 미리보기 설정
    const videoURL = URL.createObjectURL(file);
    videoPreview.src = videoURL;

    // 비디오 로드 이벤트 추가 (길이 계산용)
    videoPreview.onloadedmetadata = function () {
      const duration = Math.round(videoPreview.duration);
      durationSec.value = duration;
    };

    // 비디오 로드 에러 처리 (MSG 등 브라우저에서 재생 불가능한 형식)
    videoPreview.onerror = function() {
      // 미리보기 불가능한 형식이지만 업로드는 허용
      videoPreview.style.display = 'none';
      const placeholderMsg = document.createElement('div');
      placeholderMsg.className = 'video-format-notice';
      placeholderMsg.innerHTML = `<p><i class="fas fa-exclamation-circle"></i> 미리보기를 지원하지 않는 형식입니다.</p>
                                  <p>파일: ${file.name}</p>`;
      uploadPreview.insertBefore(placeholderMsg, videoPreview.nextSibling);

      // 예상 길이 입력 요청
      if (!durationSec.value) {
        durationSec.value = ""; // 비워두기
        durationSec.focus();
        showAlert('이 비디오 형식은 자동으로 길이를 계산할 수 없습니다. 영상 길이(초)를 직접 입력해주세요.', 'warning');
      }
    };

    // 미리보기 표시
    uploadPlaceholder.classList.add('d-none');
    uploadPreview.classList.remove('d-none');
  }

  /**
   * 썸네일 이미지 선택 처리
   * @param {File} file - 선택된 이미지 파일
   */
  function handleThumbnailFileSelect(file) {
    // 파일 유형 검증
    const validTypes = ['image/jpeg', 'image/png', 'image/webp'];
    if (!validTypes.includes(file.type)) {
      showAlert('지원하지 않는 파일 형식입니다. JPG, PNG, WEBP 파일만 허용됩니다.', 'danger');
      return;
    }

    // 파일 크기 검증 (10MB)
    const maxSize = 10 * 1024 * 1024; // 10MB
    if (file.size > maxSize) {
      showAlert('파일 크기가 너무 큽니다. 최대 10MB까지 허용됩니다.', 'danger');
      return;
    }

    // 파일 정보 설정
    thumbnailFileName.textContent = file.name;
    thumbnailFileSize.textContent = formatFileSize(file.size);

    // 이미지 미리보기 설정
    const imageURL = URL.createObjectURL(file);
    thumbnailImage.src = imageURL;

    // 미리보기 표시
    thumbnailPlaceholder.classList.add('d-none');
    thumbnailPreview.classList.remove('d-none');
  }

  /**
   * 동영상 업로더 초기화
   */
  function resetVideoUploader() {
    videoFile.value = '';
    videoPreview.src = '';
    videoPreview.style.display = ''; // 스타일 초기화

    // 포맷 공지 제거
    const formatNotice = uploadPreview.querySelector('.video-format-notice');
    if (formatNotice) {
      formatNotice.remove();
    }

    uploadPreview.classList.add('d-none');
    uploadPlaceholder.classList.remove('d-none');
  }

  /**
   * 썸네일 업로더 초기화
   */
  function resetThumbnailUploader() {
    thumbnailFile.value = '';
    thumbnailImage.src = '';
    thumbnailPreview.classList.add('d-none');
    thumbnailPlaceholder.classList.remove('d-none');
  }

  /**
   * 유튜브 영상 미리보기
   */
  function previewYoutubeVideo() {
    const id = youtubeId.value.trim();

    if (!id) {
      showAlert('유튜브 영상 ID를 입력해주세요.', 'warning');
      return;
    }

    // 유튜브 ID 형식 검증
    if (!isValidYoutubeId(id)) {
      showAlert('올바른 유튜브 영상 ID를 입력해주세요.', 'warning');
      return;
    }

    // 아이프레임 소스 설정
    youtubePreviewFrame.src = `https://www.youtube.com/embed/${id}`;
    youtubePreviewContainer.classList.remove('d-none');

    // 기본 동영상 길이 설정 (유튜브는 API 사용 없이 길이를 알 수 없음)
    if (!durationSec.value) {
      durationSec.value = 180; // 3분으로 기본 설정
    }
  }

  /**
   * 유튜브 미리보기 숨기기
   */
  function hideYoutubePreview() {
    youtubePreviewFrame.src = '';
    youtubePreviewContainer.classList.add('d-none');
  }

  /**
   * 유튜브 ID 유효성 검사
   * @param {string} id - 유튜브 ID
   * @returns {boolean} - 유효성 여부
   */
  function isValidYoutubeId(id) {
    // 유튜브 ID는 일반적으로 11자리의 영문, 숫자, 특수문자(-_)로 구성됨
    const regex = /^[a-zA-Z0-9_-]{11}$/;
    return regex.test(id);
  }

  /**
   * 파일 크기 포맷팅
   * @param {number} bytes - 바이트 수
   * @returns {string} - 포맷팅된 크기
   */
  function formatFileSize(bytes) {
    if (bytes === 0) {
      return '0 Bytes';
    }

    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  /**
   * Flatpickr 날짜/시간 선택기 초기화
   */
  function initDateTimePicker() {
    const publishedAtInput = document.getElementById('publishedAt');

    flatpickr(publishedAtInput, {
      locale: 'ko',
      enableTime: true,
      dateFormat: 'Y-m-d H:i',
      time_24hr: true,
      minuteIncrement: 5,
      allowInput: true,
      disableMobile: true,
      defaultDate: publishedAtInput.value || new Date()
    });
  }

  /**
   * 폼 유효성 검사 및 제출
   */
  function validateAndSubmit() {
    // 기본 필드 검증
    const title = document.getElementById('title').value.trim();
    const category = document.getElementById('category').value;
    const type = document.getElementById('type').value;
    const durationSecValue = durationSec.value.trim();

    if (!title) {
      showAlert('제목을 입력해주세요.', 'danger');
      document.getElementById('title').focus();
      return;
    }

    if (!category) {
      showAlert('카테고리를 선택해주세요.', 'danger');
      document.getElementById('category').focus();
      return;
    }

    if (!type) {
      showAlert('타입을 선택해주세요.', 'danger');
      document.getElementById('type').focus();
      return;
    }

    if (!durationSecValue) {
      showAlert('영상 길이를 입력해주세요.', 'danger');
      durationSec.focus();
      return;
    }

    // 업로드 방식에 따른 검증
    // const isYoutubeTab = document.getElementById('youtube-tab').classList.contains('active');

    // if (isYoutubeTab) {
    //   // 유튜브 ID 검증
    //   const youtubeIdValue = youtubeId.value.trim();
    //   if (!youtubeIdValue) {
    //     showAlert('유튜브 영상 ID를 입력해주세요.', 'danger');
    //     youtubeId.focus();
    //     return;
    //   }
    //
    //   if (!isValidYoutubeId(youtubeIdValue)) {
    //     showAlert('올바른 유튜브 영상 ID를 입력해주세요.', 'warning');
    //     youtubeId.focus();
    //     return;
    //   }
    // } else {
    // 파일 업로드 검증 (수정이 아닌 경우에만 필수)
    if (!isEditing && !videoFile.files.length) {
      showAlert('동영상 파일을 업로드해주세요.', 'danger');
      return;
    }
    // }

    // 썸네일 검증 (수정이 아닌 경우에만 필수)
    if (!isEditing && !thumbnailFile.files.length) {
      showAlert('썸네일 이미지를 업로드해주세요.', 'danger');
      return;
    }

    // 폼 제출
    submitForm();
  }

  /**
   * 폼 제출 처리
   */
  function submitForm() {
    btnSubmit.disabled = true;
    const originalBtnText = btnSubmit.innerHTML;
    btnSubmit.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 처리 중...';

    // FormData 객체 생성
    const formData = new FormData(videoForm);

    // 수정 모드인지 확인하여 URL 및 메서드 결정
    const endpoint = isEditing
        ? {url: '/admin/original/video/update', method: 'POST'}
        : {url: '/admin/original/video/upload', method: 'POST'};

    // 체크박스 상태 수동 추가 (선택되지 않은 경우 false로 전송)
    formData.set('isPublic', document.getElementById('isPublic').checked);
    formData.set('isHot', document.getElementById('isHot').checked);

    // 업로드 방식에 따른 처리
    // const isYoutubeTab = document.getElementById('youtube-tab').classList.contains('active');
    const isYoutubeTab = false;
    formData.set('uploadType', isYoutubeTab ? 'youtube' : 'file');

    // API 요청 전송
    adminAuthFetch(endpoint.url, {
      method: endpoint.method,
      body: formData
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('서버 응답 오류: ' + response.status);
      }
      return response.json();
    })
    .then(result => {
      if (result.status.code === 'OK_0000') {
        showAlert(isEditing ? '동영상이 성공적으로 수정되었습니다.' : '동영상이 성공적으로 업로드되었습니다.',
            'success');
        // 1초 후 목록 페이지로 이동
        setTimeout(() => {
          window.location.href = '/admin/original/video/list';
        }, 1000);
      } else {
        throw new Error(result.message || '처리 중 오류가 발생했습니다.');
      }
    })
    .catch(error => {
      console.error('요청 오류:', error);
      showAlert('요청 처리 중 오류가 발생했습니다: ' + error.message, 'danger');
      btnSubmit.disabled = false;
      btnSubmit.innerHTML = originalBtnText;
    });
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