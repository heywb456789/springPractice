// src/main/resources/static/js/login.js
import { checkAuthAndRedirect, handleTokenRefresh } from '../commonFetch.js'

let inviteModal;
let isNoInviteFlow = false;

// 초기화 함수
const initLoginPage = () => {
  console.log("zdkajldkjaslkdjalsdjl");
  checkAuthAndRedirect({ redirectUrl: '../main/main.html' });

  const elements = selectElements();
  registerEventListeners(elements);
  validateForm(elements);
  console.log('로그인 페이지가 초기화되었습니다.');
};

const selectElements = () => ({
  phoneInput: document.querySelector('.phone-input'),
  passwordInput: document.getElementById('password'),
  loginButton: document.getElementById('verifyButton'),
  registerButton: document.getElementById('registerButton'),
  backButton: document.querySelector('.back-button'),
  autoLoginCheckbox: document.getElementById('autoLogin'),
  inviteSubmitBtn: document.getElementById('inviteSubmitBtn'),
  inviteCodeInput: document.getElementById('inviteCodeInput'),
  inviteError: document.getElementById('inviteError'),
  noInviteLink: document.getElementById('noInviteLink'),
  inviteCancelBtn: document.getElementById('inviteCancelBtn'),
  inviteModalEl: document.getElementById('inviteModal')
});

const registerEventListeners = (elements) => {
  elements.phoneInput.addEventListener('input', (e) => handlePhoneInput(e, elements));
  elements.passwordInput.addEventListener('input', () => handlePasswordInput(elements));
  elements.loginButton.addEventListener('click', () => handleLogin(elements));
  elements.registerButton.addEventListener('click', handleRegister);
  elements.backButton.addEventListener('click', handleBack);
  elements.inviteSubmitBtn.addEventListener('click', () => handleInviteSubmit(elements));
  elements.noInviteLink.addEventListener('click', (e) => {
    e.preventDefault();
    handleNoInvite(elements);
  });
};

// 로그인 처리 (서버 응답에 따른 처리 변경)
const handleLogin = async (elements) => {
  const loginData = {
    phoneNumber: elements.phoneInput.value.trim(),
    password: elements.passwordInput.value,
    autoLogin: elements.autoLoginCheckbox.checked
  };
  try {
    const res = await jwtLogin(loginData);

    // 서버 응답 형식에 맞춰 토큰 저장
    localStorage.setItem('accessToken', res.response.token);
    localStorage.setItem('refreshToken', res.response.refreshToken);

    console.log(res.response.member)

    switch (res.response.member.status) {
      case 'TEMPORARY_INVITE':
        inviteModal = new bootstrap.Modal(elements.inviteModalEl);
        inviteModal.show();
        break;
      case 'TEMPORARY_PASS':
        window.location.href = 'login.js'; // PASS 도입되면 그쪽으로
        break;
      case 'ACTIVE':
        window.location.href = '../main/main.html';
        break;
      default:
        alert("회원님의 계정이 삭제 되었거나. 임시 정지 되었습니다. 고객센터에 문의 부탁드립니다.");
        break;
    }

  } catch (err) {
    alert(err.message);
  }
};

// 초대 코드 확인 및 제출
const handleInviteSubmit = async (elements) => {
  if (isNoInviteFlow) {
    window.location.href = '../main/main.html';
    return;
  }
  const code = elements.inviteCodeInput.value.trim().toUpperCase();
  if (!/^[A-Z0-9]{7}$/.test(code)) {
    elements.inviteError.textContent = '7자리 대문자+숫자로 입력해주세요.';
    elements.inviteError.style.display = 'block';
    return;
  }
  elements.inviteError.style.display = 'none';
  try {
    await submitInviteCode(code);
    alert('초대 코드 등록이 완료되었습니다. 메인 페이지로 이동합니다.');
    window.location.href = '../main/main.html';
  } catch (err) {
    elements.inviteError.textContent = err.message;
    elements.inviteError.style.display = 'block';
  }
};

const handleNoInvite = (elements) => {
  isNoInviteFlow = true;
  const titleEl = elements.inviteModalEl.querySelector('.modal-title');
  titleEl.textContent = '운영진 검토가 진행중입니다.';
  elements.inviteCodeInput.style.display = 'none';
  elements.inviteError.style.display = 'none';
  elements.noInviteLink.style.display = 'none';
  elements.inviteCancelBtn.style.display = 'none';
  elements.inviteSubmitBtn.textContent = '확인';
};

// JWT 로그인 API 호출 개선 (응답 구조 반영)
async function jwtLogin({ phoneNumber, password, autoLogin }) {
  const res = await fetch(`/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ phoneNumber, password, autoLogin })
  });
  if (res.status === 401) throw new Error('로그인 정보가 올바르지 않습니다.');
  if (!res.ok) throw new Error('로그인 실패: ' + res.statusText);
  return res.json(); // 서버 응답 구조 전체 반환
}

// 초대 코드 제출 요청 (Refresh Token 활용하여 401 대응)
async function submitInviteCode(code) {
  const token = localStorage.getItem('accessToken');
  let res = await fetch('/api/members/invite', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ inviteCode: code })
  });

  // Access Token 만료 시 Refresh Token으로 재발급 후 재요청
  if (res.status === 401) {
    await handleTokenRefresh();
    const newToken = localStorage.getItem('accessToken');
    res = await fetch('/api/members/invite', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${newToken}`
      },
      body: JSON.stringify({ inviteCode: code })
    });
  }

  if (!res.ok) throw new Error('초대 코드 등록 실패: ' + res.statusText);
}

// // Refresh Token을 사용하여 새로운 Access Token 발급
// async function handleTokenRefresh() {
//   const refreshToken = localStorage.getItem('refreshToken');
//   const res = await fetch('/api/auth/refresh', {
//     method: 'POST',
//     headers: { 'Content-Type': 'application/json' },
//     body: JSON.stringify({ refreshToken })
//   });
//
//   if (!res.ok) {
//     localStorage.clear(); // 토큰 만료 시 강제 로그아웃 처리
//     window.location.href = '../login/login.html';
//     throw new Error('세션이 만료되었습니다. 다시 로그인 해주세요.');
//   }
//
//   const data = await res.json();
//   localStorage.setItem('accessToken', data.response.token); // 새 Access Token 저장
// }

// 기타 이벤트 처리 (변경 없음)
const handleBack = () => window.history.back();

const handlePhoneInput = (e, elements) => {
  e.target.value = e.target.value.replace(/\D/g, '');
  validateForm(elements);
};

const handlePasswordInput = (elements) => validateForm(elements);

const handleRegister = () => window.location.href = '../login/signup.html';

const validateForm = (elements) => {
  const isPhoneValid = /^01[0-9]{8,9}$/.test(elements.phoneInput.value);
  const isPasswordValid = elements.passwordInput.value.length >= 4;
  const isFormValid = isPhoneValid && isPasswordValid;
  elements.loginButton.disabled = !isFormValid;
  elements.loginButton.style.backgroundColor = isFormValid ? '#ff9999' : '#ffcccc';
};

document.addEventListener('DOMContentLoaded', initLoginPage);
