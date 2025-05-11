import { authFetch, optionalAuthFetch, handleFetchError } from '../commonFetch.js'

document.addEventListener('DOMContentLoaded', () => {
  initBackButton();
  loadVoteData();
  initShareFeatures();
});

// 뒤로가기 버튼
function initBackButton() {
  const backBtn = document.getElementById('backButton');
  if (!backBtn) return;
  backBtn.addEventListener('click', () => {
    window.history.length > 1 ? window.history.back() : window.location.href = 'voteList.html';
  });
}

// URL에서 ID 추출
function getVoteIdFromUrl() {
  return new URLSearchParams(window.location.search).get('id');
}

// 투표 데이터 로드
async function loadVoteData() {
  const voteId = getVoteIdFromUrl();
  if (!voteId) return showError('투표 ID가 올바르지 않습니다.');

  try {
    showLoading(true);
    const res = await optionalAuthFetch(`/api/vote/posts/${voteId}`);
    const { response: voteData } = await res.json();
    updateVoteUI(voteData);
  } catch (err) {
    console.error('투표 데이터 로드 오류:', err);
    handleFetchError(err);
    showError('투표 정보를 불러오는 중 오류가 발생했습니다.');
  } finally {
    showLoading(false);
  }
}

function showLoading(active) {
  document.getElementById('loadingState').style.display = active ? 'flex' : 'none';
  document.getElementById('voteContentContainer').style.display = active ? 'none' : 'block';
}

// UI 업데이트
function updateVoteUI({ question, voteOptions, voted, votedId, votePostId }) {
  document.getElementById('voteQuestion').textContent = question;
  document.getElementById('totalVoteCount').textContent = getTotalVotes(voteOptions);

  renderVoteOptions(voteOptions, voted, votedId, votePostId);
  document.getElementById('voteMessage').textContent = voted
      ? '이미 투표에 참여하셨습니다.'
      : '투표에 참여해 주세요.';

  voted ? showVoteResults(voteOptions) : hideResults();
}

function getTotalVotes(options = []) {
  return options.reduce((sum, o) => sum + (o.voteCount || 0), 0);
}

function renderVoteOptions(options, voted, votedId, voteId) {
  const section = document.getElementById('voteOptionsSection');
  section.innerHTML = '';
  const isSelected = id => voted && votedId === id;

  if (options.length === 2) {
    const [l, r] = options;
    section.innerHTML = `
      <div class="vote-option-row">
        <button class="vote-option-button option-left ${isSelected(l.optionId) ? 'selected' : ''}" data-option-id="${l.optionId}">${l.optionName}</button>
        <span class="vs-text">VS</span>
        <button class="vote-option-button option-right ${isSelected(r.optionId) ? 'selected' : ''}" data-option-id="${r.optionId}">${r.optionName}</button>
      </div>`;
  } else {
    options.forEach(opt => {
      section.insertAdjacentHTML('beforeend', `
        <button class="vote-option-button ${isSelected(opt.optionId) ? 'selected' : ''}" data-option-id="${opt.optionId}">${opt.optionName}</button>
      `);
    });
  }

  if (!voted) initVoteButtons(voteId);
}

function initVoteButtons(voteId) {
  document.querySelectorAll('.vote-option-button').forEach(btn => {
    btn.addEventListener('click', async () => {
      const optionId = btn.dataset.optionId;
      disableButtons(true);
      try {
        await authFetch(`/api/vote/posts/${voteId}/options/${optionId}`, { method: 'POST' });
        document.getElementById('voteMessage').textContent = '투표에 참여해 주셔서 감사합니다.';
        await loadVoteData();
      } catch (err) {
        console.error('투표 오류:', err);
        if (err.message.includes('Unauthorized')) {
          alert('로그인이 필요한 기능입니다.');
          window.location.href = '/login/login.html';
        } else {
          alert('투표 처리 중 오류가 발생했습니다.');
          window.location.reload();
        }
      } finally {
        disableButtons(false);
      }
    });
  });
}

function disableButtons(state) {
  document.querySelectorAll('.vote-option-button').forEach(b => b.disabled = state);
}

function hideResults() {
  document.getElementById('voteResultsSection').style.display = 'none';
}

function showVoteResults(options) {
  const container = document.getElementById('resultsContainer');
  container.innerHTML = '';
  const total = getTotalVotes(options);

  options.forEach((o, i) => {
    const pct = total ? Math.round((o.voteCount / total) * 100) : 0;
    container.insertAdjacentHTML('beforeend', `
      <div class="result-item">
        <div class="result-label">
          <span class="result-option-name">${o.optionName}</span>
          <span class="result-percentage">${pct}% (${o.voteCount}명)</span>
        </div>
        <div class="result-bar-container">
          <div class="result-bar ${i === 0 ? 'result-bar-left' : 'result-bar-right'}" style="width:${pct}%"></div>
        </div>
      </div>`);
  });

  document.getElementById('voteResultsSection').style.display = 'block';
}

function showError(msg) {
  showLoading(false);
  document.querySelector('.vote-detail-container').innerHTML = `
    <div class="error-state">
      <div class="error-icon">...</div>
      <p class="error-title">${msg}</p>
      <button class="retry-button" onclick="window.location.reload()">다시 시도하기</button>
    </div>`;
}

// 공유 기능 (모바일/웹 지원)
function initShareFeatures() {
  document.getElementById('openShareModal')?.addEventListener('click', () => {
    new bootstrap.Modal(document.getElementById('shareModal')).show();
    setupKakaoShare();
  });
  document.getElementById('copyUrl')?.addEventListener('click', copyCurrentUrl);
}

function setupKakaoShare() {
  const title = document.getElementById('voteQuestion').textContent;
  const url = window.location.href;
  const img = 'https://image.newstomato.com/newstomato/club/share/voting.png';

  const container = document.getElementById('kakaotalk-sharing-btn');
  container.innerHTML = `<img src="https://developers.kakao.com/assets/img/about/logos/kakaotalksharing/kakaotalk_sharing_btn_medium.png" alt="카카오톡 공유 버튼"/>`;

  if (window.Kakao && Kakao.isInitialized()) {
    Kakao.Share.createDefaultButton({
      container: '#kakaotalk-sharing-btn',
      objectType: 'feed',
      content: { title, description: '토마토 뉴스 투표광장에서 참여해 보세요!', imageUrl: img, link: { mobileWebUrl: url, webUrl: url } },
      buttons: [{ title: '투표 참여하기', link: { mobileWebUrl: url, webUrl: url } }]
    });
  } else {
    console.error('Kakao SDK 미초기화');
  }
}

function copyCurrentUrl() {
  const voteId = getVoteIdFromUrl();
  const shareUrl = `https://www.xn--w69at2fhshwrs.kr/share/vote/${voteId}`;
  navigator.clipboard.writeText(shareUrl)
      .then(() => {
        alert('URL이 복사되었습니다.');
        new bootstrap.Modal(document.getElementById('shareModal')).hide();
      })
      .catch(err => {
        console.error('복사 실패', err);
        alert('URL 복사에 실패했습니다.');
      });
}
