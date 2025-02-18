document.addEventListener('DOMContentLoaded', function() {
    const categoryButtons = document.querySelectorAll('.search-categories button');
    const inputText = document.querySelector('input[type=text]');
    const searchBar = document.querySelector('.search-bar');
    const flightSearchBar = document.querySelector('.flight-search-bar');
    const datePickerInput = document.querySelector('.date-picker');
    const calendar = document.querySelector('.calendar-container');

    const placeholderMap = {
        '여행지': '여행지',
        '관광명소': '관광명소, 액티비티 또는 여행지',
        '숙박': '호텔 이름 또는 여행지',
        '음식점': '음식점 또는 여행지'
    };

    categoryButtons.forEach(button => {
        button.addEventListener('click', () => {
            categoryButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            inputText.placeholder = placeholderMap[button.innerText] || '여행지, 즐길거리, 호텔 등';
            const isFlightSearch = button.innerText === '항공권';
            if (isFlightSearch){
                searchBar.classList.add('hidden');
                flightSearchBar.classList.remove('hidden');
            }else{
                searchBar.classList.remove('hidden');
                flightSearchBar.classList.add('hidden');
            }
        });
    });

    let fpInitialized = false;

    const fp = flatpickr(datePickerInput, {
        mode: "range",
        dateFormat: "Y-m-d",
        minDate: "today",
        defaultDate: [new Date(), new Date(new Date().setDate(new Date().getDate() + 7))],
        monthSelectorType: "static",
        showMonths: 2,
        locale: "ko",
        position: "below",
        closeOnSelect: false, // 날짜 선택 시 자동으로 닫히지 않도록 설정
        onOpen: function(selectedDates, dateStr, instance) {
            if (!fpInitialized) {
                // 캘린더 타이틀 추가
                if (!document.querySelector(".calendar-title")) {
                    const titleDiv = document.createElement("div");
                    titleDiv.classList.add("calendar-title");
                    titleDiv.style.cssText = `
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        text-align: center;
                        font-size: 24px;
                        font-weight: bold;
                        padding: 20px 0;
                        background: white;
                        border-bottom: 1px solid #eee;
                        z-index: 1;
                    `;
                    updateTitle(selectedDates, titleDiv);
                    instance.calendarContainer.insertBefore(titleDiv, instance.calendarContainer.firstChild);
                }

                // 적용 버튼 추가
                if (!document.querySelector(".flatpickr-apply-button")) {
                    const applyButton = document.createElement("button");
                    applyButton.classList.add("flatpickr-apply-button");
                    applyButton.textContent = "적용";
                    applyButton.style.cssText = `
                        position: absolute;
                        bottom: 20px;
                        right: 20px;
                        background-color: black;
                        color: white;
                        border: none;
                        padding: 12px 32px;
                        font-size: 16px;
                        border-radius: 20px;
                        cursor: pointer;
                        z-index: 2;
                    `;

                    applyButton.addEventListener("click", function() {
                        const selectedDates = instance.selectedDates;
                        if (selectedDates.length === 2) {
                            const startDate = selectedDates[0].toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });
                            const endDate = selectedDates[1].toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });
                            datePickerInput.value = `${startDate} → ${endDate}`;
                            instance.close(); // 적용 버튼 클릭 시에만 캘린더 닫기
                        }
                    });

                    instance.calendarContainer.appendChild(applyButton);
                }

                // 캘린더 스타일 조정
                instance.calendarContainer.style.cssText += `
                    padding-top: 70px;
                    border-radius: 8px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                `;

                fpInitialized = true;
            }
        },
        onChange: function(selectedDates, dateStr, instance) {
            const titleDiv = instance.calendarContainer.querySelector(".calendar-title");
            if (titleDiv) {
                updateTitle(selectedDates, titleDiv);
            }
        }
    });

    function updateTitle(selectedDates, titleDiv) {
        if (selectedDates.length === 2) {
            const startDate = selectedDates[0].toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });
            const endDate = selectedDates[1].toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });
            titleDiv.textContent = `${startDate} 출발 → ${endDate} 도착`;
        } else {
            titleDiv.textContent = "출발일과 도착일을 선택하세요";
        }
    }

    const style = document.createElement('style');
    style.textContent = `
        .flatpickr-calendar {
            width: 900px;
            background: white;
        }
        .flatpickr-day.selected {
            background: black !important;
            border-color: black !important;
        }
        .flatpickr-day.inRange {
            background: #f0f0f0 !important;
            border-color: #f0f0f0 !important;
        }
        .flatpickr-day {
            border-radius: 50% !important;
            margin: 2px;
        }
        .flatpickr-day.selected.startRange,
        .flatpickr-day.selected.endRange {
            background: black !important;
        }
        .flatpickr-months {
            padding-top: 20px;
        }
    `;
    document.head.appendChild(style);

    // 날짜 선택 버튼 클릭 이벤트
    datePickerInput.addEventListener('click', (event) => {
        if (!fp.isOpen) {
            fp.open();
        }
        event.stopPropagation();
    });

    // 문서 클릭 시 캘린더 닫기 (캘린더 영역 외 클릭 시에만)
    document.addEventListener('click', (event) => {
        const calendar = document.querySelector('.flatpickr-calendar');
        if (calendar && !calendar.contains(event.target) && event.target !== datePickerInput) {
            // 캘린더 외부 클릭 시 닫지 않음 (적용 버튼으로만 닫기 가능)
            event.stopPropagation();
        }
    });
	
	document.querySelector('.traveler-picker').addEventListener('click', function(e){
		const selection = document.querySelector('.traveler-selection');
		console.log(this)
		if(e.target==this) {
			selection.classList.remove('hidden');
		} else {
            selection.classList.add('hidden');
		}
	});
	
});