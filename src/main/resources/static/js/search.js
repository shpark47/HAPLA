const categoryButtons = document.querySelectorAll('.search-categories button');
const inputText = document.querySelector('input[type=text]');
const searchBar = document.querySelector('.search-bar'); // 기본 검색창
const flightSearchBar = document.querySelector('.flight-search-bar'); // 항공권 검색창

// Placeholder 매핑 객체
const placeholderMap = {
    '여행지': '여행지',
    '관광명소': '관광명소, 액티비티 또는 여행지',
    '숙박': '호텔 이름 또는 여행지',
    '음식점': '음식점 또는 여행지'
};

categoryButtons.forEach(button => {
    button.addEventListener('click', () => {
        // 모든 버튼의 'active' 제거 후 현재 버튼에 추가
        categoryButtons.forEach(btn => btn.classList.remove('active'));
        button.classList.add('active');

        // 버튼에 해당하는 placeholder 적용
        inputText.placeholder = placeholderMap[button.innerText] || '여행지, 즐길거리, 호텔 등';

        // 검색창 표시 설정
        const isFlightSearch = button.innerText === '항공권';
        searchBar.style.display = isFlightSearch ? 'none' : '';
        flightSearchBar.style.display = isFlightSearch ? 'block' : 'none';
    });
});

document.addEventListener('DOMContentLoaded', function() {
    let startDate = null;
    let endDate = null;
    let calendar1, calendar2;
    const datePickerButton = document.querySelector('.date-picker');
    const calendarWrapper = document.getElementById('calendarWrapper');
    
	// 캘린더 초기화 함수
	function initializeCalendars() {
	    const calendarEl1 = document.getElementById('calendar1');
	    const calendarEl2 = document.getElementById('calendar2');
	    
	    const sharedOptions = {
	        initialView: 'dayGridMonth',
	        contentHeight: 'auto',
	        headerToolbar: false,
	        locale: 'ko',
	        selectable: true,
	        select: handleDateSelect,
	        unselect: handleDateUnselect,
	        aspectRatio: 1.8,
	        showNonCurrentDates: false, // 해당 월의 날짜만 표시
	        fixedWeekCount: false, // 실제 주 수만큼만 표시
	        dayCellDidMount: function(arg) {
	            // 현재 월이 아닌 날짜 셀 숨기기
	            if (!arg.isPast && arg.date.getMonth() !== arg.view.currentStart.getMonth()) {
	                arg.el.style.visibility = 'hidden';
	            }
	        }
	    };

	    // 첫 번째 달력: 현재 달
	    const now = new Date();
	    const currentMonth = new Date(now.getFullYear(), now.getMonth(), 1);
	    calendar1 = new FullCalendar.Calendar(calendarEl1, {
	        ...sharedOptions,
	        initialDate: currentMonth,
	        validRange: {
	            start: currentMonth,
	            end: new Date(now.getFullYear(), now.getMonth() + 1, 1)
	        }
	    });

	    // 두 번째 달력: 다음 달
	    const nextMonth = new Date(now.getFullYear(), now.getMonth() + 1, 1);
	    const nextMonthEnd = new Date(now.getFullYear(), now.getMonth() + 2, 1);
	    calendar2 = new FullCalendar.Calendar(calendarEl2, {
	        ...sharedOptions,
	        initialDate: nextMonth,
	        validRange: {
	            start: nextMonth,
	            end: nextMonthEnd
	        }
	    });

	    calendar1.render();
	    calendar2.render();
	    updateMonthDisplay();
	}

    // 캘린더 렌더링 함수
    function renderCalendars() {
        calendar1.render();
        calendar2.render();
    }

    // 날짜 선택 핸들러
    function handleDateSelect(info) {
        if (!startDate || (startDate && endDate)) {
            // 새로운 선택 시작
            startDate = info.start;
            endDate = null;
            clearSelection();
            highlightDate(info.start);
        } else {
            // 범위 선택 완료
            endDate = info.end;
            if (endDate < startDate) {
                // 시작일이 종료일보다 늦은 경우 swap
                [startDate, endDate] = [endDate, startDate];
            }
            highlightDateRange(startDate, endDate);
        }
        updateSelectedDatesDisplay();
    }

    // 선택 취소 핸들러
    function handleDateUnselect() {
        if (!endDate) {
            startDate = null;
            clearSelection();
            updateSelectedDatesDisplay();
        }
    }

    // 날짜 하이라이트 함수
    function highlightDate(date) {
        const dateStr = date.toISOString().split('T')[0];
        document.querySelectorAll('.fc-day').forEach(el => {
            if (el.getAttribute('data-date') === dateStr) {
                el.classList.add('fc-day-selected');
            }
        });
    }

    // 날짜 범위 하이라이트 함수
    function highlightDateRange(start, end) {
        clearSelection();
        let current = new Date(start);
        while (current < end) {
            highlightDate(current);
            current.setDate(current.getDate() + 1);
        }
    }

    // 선택 초기화 함수
    function clearSelection() {
        document.querySelectorAll('.fc-day-selected').forEach(el => {
            el.classList.remove('fc-day-selected');
        });
    }

    // 선택된 날짜 표시 업데이트
    function updateSelectedDatesDisplay() {
        const dateDisplay = document.getElementById('selectedDates');
        if (startDate) {
            const startStr = startDate.toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });
            const endStr = endDate ? 
                endDate.toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' }) :
                '';
            dateDisplay.textContent = endStr ? `${startStr} - ${endStr}` : startStr;
        } else {
            dateDisplay.textContent = '';
        }
    }

    // 월 표시 업데이트
    function updateMonthDisplay() {
        const monthDisplay = document.getElementById('currentMonth');
        const month1 = calendar1.getDate().toLocaleDateString('ko-KR', { year: 'numeric', month: 'long' });
        const month2 = calendar2.getDate().toLocaleDateString('ko-KR', { year: 'numeric', month: 'long' });
        monthDisplay.textContent = `${month1} - ${month2}`;
    }

    // 이전/다음 월 버튼 핸들러
    document.getElementById('prevMonth').addEventListener('click', () => {
        calendar1.prev();
        calendar2.prev();
        updateMonthDisplay();
    });

    document.getElementById('nextMonth').addEventListener('click', () => {
        calendar1.next();
        calendar2.next();
        updateMonthDisplay();
    });

    // 적용 버튼 핸들러
    document.getElementById('applyButton').addEventListener('click', () => {
        if (startDate && endDate) {
            console.log('Selected date range:', {
                start: startDate.toISOString(),
                end: endDate.toISOString()
            });
            // 여기에 날짜 선택 완료 후의 로직을 추가하세요
            datePickerButton.textContent = `${startDate.toLocaleDateString('ko-KR')} → ${endDate.toLocaleDateString('ko-KR')}`;
        }
        calendarWrapper.style.display = 'none';
    });

    // 캘린더 초기화
    initializeCalendars();

    // 날짜 선택 버튼 클릭 이벤트
    datePickerButton.addEventListener('click', (event) => {
        const buttonRect = datePickerButton.getBoundingClientRect();
        calendarWrapper.style.top = `${buttonRect.bottom + window.scrollY}px`;
        calendarWrapper.style.left = `${buttonRect.left + window.scrollX}px`;
        calendarWrapper.style.display = 'block';
        renderCalendars(); // 캘린더 렌더링
        event.stopPropagation();
    });

    // 문서 클릭 시 캘린더 닫기
    document.addEventListener('click', (event) => {
        if (!calendarWrapper.contains(event.target) && event.target !== datePickerButton) {
            calendarWrapper.style.display = 'none';
        }
    });

    // 캘린더 내부 클릭 시 이벤트 전파 중지
    calendarWrapper.addEventListener('click', (event) => {
        event.stopPropagation();
    });
});