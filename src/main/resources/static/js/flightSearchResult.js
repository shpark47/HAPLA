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
		onOpen: function(selectedDates, dateStr, instance) {
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
		onChange: function(selectedDates, dateStr, instance) {
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
	.hidden { display: none !important; } /* 필터링용 CSS 추가 */
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
document.querySelector('#return').addEventListener('click', function() {
	mode = "range";
	updateDatePicker();
});

document.querySelector('#go').addEventListener('click', () => {
	mode = "single";
	updateDatePicker();
});


document.querySelector('.research-btn').addEventListener('click', () => {
	const form = document.querySelector('.research-form');
	form.action = '/flight/flightSearch';
	form.submit();
});

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
const flightContainer = document.querySelector('.flight-container');
const flightSearchResult = window.flightSearchResult;


// 시간대 확인 함수
const getTimeSlot = (timeStr) => {
	// timeStr 형식: "HH:mm:ss" (예: "08:00:00")

	const hours = (timeStr.split(':')[0]); // 시간 부분 추출
	const hour = parseInt(hours.slice(0, 2));
	if (hour >= 6 && hour < 12) return 'morning';
	if (hour >= 12 && hour < 18) return 'afternoon';
	if (hour >= 18 && hour < 24) return 'evening';
	return 'night';
};



const applyFilter = (flightData) => {
	let hasInbound = null;
	
	const flightContainers = document.querySelectorAll('.flight-container');

	// 경유 필터링 체크박스
	const checkedLayovers = document.querySelectorAll('input[name="layover"]:checked');
	const selectedLayoverOptions = Array.from(checkedLayovers).map(input => input.value);

	// 가는 날 출발 시간 필터링 체크박스
	const outChecked = document.querySelectorAll('input[name="out-departure-time"]:checked');
	const outSelectedOptions = Array.from(outChecked).map(input => input.value);
	
	// 오는 날 출발 시간 필터링 체크박스
	const inChecked = document.querySelectorAll('input[name="in-departure-time"]:checked');
	const inSelectedOptions = Array.from(inChecked).map(input => input.value);

	const airline = document.querySelectorAll('input[name="airline-filter"]:checked');
	const airlineSelectedOptions = Array.from(airline).map(input => input.value);

	flightContainers.forEach((flightContainer, index) => {
		const flight = flightData[index] || {};
		const outboundStops = parseInt(flightContainer.dataset.outboundStops || 0);
		const inboundStops = parseInt(flightContainer.dataset.inboundStops || 0);

		// 시간 데이터에서 "HH:mm:ss" 형식으로 변환
		const outboundTime = (flightContainer.dataset.outboundTime?.split('T')[1] || '').replace(/:/g, '');
		const inboundTime = (flightContainer.dataset.inboundTime?.split('T')[1] || '').replace(/:/g, '');

		hasInbound = flight['inboundDepartureTime'] != null; // 귀국 구간 존재 여부

		// 기본적으로 보이도록 설정
		flightContainer.classList.remove('hidden');

		let shouldHide = false; // 모든 조건을 체크하기 위한 플래그

		// 1. 경유 필터링
		if (selectedLayoverOptions.length > 0) {
			const isDirect = selectedLayoverOptions.includes('direct');
			const isOneStop = selectedLayoverOptions.includes('oneStop');
			const isMultiStop = selectedLayoverOptions.includes('multiStop');

			if (
				// 직항만 선택: 출발 또는 귀국이 직항이 아니면 숨김
				(isDirect && !isOneStop && !isMultiStop && (outboundStops != 0 || (hasInbound && inboundStops != 0))) ||
				// 1회 경유만 선택: 출발과 귀국(있을 경우)이 1회가 아니면 숨김
				(isOneStop && !isDirect && !isMultiStop &&
					(outboundStops != 1 || (hasInbound && inboundStops != 1))) ||
				// 2회 이상 경유만 선택: 출발과 귀국(있을 경우)이 2회 미만이면 숨김
				(isMultiStop && !isDirect && !isOneStop &&
					(outboundStops < 2 && (!hasInbound || inboundStops < 2))) ||
				// 직항 + 1회 경유: 2회 이상 경유 숨김
				(isDirect && isOneStop && !isMultiStop &&
					(outboundStops >= 2 || (hasInbound && inboundStops >= 2))) ||
				// 1회 경유 + 2회 이상 경유: 직항 숨김
				(isOneStop && isMultiStop && !isDirect &&
					(outboundStops == 0 || (hasInbound && inboundStops == 0))) ||
				// 직항 + 2회 이상 경유: 1회 경유 숨김
				(isDirect && isMultiStop && !isOneStop &&
					(outboundStops == 1 || (hasInbound && inboundStops == 1)))
			) {
				shouldHide = true; // 조건에 해당하면 숨김 플래그 설정
			}
		}

		// 2. 가는 날 출발 시간 필터링
		if (outSelectedOptions.length > 0 && !shouldHide) {
			const outboundTimeSlot = getTimeSlot(outboundTime);
			const timeMatch = outSelectedOptions.includes(outboundTimeSlot);
			if (!timeMatch) {
				shouldHide = true; // 시간대가 선택된 옵션과 맞지 않으면 숨김
			}
		}

		// 3. 오는 날 출발 시간 필터링 (왕복일 경우에만 적용)
		if (hasInbound && inSelectedOptions.length > 0 && !shouldHide) {
			const inboundTimeSlot = getTimeSlot(inboundTime);
			const timeMatch = inSelectedOptions.includes(inboundTimeSlot);
			if (!timeMatch) {
				shouldHide = true; // 시간대가 선택된 옵션과 맞지 않으면 숨김
			}
		}

		if (airlineSelectedOptions.length > 0 && !shouldHide) {
			if (!airlineSelectedOptions.some(option => flight.airline.includes(option))) {
				// .some -> 일치하는게 하나라도 있으면 true 반환(배열을 돌다가 한번이라도 true면 true 반환 후 탈출)
				shouldHide = true;
			}
		}


		// 최종적으로 숨김 여부 결정
		if (shouldHide) {
			flightContainer.classList.add('hidden');
		}

	});
};


// 이벤트 리스너 추가
document.addEventListener('change', (e) => {
	if (
		e.target.matches('input[name="layover"]') ||
		e.target.matches('input[name="out-departure-time"]') ||
		e.target.matches('input[name="in-departure-time"]') ||
		e.target.matches('input[name="airline-filter"]')
	) {
		applyFilter(flightSearchResult); // 데이터 전달
	}
});

document.addEventListener('DOMContentLoaded', () => {
	applyFilter(flightSearchResult); // 초기 필터링 적용
});

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function() {
	const flightList = document.getElementById('flight-list');
	const sortSelect = document.getElementById('sort-select');
	let flightData = window.flightSearchResult || []; // Thymeleaf에서 전달된 데이터

	// 날짜 문자열을 Date 객체로 변환하고 시간 차이를 계산하는 함수
	function getDurationInHours(startTime, endTime) {
		const start = new Date(startTime);
		const end = new Date(endTime);
		const diffMs = end - start;
		return diffMs / (1000 * 60 * 60); // 밀리초를 시간 단위로 변환
	}

	// 총 소요 시간 계산 (outbound + inbound)
	function getTotalDuration(flight) {
		const outboundDuration = getDurationInHours(flight.outboundDepartureTime, flight.outboundArrivalTime);
		const inboundDuration = flight.inboundDepartureTime && flight.inboundArrivalTime
			? getDurationInHours(flight.inboundDepartureTime, flight.inboundArrivalTime)
			: 0;
		return outboundDuration + inboundDuration;
	}

	// 가격에서 숫자만 추출 (예: "1658.50 EUR" -> 1658.50)
	function parsePrice(priceStr) {
		return parseFloat(priceStr.split(' ')[0]);
	}

	// 가성비 점수 계산 (낮을수록 가성비 좋음: 가격/시간)
	function getValueScore(flight) {
		const price = parsePrice(flight.price);
		const totalDuration = getTotalDuration(flight);
		return totalDuration > 0 ? price / totalDuration : Infinity; // 시간이 0이면 Infinity 반환
	}

	// 항공편 렌더링 함수
	function renderFlights(flights) {
		flightList.innerHTML = ''; // 기존 목록 초기화
		flights.forEach(flight => {
			const flightDiv = document.createElement('div');
			flightDiv.className = 'flight-container';
			flightDiv.setAttribute('data-outbound-stops', flight.outboundTotalStops);
			flightDiv.setAttribute('data-inbound-stops', flight.inboundTotalStops || 0);
			flightDiv.setAttribute('data-inbound-time', flight.inboundDepartureTime || '');
			flightDiv.setAttribute('data-outbound-time', flight.outboundDepartureTime);

			// 시간 포맷팅 함수
			const formatTime = (dateStr) => {
				const date = new Date(dateStr);
				return date.toLocaleTimeString('ko-KR', { hour: 'numeric', minute: '2-digit', hour12: true });
			};

			flightDiv.innerHTML = `
                <div class="flight-info">
                    <div class="airline-info">
                        <span style="margin-left:3rem"class="${flight.inboundAirline != flight.outboundAirline ? 'outbound-info' : ''}">${flight.outboundKorAirlineName != null ? flight.outboundKorAirlineName : flight.airlineNm} </span>
                        ${flight.inboundAirline && flight.inboundAirline !== flight.outboundAirline
					? `<span class="${flight.inboundAirline != null ? 'inbound-info' : ''}">${flight.inboundKorAirlineName}</span>` : ''}
                    </div>
                    <div class="flight-details">
                        <div class="departure">
                            <div class="flight-title">
                                <div class="time-info outbound">
                                    <div class="airport-code">${flight.outboundDepartureAirport != null ? flight.outboundDepartureAirport : flight.airlineNm}</div>
                                    <div class="flight-time">${flight.outboundDepartureTime  ?  formatTime(flight.outboundDepartureTime) : formatTime(flight.depPlandTime)}</div>
                                </div>
                                <div class="flight-path"></div>
                                <div class="time-info outbound">
                                    <div class="airport-code">${flight.outboundArrivalAirport != null ? flight.outboundArrivalAirport : flight.airlineNm}</div>
									<div class="flight-time">${flight.outboundArrivalTime ? formatTime(flight.outboundArrivalTime) : formatTime(flight.arrPlandTime)}</div>

                                </div>
                            </div>
                            <div class="duration">
                                <span>${flight.outboundDepartureTime ? formatTime(flight.outboundDepartureTime) : formatTime(flight.depPlandTime)}</span> → 
                                <span>${flight.outboundArrivalTime ? formatTime(flight.outboundArrivalTime) : formatTime(flight.arrPlandTime)}</span>
                                (<span>${flight.outboundHasConnections === 'true' ? '경유 ' + flight.outboundTotalStops + '회' : '직항'}</span>)
                            </div>
                        </div>
                        ${flight.inboundDepartureTime ? `
                            <div class="return-section">
                                <hr style="border: 1px dashed #ccc; margin: 10px 0;">
                                <div class="return">
                                    <div class="flight-title">
                                        <div class="time-info inbound">
                                            <div class="airport-code">${flight.inboundDepartureAirport}</div>
                                            <div class="flight-time">${formatTime(flight.inboundDepartureTime)}</div>
                                        </div>
                                        <div class="flight-path"></div>
                                        <div class="time-info inbound">
                                            <div class="airport-code">${flight.inboundArrivalAirport}</div>
                                            <div class="flight-time">${formatTime(flight.inboundArrivalTime)}</div>
                                        </div>
                                    </div>
                                    <div class="duration">
                                        <span>${formatTime(flight.inboundDepartureTime)}</span> → 
                                        <span>${formatTime(flight.inboundArrivalTime)}</span>
                                        (<span>${flight.inboundHasConnections === 'true' ? '경유 ' + flight.inboundTotalStops + '회' : '직항'}</span>)
                                    </div>
                                </div>
                            </div>` : ''}
                    </div>
                </div>
                <div class="flight-price">
                    <div class="price-info">
                        <div class="price-title">
                            <strong>가격 : </strong> <span>${flight.price != null ? flight.price : flight.economyCharge}</span>
                        </div>
                        <button class="view-deal">예약가능</button>
                    </div>
                </div>
            `;
			flightList.appendChild(flightDiv);
		});
	}

	// 정렬 함수
	function sortFlights(criterion) {
		let sortedFlights = [...flightData]; // 원본 데이터 복사
		switch (criterion) {
			case 'best': // 가성비 최고
				sortedFlights.sort((a, b) => getValueScore(a) - getValueScore(b));
				break;
			case 'price': // 최저가
				sortedFlights.sort((a, b) => parsePrice(a.price) - parsePrice(b.price));
				break;
			case 'duration': // 최단시간
				sortedFlights.sort((a, b) => getTotalDuration(a) - getTotalDuration(b));
				break;
			default:
				break;
		}
		renderFlights(sortedFlights); // 정렬된 데이터로 화면 갱신
	}

	// 초기 렌더링
	renderFlights(flightData);

	// 정렬 선택 이벤트 리스너
	sortSelect.addEventListener('change', function() {
		sortFlights(this.value);
	});
});


window.onload = () => {

	let airline = [];
	let korAirline = [];
	let seenAirlines = new Set();
	// set -> es6에서부터 사용 가능한 데이터 구조 배열과 유사하지만 중복된값저장 불가능
	let flightSearchResult = window.flightSearchResult;
	console.log("flightSearchResult:", flightSearchResult);

	if (flightSearchResult) {
		for (let flightSearchResults of flightSearchResult) {
			let outboundAirline = flightSearchResults['outboundAirline'];
			let inboundAirline = flightSearchResults['inboundAirline'];
			let outboundKorAirline = flightSearchResults['outboundKorAirlineName'];
			let inboundKorAirline = flightSearchResults['inboundKorAirlineName'];
			let airlineNm = flightSearchResults['airlineNm'];
			
			if(outboundAirline != '') {
				if (outboundAirline && !seenAirlines.has(outboundAirline)) {
					airline.push(outboundAirline);
					korAirline.push(outboundKorAirline);
					seenAirlines.add(outboundAirline);
				}
				if (inboundAirline && !seenAirlines.has(inboundAirline)) {
					airline.push(inboundAirline);
					korAirline.push(inboundKorAirline);
					seenAirlines.add(inboundAirline);
				}
			}
			console.log(airlineNm);
//			console.log(seenAirlines);
			if(airlineNm != undefined) {
				if(!seenAirlines.has(airlineNm)) {
					console.log('이상한곳에 들어옴');
					airline.push(airlineNm);
					korAirline.push(airlineNm);
					seenAirlines.add(airlineNm);
				}
			}
		}

		let container = document.getElementsByClassName("filter-group")[3];

		console.log("container:", container);

		if (container) {
			let visibleCount = 5;
			let hiddenCheckboxes = [];
			let showMoreButton;

			for (let i = 0; i < airline.length; i++) {
				let airlines = airline[i];
				let korAirlines = korAirline[i];
				let label = document.createElement("label");
				let checkbox = document.createElement("input");
				checkbox.type = "checkbox";
				checkbox.name = "airline-filter";
				checkbox.value = airlines;

				label.appendChild(checkbox);
				label.appendChild(document.createTextNode(korAirlines));

				if (i < visibleCount) {
					container.appendChild(label);
				} else {
					hiddenCheckboxes.push(label);
				}

				console.log("checkbox.value:", checkbox.value, "label.textContent:", label.textContent);
			}

			if (hiddenCheckboxes.length > 0) {
				showMoreButton = document.createElement("button");
				showMoreButton.textContent = "더보기";
				showMoreButton.classList.add('show-more-button');
				container.appendChild(showMoreButton);

				showMoreButton.addEventListener("click", () => {
					if (showMoreButton.textContent === "더보기") {
						// 숨겨진 체크박스를 더보기 버튼 위에 추가
						hiddenCheckboxes.forEach(label => container.insertBefore(label, showMoreButton));
						showMoreButton.textContent = "숨기기";
					} else {
						// 숨겨진 체크박스를 제거
						hiddenCheckboxes.forEach(label => container.removeChild(label));
						showMoreButton.textContent = "더보기";
					}
				});
			}
		}
	}
}

document.querySelector('.search-bar').addEventListener('keyup', (e) => {
	if(e.key=='Enter') {
//		console.log('enter');
		searchPlaces();
		}
});

window.onbeforeunload = function() {
	$('#loading').show();
	$('body').css('overflow', 'hidden');
}  //현재 페이지에서 다른 페이지로 넘어갈 때 표시해주는 기능
window.addEventListener('load', () => {
	$('#loading').hide();
	$('body').css('overflow', 'auto');
});


