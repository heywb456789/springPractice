/**
 * 포인트 내역 목록 페이지 JavaScript
 */
import {adminAuthFetch} from '/js/commonFetch.js';

document.addEventListener('DOMContentLoaded', function () {
    'use strict';

    // DOM 요소
    const searchForm = document.getElementById('searchForm');
    const dateRangeInput = document.getElementById('dateRange');
    const selectAllCheckbox = document.getElementById('selectAll');
    const pointCheckboxes = document.querySelectorAll('.point-select');
    const btnExportExcel = document.getElementById('btnExportExcel');
    const btnAddPoints = document.getElementById('btnAddPoints');
    const addPointsModal = new bootstrap.Modal(document.getElementById('addPointsModal'));
    const cancelPointModal = new bootstrap.Modal(document.getElementById('cancelPointModal'));
    const bulkCancelModal = new bootstrap.Modal(document.getElementById('bulkCancelModal'));
    const btnBulkCancel = document.getElementById('btnBulkCancel');
    const btnBulkExport = document.getElementById('btnBulkExport');

    // 초기화
    initPage();

    /**
     * 페이지 초기화 함수
     */
    function initPage() {
        // 이벤트 리스너 등록
        initEventListeners();

        // Flatpickr 달력 초기화
        initFlatpickrDateRange();

        // 툴팁 초기화
        initTooltips();

        // 숫자 입력 필드 포맷 초기화
        initNumberInputs();
    }

    /**
     * 툴팁 초기화
     */
    function initTooltips() {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }

    /**
     * 이벤트 리스너 초기화
     */
    function initEventListeners() {
        // 검색 폼 제출 이벤트
        if (searchForm) {
            searchForm.addEventListener('submit', function(e) {
                e.preventDefault();
                performSearch();
            });

            // 검색 버튼 클릭 이벤트
            const searchButton = searchForm.querySelector('button[type="submit"]');
            if (searchButton) {
                searchButton.addEventListener('click', function(e) {
                    e.preventDefault();
                    performSearch();
                });
            }

            // 검색어 입력 필드에서 엔터 키 이벤트
            const searchTextInput = document.getElementById('searchText');
            if (searchTextInput) {
                searchTextInput.addEventListener('keypress', function(e) {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        performSearch();
                    }
                });
            }
        }

        // 엑셀 내보내기 버튼 이벤트
        if (btnExportExcel) {
            btnExportExcel.addEventListener('click', exportToExcel);
        }

        // 포인트 지급 버튼 이벤트
        if (btnAddPoints) {
            btnAddPoints.addEventListener('click', function() {
                // 모달 필드 초기화
                document.getElementById('userId').value = '';
                document.getElementById('userName').value = '';
                document.getElementById('pointAmount').value = '';
                document.getElementById('pointDescription').value = '';
                document.getElementById('expiryDate').value = '';
                document.getElementById('userIdList').value = '';
                document.getElementById('userCount').textContent = '0';

                // 기본 지급 대상 유형 설정
                document.getElementById('pointRecipientType').value = 'INDIVIDUAL';
                toggleRecipientFields('INDIVIDUAL');

                // 모달 표시
                addPointsModal.show();
            });
        }

        // 지급 대상 유형 변경 이벤트
        const pointRecipientType = document.getElementById('pointRecipientType');
        if (pointRecipientType) {
            pointRecipientType.addEventListener('change', function() {
                toggleRecipientFields(this.value);
            });
        }

        // 빠른 입력 사유 버튼 이벤트
        const quickReasonButtons = document.querySelectorAll('.quick-reason');
        quickReasonButtons.forEach(button => {
            button.addEventListener('click', function() {
                document.getElementById('pointDescription').value = this.dataset.reason;
            });
        });

        // 회원 검색 버튼 이벤트
        const btnSearchUser = document.getElementById('btnSearchUser');
        if (btnSearchUser) {
            btnSearchUser.addEventListener('click', searchUser);
        }

        // 포인트 지급 제출 버튼 이벤트
        const btnSubmitAddPoints = document.getElementById('btnSubmitAddPoints');
        if (btnSubmitAddPoints) {
            btnSubmitAddPoints.addEventListener('click', submitAddPoints);
        }

        // 다수 회원 아이디 입력 시 개수 업데이트
        const userIdList = document.getElementById('userIdList');
        if (userIdList) {
            userIdList.addEventListener('input', updateUserCount);
        }

        // 전체 선택 체크박스 이벤트
        if (selectAllCheckbox) {
            selectAllCheckbox.addEventListener('change', function() {
                const isChecked = this.checked;
                pointCheckboxes.forEach(checkbox => {
                    if (!checkbox.disabled) {
                        checkbox.checked = isChecked;
                    }
                });
                updateBulkActionButtonState();
            });
        }

        // 개별 체크박스 이벤트
        pointCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', function() {
                updateSelectAllCheckboxState();
                updateBulkActionButtonState();
            });
        });

        // 포인트 회수 버튼 이벤트 (동적으로 추가될 수 있으므로 이벤트 위임 사용)
        document.addEventListener('click', function(e) {
            const cancelBtn = e.target.closest('.btn-cancel-point');
            if (cancelBtn) {
                e.preventDefault();
                showCancelPointModal(cancelBtn.dataset.id, cancelBtn.dataset.amount);
            }
        });

        // 포인트 회수 확인 버튼 이벤트
        const btnConfirmCancel = document.getElementById('btnConfirmCancel');
        if (btnConfirmCancel) {
            btnConfirmCancel.addEventListener('click', confirmCancelPoint);
        }

        // 일괄 회수 버튼 이벤트
        if (btnBulkCancel) {
            btnBulkCancel.addEventListener('click', function() {
                const selectedCount = document.querySelectorAll('.point-select:checked').length;
                document.getElementById('selectedItemCount').textContent = selectedCount;
                document.getElementById('bulkCancelReason').value = '';

                if (selectedCount > 0) {
                    bulkCancelModal.show();
                } else {
                    showToast('회수할 항목을 선택해주세요.', 'warning');
                }
            });
        }

        // 일괄 회수 확인 버튼 이벤트
        const btnConfirmBulkCancel = document.getElementById('btnConfirmBulkCancel');
        if (btnConfirmBulkCancel) {
            btnConfirmBulkCancel.addEventListener('click', confirmBulkCancelPoints);
        }

        // 선택 항목 내보내기 버튼 이벤트
        if (btnBulkExport) {
            btnBulkExport.addEventListener('click', exportSelectedItems);
        }
    }

    /**
     * 지급 대상 필드 토글
     * @param {string} type - 지급 대상 유형 ('INDIVIDUAL', 'MULTIPLE', 'ALL')
     */
    function toggleRecipientFields(type) {
        const individualSection = document.getElementById('individualUserSection');
        const multipleSection = document.getElementById('multipleUserSection');

        if (type === 'INDIVIDUAL') {
            individualSection.style.display = 'block';
            multipleSection.style.display = 'none';
        } else if (type === 'MULTIPLE') {
            individualSection.style.display = 'none';
            multipleSection.style.display = 'block';
        } else { // ALL
            individualSection.style.display = 'none';
            multipleSection.style.display = 'none';
        }
    }

    /**
     * 다수 회원 아이디 개수 업데이트
     */
    function updateUserCount() {
        const userIdList = document.getElementById('userIdList');
        const userCountElement = document.getElementById('userCount');

        if (!userIdList || !userCountElement) return;

        const text = userIdList.value.trim();
        if (!text) {
            userCountElement.textContent = '0';
            return;
        }

        // 쉼표 또는 줄바꿈으로 구분된 아이디 분리
        const userIds = text.split(/[\s,]+/).filter(id => id.trim() !== '');
        userCountElement.textContent = userIds.length;
    }

    /**
     * 회원 검색
     */
    async function searchUser() {
        const userId = document.getElementById('userId').value.trim();
        const userNameInput = document.getElementById('userName');

        if (!userId) {
            showToast('회원 아이디를 입력해주세요.', 'warning');
            return;
        }

        try {
            const response = await adminAuthFetch(`/admin/users/search?userId=${userId}`);

            if (!response.ok) {
                throw new Error('회원 검색 중 오류가 발생했습니다.');
            }

            const data = await response.json();

            if (data.user) {
                userNameInput.value = data.user.name;
            } else {
                userNameInput.value = '';
                showToast('해당 회원을 찾을 수 없습니다.', 'error');
            }
        } catch (error) {
            console.error('회원 검색 오류:', error);
            showToast('회원 검색 중 오류가 발생했습니다.', 'error');
        }
    }

    /**
     * 포인트 지급 제출
     */
    async function submitAddPoints() {
        // 입력값 검증
        const recipientType = document.getElementById('pointRecipientType').value;
        const pointAmount = parseInt(document.getElementById('pointAmount').value);
        const pointDescription = document.getElementById('pointDescription').value.trim();
        const expiryDate = document.getElementById('expiryDate').value;

        if (!pointAmount || pointAmount <= 0) {
            showToast('유효한 포인트 금액을 입력해주세요.', 'warning');
            return;
        }

        if (!pointDescription) {
            showToast('포인트 지급 사유를 입력해주세요.', 'warning');
            return;
        }

        let userIds = [];

        if (recipientType === 'INDIVIDUAL') {
            const userId = document.getElementById('userId').value.trim();
            const userName = document.getElementById('userName').value.trim();

            if (!userId || !userName) {
                showToast('유효한 회원을 선택해주세요.', 'warning');
                return;
            }

            userIds = [userId];
        } else if (recipientType === 'MULTIPLE') {
            const userIdListText = document.getElementById('userIdList').value.trim();
            if (!userIdListText) {
                showToast('회원 아이디 목록을 입력해주세요.', 'warning');
                return;
            }

            userIds = userIdListText.split(/[\s,]+/).filter(id => id.trim() !== '');

            if (userIds.length === 0) {
                showToast('유효한 회원 아이디 목록을 입력해주세요.', 'warning');
                return;
            }
        }

        // 확인 메시지
        let confirmMessage = '';
        if (recipientType === 'INDIVIDUAL') {
            confirmMessage = `${document.getElementById('userName').value} 회원에게 ${pointAmount.toLocaleString()}P를 지급하시겠습니까?`;
        } else if (recipientType === 'MULTIPLE') {
            confirmMessage = `${userIds.length}명의 회원에게 각각 ${pointAmount.toLocaleString()}P를 지급하시겠습니까?`;
        } else {
            confirmMessage = `모든 회원에게 각각 ${pointAmount.toLocaleString()}P를 지급하시겠습니까?`;
        }

        if (!confirm(confirmMessage)) {
            return;
        }

        // 제출 버튼 비활성화
        const btnSubmitAddPoints = document.getElementById('btnSubmitAddPoints');
        const originalButtonText = btnSubmitAddPoints.innerHTML;
        btnSubmitAddPoints.disabled = true;
        btnSubmitAddPoints.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 처리 중...';

        try {
            const requestData = {
                recipientType,
                userIds: recipientType !== 'ALL' ? userIds : null,
                amount: pointAmount,
                description: pointDescription,
                expiryDate: expiryDate || null
            };

            const response = await adminAuthFetch('/admin/points/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            });

            if (!response.ok) {
                throw new Error('포인트 지급 중 오류가 발생했습니다.');
            }

            const result = await response.json();

            if (result.success) {
                showToast('포인트가 성공적으로 지급되었습니다.', 'success');
                addPointsModal.hide();

                // 페이지 새로고침
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                throw new Error(result.message || '포인트 지급 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('포인트 지급 오류:', error);
            showToast(error.message, 'error');
        } finally {
            btnSubmitAddPoints.disabled = false;
            btnSubmitAddPoints.innerHTML = originalButtonText;
        }
    }

    /**
     * 포인트 회수 모달 표시
     * @param {number} pointId - 포인트 내역 ID
     * @param {number} amount - 포인트 금액
     */
    function showCancelPointModal(pointId, amount) {
        document.getElementById('cancelPointId').value = pointId;
        document.getElementById('cancelAmount').value = amount;
        document.getElementById('cancelAmount').max = amount;
        document.getElementById('maxCancelAmount').textContent = parseInt(amount).toLocaleString();
        document.getElementById('cancelReason').value = '';

        cancelPointModal.show();
    }

    /**
     * 포인트 회수 확인
     */
    async function confirmCancelPoint() {
        const pointId = document.getElementById('cancelPointId').value;
        const amount = parseInt(document.getElementById('cancelAmount').value);
        const reason = document.getElementById('cancelReason').value.trim();

        if (!amount || amount <= 0) {
            showToast('유효한 회수 금액을 입력해주세요.', 'warning');
            return;
        }

        const maxAmount = parseInt(document.getElementById('cancelAmount').max);
        if (amount > maxAmount) {
            showToast(`최대 ${maxAmount.toLocaleString()}P까지 회수 가능합니다.`, 'warning');
            return;
        }

        if (!reason) {
            showToast('회수 사유를 입력해주세요.', 'warning');
            return;
        }

        // 확인 메시지
        if (!confirm(`${amount.toLocaleString()}P를 회수하시겠습니까?`)) {
            return;
        }

        // 회수 버튼 비활성화
        const btnConfirmCancel = document.getElementById('btnConfirmCancel');
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
     * 일괄 포인트 회수 확인
     */
    async function confirmBulkCancelPoints() {
        const selectedCheckboxes = document.querySelectorAll('.point-select:checked');
        const reason = document.getElementById('bulkCancelReason').value.trim();

        if (selectedCheckboxes.length === 0) {
            showToast('회수할 항목을 선택해주세요.', 'warning');
            return;
        }

        if (!reason) {
            showToast('회수 사유를 입력해주세요.', 'warning');
            return;
        }

        // 확인 메시지
        if (!confirm(`선택한 ${selectedCheckboxes.length}개 항목의 포인트를 회수하시겠습니까?`)) {
            return;
        }

        // 회수 버튼 비활성화
        const btnConfirmBulkCancel = document.getElementById('btnConfirmBulkCancel');
        const originalButtonText = btnConfirmBulkCancel.innerHTML;
        btnConfirmBulkCancel.disabled = true;
        btnConfirmBulkCancel.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 처리 중...';

        try {
            const pointIds = Array.from(selectedCheckboxes).map(checkbox => checkbox.dataset.id);

            const response = await adminAuthFetch('/admin/points/bulk-cancel', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    pointIds,
                    reason
                })
            });

            if (!response.ok) {
                throw new Error('일괄 포인트 회수 중 오류가 발생했습니다.');
            }

            const result = await response.json();

            if (result.success) {
                showToast(`${result.canceledCount}개 항목의 포인트가 성공적으로 회수되었습니다.`, 'success');
                bulkCancelModal.hide();

                // 페이지 새로고침
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                throw new Error(result.message || '일괄 포인트 회수 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('일괄 포인트 회수 오류:', error);
            showToast(error.message, 'error');
        } finally {
            btnConfirmBulkCancel.disabled = false;
            btnConfirmBulkCancel.innerHTML = originalButtonText;
        }
    }

    /**
     * 전체 선택 체크박스 상태 업데이트
     */
    function updateSelectAllCheckboxState() {
        if (!selectAllCheckbox) return;

        const checkboxes = document.querySelectorAll('.point-select:not(:disabled)');
        const checkedCheckboxes = document.querySelectorAll('.point-select:not(:disabled):checked');

        selectAllCheckbox.checked = checkboxes.length > 0 && checkboxes.length === checkedCheckboxes.length;
        selectAllCheckbox.indeterminate = checkedCheckboxes.length > 0 && checkedCheckboxes.length < checkboxes.length;
    }

    /**
     * 일괄 작업 버튼 상태 업데이트
     */
    function updateBulkActionButtonState() {
        const selectedCount = document.querySelectorAll('.point-select:checked').length;

        if (btnBulkCancel) {
            btnBulkCancel.classList.toggle('disabled', selectedCount === 0);
        }

        if (btnBulkExport) {
            btnBulkExport.classList.toggle('disabled', selectedCount === 0);
        }
    }

    /**
     * 선택된 항목 엑셀 내보내기
     */
    function exportSelectedItems() {
        const selectedCheckboxes = document.querySelectorAll('.point-select:checked');

        if (selectedCheckboxes.length === 0) {
            showToast('내보낼 항목을 선택해주세요.', 'warning');
            return;
        }

        const pointIds = Array.from(selectedCheckboxes).map(checkbox => checkbox.dataset.id).join(',');

        // 현재 URL의 쿼리 파라미터 구성
        const params = new URLSearchParams();
        params.set('pointIds', pointIds);
        params.set('export', 'excel');

        // 엑셀 다운로드 링크 구성 및 실행
        const downloadUrl = `/admin/points/export?${params.toString()}`;
        window.location.href = downloadUrl;
    }

    /**
     * 전체 내역 엑셀 내보내기
     */
    function exportToExcel() {
        // 현재 검색 조건을 유지한 채로 엑셀 내보내기
        const searchParams = new URLSearchParams(window.location.search);
        searchParams.set('export', 'excel');

        const downloadUrl = `/admin/points/export?${searchParams.toString()}`;
        window.location.href = downloadUrl;
    }

    /**
     * 검색 실행
     */
    function performSearch() {
        if (searchForm) {
            searchForm.submit();
        }
    }

    /**
     * Flatpickr 날짜 범위 선택기 초기화 함수
     */
    function initFlatpickrDateRange() {
        if (!dateRangeInput || typeof flatpickr === 'undefined') {
            console.error('Flatpickr가 로드되지 않았거나 dateRangeInput 요소가 없습니다.');
            return;
        }

        try {
            // 날짜 범위 값 파싱
            const currentValue = dateRangeInput.value.trim();
            let initialDates = null;

            if (currentValue) {
                // 날짜 범위 분리
                const parts = currentValue.split(/\s*~\s*/);
                if (parts.length === 2) {
                    const startDate = parts[0].trim();
                    const endDate = parts[1].trim();

                    // 날짜 객체로 변환 시도
                    try {
                        initialDates = [new Date(startDate), new Date(endDate)];

                        // 유효하지 않은 날짜 확인
                        if (isNaN(initialDates[0].getTime()) || isNaN(initialDates[1].getTime())) {
                            console.warn('유효하지 않은 날짜 형식입니다:', startDate, endDate);
                            initialDates = null;
                        }
                    } catch (e) {
                        console.warn('날짜 변환 실패:', e);
                        initialDates = null;
                    }
                }
            }

            // 상위 요소에 특별한 클래스 추가
            const wrapper = dateRangeInput.closest('.input-group');
            if (!wrapper) return;

            wrapper.classList.add('date-range-input-group');

            // 클리어 버튼 생성
            let clearBtn = wrapper.querySelector('.date-clear-btn');
            if (!clearBtn) {
                clearBtn = document.createElement('button');
                clearBtn.type = 'button';
                clearBtn.className = 'date-clear-btn';
                clearBtn.innerHTML = '<i class="fas fa-times"></i>';
                wrapper.appendChild(clearBtn);
            }

            // 초기 클리어 버튼 표시 여부
            clearBtn.style.display = initialDates ? 'flex' : 'none';

            // 달력 아이콘 설정
            const calendarIcon = wrapper.querySelector('.input-group-text');
            if (calendarIcon) {
                calendarIcon.style.cursor = 'pointer';
            }

            // Flatpickr 설정
            const fpInstance = flatpickr(dateRangeInput, {
                mode: 'range',
                dateFormat: 'Y-m-d',  // 날짜 형식을 명확하게 지정
                locale: 'ko',
                disableMobile: true,
                defaultDate: initialDates,
                onChange: function(selectedDates) {
                    if (selectedDates.length === 2) {
                        // 날짜가 모두 선택된 경우, 명확한 형식으로 표시
                        const formatted = selectedDates.map(date => formatDate(date));
                        dateRangeInput.value = formatted.join(' ~ ');
                        clearBtn.style.display = 'flex';
                    }
                }
            });

            // 달력 아이콘 클릭 이벤트
            if (calendarIcon) {
                calendarIcon.addEventListener('click', () => fpInstance.toggle());
            }

            // 클리어 버튼 클릭 이벤트
            if (clearBtn) {
                clearBtn.addEventListener('click', e => {
                    e.preventDefault();
                    e.stopPropagation();
                    fpInstance.clear();
                    dateRangeInput.value = '';
                    clearBtn.style.display = 'none';
                });
            }
        } catch (error) {
            console.error('Flatpickr 초기화 중 오류가 발생했습니다:', error);
        }
    }

    /**
     * 숫자 입력 필드 포맷 초기화
     */
    function initNumberInputs() {
        const numberInputs = document.querySelectorAll('input[type="number"]');

        numberInputs.forEach(input => {
            input.addEventListener('input', function() {
                // 음수 입력 방지
                if (parseFloat(this.value) < 0) {
                    this.value = 0;
                }
            });
        });
    }

    /**
     * Date 객체를 YYYY-MM-DD 형식으로 변환
     * @param {Date} date
     * @returns {string}
     */
    function formatDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
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