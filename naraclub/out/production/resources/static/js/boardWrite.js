/**
 * 게시글 작성 페이지 스크립트
 */

// 전역 변수
const MAX_IMAGE_COUNT = 3; // 최대 이미지 업로드 개수
const MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 최대 이미지 크기 (5MB)
const ALLOWED_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']; // 허용된 이미지 타입

// 업로드된 이미지 파일 저장
let uploadedImages = [];

document.addEventListener('DOMContentLoaded', function() {
  // 뒤로가기 버튼 이벤트
  initBackButton();

  // 이미지 업로드 이벤트
  initImageUpload();

  // 폼 제출 이벤트
  initFormSubmit();

  // 입력 필드 변경 감지 (버튼 활성화용)
  initFormInputs();
});

/**
 * 뒤로가기 버튼 초기화
 */
function initBackButton() {
  const backButton = document.getElementById('backButton');

  if (backButton) {
    backButton.addEventListener('click', function() {
      // 작성 중인 내용이 있는 경우 경고
      const title = document.getElementById('title').value.trim();
      const content = document.getElementById('content').value.trim();

      if (title || content || uploadedImages.length > 0) {
        if (confirm('작성 중인 내용이 있습니다. 페이지를 나가시겠습니까?')) {
          window.location.href = 'board-list.html';
        }
      } else {
        window.location.href = 'board-list.html';
      }
    });
  }
}

/**
 * 이미지 업로드 관련 이벤트 초기화
 */
function initImageUpload() {
  const uploadBox = document.getElementById('uploadBox');
  const fileInput = document.getElementById('fileInput');
  const imagePreviewContainer = document.getElementById('imagePreviewContainer');

  // 업로드 박스 클릭 시 파일 선택 창 열기
  if (uploadBox && fileInput) {
    uploadBox.addEventListener('click', function() {
      if (uploadedImages.length >= MAX_IMAGE_COUNT) {
        alert(`이미지는 최대 ${MAX_IMAGE_COUNT}장까지 업로드 가능합니다.`);
        return;
      }
      fileInput.click();
    });
  }

  // 파일 선택 시 이벤트
  if (fileInput) {
    fileInput.addEventListener('change', function(e) {
      const files = Array.from(e.target.files);

      // 최대 업로드 개수 확인
      if (files.length + uploadedImages.length > MAX_IMAGE_COUNT) {
        alert(`이미지는 최대 ${MAX_IMAGE_COUNT}장까지 업로드 가능합니다. 현재 ${uploadedImages.length}장이 업로드되어 있습니다.`);
        return;
      }

      // 각 파일 처리
      files.forEach(file => {
        // 파일 유효성 검사
        if (!validateImageFile(file)) {
          return;
        }

        // 이미지 미리보기 생성
        createImagePreview(file, imagePreviewContainer);
      });

      // 파일 입력 초기화 (같은 파일 다시 선택 가능하도록)
      fileInput.value = '';

      // 폼 상태 업데이트
      updateFormState();
    });
  }
}

/**
 * 이미지 파일 유효성 검사
 * @param {File} file - 검사할 파일
 * @returns {boolean} 유효한지 여부
 */
function validateImageFile(file) {
  // 파일 타입 확인
  if (!ALLOWED_IMAGE_TYPES.includes(file.type)) {
    alert('지원되지 않는 이미지 형식입니다. JPG, PNG, GIF, WEBP 형식만 업로드 가능합니다.');
    return false;
  }

  // 파일 크기 확인
  if (file.size > MAX_IMAGE_SIZE) {
    alert(`이미지 크기는 최대 5MB까지 가능합니다. 현재 파일 크기: ${(file.size / (1024 * 1024)).toFixed(2)}MB`);
    return false;
  }

  return true;
}

/**
 * 이미지 미리보기 생성
 * @param {File} file - 이미지 파일
 * @param {HTMLElement} container - 미리보기를 추가할 컨테이너
 */
function createImagePreview(file, container) {
  const reader = new FileReader();

  reader.onload = function(e) {
    // 업로드된 이미지 배열에 추가
    uploadedImages.push({
      file: file,
      dataUrl: e.target.result
    });

    // 미리보기 요소 생성
    const preview = document.createElement('div');
    preview.className = 'image-preview';
    preview.innerHTML = `
      <img src="${e.target.result}" alt="미리보기">
      <div class="delete-image" data-index="${uploadedImages.length - 1}">
        <i class="fas fa-times"></i>
      </div>
    `;

    // 삭제 버튼 이벤트
    const deleteButton = preview.querySelector('.delete-image');
    deleteButton.addEventListener('click', function(event) {
      event.stopPropagation();
      const index = parseInt(this.getAttribute('data-index'));
      deleteImagePreview(index, preview);
    });

    // 컨테이너에 추가
    container.appendChild(preview);

    // 업로드 버튼 상태 업데이트
    updateUploadButtonState();
  };

  reader.readAsDataURL(file);
}

/**
 * 이미지 미리보기 삭제
 * @param {number} index - 삭제할 이미지 인덱스
 * @param {HTMLElement} previewElement - 삭제할 미리보기 요소
 */
function deleteImagePreview(index, previewElement) {
  // 배열에서 제거
  uploadedImages.splice(index, 1);

  // DOM에서 제거
  previewElement.remove();

  // 남은 미리보기 인덱스 재설정
  document.querySelectorAll('.delete-image').forEach((button, idx) => {
    button.setAttribute('data-index', idx);
  });

  // 업로드 버튼 상태 업데이트
  updateUploadButtonState();

  // 폼 상태 업데이트
  updateFormState();
}

/**
 * 업로드 버튼 상태 업데이트
 */
function updateUploadButtonState() {
  const uploadBox = document.getElementById('uploadBox');

  if (uploadBox) {
    if (uploadedImages.length >= MAX_IMAGE_COUNT) {
      uploadBox.style.opacity = '0.5';
      uploadBox.style.cursor = 'not-allowed';
    } else {
      uploadBox.style.opacity = '1';
      uploadBox.style.cursor = 'pointer';
    }
  }
}

/**
 * 폼 제출 이벤트 초기화
 */
function initFormSubmit() {
  const postForm = document.getElementById('postForm');

  if (postForm) {
    postForm.addEventListener('submit', function(e) {
      e.preventDefault();

      // 폼 데이터 수집
      const title = document.getElementById('title').value.trim();
      const content = document.getElementById('content').value.trim();

      // 기본 유효성 검사
      if (!title) {
        alert('제목을 입력해주세요.');
        document.getElementById('title').focus();
        return;
      }

      // 폼 데이터 구성
      const formData = new FormData();
      formData.append('title', title);
      formData.append('content', content);

      // 이미지 파일 추가
      uploadedImages.forEach((image, index) => {
        formData.append(`image_${index}`, image.file);
      });

      // 서버에 전송 (실제 구현 시 API 호출)
      submitPost(formData);
    });
  }
}

/**
 * 게시글 서버 제출 함수
 * @param {FormData} formData - 제출할 폼 데이터
 */
function submitPost(formData) {
  console.log('게시글 제출...');

  // FormData 내용 확인 (개발용)
  for (let [key, value] of formData.entries()) {
    console.log(`${key}: ${value instanceof File ? value.name : value}`);
  }

  // API 호출 대신 임시 알림
  alert('게시글이 성공적으로 등록되었습니다.');
  window.location.href = 'board-list.html';

  // 실제 API 호출 예시 (구현 시 주석 해제)
  /*
  fetch('/api/posts', {
    method: 'POST',
    body: formData
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  })
  .then(data => {
    console.log('게시글 등록 성공:', data);
    alert('게시글이 성공적으로 등록되었습니다.');
    window.location.href = 'board-detail.html?id=' + data.id;
  })
  .catch(error => {
    console.error('게시글 등록 오류:', error);
    alert('게시글 등록 중 오류가 발생했습니다. 다시 시도해주세요.');
  });
  */
}

/**
 * 폼 입력 필드 이벤트 초기화
 */
function initFormInputs() {
  const titleInput = document.getElementById('title');
  const contentInput = document.getElementById('content');

  // 입력 필드 변경 시 폼 상태 업데이트
  if (titleInput && contentInput) {
    titleInput.addEventListener('input', updateFormState);
    contentInput.addEventListener('input', updateFormState);
  }
}

/**
 * 폼 상태 업데이트 (버튼 활성화 등)
 */
function updateFormState() {
  const titleInput = document.getElementById('title');
  const contentInput = document.getElementById('content');
  const submitButton = document.getElementById('submitButton');

  if (titleInput && contentInput && submitButton) {
    const title = titleInput.value.trim();
    const content = contentInput.value.trim();

    // 제목이 입력되어 있으면 버튼 활성화
    if (title) {
      submitButton.classList.add('active');
    } else {
      submitButton.classList.remove('active');
    }
  }
}