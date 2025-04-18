// DOM이 로드된 후 실행될 함수
  document.addEventListener('DOMContentLoaded', () => {
    const elements = {
      form: document.getElementById('signupForm'),
      fullName: document.getElementById('fullName'),
      phoneNumber: document.getElementById('phoneNumber'),
      verificationCode: document.getElementById('verificationCode'),
      sendCodeButton: document.getElementById('sendCodeButton'),
      userId: document.getElementById('userId'),
      password: document.getElementById('password'),
      confirmPassword: document.getElementById('confirmPassword'),
      email: document.getElementById('email'),
      agreeAll: document.getElementById('agreeAll'),
      agreeService: document.getElementById('agreeService'),
      agreePrivacy: document.getElementById('agreePrivacy'),
      agreeMarketing: document.getElementById('agreeMarketing'),
      signupButton: document.getElementById('signupButton'),
      backButton: document.querySelector('.back-button'),

      // 에러 메시지
      nameError: document.getElementById('nameError'),
      phoneError: document.getElementById('phoneError'),
      codeError: document.getElementById('codeError'),
      userIdError: document.getElementById('userIdError'),
      passwordError: document.getElementById('passwordError'),
      confirmPasswordError: document.getElementById('confirmPasswordError'),
      emailError: document.getElementById('emailError')
    };

    // 전화번호 입력 처리
    elements.phoneNumber.addEventListener('input', (e) => {
      // 숫자만 입력 가능하도록
      e.target.value = e.target.value.replace(/\D/g, '');
      validatePhone(elements);
    });

    // 인증번호 발송 버튼 클릭 처리
    elements.sendCodeButton.addEventListener('click', () => {
      if (validatePhone(elements)) {
        alert(`${elements.phoneNumber.value} 번호로 인증번호가 발송되었습니다.`);
        // 실제 구현시 인증번호 발송 API 호출
      }
    });

    // 아이디 입력 처리
    elements.userId.addEventListener('input', () => {
      validateUserId(elements);
    });

    // 비밀번호 입력 처리
    elements.password.addEventListener('input', () => {
      validatePassword(elements);
      if (elements.confirmPassword.value) {
        validateConfirmPassword(elements);
      }
    });

    // 비밀번호 확인 입력 처리
    elements.confirmPassword.addEventListener('input', () => {
      validateConfirmPassword(elements);
    });

    // 이메일 입력 처리
    elements.email.addEventListener('input', () => {
      if (elements.email.value) {
        validateEmail(elements);
      } else {
        elements.emailError.style.display = 'none';
      }
    });

    // 전체 동의 체크박스 처리
    elements.agreeAll.addEventListener('change', () => {
      const isChecked = elements.agreeAll.checked;
      elements.agreeService.checked = isChecked;
      elements.agreePrivacy.checked = isChecked;
      elements.agreeMarketing.checked = isChecked;
      validateForm(elements);
    });

    // 개별 약관 체크박스 처리
    [elements.agreeService, elements.agreePrivacy, elements.agreeMarketing].forEach(checkbox => {
      checkbox.addEventListener('change', () => {
        // 모든 약관이 체크되었는지 확인
        elements.agreeAll.checked =
          elements.agreeService.checked &&
          elements.agreePrivacy.checked &&
          elements.agreeMarketing.checked;
        validateForm(elements);
      });
    });

    // 폼 제출 처리
    elements.form.addEventListener('submit', (e) => {
      e.preventDefault();

      if (validateForm(elements)) {
        // 회원가입 데이터 구성
        const signupData = {
          name: elements.fullName.value,
          phoneNumber: elements.phoneNumber.value,
          verificationCode: elements.verificationCode.value,
          userId: elements.userId.value,
          password: elements.password.value,
          email: elements.email.value,
          marketingAgree: elements.agreeMarketing.checked
        };

        console.log('회원가입 데이터:', signupData);
        alert('회원가입이 완료되었습니다!');

        // 실제 구현 시 서버로 회원가입 요청을 보내는 코드가 여기에 들어갑니다
        // 성공 시 로그인 페이지로 이동
        // window.location.href = 'login.html';
      }
    });

    // 뒤로가기 버튼 클릭 처리
    elements.backButton.addEventListener('click', () => {
      alert('이전 페이지로 돌아갑니다.');
      // 실제 구현 시 이전 페이지로 이동
      // window.history.back();
    });

    // 입력 필드 이벤트 등록
    elements.fullName.addEventListener('input', () => validateName(elements));
    elements.verificationCode.addEventListener('input', () => validateVerificationCode(elements));

    // 모든 입력 필드에 대한 검증 이벤트 등록
    const allInputs = [
      elements.fullName, elements.phoneNumber, elements.verificationCode,
      elements.userId, elements.password, elements.confirmPassword, elements.email
    ];

    allInputs.forEach(input => {
      input.addEventListener('input', () => validateForm(elements));
    });
  });

  // 이름 검증 함수
  const validateName = (elements) => {
    const isValid = elements.fullName.value.trim().length >= 2;
    elements.nameError.style.display = isValid ? 'none' : 'block';
    return isValid;
  };

  // 전화번호 검증 함수
  const validatePhone = (elements) => {
    // 한국 휴대폰 번호 체크 (01X로 시작하는 10-11자리 숫자)
    const isValid = /^01[0-9]{8,9}$/.test(elements.phoneNumber.value);
    elements.phoneError.style.display = isValid ? 'none' : 'block';

    // 버튼 상태 업데이트
    elements.sendCodeButton.disabled = !isValid;
    return isValid;
  };

  // 인증번호 검증 함수
  const validateVerificationCode = (elements) => {
    // 6자리 숫자인지 확인
    const isValid = /^\d{6}$/.test(elements.verificationCode.value);
    elements.codeError.style.display = isValid ? 'none' : 'block';
    return isValid;
  };

  // 아이디 검증 함수
  const validateUserId = (elements) => {
    // 영문, 숫자 조합 4-20자
    const isValid = /^[a-zA-Z0-9]{4,20}$/.test(elements.userId.value);
    elements.userIdError.style.display = isValid ? 'none' : 'block';
    return isValid;
  };

  // 비밀번호 검증 함수
  const validatePassword = (elements) => {
    // 영문, 숫자, 특수문자 조합 8-20자
    const isValid = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()]).{8,20}$/.test(elements.password.value);
    elements.passwordError.style.display = isValid ? 'none' : 'block';
    return isValid;
  };

  // 비밀번호 확인 검증 함수
  const validateConfirmPassword = (elements) => {
    const isValid = elements.password.value === elements.confirmPassword.value;
    elements.confirmPasswordError.style.display = isValid ? 'none' : 'block';
    return isValid;
  };

  // 이메일 검증 함수
  const validateEmail = (elements) => {
    // 이메일 형식 검사
    const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(elements.email.value);
    elements.emailError.style.display = isValid ? 'none' : 'block';
    return isValid || elements.email.value === '';
  };

  // 폼 전체 검증 함수
  const validateForm = (elements) => {
    const isNameValid = validateName(elements);
    const isPhoneValid = validatePhone(elements);
    const isCodeValid = validateVerificationCode(elements);
    const isUserIdValid = validateUserId(elements);
    const isPasswordValid = validatePassword(elements);
    const isConfirmPasswordValid = validateConfirmPassword(elements);
    const isEmailValid = validateEmail(elements);
    const isTermsValid = elements.agreeService.checked && elements.agreePrivacy.checked;

    // 모든 필드가 유효할 때만 버튼 활성화
    const isFormValid = isNameValid && isPhoneValid && isCodeValid &&
                        isUserIdValid && isPasswordValid && isConfirmPasswordValid &&
                        isEmailValid && isTermsValid;

    elements.signupButton.disabled = !isFormValid;

    if (isFormValid) {
      elements.signupButton.style.backgroundColor = '#ff9999';
    } else {
      elements.signupButton.style.backgroundColor = '#ffcccc';
    }

    return isFormValid;
  };