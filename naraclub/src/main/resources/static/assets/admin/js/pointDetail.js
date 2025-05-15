/**
 * 포인트 내역 상세 페이지 JavaScript
 */
import {adminAuthFetch} from '/js/commonFetch.js';

document.addEventListener('DOMContentLoaded', function () {
    'use strict';

    // DOM 요소
    const cancelPointForm = document.getElementById('cancelPointForm');
    const btnConfirmCancel = document.getElementById('btnConfirmCancel');
    const cancelPointModal = new bootstrap.Modal(document.getElementById('cancelPointModal'));

    // 초기화
    initPage();

    /**
     * 페이지 초기화 함수
     */
    function initPage() {
        // 이벤트 리스너 등록
        initEventListeners();

        // 포인트 회수 모달 유효성 검사 초기화
        initCancelPointValidation();
    }

    /**
     * 이벤트 리스너 초기화
     */
    function initEventListeners() {
        // 포인트 회수 확인 버튼 이벤트
        if (btnConfirmCancel) {
            btnConfirmCancel.addEventListener('click', confirmCancelPoint);
        }

        // 금액 입력 이벤트
        const cancelAmountInput = document.getElementById('cancelAmount');
        if (cancelAmountInput) {
            cancelAmountInput.addEventListener('input', function() {
                validateCancelAmount();
            });
        }
    }

    /**
     * 취소 금액 유효성 검사 초기화
     */
    function initCancelPointValidation() {
        const cancelAmountInput = document.getElementById('cancelAmount');
        if (cancelAmountInput) {
            // 최소값 설정
            cancelAmountInput.min = 10;

            // 최대값 설정
            const maxAmount = parseInt(cancelAmountInput.getAttribute('max') || '0');
            cancelAmountInput.max = maxAmount;

            // 기본값 설정 (최대값)
            cancelAmountInput.value = maxAmount;
        }
    }

    /**
     * 취소 금액 유효성 검사
     */
    function validateCancelAmount() {
        const cancelAmountInput = document.getElementById('cancelAmount');
        const btnConfirmCancel = document.getElementById('btnConfirmCancel');

        if (!cancelAmountInput || !btnConfirmCancel) return;

        const amount = parseInt(cancelAmountInput.value);
        const maxAmount = parseInt(cancelAmountInput.max);

        // 금액이 유효하지 않은 경우
        if (isNaN(amount) || amount <= 0) {
            cancelAmountInput.classList.add('is-invalid');
            btnConfirmCancel.disabled = true;
            return false;
        }

        // 금액이 최대값을 초과하는 경우
        if (amount > maxAmount) {
            cancelAmountInput.value = maxAmount;
            showToast(`최대 ${maxAmount.toLocaleString()}P까지 회수 가능합니다.`, 'warning');
            return false;
        }

        // 금액이 최소값보다 작은 경우
        if (amount < 10) {
            cancelAmountInput.value = 10;
            showToast('최소 10P 이상 회수해야 합니다.', 'warning');
            return false;
        }

        cancelAmountInput.classList.remove('is-invalid');
        btnConfirmCancel.disabled = false;
        return true;
    }

    /**
     * 포인트 회수 확인
     */
    async function confirmCancelPoint() {
        const pointId = document.getElementById('cancelPointId').value;
        const cancelAmountInput = document.getElementById('cancelAmount');
        const cancelReasonInput = document.getElementById('cancelReason');

        if (!cancelAmountInput || !cancelReasonInput) return;

        const amount = parseInt(cancelAmountInput.value);
        const reason = cancelReasonInput.value.trim();

        // 금액 유효성 검사
        if (!validateCancelAmount()) {
            return;
        }

        // 사유 유효성 검사
        if (!reason) {
            cancelReasonInput.classList.add('is-invalid');
            showToast('회수 사유를 입력해주세요.', 'warning');
            return;
        } else {
            cancelReasonInput.classList.remove('is-invalid');
        }

        // 확인 메시지
        if (!confirm(`${amount.toLocaleString()}P를 회수하시겠습니까?`)) {
            return;
        }

        // 회수 버튼 비활성화
        const originalButtonText = btnConfirmCancel.innerHTML;
        btnConfirmCancel.disabled = true;
        btnConfirmCancel.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 처리 중...';

        try {
            const response = await adminAuthFetch('/admin/points/cancel', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    pointId,
                    amount,
                    reason
                })
            });

            if (!response.ok) {
                throw new Error('포인트 회수 중 오류가 발생했습니다.');
            }

            const result = await response.json();

            if (result.success) {
                showToast('포인트가 성공적으로 회수되었습니다.', 'success');
                cancelPointModal.hide();

                // 페이지 새로고침
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                throw new Error(result.message || '포인트 회수 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('포인트 회수 오류:', error);
            showToast(error.message, 'error');
        } finally {
            btnConfirmCancel.disabled = false;
            btnConfirmCancel.innerHTML = originalButtonText;
        }
    }

    /**
     * 토스트 알림 표시
     * @param {string} message - 알림 메시지
     * @param {string} type - 알림 타입 (success, error, warning, info)
     */
    function showToast(message, type = 'info') {
        // 만약 Toastr 라이브러리가 있다면 사용
        if (window.toastr) {
            toastr[type](message);
            return;
        }

        // 커스텀 알림 시스템 사용
        if (window.CustomNotification) {
            window.CustomNotification.show(message, type);
            return;
        }

        // 기본 alert 사용
        alert(message);
    }
});