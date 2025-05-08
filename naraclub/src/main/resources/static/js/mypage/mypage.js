// 마이페이지 스크립트
import {authFetch} from "../commonFetch.js";

document.addEventListener('DOMContentLoaded', function() {
    // 로그인 상태 확인
    checkLoginStatus();

    // 프로필 이미지 수정 기능
    initProfileImageEdit();

    // 사용자 이름 수정 기능
    initNameEdit();

    // 로그아웃 기능
    initLogout();

    // 계정 연동 기능
    initAccountLinking();
});

// 로그인 상태 확인
async function checkLoginStatus() {
  try {
    const token = localStorage.getItem('accessToken');

    // 인증 상태 확인 API 호출
    const res = await fetch('/api/auth/validate', {
      method: 'GET',
      headers: { 'Authorization': `Bearer ${token}` }
    });

    // 204 No Content면 인증 완료 상태
    if (res.status === 204) {
      // 인증 성공 시 활동 내역 데이터 로드
      fetchUserInfo();
    } else {
      // 401 등 인증 실패
      throw new Error('인증 실패');
    }
  } catch (error) {
    console.error('인증 확인 오류:', error);

    // 인증 실패 시 로그인 페이지로 리디렉션
    alert('로그인이 필요합니다.');
    window.location.href = '/login/login.html';
  }
}

// 사용자 정보 불러오기
async function fetchUserInfo() {
    try {
        const response = await authFetch(`/api/auth/me`);

        if (!response.ok) {
            if (response.status === 401) {
                alert('로그인이 필요합니다.');
                window.location.href = '/login/login.html';
                return;
            }
            throw new Error('서버 응답 오류: ' + response.status);
        }

        const data = await response.json();

        // API 응답 구조에 맞게 데이터 파싱
        if (data.status && data.status.code === "OK_0000" && data.response) {
            displayUserInfo(data.response);
        } else {
            throw new Error('잘못된 응답 형식');
        }
    } catch (error) {
        console.error('회원 정보 가져오기 오류:', error);

        if (error instanceof Response) {
            if (error.status === 401) {
                alert('로그인이 필요합니다.');
                window.location.href = '/login/login.html';
                return;
            }
        }
    }
}

// 사용자 정보 표시
function displayUserInfo(userInfo) {
    const userNameElement = document.getElementById('userName');

    // 사용자 이름
    const userName = userInfo.name || '사용자';
    userNameElement.textContent = userName;

    // 원래 이름으로 저장 (편집 취소 시 사용)
    userNameElement.setAttribute('data-original-name', userName);

    // 회원 등급 (role 값에 따라 표시)
    let userLevel = '일반회원';
    if (userInfo.role === 'USER_ACTIVE') {
        userLevel = '일반회원';
    } else if (userInfo.role === 'ADMIN') {
        userLevel = '관리자';
    }
    document.querySelector('.profile-level').textContent = userLevel;

    // 프로필 이미지
    if (userInfo.profileImg && userInfo.profileImg.trim() !== '') {
        const img = document.createElement('img');
        img.src = userInfo.profileImg;
        img.alt = '프로필 이미지';

        const profileImageContainer = document.getElementById('profileImage');
        profileImageContainer.innerHTML = '';
        profileImageContainer.appendChild(img);
    }

    // SNS 연동 상태 (현재는 구현 전이므로 기본값으로 처리)
    const linkedAccounts = {
        twitter: false,
        dcinside: false
    };

    // 나중에 snsLinks가 구현되면 아래 주석 해제
    // if (userInfo.snsLinks && Array.isArray(userInfo.snsLinks)) {
    //     userInfo.snsLinks.forEach(link => {
    //         if (link.type === 'TWITTER') linkedAccounts.twitter = true;
    //         if (link.type === 'DCINSIDE') linkedAccounts.dcinside = true;
    //     });
    // }

    updateAccountLinkStatus(linkedAccounts);
}

// 프로필 이미지 수정 초기화
function initProfileImageEdit() {
    const profileImageEdit = document.getElementById('profileImageEdit');
    const profileImageInput = document.getElementById('profileImageInput');

    profileImageEdit.addEventListener('click', function() {
        profileImageInput.click();
    });

    profileImageInput.addEventListener('change', function(event) {
        if (event.target.files && event.target.files[0]) {
            const file = event.target.files[0];
            const reader = new FileReader();

            reader.onload = function(e) {
                const img = document.createElement('img');
                img.src = e.target.result;
                img.alt = '프로필 이미지';

                const profileImageContainer = document.getElementById('profileImage');
                profileImageContainer.innerHTML = '';
                profileImageContainer.appendChild(img);

                // 프로필 이미지 업로드 API 호출
                uploadProfileImage(file);
            };

            reader.readAsDataURL(file);
        }
    });
}

// 프로필 이미지 업로드 (서버 API 호출)
async function uploadProfileImage(file) {
    try {
        const formData = new FormData();
        formData.append('file', file);

        const response = await authFetch('/api/user/profile-image', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error('프로필 이미지 업로드 실패: ' + response.status);
        }

        const data = await response.json();
        console.log('프로필 이미지 업로드 성공:', data);

        // 성공 메시지 표시 또는 추가 작업
        alert('프로필 이미지가 변경되었습니다.');
    } catch (error) {
        console.error('프로필 이미지 업로드 오류:', error);
        alert('프로필 이미지 변경에 실패했습니다.');
    }
}

function initNameEdit() {
    const editNameBtn = document.getElementById('editNameBtn');
    const userNameElement = document.getElementById('userName');

    // 원래 이름 저장
    if (userNameElement.textContent.trim() !== '') {
        userNameElement.setAttribute('data-original-name', userNameElement.textContent);
    }

    editNameBtn.addEventListener('click', function() {
        // 현재 텍스트 가져오기
        const currentName = userNameElement.getAttribute('data-original-name') || userNameElement.textContent;
        console.log("편집 버튼 클릭, 현재 이름:", currentName);

        // 편집 모드인지 확인
        const isEditing = userNameElement.classList.contains('editing');

        if (!isEditing) {
            console.log("편집 모드로 전환");

            // 편집 모드로 전환
            userNameElement.classList.add('editing');

            // 입력 필드 생성 및 추가
            const inputField = document.createElement('input');
            inputField.type = 'text';
            inputField.id = 'userNameInput';
            inputField.className = 'user-name-input';
            inputField.value = currentName;

            // 기존 내용 비우고 입력 필드 추가
            userNameElement.innerHTML = '';
            userNameElement.appendChild(inputField);

            // 버튼 변경
            editNameBtn.innerHTML = '';
            const checkIcon = document.createElement('i');
            checkIcon.className = 'fa-solid fa-check';
            editNameBtn.appendChild(checkIcon);
            editNameBtn.classList.add('save-btn');

            // 입력 필드에 자동 포커스
            inputField.focus();
            inputField.select(); // 텍스트 전체 선택

            // 입력 필드에 엔터 키 이벤트 추가
            inputField.addEventListener('keypress', function(event) {
                if (event.key === 'Enter') {
                    event.preventDefault();
                    const newName = inputField.value.trim();

                    if (newName && newName !== currentName) {
                        updateUserName(newName);
                    } else {
                        cancelNameEdit();
                    }
                }
            });
        } else {
            console.log("저장 모드 실행");

            // 입력 필드에서 값 가져오기
            const inputField = document.getElementById('userNameInput');
            if (!inputField) {
                console.error("입력 필드를 찾을 수 없음");
                return;
            }

            const newName = inputField.value.trim();

            if (newName && newName !== currentName) {
                // 새 이름으로 업데이트 API 호출
                updateUserName(newName);
            } else {
                // 이름이 없거나 변경되지 않았으면 원래 이름으로 되돌림
                cancelNameEdit();
            }
        }
    });

    // 외부 클릭 시 편집 모드 종료
    document.addEventListener('click', function(event) {
        const userNameElement = document.getElementById('userName');
        const editNameBtn = document.getElementById('editNameBtn');

        // 이름 편집 중이고, 클릭이 이름 영역 또는 편집 버튼 외부인 경우
        if (userNameElement.classList.contains('editing') &&
            !userNameElement.contains(event.target) &&
            event.target !== editNameBtn &&
            !editNameBtn.contains(event.target)) {

            console.log("외부 클릭으로 편집 취소");
            cancelNameEdit();
        }
    });
}

// 편집 버튼 클릭 핸들러 (함수 분리)
function handleEditButtonClick() {
    const userNameElement = document.getElementById('userName');
    const editNameBtn = document.getElementById('editNameBtn');

    if (!userNameElement || !editNameBtn) {
        console.error("필수 요소를 찾을 수 없음");
        return;
    }

    // 편집 모드인지 확인
    if (userNameElement.querySelector('input')) {
        // 이미 편집 모드인 경우 (저장 처리)
        const inputField = userNameElement.querySelector('input');
        const newName = inputField.value.trim();
        const currentName = userNameElement.getAttribute('data-original-name') || '';

        if (newName && newName !== currentName) {
            updateUserName(newName);
        } else {
            restoreOriginalName();
        }
    } else {
        // 편집 모드가 아닌 경우 (편집 시작)
        startEditMode();
    }
}

// 사용자 이름 업데이트 (서버 API 호출)
async function updateUserName(newName) {
    try {
        // API 호출 전 입력 필드 비활성화
        const userNameElement = document.getElementById('userName');
        const inputField = userNameElement.querySelector('input');
        if (inputField) {
            inputField.disabled = true;
        }

        const response = await authFetch('/api/user/update-name', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name: newName })
        });

        if (!response.ok) {
            throw new Error('이름 변경 실패: ' + response.status);
        }

        const data = await response.json();
        console.log('API 응답:', data);

        // API 성공 시에만 UI 업데이트
        if (data.status && data.status.code === "OK_0000") {
            // 이름 업데이트
            userNameElement.innerHTML = '';
            userNameElement.textContent = newName;

            // 새 이름을 다음 편집을 위해 저장
            userNameElement.setAttribute('data-original-name', newName);

            // 버튼 원상복구
            const editNameBtn = document.getElementById('editNameBtn');
            editNameBtn.innerHTML = '';
            const icon = document.createElement('i');
            icon.className = 'fa-solid fa-pen';
            editNameBtn.appendChild(icon);

            // 외부 클릭 이벤트 제거
            document.removeEventListener('click', handleOutsideClick);

            // 성공 메시지
            alert('이름이 변경되었습니다.');
        } else {
            throw new Error('서버 응답 오류');
        }
    } catch (error) {
        console.error('이름 변경 오류:', error);
        alert('이름 변경에 실패했습니다.');

        // 오류 시 원래 이름으로 복원
        restoreOriginalName();
    }
}

// 편집 모드 시작
function startEditMode() {
    const userNameElement = document.getElementById('userName');
    const editNameBtn = document.getElementById('editNameBtn');

    if (!userNameElement || !editNameBtn) return;

    // 현재 이름 가져오기
    const currentName = userNameElement.textContent.trim();

    // 원래 이름 저장
    userNameElement.setAttribute('data-original-name', currentName);

    // 입력 필드 생성
    const inputField = document.createElement('input');
    inputField.type = 'text';
    inputField.value = currentName;
    inputField.className = 'user-name-input';

    // 입력 필드 엔터 키 처리
    inputField.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            const newName = inputField.value.trim();
            if (newName && newName !== currentName) {
                updateUserName(newName);
            } else {
                restoreOriginalName();
            }
        }
    });

    // 기존 내용 비우고 입력 필드 추가
    userNameElement.innerHTML = '';
    userNameElement.appendChild(inputField);

    // 버튼 변경
    editNameBtn.innerHTML = '';
    const icon = document.createElement('i');
    icon.className = 'fa-solid fa-check';
    editNameBtn.appendChild(icon);

    // 입력 필드 포커스
    inputField.focus();
    inputField.select();

    // 외부 클릭 이벤트 (한 번만 등록)
    document.removeEventListener('click', handleOutsideClick);
    document.addEventListener('click', handleOutsideClick);
}

// 외부 클릭 처리
function handleOutsideClick(event) {
    const userNameElement = document.getElementById('userName');
    const editNameBtn = document.getElementById('editNameBtn');

    if (!userNameElement || !editNameBtn) return;

    const inputField = userNameElement.querySelector('input');
    if (!inputField) return;

    // 이름 영역이나 편집 버튼이 아닌 영역 클릭 시
    if (!userNameElement.contains(event.target) &&
        !editNameBtn.contains(event.target)) {
        restoreOriginalName();
        document.removeEventListener('click', handleOutsideClick);
    }
}

// 원래 이름으로 복원
function restoreOriginalName() {
    const userNameElement = document.getElementById('userName');
    const editNameBtn = document.getElementById('editNameBtn');

    if (!userNameElement || !editNameBtn) return;

    // 원래 이름 가져오기
    const originalName = userNameElement.getAttribute('data-original-name') || '';

    // 원래 이름으로 복원
    userNameElement.innerHTML = '';
    userNameElement.textContent = originalName;

    // 버튼 원상복구
    editNameBtn.innerHTML = '';
    const icon = document.createElement('i');
    icon.className = 'fa-solid fa-pen';
    editNameBtn.appendChild(icon);

    // 외부 클릭 이벤트 제거
    document.removeEventListener('click', handleOutsideClick);
}


// 로그아웃 초기화
function initLogout() {
    const logoutBtn = document.getElementById('logoutBtn');

    logoutBtn.addEventListener('click', function() {
        // 로그아웃 확인
        if (confirm('로그아웃 하시겠습니까?')) {
            // 로컬스토리지에서 토큰 제거
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');

            // 로그아웃 API 호출 (선택적)
            logoutAPI();

            // 로그인 페이지로 리다이렉트
            window.location.href = '/main/main.html';
        }
    });
}

// 로그아웃 API 호출 (선택적)
async function logoutAPI() {
    try {
        const response = await authFetch('/api/auth/logout', {
            method: 'POST'
        });

        if (!response.ok) {
            console.warn('로그아웃 API 호출 실패:', response.status);
        }
    } catch (error) {
        console.warn('로그아웃 API 호출 오류:', error);
        // 오류가 발생해도 로컬에서는 로그아웃 처리가 되므로 무시
    }
}

// 계정 연동 초기화
function initAccountLinking() {
    const twitterLinkBtn = document.getElementById('twitterLinkBtn');
    const dcinsideLinkBtn = document.getElementById('dcinsideLinkBtn');

    twitterLinkBtn.addEventListener('click', function() {
        // X(트위터) 계정 연동 처리
        const isLinked = twitterLinkBtn.textContent === '연동해제';

        if (isLinked) {
            // 연동 해제 처리
            unlinkAccount('twitter');
        } else {
            // 연동 처리
            linkAccount('twitter');
        }
    });

    dcinsideLinkBtn.addEventListener('click', function() {
        // 디시인사이드 계정 연동 처리
        const isLinked = dcinsideLinkBtn.textContent === '연동해제';

        if (isLinked) {
            // 연동 해제 처리
            unlinkAccount('dcinside');
        } else {
            // 연동 처리
            linkAccount('dcinside');
        }
    });
}

// 계정 연동 상태 업데이트
function updateAccountLinkStatus(linkedAccounts) {
    // 트위터 연동 상태
    const twitterStatus = document.getElementById('twitterLinkStatus');
    const twitterLinkBtn = document.getElementById('twitterLinkBtn');

    if (linkedAccounts.twitter) {
        twitterStatus.textContent = '연동됨';
        twitterStatus.classList.remove('not-linked');
        twitterStatus.classList.add('linked');
        twitterLinkBtn.textContent = '연동해제';
        twitterLinkBtn.classList.add('unlink-btn');
        twitterLinkBtn.classList.remove('link-btn');
    } else {
        twitterStatus.textContent = '연동되지 않음';
        twitterStatus.classList.add('not-linked');
        twitterStatus.classList.remove('linked');
        twitterLinkBtn.textContent = '연동하기';
        twitterLinkBtn.classList.remove('unlink-btn');
        twitterLinkBtn.classList.add('link-btn');
    }

    // 디시인사이드 연동 상태
    const dcinsideStatus = document.getElementById('dcinsideLinkStatus');
    const dcinsideLinkBtn = document.getElementById('dcinsideLinkBtn');

    if (linkedAccounts.dcinside) {
        dcinsideStatus.textContent = '연동됨';
        dcinsideStatus.classList.remove('not-linked');
        dcinsideStatus.classList.add('linked');
        dcinsideLinkBtn.textContent = '연동해제';
        dcinsideLinkBtn.classList.add('unlink-btn');
        dcinsideLinkBtn.classList.remove('link-btn');
    } else {
        dcinsideStatus.textContent = '연동되지 않음';
        dcinsideStatus.classList.add('not-linked');
        dcinsideStatus.classList.remove('linked');
        dcinsideLinkBtn.textContent = '연동하기';
        dcinsideLinkBtn.classList.remove('unlink-btn');
        dcinsideLinkBtn.classList.add('link-btn');
    }
}

// 계정 연동 처리 (서버 API 호출)
async function linkAccount(accountType) {
    try {
        // 각 SNS에 맞는 URL로 리다이렉트 (OAuth 인증 페이지)
        let authUrl = '';

        if (accountType === 'twitter') {
            authUrl = '/api/oauth/twitter';
        } else if (accountType === 'dcinside') {
            authUrl = '/api/oauth/dcinside';
        }

        // 인증 페이지로 이동 (팝업 또는 새 창)
        window.open(authUrl, `${accountType}Auth`, 'width=600,height=600');

        // 실제 환경에서는 OAuth 콜백을 처리하는 이벤트 리스너가 필요함
        // 여기서는 데모용으로 연동 성공 처리
        console.log(`${accountType} 연동 시작됨`);

        // 실제 연동은 OAuth 콜백을 통해 처리되어야 함
        // 데모용 코드는 제거하고 실제 연동 로직으로 대체해야 함
    } catch (error) {
        console.error(`${accountType} 연동 오류:`, error);
        alert('계정 연동에 실패했습니다.');
    }
}

// 계정 연동 해제 처리 (서버 API 호출)
async function unlinkAccount(accountType) {
    try {
        // API 엔드포인트 설정
        let endpoint = '';
        if (accountType === 'twitter') {
            endpoint = '/api/oauth/unlink/twitter';
        } else if (accountType === 'dcinside') {
            endpoint = '/api/oauth/unlink/dcinside';
        }

        const response = await authFetch(endpoint, {
            method: 'POST'
        });

        if (!response.ok) {
            throw new Error(`${accountType} 연동 해제 실패: ${response.status}`);
        }

        const data = await response.json();
        console.log(`${accountType} 연동 해제 성공:`, data);

        // UI 업데이트
        const linkedAccounts = {
            twitter: accountType === 'twitter' ? false : document.getElementById('twitterLinkStatus').classList.contains('linked'),
            dcinside: accountType === 'dcinside' ? false : document.getElementById('dcinsideLinkStatus').classList.contains('linked')
        };

        updateAccountLinkStatus(linkedAccounts);

        // 성공 메시지
        alert(`${accountType === 'twitter' ? 'X(트위터)' : '디시인사이드'} 계정 연동이 해제되었습니다.`);
    } catch (error) {
        console.error(`${accountType} 연동 해제 오류:`, error);
        alert('계정 연동 해제에 실패했습니다.');
    }
}