/**
 * 관리자 관리 페이지 JavaScript
 */
import {adminAuthFetch} from '/js/commonFetch.js';

document.addEventListener('DOMContentLoaded', function () {
  'use strict';

  // DOM 요소
  const addAdminModal = new bootstrap.Modal(document.getElementById('addAdminModal'));
  const editAdminModal = new bootstrap.Modal(document.getElementById('editAdminModal'));
  const removeAdminModal = new bootstrap.Modal(document.getElementById('removeAdminModal'));
  const approveRequestModal = new bootstrap.Modal(document.getElementById('approveRequestModal'));
  const rejectRequestModal = new bootstrap.Modal(document.getElementById('rejectRequestModal'));
  const reasonViewModal = new bootstrap.Modal(document.getElementById('reasonViewModal'));

  const userSearchInput = document.getElementById('userSearch');
  const btnSearchUser = document.getElementById('btnSearchUser');
  const userList = document.getElementById('userList');
  const userSearchResults = document.getElementById('userSearchResults');
  const selectedUserInfo = document.getElementById('selectedUserInfo');
  const roleSelection = document.getElementById('roleSelection');
  const selectedUserAvatar = document.getElementById('selectedUserAvatar');
  const selectedUserName = document.getElementById('selectedUserName');
  const selectedUserEmail = document.getElementById('selectedUserEmail');
  const selectedUserId = document.getElementById('selectedUserId');
  const btnAddAdmin = document.getElementById('btnAddAdmin');

  const btnSaveAdminEdit = document.getElementById('btnSaveAdminEdit');
  const editAdminId = document.getElementById('editAdminId');
  const editAdminName = document.getElementById('editAdminName');

  const btnConfirmRemoveAdmin = document.getElementById('btnConfirmRemoveAdmin');
  const removeAdminId = document.getElementById('removeAdminId');
  const removeAdminName = document.getElementById('removeAdminName');

  const btnConfirmApprove = document.getElementById('btnConfirmApprove');
  const approveRequestId = document.getElementById('approveRequestId');
  const approveUserId = document.getElementById('approveUserId');
  const approveRequestName = document.getElementById('approveRequestName');

  const btnConfirmReject = document.getElementById('btnConfirmReject');
  const rejectRequestId = document.getElementById('rejectRequestId');
  const rejectUserId = document.getElementById('rejectUserId');
  const rejectRequestName = document.getElementById('rejectRequestName');
  const rejectReason = document.getElementById('rejectReason');

  const reasonContent = document.getElementById('reasonContent');

  // 초기화
  initPage();

  /**
   * 페이지 초기화 함수
   */
  function initPage() {
    // 이벤트 리스너 등록
    initEventListeners();

    // 툴팁 초기화
    initTooltips();
  }

  /**
   * 이벤트 리스너 초기화
   */
  function initEventListeners() {
    // 사용자 검색 버튼 클릭 이벤트
    if (btnSearchUser) {
      btnSearchUser.addEventListener('click', searchUsers);
    }

    // 사용자 검색 엔터 이벤트
    if (userSearchInput) {
      userSearchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
          e.preventDefault();
          searchUsers();
        }
      });
    }

    // 관리자 추가 버튼 클릭 이벤트
    if (btnAddAdmin) {
      btnAddAdmin.addEventListener('click', addAdmin);
    }

    // 관리자 편집 버튼 클릭 이벤트
    document.addEventListener('click', function(e) {
      const editBtn = e.target.closest('.btn-edit-admin');
      if (editBtn) {
        const adminId = editBtn.dataset.adminId;
        const adminName = editBtn.dataset.adminName;
        const adminRoles = editBtn.dataset.adminRoles ? editBtn.dataset.adminRoles.split(',') : [];
        showEditAdminModal(adminId, adminName, adminRoles);
      }
    });

    // 관리자 제거 버튼 클릭 이벤트
    document.addEventListener('click', function(e) {
      const removeBtn = e.target.closest('.btn-remove-admin');
      if (removeBtn) {
        const adminId = removeBtn.dataset.adminId;
        const adminName = removeBtn.dataset.adminName;
        showRemoveAdminModal(adminId, adminName);
      }
    });

    // 요청 승인 버튼 클릭 이벤트
    document.addEventListener('click', function(e) {
      const approveBtn = e.target.closest('.btn-approve-request');
      if (approveBtn) {
        const requestId = approveBtn.dataset.requestId;
        const userId = approveBtn.dataset.userId;
        const userName = approveBtn.dataset.userName;
        showApproveRequestModal(requestId, userId, userName);
      }
    });

    // 요청 거부 버튼 클릭 이벤트
    document.addEventListener('click', function(e) {
      const rejectBtn = e.target.closest('.btn-reject-request');
      if (rejectBtn) {
        const requestId = rejectBtn.dataset.requestId;
        const userId = rejectBtn.dataset.userId;
        const userName = rejectBtn.dataset.userName;
        showRejectRequestModal(requestId, userId, userName);
      }
    });

    // 요청 사유 보기 버튼 클릭 이벤트
    document.addEventListener('click', function(e) {
      const viewReasonBtn = e.target.closest('.view-reason-btn');
      if (viewReasonBtn) {
        const reason = viewReasonBtn.getAttribute('title') || '사유가 없습니다.';
        showReasonViewModal(reason);
      }
    });

    // 관리자 편집 저장 버튼 클릭 이벤트
    if (btnSaveAdminEdit) {
      btnSaveAdminEdit.addEventListener('click', saveAdminEdit);
    }

    // 관리자 제거 확인 버튼 클릭 이벤트
    if (btnConfirmRemoveAdmin) {
      btnConfirmRemoveAdmin.addEventListener('click', confirmRemoveAdmin);
    }

    // 요청 승인 확인 버튼 클릭 이벤트
    if (btnConfirmApprove) {
      btnConfirmApprove.addEventListener('click', confirmApproveRequest);
    }

    // 요청 거부 확인 버튼 클릭 이벤트
    if (btnConfirmReject) {
      btnConfirmReject.addEventListener('click', confirmRejectRequest);
    }
  }

  /**
   * 툴팁 초기화
   */
  function initTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
      return new bootstrap.Tooltip(tooltipTriggerEl);
    });
  }

  /**
   * 사용자 검색 함수
   */
  async function searchUsers() {
    const searchText = userSearchInput.value.trim();

    if (!searchText) {
      showAlert('검색어를 입력하세요.', 'warning');
      return;
    }

    // 검색 버튼 비활성화 및 로딩 표시
    btnSearchUser.disabled = true;
    btnSearchUser.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

    try {
      const response = await adminAuthFetch(`/admin/api/users/search?query=${encodeURIComponent(searchText)}`);

      if (!response.ok) {
        throw new Error('서버 응답 오류: ' + response.status);
      }

      const result = await response.json();

      // 검색 결과 표시
      displaySearchResults(result.users);
    } catch (error) {
      console.error('사용자 검색 오류:', error);
      showAlert('사용자 검색 중 오류가 발생했습니다: ' + error.message, 'danger');
    } finally {
      // 버튼 상태 복원
      btnSearchUser.disabled = false;
      btnSearchUser.innerHTML = '<i class="fas fa-search"></i>';
    }
  }

  /**
   * 검색 결과 표시 함수
   * @param {Array} users - 검색된 사용자 목록
   */
  function displaySearchResults(users) {
    // 검색 결과 초기화
    userList.innerHTML = '';

    if (users.length === 0) {
      userList.innerHTML = `
        <div class="text-center py-3 text-muted">
          검색 결과가 없습니다.
        </div>
      `;
    } else {
      // 검색 결과 표시
      users.forEach(user => {
        const listItem = document.createElement('button');
        listItem.className = 'list-group-item list-group-item-action';
        listItem.dataset.userId = user.userId;
        listItem.dataset.userName = user.name;
        listItem.dataset.userEmail = user.email;
        listItem.dataset.userAvatar = user.profileImage || '/assets/admin/images/user_default.png';

        listItem.innerHTML = `
          <div class="d-flex align-items-center">
            <div class="user-list-avatar">
              <img src="${user.profileImage || '/assets/admin/images/user_default.png'}" alt="프로필">
            </div>
            <div class="user-list-info">
              <div class="user-list-name">${user.name}</div>
              <div class="user-list-email">${user.email}</div>
            </div>
            ${user.isAdmin ? '<span class="badge bg-primary ms-auto">관리자</span>' : ''}
          </div>
        `;

        // 클릭 이벤트 (사용자 선택)
        listItem.addEventListener('click', function() {
          selectUser(this.dataset.userId, this.dataset.userName, this.dataset.userEmail, this.dataset.userAvatar);
        });

        userList.appendChild(listItem);
      });
    }

    // 검색 결과 영역 표시
    userSearchResults.classList.remove('d-none');
  }

  /**
   * 사용자 선택 함수
   * @param {string} userId - 선택한 사용자 ID
   * @param {string} userName - 선택한 사용자 이름
   * @param {string} userEmail - 선택한 사용자 이메일
   * @param {string} userAvatar - 선택한 사용자 프로필 이미지
   */
  function selectUser(userId, userName, userEmail, userAvatar) {
    // 선택한 사용자 정보 표시
    selectedUserAvatar.src = userAvatar;
    selectedUserName.textContent = userName;
    selectedUserEmail.textContent = userEmail;
    selectedUserId.value = userId;

    // 선택 영역 표시
    selectedUserInfo.classList.remove('d-none');
    roleSelection.classList.remove('d-none');

    // 검색 결과 숨김
    userSearchResults.classList.add('d-none');

    // 추가 버튼 활성화
    btnAddAdmin.disabled = false;
  }

  /**
   * 관리자 추가 함수
   */
  async function addAdmin() {
    const userId = selectedUserId.value;

    if (!userId) {
      showAlert('관리자로 추가할 사용자를 선택하세요.', 'warning');
      return;
    }

    // 선택된 권한 수집
    const selectedRoles = [];
    document.querySelectorAll('input[name="addRoles"]:checked').forEach(checkbox => {
      selectedRoles.push(checkbox.value);
    });

    if (selectedRoles.length === 0) {
      showAlert('최소한 하나 이상의 권한을 선택하세요.', 'warning');
      return;
    }

    // 버튼 비활성화 및 로딩 표시
    btnAddAdmin.disabled = true;
    btnAddAdmin.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 저장 중...';

    try {
      const response = await adminAuthFetch('/admin/api/admin/add', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          userId: userId,
          roles: selectedRoles
        })
      });

      if (!response.ok) {
        throw new Error('서버 응답 오류: ' + response.status);
      }

      const result = await response.json();

      if (result.success) {
        // 관리자 추가 성공
        addAdminModal.hide();
        showAlert('관리자가 성공적으로 추가되었습니다.', 'success');

        // 페이지 새로고침
        setTimeout(() => {
          window.location.reload();
        }, 1000);
      } else {
        throw new Error(result.message || '관리자 추가 중 오류가 발생했습니다.');
      }
    } catch (error) {
      console.error('관리자 추가 오류:', error);
      showAlert('관리자 추가 중 오류가 발생했습니다: ' + error.message, 'danger');
    } finally {
      // 버튼 상태 복원
      btnAddAdmin.innerHTML = '저장';
      btnAddAdmin.disabled = false;
    }
  }

  /**
   * 관리자 편집 모달 표시
   * @param {string} adminId - 관리자 ID
   * @param {string} adminName - 관리자 이름
   * @param {Array} adminRoles - 관리자 권한 목록
   */
  function showEditAdminModal(adminId, adminName, adminRoles) {
    // 모달 정보 설정
    editAdminId.value = adminId;
    editAdminName.textContent = adminName;

    // 권한 체크박스 설정
    document.querySelectorAll('input[name="editRoles"]').forEach(checkbox => {
      checkbox.checked = adminRoles.includes(checkbox.value);
    });

    // 모달 표시
    editAdminModal.show();
  }

  /**
   * 관리자 제거 모달 표시
   * @param {string} adminId - 관리자 ID
   * @param {string} adminName - 관리자 이름
   */
  function showRemoveAdminModal(adminId, adminName) {
    // 모달 정보 설정
    removeAdminId.value = adminId;
    removeAdminName.textContent = adminName;

    // 모달 표시
    removeAdminModal.show();
  }

  /**
   * 요청 승인 모달 표시
   * @param {string} requestId - 요청 ID
   * @param {string} userId - 사용자 ID
   * @param {string} userName - 사용자 이름
   */
  function showApproveRequestModal(requestId, userId, userName) {
    // 모달 정보 설정
    approveRequestId.value = requestId;
    approveUserId.value = userId;
    approveRequestName.textContent = userName;

    // 권한 체크박스 초기화
    document.querySelectorAll('input[name="approveRoles"]').forEach(checkbox => {
      // 기본적으로 ADMIN 권한만 체크
      checkbox.checked = checkbox.value === 'ADMIN';
    });

    // 모달 표시
    approveRequestModal.show();
  }

  /**
   * 요청 거부 모달 표시
   * @param {string} requestId - 요청 ID
   * @param {string} userId - 사용자 ID
   * @param {string} userName - 사용자 이름
   */
  function showRejectRequestModal(requestId, userId, userName) {
    // 모달 정보 설정
    rejectRequestId.value = requestId;
    rejectUserId.value = userId;
    rejectRequestName.textContent = userName;
    rejectReason.value = '';

    // 모달 표시
    rejectRequestModal.show();
  }

  /**
   * 요청 사유 모달 표시
   * @param {string} reason - 요청 사유
   */
  function showReasonViewModal(reason) {
    // 모달 내용 설정
    reasonContent.textContent = reason;

    // 모달 표시
    reasonViewModal.show();
  }

  /**
   * 관리자 편집 저장
   */
  async function saveAdminEdit() {
    const adminId = editAdminId.value;

    if (!adminId) {
      showAlert('관리자 ID를 찾을 수 없습니다.', 'warning');
      return;
    }

    // 선택된 권한 수집
    const selectedRoles = [];
    document.querySelectorAll('input[name="editRoles"]:checked').forEach(checkbox => {
      selectedRoles.push(checkbox.value);
    });

    if (selectedRoles.length === 0) {
      showAlert('최소한 하나 이상의 권한을 선택하세요.', 'warning');
      return;
    }

    // 버튼 비활성화 및 로딩 표시
    btnSaveAdminEdit.disabled = true;
    btnSaveAdminEdit.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 저장 중...';

    try {
      const response = await adminAuthFetch(`/admin/api/admin/${adminId}/roles`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          roles: selectedRoles
        })
      });

      if (!response.ok) {
        throw new Error('서버 응답 오류: ' + response.status);
      }

      const result = await response.json();

      if (result.success) {
        // 관리자 편집 성공
        editAdminModal.hide();
        showAlert('관리자 권한이 성공적으로 변경되었습니다.', 'success');

        // 페이지 새로고침
        setTimeout(() => {
          window.location.reload();
        }, 1000);
      } else {
        throw new Error(result.message || '관리자 권한 변경 중 오류가 발생했습니다.');
      }
    } catch (error) {
      console.error('관리자 권한 변경 오류:', error);
      showAlert('관리자 권한 변경 중 오류가 발생했습니다: ' + error.message, 'danger');
    } finally {
      // 버튼 상태 복원
      btnSaveAdminEdit.innerHTML = '저장';
      btnSaveAdminEdit.disabled = false;
    }
  }

  /**
   * 관리자 제거 확인
   */
  async function confirmRemoveAdmin() {
    const adminId = removeAdminId.value;

    if (!adminId) {
      showAlert('관리자 ID를 찾을 수 없습니다.', 'warning');
      return;
    }

    // 버튼 비활성화 및 로딩 표시
    btnConfirmRemoveAdmin.disabled = true;
    btnConfirmRemoveAdmin.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 제거 중...';

    try {
      const response = await adminAuthFetch(`/admin/api/admin/${adminId}/remove`, {
        method: 'POST'
      });

      if (!response.ok) {
        throw new Error('서버 응답 오류: ' + response.status);
      }

      const result = await response.json();

      if (result.success) {
        // 관리자 제거 성공
        removeAdminModal.hide();
        showAlert('관리자가 성공적으로 제거되었습니다.', 'success');

        // 페이지 새로고침
        setTimeout(() => {
          window.location.reload();
        }, 1000);
      } else {
        throw new Error(result.message || '관리자 제거 중 오류가 발생했습니다.');
      }
    } catch (error) {
      console.error('관리자 제거 오류:', error);
      showAlert('관리자 제거 중 오류가 발생했습니다: ' + error.message, 'danger');
    } finally {
      // 버튼 상태 복원
      btnConfirmRemoveAdmin.innerHTML = '제거';
      btnConfirmRemoveAdmin.disabled = false;
    }
  }

  /**
   * 요청 승인 확인
   */
  async function confirmApproveRequest() {
    const requestId = approveRequestId.value;
    const userId = approveUserId.value;

    if (!requestId || !userId) {
      showAlert('요청 정보를 찾을 수 없습니다.', 'warning');
      return;
    }

    // 선택된 권한 수집
    const selectedRoles = [];
    document.querySelectorAll('input[name="approveRoles"]:checked').forEach(checkbox => {
      selectedRoles.push(checkbox.value);
    });

    if (selectedRoles.length === 0) {
      showAlert('최소한 하나 이상의 권한을 선택하세요.', 'warning');
      return;
    }

    // 버튼 비활성화 및 로딩 표시
    btnConfirmApprove.disabled = true;
    btnConfirmApprove.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 승인 중...';

    try {
      const response = await adminAuthFetch(`/admin/api/admin/requests/${requestId}/approve`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          userId: userId,
          roles: selectedRoles
        })
      });

      if (!response.ok) {
        throw new Error('서버 응답 오류: ' + response.status);
      }

      const result = await response.json();

      if (result.success) {
        // 요청 승인 성공
        approveRequestModal.hide();
        showAlert('관리자 등록 요청이 성공적으로 승인되었습니다.', 'success');

        // 페이지 새로고침
        setTimeout(() => {
          window.location.reload();
        }, 1000);
      } else {
        throw new Error(result.message || '요청 승인 중 오류가 발생했습니다.');
      }
    } catch (error) {
      console.error('요청 승인 오류:', error);
      showAlert('요청 승인 중 오류가 발생했습니다: ' + error.message, 'danger');
    } finally {
      // 버튼 상태 복원
      btnConfirmApprove.innerHTML = '승인';
      btnConfirmApprove.disabled = false;
    }
  }

  /**
   * 요청 거부 확인
   */
  async function confirmRejectRequest() {
    const requestId = rejectRequestId.value;

    if (!requestId) {
      showAlert('요청 정보를 찾을 수 없습니다.', 'warning');
      return;
    }

    const reason = rejectReason.value.trim();

    // 버튼 비활성화 및 로딩 표시
    btnConfirmReject.disabled = true;
    btnConfirmReject.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 거부 중...';

    try {
      const response = await adminAuthFetch(`/admin/api/admin/requests/${requestId}/reject`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          reason: reason || null
        })
      });

      if (!response.ok) {
        throw new Error('서버 응답 오류: ' + response.status);
      }

      const result = await response.json();

      if (result.success) {
        // 요청 거부 성공
        rejectRequestModal.hide();
        showAlert('관리자 등록 요청이 거부되었습니다.', 'success');

        // 페이지 새로고침
        setTimeout(() => {
          window.location.reload();
        }, 1000);
      } else {
        throw new Error(result.message || '요청 거부 중 오류가 발생했습니다.');
      }
    } catch (error) {
      console.error('요청 거부 오류:', error);
      showAlert('요청 거부 중 오류가 발생했습니다: ' + error.message, 'danger');
    } finally {
      // 버튼 상태 복원
      btnConfirmReject.innerHTML = '거부';
      btnConfirmReject.disabled = false;
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