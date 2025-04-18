document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('signupForm');
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const oneId = document.getElementById('oneId').value;
    const phoneNumber = document.getElementById('phone').value;
    const inviteCode = document.getElementById('inviteCode').value;
    const payload = { oneId, phoneNumber, inviteCode };

    try {
      const res = await fetch('/api/members', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!res.ok) throw new Error('가입 실패');
      alert('가입 완료, 로그인 페이지로 이동합니다');
      window.location.href = 'login.html';
    } catch (err) {
      alert(err.message);
    }
  });
});