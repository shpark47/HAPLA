document.addEventListener('DOMContentLoaded', function() {
    const categoryButtons = document.querySelectorAll('.search-categories button');
    const inputText = document.querySelector('input[type=text]');
    const searchBar = document.querySelector('.search-bar');
    const flightSearchBar = document.querySelector('.flight-search-bar');
    const datePickerInput = document.querySelector('.date-picker');
    

    const placeholderMap = {
        '여행지': '여행지',
        '관광명소': '관광명소, 액티비티 또는 여행지',
        '숙박': '호텔 이름 또는 여행지',
        '음식점': '음식점 또는 여행지'
    };

    for (let button of categoryButtons) {
        button.addEventListener('click', () => {
            for (let btn of categoryButtons) {
                btn.classList.remove('active');
            }
            button.classList.add('active');
            inputText.placeholder = placeholderMap[button.innerText] || '여행지, 즐길거리, 호텔 등';
            const isFlightSearch = button.innerText === '항공권';
            searchBar.style.display = isFlightSearch ? 'none' : '';
            flightSearchBar.style.display = isFlightSearch ? 'block' : 'none';
        });
    }

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

    const selection = document.querySelector('.traveler-selection');
    const traveler = document.querySelector('.traveler-picker');
    const resetBtn = document.querySelector('.reset');
    const applyButton = document.querySelector('.apply');
    const incrementButtons = document.querySelectorAll('.increment');
    const decrementButtons = document.querySelectorAll('.decrement');
    const countElements = document.querySelectorAll('.count');
    const maxTravelers = 9; // 최대 여행자 수
	
	// 경고 메시지 추가
	    const warningMessage = document.createElement('p');
	    warningMessage.textContent = "최대 9명까지만 선택할 수 있습니다.";
	    warningMessage.style.cssText = `
	        color: red;
	        font-size: 14px;
	        margin-top: 8px;
	        display: none;
	    `;
	    selection.appendChild(warningMessage);

    // 여행자 선택창 열기
    traveler.addEventListener('click', function(e) {
        e.stopPropagation();
        selection.style.display = 'block';
    });

    // 여행자 선택창 닫기
    applyButton.addEventListener('click', function() {
        updateTravelerCount(); // 총 인원 업데이트
        selection.style.display = 'none';
    });

    // 여행자 선택창 외부 클릭 시 닫기
    document.addEventListener('click', function(e) {
        if (!selection.contains(e.target) && e.target !== traveler) {
            selection.style.display = 'none';
        }
    });

    // 내부 클릭 시 닫히지 않게 방지
    selection.addEventListener('click', function(e) {
        e.stopPropagation();
    });

	// 여행자 수 증가 & 감소 버튼 이벤트
	 for (let button of incrementButtons) {
	     button.addEventListener('click', function() {
	         let totalTravelers = getTotalTravelers();
	         if (totalTravelers < maxTravelers) {
	             let countElement = button.previousElementSibling;
	             countElement.textContent = parseInt(countElement.textContent) + 1;
	         }
	         updateWarningMessage();
	     });
	 }

	 for (let button of decrementButtons) {
	     button.addEventListener('click', function() {
	         let countElement = button.nextElementSibling;
	         let currentCount = parseInt(countElement.textContent);
	         if (currentCount > 0) {
	             countElement.textContent = currentCount - 1;
	         }
	         updateWarningMessage();
	     });
	 }

    // 재설정 버튼 클릭 시 모든 여행자 수 초기화
    resetBtn.addEventListener('click', function() {
        for (let countElement of countElements) {
            countElement.textContent = '0';
        }
        updateTravelerCount();
    });

    // 총 인원 계산
    function getTotalTravelers() {
        let total = 0;
        for (let countEl of countElements) {
            total += parseInt(countEl.textContent);
        }
        return total;
    }

    // 적용 버튼 클릭 시 여행자 수 업데이트
    function updateTravelerCount() {
        let totalTravelers = getTotalTravelers();
        traveler.textContent = `여행자 ${totalTravelers}명`;
    }
	
	// 최대 인원 초과 시 경고 메시지 표시
	   function updateWarningMessage() {
	       let totalTravelers = getTotalTravelers();
	       if (totalTravelers >= maxTravelers) {
	           warningMessage.style.display = 'block';
	       } else {
	           warningMessage.style.display = 'none';
	       }
	   }
		document.getElementsByName('departureName')[0].addEventListener('input', (e) =>{
			const departureValue = e.target.value;
			iataSearch(departureValue);
		});
		
		document.getElementsByName('arrivalName')[0].addEventListener('input', (e) => {
			const arrivalValue = e.target.value;
			iataSearch(arrivalValue);
		});
	   
		// AJAX 검색 함수
		    const iataSearch = (value, dropdown, input) => {
		        $.ajax({
		            url: 'flight/iataSearch',
		            data: { value: value },
		            success: data => {
		                dropdown.innerHTML = '';
		                
		                if (data.length > 0) {
		                    data.forEach(result => {
		                        const item = createResultItem(result);
		                        item.addEventListener('click', () => {
		                            input.value = result.nameKo || result.nameEn;
		                            input.dataset.iata = result.iata;
		                            dropdown.style.display = 'none';
		                        });
		                        dropdown.appendChild(item);
		                    });
		                    dropdown.style.display = 'block';
		                } else {
		                    dropdown.style.display = 'none';
		                }
		            },
		            error: error => {
		                console.error('검색 중 오류 발생:', error);
		                dropdown.style.display = 'none';
		            }
		        });
		    };

		// 검색 결과 아이템 생성 함수 수정
		const createResultItem = (result) => {
		    const item = document.createElement('div');
		    item.className = 'search-result-item';
		    item.style.cssText = `
		        padding: 12px;
		        display: flex;
		        align-items: center;
		        gap: 12px;
		        cursor: pointer;
		        border-bottom: 1px solid #f0f0f0;
		    `;
		    
		    const icon = result.type === 'city' 
		        ? '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M12 1v2M12 21v2M4.2 4.2l1.4 1.4M18.4 18.4l1.4 1.4M1 12h2M21 12h2M4.2 19.8l1.4-1.4M18.4 5.6l1.4-1.4"/></svg>'
		        : '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 2L2 22M5.45 5.11L2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"/></svg>';

		    const content = document.createElement('div');
		    content.style.cssText = `flex: 1;`;
		    
		    const mainText = document.createElement('div');
		    mainText.style.cssText = `font-weight: 500; font-size: 14px;`;
		    mainText.textContent = `${result.nameKo || result.nameEn}, ${result.country}`;
		    
		    const subText = document.createElement('div');
		    subText.style.cssText = `color: #666; font-size: 12px;`;
		    subText.textContent = result.type === 'city' ? `모든 공항 (${result.iata})` : `${result.nameEn} (${result.iata})`;
		    
		    content.appendChild(mainText);
		    content.appendChild(subText);
		    
		    item.innerHTML = icon;
		    item.appendChild(content);
		    
		    return item;
		};
});
