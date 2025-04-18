// src/main/resources/static/js/login.js

let inviteModal;           // Bootstrap Modal 인스턴스
let isNoInviteFlow = false; // “초대코드 없음” 플래그

// 초기화 함수
const initLoginPage = () => {
  const elements = selectElements();
  registerEventListeners(elements);
  validateForm(elements);
  console.log('로그인 페이지가 초기화되었습니다.');
};

// DOM 요소를 선택하는 함수
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

// 모든 이벤트 리스너 등록 함수
const registerEventListeners = (elements) => {
  elements.phoneInput.addEventListener('input', (e) => handlePhoneInput(e, elements));
  elements.passwordInput.addEventListener('input', () => handlePasswordInput(elements));
  elements.loginButton.addEventListener('click', () => handleLogin(elements));
  elements.registerButton.addEventListener('click', handleRegister);
  elements.backButton.addEventListener('click', handleBack);

  // 초대 코드 모달 관련
  elements.inviteSubmitBtn.addEventListener('click', () => handleInviteSubmit(elements));
  elements.noInviteLink.addEventListener('click', (e) => {
    e.preventDefault();
    handleNoInvite(elements);
  });
};

// 로그인 버튼 클릭 이벤트 핸들러
const handleLogin = async (elements) => {
  const loginData = {
    phoneNumber: elements.phoneInput.value.trim(),
    password: elements.passwordInput.value,
    autoLogin: elements.autoLoginCheckbox.checked
  };
  try {
    const { token } = await jwtLogin(loginData);
    localStorage.setItem('jwtToken', token);
    // 모달 인스턴스 생성 및 표시
    inviteModal = new bootstrap.Modal(elements.inviteModalEl);
    inviteModal.show();
  } catch (err) {
    alert(err.message);
  }
};

// 초대 코드 확인 핸들러
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
    const token = localStorage.getItem('jwtToken');
    await submitInviteCode(code, token);
    alert('초대 코드 등록이 완료되었습니다. 메인 페이지로 이동합니다.');
    window.location.href = '../main/main.html';
  } catch (err) {
    elements.inviteError.textContent = err.message;
    elements.inviteError.style.display = 'block';
  }
};

// “초대코드가 없습니까?” 핸들러
const handleNoInvite = (elements) => {
  isNoInviteFlow = true;
  const titleEl = elements.inviteModalEl.querySelector('.modal-title');
  titleEl.textContent = '운영진 검토가 진행중입니다.';
  // 숨기기
  elements.inviteCodeInput.style.display = 'none';
  elements.inviteError.style.display = 'none';
  elements.noInviteLink.style.display = 'none';
  elements.inviteCancelBtn.style.display = 'none';
  elements.inviteSubmitBtn.textContent = '확인';
};

// JWT 로그인 요청 (내부 API 호출)
async function jwtLogin({ phoneNumber, password }) {
  const res = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({"phoneNumber": phoneNumber, "password":password })
  });
  if (!res.ok) throw new Error('로그인 실패: ' + res.statusText);
  return res.json();
}

// 초대 코드 제출 요청
async function submitInviteCode(code, token) {
  const res = await fetch('/api/members/invite', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ inviteCode: code })
  });
  if (!res.ok) throw new Error('초대 코드 등록 실패');
}

// 뒤로가기 버튼 클릭 핸들러
const handleBack = () => window.history.back();

// 전화번호 입력 핸들러
const handlePhoneInput = (e, elements) => {
  e.target.value = e.target.value.replace(/\D/g, '');
  validateForm(elements);
};

// 비밀번호 입력 핸들러
const handlePasswordInput = (elements) => validateForm(elements);

// 회원가입 버튼 핸들러
const handleRegister = () => window.location.href = '../login/signup.html';

// 폼 검증
const validateForm = (elements) => {
  const isPhoneValid = /^01[0-9]{8,9}$/.test(elements.phoneInput.value);
  const isPasswordValid = elements.passwordInput.value.length >= 4;
  const isFormValid = isPhoneValid && isPasswordValid;
  elements.loginButton.disabled = !isFormValid;
  elements.loginButton.style.backgroundColor = isFormValid ? '#ff9999' : '#ffcccc';
};

document.addEventListener('DOMContentLoaded', initLoginPage);
