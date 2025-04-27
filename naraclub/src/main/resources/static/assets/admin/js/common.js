/**
 * Spark Admin Panel - Common JS
 */

// 즉시 실행 함수를 사용하여 전역 네임스페이스를 오염시키지 않음
(() => {
  'use strict';
  
  // DOM 요소들
  const sidebar = document.getElementById('sidebar');
  const content = document.getElementById('content');
  const sidebarCollapse = document.getElementById('sidebarCollapse');
  const submenuLinks = document.querySelectorAll('.nav-link[data-bs-toggle="collapse"]');
  
  /**
   * 문서 로드 완료 시 실행할 함수
   */
  const domReady = () => {
    initSidebar();
    initTooltips();
    handleResponsive();
  };
  
  /**
   * 사이드바 토글 및 이벤트 리스너 초기화
   */
  const initSidebar = () => {
    // 사이드바 토글 버튼 클릭 이벤트
    if (sidebarCollapse) {
      sidebarCollapse.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
        content.classList.toggle('full-width');
      });
    }
    
    // 서브메뉴 토글
    document.querySelectorAll('.nav-link').forEach(link => {
      if (link.getAttribute('data-bs-toggle') === 'collapse') {
        link.addEventListener('click', (e) => {
          e.preventDefault();
          const target = document.querySelector(link.getAttribute('href'));
          if (target) {
            target.classList.toggle('show');
            link.setAttribute('aria-expanded', target.classList.contains('show'));
          }
        });
      }
    });
    
    // 모바일에서 사이드바 외부 클릭 시 닫기
    document.addEventListener('click', (e) => {
      if (window.innerWidth <= 768 && sidebar.classList.contains('active') && !sidebar.contains(e.target) && e.target !== sidebarCollapse) {
        sidebar.classList.remove('active');
        content.classList.remove('active');
      }
    });
  };
  
  /**
   * 툴팁 초기화
   */
  const initTooltips = () => {
    // Bootstrap 툴팁 초기화
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
      return new bootstrap.Tooltip(tooltipTriggerEl);
    });
  };
  
  /**
   * 반응형 처리
   */
  const handleResponsive = () => {
    const mobileBreakpoint = 768;
    
    // 초기 화면 크기에 따라 사이드바 상태 설정
    if (window.innerWidth <= mobileBreakpoint) {
      sidebar.classList.add('collapsed');
      content.classList.add('full-width');
    }
    
    // 화면 크기 변경 시 사이드바 상태 조정
    window.addEventListener('resize', () => {
      if (window.innerWidth <= mobileBreakpoint) {
        sidebar.classList.add('collapsed');
        content.classList.add('full-width');
      } else {
        // 데스크톱 사이즈로 변경 시 기본 상태로 복원 (선택적)
        // sidebar.classList.remove('collapsed');
        // content.classList.remove('full-width');
      }
    });
  };
  
  /**
   * 새로고침 버튼 처리
   */
  const initRefreshButtons = () => {
    document.querySelectorAll('.refresh-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        
        // 새로고침 효과
        const card = btn.closest('.card');
        if (card) {
          const spinner = document.createElement('div');
          spinner.className = 'refresh-spinner';
          card.classList.add('refreshing');
          card.appendChild(spinner);
          
          // 가상의 로딩 시간 후 스피너 제거
          setTimeout(() => {
            card.classList.remove('refreshing');
            spinner.remove();
          }, 1000);
        }
      });
    });
  };
  
  /**
   * 카드 토글 버튼 처리
   */
  const initCardToggles = () => {
    document.querySelectorAll('.toggle-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        
        // 토글 메뉴 생성
        const menu = document.createElement('div');
        menu.className = 'card-toggle-menu';
        menu.innerHTML = `
          <ul>
            <li><a href="#"><i class="fas fa-expand"></i> Expand</a></li>
            <li><a href="#"><i class="fas fa-compress"></i> Compress</a></li>
            <li><a href="#"><i class="fas fa-times"></i> Remove</a></li>
          </ul>
        `;
        
        // 이미 메뉴가 있으면 제거, 없으면 추가
        const existingMenu = document.querySelector('.card-toggle-menu');
        if (existingMenu) {
          existingMenu.remove();
        } else {
          document.body.appendChild(menu);
          
          // 메뉴 위치 설정
          const rect = btn.getBoundingClientRect();
          menu.style.top = rect.bottom + 'px';
          menu.style.left = (rect.left - menu.offsetWidth + rect.width) + 'px';
          
          // 외부 클릭 시 메뉴 닫기
          document.addEventListener('click', function closeMenu(e) {
            if (!menu.contains(e.target) && e.target !== btn) {
              menu.remove();
              document.removeEventListener('click', closeMenu);
            }
          });
        }
      });
    });
  };
  
  /**
   * Ajax 요청 함수
   * @param {string} url - 요청 URL
   * @param {Object} options - 옵션 (method, data 등)
   * @returns {Promise} - Fetch Promise
   */
  const ajaxRequest = (url, options = {}) => {
    const defaultOptions = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
      }
    };
    
    const fetchOptions = { ...defaultOptions, ...options };
    
    if (fetchOptions.data && fetchOptions.method !== 'GET') {
      fetchOptions.body = JSON.stringify(fetchOptions.data);
      delete fetchOptions.data;
    }
    
    return fetch(url, fetchOptions)
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      });
  };
  
  /**
   * 알림 표시 함수
   * @param {string} message - 알림 메시지
   * @param {string} type - 알림 타입 (success, danger, warning, info)
   * @param {number} duration - 표시 시간 (ms)
   */
  const showNotification = (message, type = 'info', duration = 3000) => {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
      <div class="notification-icon">
        <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'danger' ? 'exclamation-circle' : type === 'warning' ? 'exclamation-triangle' : 'info-circle'}"></i>
      </div>
      <div class="notification-content">${message}</div>
      <button class="notification-close"><i class="fas fa-times"></i></button>
    `;
    
    // 알림 컨테이너 확인 또는 생성
    let container = document.querySelector('.notification-container');
    if (!container) {
      container = document.createElement('div');
      container.className = 'notification-container';
      document.body.appendChild(container);
    }
    
    container.appendChild(notification);
    
    // 닫기 버튼
    const closeBtn = notification.querySelector('.notification-close');
    if (closeBtn) {
      closeBtn.addEventListener('click', () => {
        notification.classList.add('notification-fade-out');
        setTimeout(() => {
          container.removeChild(notification);
          if (container.children.length === 0) {
            document.body.removeChild(container);
          }
        }, 300);
      });
    }
    
    // 자동 닫기
    if (duration > 0) {
      setTimeout(() => {
        if (notification.parentNode) {
          notification.classList.add('notification-fade-out');
          setTimeout(() => {
            if (notification.parentNode) {
              container.removeChild(notification);
              if (container.children.length === 0 && container.parentNode) {
                document.body.removeChild(container);
              }
            }
          }, 300);
        }
      }, duration);
    }
  };
  
  // 스파크 전역 객체 생성
  window.Spark = {
    initRefreshButtons,
    initCardToggles,
    ajaxRequest,
    showNotification
  };
  
  // 문서 로드 완료 시 초기화
  document.addEventListener('DOMContentLoaded', () => {
    domReady();
    initRefreshButtons();
    initCardToggles();
  });
})();