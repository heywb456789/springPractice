/**
 * 투표 상세 페이지 스크립트
 */

import { authFetch, optionalAuthFetch, handleTokenRefresh } from './commonFetch.js';

document.addEventListener('DOMContentLoaded', function () {
  // 뒤로가기 버튼 이벤트
  initBackButton();

  // 투표 데이터 로드
  loadVoteData();
});

/**
 * 뒤로가기 버튼 초기화
 */
function initBackButton() {
  const backButton = document.getElementById('backButton');

  if (backButton) {
    backButton.addEventListener('click', function () {
      // 이전 페이지로 이동 (히스토리가 있는 경우)
      if (window.history.length > 1) {
        window.history.back();
      } else {
        // 히스토리가 없는 경우 목록 페이지로 이동
        window.location.href = 'voteList.html';
      }
    });
  }
}

/**
 * URL에서 투표 ID 추출
 * @returns {string} 투표 ID
 */
function getVoteIdFromUrl() {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('id');
}

/**
 * 투표 데이터 로드
 */
async function loadVoteData() {
  const voteId = getVoteIdFromUrl();

  if (!voteId) {
    showError('투표 ID가 올바르지 않습니다.');
    return;
  }

  try {
    // 로딩 상태 표시
    document.getElementById('loadingState').style.display = 'flex';
    document.getElementById('voteContentContainer').style.display = 'none';

    // API 호출
    const res = await optionalAuthFetch(`/api/vote/posts/${voteId}`);
    const { response: voteData } = await res.json();

    // 로딩 상태 숨김
    document.getElementById('loadingState').style.display = 'none';
    document.getElementById('voteContentContainer').style.display = 'block';

    // 투표 데이터 표시
    updateVoteUI(voteData);

  } catch (err) {
    console.error('투표 데이터 로드 오류:', err);
    showError('투표 정보를 불러오는 중 오류가 발생했습니다.');
  }
}

/**
 * 투표 UI 업데이트
 * @param {Object} voteData - 투표 데이터
 */
function updateVoteUI(voteData) {
  // 질문 표시
  document.getElementById('voteQuestion').textContent = voteData.question;

  // 총 투표수 표시
  document.getElementById('totalVoteCount').textContent = getTotalVotes(voteData.voteOptions);

  // 투표 옵션 표시 (votedId 기준으로 selected 처리)
  renderVoteOptions(voteData);

  // 투표 여부에 따라 결과 표시
  if (voteData.voted) {
    document.getElementById('voteMessage').textContent = '이미 투표에 참여하셨습니다.';
    showVoteResults(voteData.voteOptions);
  } else {
    document.getElementById('voteMessage').textContent = '투표에 참여해 주세요.';
    document.getElementById('voteResultsSection').style.display = 'none';
  }

  // 댓글 이벤트
  initCommentSubmit(voteData.votePostId);
}

/**
 * 총 투표수 계산
 * @param {Array} options - 투표 옵션 배열
 * @returns {number} 총 투표수
 */
function getTotalVotes(options) {
  if (!options || !options.length) return 0;
  return options.reduce((sum, option) => sum + (option.voteCount || 0), 0);
}

/**
 * 투표 옵션 렌더링
 * @param {Object} voteData - 투표 데이터
 */
function renderVoteOptions(voteData) {
  const optionsSection = document.getElementById('voteOptionsSection');
  optionsSection.innerHTML = '';

  const isSelected = optionId => voteData.voted && voteData.votedId === optionId;

  if (voteData.voteOptions.length === 2) {
    const [left, right] = voteData.voteOptions;
    const row = document.createElement('div');
    row.className = 'vote-option-row';
    row.innerHTML = `
      <button class="vote-option-button option-left ${isSelected(left.optionId) ? 'selected' : ''}"
              data-option-id="${left.optionId}">
        ${left.optionName}
      </button>
      <span class="vs-text">VS</span>
      <button class="vote-option-button option-right ${isSelected(right.optionId) ? 'selected' : ''}"
              data-option-id="${right.optionId}">
        ${right.optionName}
      </button>
    `;
    optionsSection.appendChild(row);
  } else {
    voteData.voteOptions.forEach(opt => {
      const btn = document.createElement('button');
      btn.className = `vote-option-button ${isSelected(opt.optionId) ? 'selected' : ''}`;
      btn.setAttribute('data-option-id', opt.optionId);
      btn.textContent = opt.optionName;
      optionsSection.appendChild(btn);
    });
  }

  // 아직 투표 안 한 경우에만 클릭 이벤트 설정
  if (!voteData.voted) {
    initVoteButtons(voteData.votePostId);
  }
}

/**
 * 투표 버튼 이벤트 초기화
 * @param {number} voteId - 투표 ID
 */
function initVoteButtons(voteId) {
  const optionButtons = document.querySelectorAll('.vote-option-button');

  optionButtons.forEach(button => {
    button.addEventListener('click', async function() {
      const optionId = this.getAttribute('data-option-id');

      try {
        // 버튼 중복 클릭 방지
        optionButtons.forEach(btn => btn.disabled = true);

        // 투표 API 호출
        const res = await authFetch(
          `/api/vote/posts/${voteId}/options/${optionId}`,
          {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
          }
        );
        await res.json();

        // 투표 완료 메시지 표시
        document.getElementById('voteMessage').textContent = '투표에 참여해 주셔서 감사합니다.';

        // 다시 상세 조회해서 UI 전체 갱신
        await loadVoteData();

      } catch (err) {
        console.error('투표 오류:', err);
        if (err.message && err.message.includes('Unauthorized')) {
          alert('로그인이 필요한 기능입니다.');
          window.location.href = '/login/login.html';
        } else {
          alert('투표 처리 중 오류가 발생했습니다. 다시 시도해주세요.');
        }
      } finally {
        // 버튼 활성화
        optionButtons.forEach(btn => btn.disabled = false);
      }
    });
  });
}


/**
 * 투표 결과 표시
 * @param {Array} options - 투표 옵션 배열
 */
function showVoteResults(options) {
  const resultsSection = document.getElementById('voteResultsSection');
  const resultsContainer = document.getElementById('resultsContainer');

  if (!resultsSection || !resultsContainer || !options || !options.length) return;

  // 결과 컨테이너 비우기
  resultsContainer.innerHTML = '';

  // 총 투표수 계산
  const totalVotes = getTotalVotes(options);

  // 각 옵션별 결과 생성
  options.forEach((option, index) => {
    const percentage = totalVotes > 0 ? Math.round((option.voteCount / totalVotes) * 100) : 0;

    const resultItem = document.createElement('div');
    resultItem.className = 'result-item';

    resultItem.innerHTML = `
      <div class="result-label">
        <span class="result-option-name">${option.optionName}</span>
        <span class="result-percentage">${percentage}% (${option.voteCount}명)</span>
      </div>
      <div class="result-bar-container">
        <div class="result-bar ${index === 0 ? 'result-bar-left' : 'result-bar-right'}" 
             style="width: ${percentage}%"></div>
      </div>
    `;

    resultsContainer.appendChild(resultItem);
  });

  // 결과 섹션 표시
  resultsSection.style.display = 'block';
}

/**
 * 댓글 제출 이벤트 초기화
 * @param {number} voteId - 투표 ID
 */
function initCommentSubmit(voteId) {
  const commentInput = document.getElementById('commentInput');
  const commentSubmitBtn = document.getElementById('commentSubmitBtn');

  if (!commentInput || !commentSubmitBtn) return;

  commentSubmitBtn.addEventListener('click', async function() {
    const comment = commentInput.value.trim();

    if (!comment) {
      alert('댓글 내용을 입력해주세요.');
      return;
    }

    try {
      // 댓글 API 호출
      await authFetch(`/api/vote/posts/${voteId}/comments`, {
        method: 'POST',
        body: JSON.stringify({ content: comment }),
        headers: {
          'Content-Type': 'application/json'
        }
      });

      // 댓글 입력창 초기화
      commentInput.value = '';

      // 성공 메시지
      alert('댓글이 등록되었습니다.');

      // TODO: 댓글 목록 업데이트 (필요한 경우)

    } catch (err) {
      console.error('댓글 등록 오류:', err);

      if (err.message && err.message.includes('Unauthorized')) {
        alert('로그인이 필요한 기능입니다.');
        // 로그인 페이지로 이동
        window.location.href = '/login/login.html';
      } else {
        alert('댓글 등록 중 오류가 발생했습니다. 다시 시도해주세요.');
      }
    }
  });
}

/**
 * 오류 메시지 표시
 * @param {string} message - 오류 메시지
 */
function showError(message) {
  // 로딩 상태 숨김
  document.getElementById('loadingState').style.display = 'none';

  // 투표 컨테이너에 오류 메시지 표시
  const voteDetailContainer = document.querySelector('.vote-detail-container');

  if (voteDetailContainer) {
    voteDetailContainer.innerHTML = `
      <div class="error-state">
        <div class="error-icon">
          <svg width="60" height="60" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 4C7.58172 4 4 7.58172 4 12C4 16.4183 7.58172 20 12 20C16.4183 20 20 16.4183 20 12C20 7.58172 16.4183 4 12 4Z" stroke="#ff6b6b" stroke-width="2"/>
            <path d="M12 8V13" stroke="#ff6b6b" stroke-width="2" stroke-linecap="round"/>
            <circle cx="12" cy="16" r="1" fill="#ff6b6b"/>
          </svg>
        </div>
        <p class="error-title">${message}</p>
        <button class="retry-button" onclick="window.location.reload()">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4C7.58 4 4.01 7.58 4.01 12C4.01 16.42 7.58 20 12 20C15.73 20 18.84 17.45 19.73 14H17.65C16.83 16.33 14.61 18 12 18C8.69 18 6 15.31 6 12C6 8.69 8.69 6 12 6C13.66 6 15.14 6.69 16.22 7.78L13 11H20V4L17.65 6.35Z" fill="#4263eb"/>
          </svg>
          다시 시도하기
        </button>
      </div>
    `;
  }
}