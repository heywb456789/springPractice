// commonFetch.js

export async function checkAuthAndRedirect(redirectUrl, validatePath = '/api/auth/validate') {
  try {
    const token = localStorage.getItem('accessToken');
    const res = await fetch(validatePath, {
      method: 'GET',
      headers: { 'Authorization': `Bearer ${token}` }  // 토큰 헤더 방식일 때
    });
    // 204 No Content 면 인증 완료 상태
    if (res.status === 204) {
      window.location.replace(redirectUrl);
    }
    // 401 등은 아무 동작 안함 → 로그인 페이지 계속 보여줌
  } catch (err) {
    console.error('Auth check failed:', err);
    // 네트워크 오류도 그냥 무시
  }
}

export async function handleTokenRefresh() {
  const refreshToken = localStorage.getItem('refreshToken');
  const res = await fetch('/api/auth/refresh', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken })
  });

  if (!res.ok) {
    localStorage.clear();
    window.location.href = '../login/login.html';
    throw new Error('세션이 만료되었습니다. 다시 로그인 해주세요.');
  }

  const data = await res.json();
  localStorage.setItem('accessToken', data.response.token);
}

/**
 * 1) 인증이 필수인 API 호출용
 *   - 토큰 없으면 바로 에러
 *   - 토큰 만료시 refresh → 재시도 → 실패면 에러
 */
export async function authFetch(url, options = {}) {
  const token = localStorage.getItem('accessToken');
  if (!token) {
    throw new Error('Unauthorized');
  }

  // 1) 기본 헤더 세팅
  let headers = {
    // JSON 바디일 때만 Content-Type
    ...(options.body instanceof FormData
      ? {}
      : { 'Content-Type': options.contentType || 'application/json' }
    ),
    'Authorization': `Bearer ${token}`,
    // 사용자가 직접 넘긴 추가 헤더
    ...(options.headers || {})
  };

  // 2) 첫 호출
  let res = await fetch(url, {
    method: options.method || 'GET',
    headers,
    credentials: 'include',
    body: options.body
  });

  // 토큰 만료
  if (res.status === 401) {
    await handleTokenRefresh();            // refresh 시도
    const newToken = localStorage.getItem('accessToken');
    headers.Authorization = `Bearer ${newToken}`;

    res = await fetch(url, {
      method: options.method || 'GET',
      headers,
      credentials: 'include',
      body: options.body
    });
  }

  if (!res.ok) {
    throw new Error(`HTTP ${res.status}`);
  }
  return res;
}

/**
 * 2) 인증이 선택인 API 호출용
 *   - 토큰 있으면 authFetch 흐름
 *   - 토큰 없거나 authFetch 전부 실패 시 (401, refresh 실패) → 토큰 없이 fetch
 */
export async function optionalAuthFetch(url, options = {}) {
  const token = localStorage.getItem('accessToken');
  if (token) {
    try {
      return await authFetch(url, options);
    } catch (err) {
      console.warn('AuthFetch 실패, 토큰 없는 상태로 재시도:', err);
      // fallback to no-token fetch
    }
  }
  // 토큰 없거나 authFetch 실패 시
  const res = await fetch(url, {
    method: options.method || 'GET',
    headers: options.headers || {},
    credentials: 'include',
    body: options.body
  });
  if (!res.ok && res.status !== 401) {
    // 401도 로그인 필요하다고 판단하면 그냥 리턴해도 무방
    throw new Error(`HTTP ${res.status}`);
  }
  return res;
}
