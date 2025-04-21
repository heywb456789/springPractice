/**
 * 로그인 상태(토큰 유효성)를 확인하고,
 * 살아있다면 지정된 URL로 리다이렉트합니다.
 *
 * @param {string} redirectUrl   인증됐을 때 이동할 URL
 * @param {string} validatePath  (선택) 인증 확인용 엔드포인트 경로, 기본 '/api/auth/validate'
 */

export async function checkAuthAndRedirect({
  redirectUrl,
  validatePath = '/api/auth/validate',
  refreshPath  = '/api/auth/refresh',
  loginPage    = '/login/login.html'
}) {
  const accessToken  = localStorage.getItem('accessToken');
  const refreshToken = localStorage.getItem('refreshToken');

  // 1) 토큰 둘 다 없으면 → 로그인 페이지 유지
  if (!accessToken && !refreshToken) {
    return;
  }

  try {
    // 2) 액세스 토큰만 없으면 → 리프레시 시도
    if (!accessToken && refreshToken) {
      const refreshed = await tryRefresh(refreshPath, refreshToken, loginPage);
      if (refreshed) {
        window.location.replace(redirectUrl);
      }
      return;
    }

    // 3) 액세스 토큰이 있으면 → 검증 시도
    let res = await fetch(validatePath, {
      headers: { 'Authorization': `Bearer ${accessToken}` }
    });

    if (res.status === 204) {
      // 유효한 액세스 토큰
      window.location.replace(redirectUrl);
      return;
    }

    if (res.status === 401 && refreshToken) {
      // 액세스 토큰 만료 → 리프레시 시도
      const refreshed = await tryRefresh(refreshPath, refreshToken, loginPage);
      if (refreshed) {
        window.location.replace(redirectUrl);
      }
    }
    // 그 외 상태(예: 403)는 로그인 페이지 유지
  } catch (err) {
    console.error('Auth check failed:', err);
    // 네트워크 오류 등은 로그인 페이지 유지
  }
}

/**
 * 리프레시 토큰으로 액세스 토큰 재발급 시도
 * @returns {Promise<boolean>} 재발급 성공 시 true, 아니면 false
 */
async function tryRefresh(refreshPath, refreshToken, loginPage) {
  try {
    const refreshRes = await fetch(refreshPath, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify({ refreshToken })
    });
    if (!refreshRes.ok) {
      // 재발급 실패 → 강제 로그아웃
      localStorage.clear();
      window.location.replace(loginPage);
      return false;
    }
    const { response: auth } = await refreshRes.json();
    localStorage.setItem('accessToken', auth.token);
    if (auth.refreshToken) {
      localStorage.setItem('refreshToken', auth.refreshToken);
    }
    return true;
  } catch (e) {
    console.error('Refresh failed:', e);
    localStorage.clear();
    window.location.replace(loginPage);
    return false;
  }
}

