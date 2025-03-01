let mode = "range"; // 기본값은 왕복(range)
fpInitialized = false; // flatpickr 초기화 상태를 추적

const updateDatePicker = () => {
    // 기존 flatpickr 인스턴스가 있으면 제거
    if (fp && fp.destroy) {
        fp.destroy();
    }

    fp = flatpickr(datePickerInput, {
        mode: mode, // 왕복(range), 편도(single), 다구간(single) 설정
        dateFormat: "Y-m-d",
        minDate: "today",
        defaultDate: mode === "range" ? [new Date(), new Date(new Date().setDate(new Date().getDate() + 7))] : new Date(),
        monthSelectorType: "static",
        showMonths: 2,
        locale: "ko",
        position: "below",
        closeOnSelect: mode === "single", // 편도/다구간은 날짜 한 번 선택 시 자동 닫기, 왕복은 유지
        onOpen: function (selectedDates, dateStr, instance) {
            if (!fpInitialized) {
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

                instance.calendarContainer.style.cssText += `
                    padding-top: 70px;
                    border-radius: 8px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                `;

                fpInitialized = true;
            }
            updateCalendarStyles(selectedDates, instance); // 스타일 업데이트
        },
        onChange: function (selectedDates, dateStr, instance) {
            const titleDiv = instance.calendarContainer.querySelector(".calendar-title");
            if (titleDiv) {
                updateTitle(selectedDates, titleDiv);
            }

            // 날짜 선택 처리 (왕복, 편도, 다구간에 따라 다름)
            if (mode === "range" && selectedDates.length === 2) {
                const startDate = selectedDates[0].toISOString().split('T')[0]; // YYYY-MM-DD
                const endDate = selectedDates[1].toISOString().split('T')[0];   // YYYY-MM-DD
                const startDateKr = selectedDates[0].toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });
                const endDateKr = selectedDates[1].toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });

                datePickerInput.value = `${startDate} ~ ${endDate}`; // 데이터 전송용
                datePickerInput.dataset.displayValue = `${startDateKr} → ${endDateKr}`; // 사용자 표시용
                instance.close(); // 두 날짜 선택 후 자동 닫기
                updateCalendarStyles(selectedDates, instance); // 스타일 업데이트
            } else if (mode === "single" && selectedDates.length === 1) {
                const singleDate = selectedDates[0].toISOString().split('T')[0]; // YYYY-MM-DD
                const singleDateKr = selectedDates[0].toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });

                datePickerInput.value = singleDate; // 데이터 전송용
                datePickerInput.dataset.displayValue = singleDateKr; // 사용자 표시용
                instance.close(); // 날짜 한 번 선택 후 자동 닫기
                updateCalendarStyles(selectedDates, instance); // 스타일 업데이트
            }
        }
    });
};

// 타이틀 업데이트 함수 (기존 유지)
function updateTitle(selectedDates, titleDiv) {
    if (selectedDates.length === 2) {
        const startDate = selectedDates[0].toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });
        const endDate = selectedDates[1].toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });
        titleDiv.textContent = `${startDate} 출발 → ${endDate} 도착`;
    } else if (selectedDates.length === 1) {
        const singleDate = selectedDates[0].toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });
        titleDiv.textContent = `${singleDate} 출발`;
    } else {
        titleDiv.textContent = "출발일과 도착일을 선택하세요";
    }
}

// 날짜 범위 선택 시 스타일 변경 함수 (기존 유지)
function updateCalendarStyles(selectedDates, instance) {
    const days = instance.calendarContainer.querySelectorAll('.flatpickr-day');

    // 모든 날짜 초기화
    days.forEach(day => {
        day.classList.remove('selected-range-start', 'selected-range-end', 'selected-range-in-between');
        day.style.backgroundColor = '';
        day.style.borderColor = '';
        day.style.color = '';
    });

    // 날짜 범위 스타일 적용 (왕복일 경우만)
    if (mode === "range" && selectedDates.length === 2) {
        const [startDate, endDate] = selectedDates;
        const startUnix = startDate.getTime();
        const endUnix = endDate.getTime();

        days.forEach(day => {
            const dayDate = day.dateObj.getTime();
            if (dayDate === startUnix) {
                day.classList.add('selected-range-start');
                day.style.backgroundColor = '#000';
                day.style.borderColor = '#000';
                day.style.color = '#fff';
                day.style.borderRadius = '50% 0 0 50%'; // 시작 날짜 왼쪽 둥글게
            } else if (dayDate === endUnix) {
                day.classList.add('selected-range-end');
                day.style.backgroundColor = '#000';
                day.style.borderColor = '#000';
                day.style.color = '#fff';
                day.style.borderRadius = '0 50% 50% 0'; // 끝 날짜 오른쪽 둥글게
            } else if (dayDate > startUnix && dayDate < endUnix) {
                day.classList.add('selected-range-in-between');
                day.style.backgroundColor = '#f0f0f0';
                day.style.borderColor = '#f0f0f0';
                day.style.color = '#333';
            }
        });
    } else if (mode === "single" && selectedDates.length === 1) {
        const singleDate = selectedDates[0].getTime();
        days.forEach(day => {
            const dayDate = day.dateObj.getTime();
            if (dayDate === singleDate) {
                day.classList.add('selected-range-start');
                day.style.backgroundColor = '#000';
                day.style.borderColor = '#000';
                day.style.color = '#fff';
                day.style.borderRadius = '50%'; // 단일 날짜는 완전 둥글게
            }
        });
    }
}

style = document.createElement('style');
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
    .flatpickr-day.selected-range-start {
        background: #000 !important;
        border-color: #000 !important;
        color: #fff !important;
        border-radius: 50% 0 0 50% !important;
    }
    .flatpickr-day.selected-range-end {
        background: #000 !important;
        border-color: #000 !important;
        color: #fff !important;
        border-radius: 0 50% 50% 0 !important;
    }
    .flatpickr-day.selected-range-in-between {
        background: #f0f0f0 !important;
        border-color: #f0f0f0 !important;
        color: #333 !important;
    }
    .flatpickr-day.selected-single {
        background: #000 !important;
        border-color: #000 !important;
        color: #fff !important;
        border-radius: 50% !important;
    }
    .flatpickr-months {
        padding-top: 20px;
    }
    .iata-dropdown {
        position: absolute;
        top: 100%;
        left: 0;
        right: 0;
        background: white;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        margin-top: 4px;
        max-height: 300px;
        overflow-y: auto;
        z-index: 1000;
    }
    .search-result-item {
        padding: 12px;
        display: flex;
        align-items: center;
        gap: 12px;
        cursor: pointer;
        border-bottom: 1px solid #f0f0f0;
        transition: background-color 0.2s;
    }
    .search-result-item:hover,
    .search-result-item.active {
        background-color: #f8f8f8;
    }
    .search-result-content {
        flex: 1;
    }
    .search-result-title {
        font-weight: 500;
        font-size: 14px;
    }
    .search-result-subtitle {
        color: #666;
        font-size: 12px;
        margin-top: 2px;
    }
`;
document.head.appendChild(style);

datePickerInput.addEventListener('click', (event) => {
    if (!fp.isOpen) {
        fp.open();
    }
    event.stopPropagation();
});

document.addEventListener('click', (event) => {
    const calendar = document.querySelector('.flatpickr-calendar');
    if (calendar && !calendar.contains(event.target) && event.target !== datePickerInput) {
        event.stopPropagation();
    }
});

// 버튼 이벤트 리스너
document.querySelector('#return').addEventListener('click',  function() {
    mode = "range";
    updateDatePicker();
});

document.querySelector('#go').addEventListener('click', () => {
    mode = "single";
    updateDatePicker();
});


//document.querySelector('.search-btn').addEventListener('click', () => {
//    const form = document.querySelector('.search-form');
//    form.action = '/flight/flightSearch';
//    form.submit();
//});

const inputBtns = document.querySelectorAll('.trip-group button');


inputBtns.forEach(array => {  // 🔴 'array'는 각 요소(버튼)
    array.addEventListener('click', () => {
		inputBtns.forEach(btn => btn.classList.remove('active'));
		array.classList.add('active');
	});
});


const researchBtn = document.querySelector('.research-btn');
const departureName = document.querySelector('input[name="departureName"]');
const arrivalName = document.querySelector('input[name="arrivalName"]');
const dates = document.querySelector('input[name="dates"]');
const travelers = document.querySelector('input[name="travelers"]');



researchBtn.addEventListener('click', function() {
    const url = `/flight/flightSearch?departureName=${encodeURIComponent(departureName.value)}&arrivalName=${encodeURIComponent(arrivalName.value)}&dates=${encodeURIComponent(dates.value)}&travelers=${encodeURIComponent(travelers.value)}&query=${encodeURIComponent(query)}`;

    fetch(url, {
        method: 'get',
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        },
    })
    .then(response => response.json())
    .then(data => {
        const resultsContainer = document.querySelector('.search-results-container');
        const flightResults = document.querySelector('.flight-results'); // 기존 검색 결과 부분

        if (flightResults) {
            flightResults.remove(); // 기존 검색 결과만 삭제
        }

        // 새로운 검색 결과 생성
        const newResults = document.createElement('main');
        newResults.classList.add('flight-results');

        if (data.length > 0) {
            newResults.innerHTML = `
                <div class="flight-result-header">
                    <h2>${data.length}개의 항공권 검색됨</h2>
                    <div class="sort-options">
                        <span>정렬순서: </span> 
                        <select id="sort-select">
                            <option value="best">가성비 최고</option>
                            <option value="price">최저가</option>
                            <option value="duration">최단시간</option>
                        </select>
                    </div>
                </div>
                <div id="flight-list">
                    ${data.map(flight => `
                        <div class="flight-container">
                            <div class="flight-info">
                                <div class="airline-info">
                                    <span>${flight.outboundAirline}</span>
                                    ${flight.inboundAirline && flight.inboundAirline !== flight.outboundAirline ? `<span>${flight.inboundAirline}</span>` : ''}
                                </div>
                                <div class="flight-details">
                                    <div class="departure">
                                        <div class="flight-title">
                                            <div class="time-info outbound">
                                                <div class="airport-code">${flight.outboundDepartureAirport}</div>
                                                <div class="flight-time">${flight.outboundDepartureTime}</div>
                                            </div>
                                            <div class="flight-path"></div>
                                            <div class="time-info outbound">
                                                <div class="airport-code">${flight.outboundArrivalAirport}</div>
                                                <div class="flight-time">${flight.outboundArrivalTime}</div>
                                            </div>
                                        </div>
                                        <div class="duration">
                                            ${flight.outboundDepartureTime} → ${flight.outboundArrivalTime} 
                                            (${flight.outboundHasConnections ? `경유 ${flight.outboundTotalStops}회` : '직항'})
                                        </div>
                                    </div>
                                    ${flight.inboundDepartureTime ? `
                                        <div class="return-section">
                                            <hr style="border: 1px dashed #ccc; margin: 10px 0;">
                                            <div class="return">
                                                <div class="flight-title">
                                                    <div class="time-info inbound">
                                                        <div class="airport-code">${flight.inboundDepartureAirport}</div>
                                                        <div class="flight-time">${flight.inboundDepartureTime}</div>
                                                    </div>
                                                    <div class="flight-path"></div>
                                                    <div class="time-info inbound">
                                                        <div class="airport-code">${flight.inboundArrivalAirport}</div>
                                                        <div class="flight-time">${flight.inboundArrivalTime}</div>
                                                    </div>
                                                </div>
												<div class="duration">
													<span>${formatTime(flight.outboundDepartureTime)}</span> → 
													<span>${formatTime(flight.outboundArrivalTime)}</span> 
													(${flight.outboundHasConnections === 'true' ? `경유 ${flight.outboundTotalStops}회` : '직항'})
												</div>
                                            </div>
                                        </div>
                                    ` : ''}
                                </div>
                            </div>
                            <div class="flight-price">
                                <div class="price-info">
                                    <div class="price-title">
                                        <strong>가격 :&nbsp</strong> <span>${flight.price}</span>
                                    </div>
                                    <button class="view-deal">예약하기</button>
                                </div>
                            </div>
                        </div>
                    `).join('')}
                </div>
            `;
        } else {
            newResults.innerHTML = `<p>검색 결과가 없습니다.</p>`;
        }

        resultsContainer.appendChild(newResults);
    })
    .catch(error => console.error('Error fetching flight data:', error));
});

// 시간 형식 변환 함수 (HH:mm 포맷을 오전/오후로 변환)
function formatTime(timeString) {
	const date = new Date(timeString);
	const hours = date.getHours();
	const minutes = String(date.getMinutes()).padStart(2, '0');
	const period = hours < 12 ? '오전' : '오후';
	const formattedHours = hours % 12 || 12; // 0시는 12로 변환
	return `${period} ${formattedHours}:${minutes}`;
}
