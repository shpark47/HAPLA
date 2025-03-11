let query='';

const categoryButtons = document.querySelectorAll('.search-categories button');
const inputText = document.querySelector('#search-input-text');
const searchBar = document.querySelector('.search-bar');
const flightSearchBar = document.querySelector('.flight-search-bar');
const datePickerInput = document.querySelector('.date-picker');

const placeholderMap = {
    '여행지': '여행지',
    '관광명소': '관광명소, 액티비티 또는 여행지',
    '숙박': '호텔 이름 또는 여행지',
    '음식점': '음식점 또는 여행지'
};

// 일반 검색 ------------------------------------------------------------------------
let selectedCategory = "전체"; // 기본 카테고리

categoryButtons.forEach(button => {
    button.addEventListener('click', function () {
        categoryButtons.forEach(btn => btn.classList.remove('active'));
        button.classList.add('active');
        inputText.placeholder = placeholderMap[button.innerText] || '여행지, 즐길거리, 호텔 등';
        const isFlightSearch = button.innerText === '항공권';
        searchBar.style.display = isFlightSearch ? 'none' : 'block';
        flightSearchBar.style.display = isFlightSearch ? 'block' : 'none';
        selectedCategory = this.getAttribute("data-category");
    });
});

// ✅ 장소 검색 함수 (서버로 요청을 보냄)
async function searchPlaces() {
    let city = document.querySelector('#search-input-text').value; // 검색 입력창에서 도시 이름 가져오기
    if (!city) { // 도시 이름이 입력되지 않은 경우
        alert("도시명을 입력하세요."); // 경고 메시지 표시
        return; // 함수 종료
    }

    if (selectedCategory == '전체'){
        fetch(`/searchAll?city=${encodeURIComponent(city)}`)
            .then(response => response.json())
            .then(data => {
                displayAllPlace(data);
            });
    }else{
        // 서버로 도시 이름과 선택된 카테고리 보내기
        let response = await fetch(`/search?city=${encodeURIComponent(city)}&category=${selectedCategory}`);
        // fetch를 사용해 서버에 GET 요청을 보내고, 도시 이름과 카테고리를 쿼리 파라미터로 전달
        // encodeURIComponent로 특수 문자를 URL에 맞게 인코딩
        let data = await response.json(); // 서버 응답을 JSON 형식으로 변환

        // 서버 응답이 성공적이면 장소 표시
        if (data.status === "OK") { // 응답 상태가 "OK"인 경우
            displayPlaces(data.results); // 검색 결과를 화면에 표시하는 함수 호출
        } else { // 응답 상태가 "OK"가 아닌 경우 (검색 실패)
            alert("검색 결과가 없습니다."); // 사용자에게 검색 실패 메시지 표시
        }
    }

    fetch(`https://api.weatherapi.com/v1/current.json?key=b7639a3b840040e1abc73111250703&q=${city}&lang=ko`)
        .then(response => response.json())
        .then(data => {
            console.log('도시:', data.location.name);
            console.log('현재 기온:', data.current.temp_c, '°C');
            console.log('날씨:', data.current.condition.text);
            console.log('습도:', data.current.humidity, '%');
            console.log('체감 기온:', data.current.feelslike_c, '°C');
            console.log('바람 속도:', data.current.wind_kph, 'km/h');
        })
        .catch(error => console.error('오류:', error));
}

// ✅ 검색된 장소를 화면에 표시하는 함수
function displayPlaces(places) {
    let container = document.getElementById('search-all-data');
    let inDiv = document.createElement("div"); // 장소 카드를 표시할 컨테이너 요소 선택
    inDiv.classList.add('destinations-grid');
    container.innerHTML = '';

    const h2 = document.createElement('h2');
    h2.innerText = '검색 결과';
    container.appendChild(h2);
    container.appendChild(inDiv);

    places.forEach(place => { // 검색된 장소 목록을 하나씩 순회

        let name = place.name || "이름 없음"; // 장소 이름이 없으면 "이름 없음"으로 기본값 설정
        let rating = place.rating ? `⭐ ${place.rating}` : "⭐ 없음"; // 평점이 있으면 별표와 함께 표시, 없으면 "없음"
        let reviews = place.user_ratings_total ? `${place.user_ratings_total} 리뷰` : "리뷰 없음";
        // 리뷰 수가 있으면 숫자와 함께 표시, 없으면 "리뷰 없음"

        // 사진 URL 생성: Google Places API의 photo 엔드포인트를 통해 사진을 가져옵니다.
        let photoUrl = place.photos && place.photos[0].photo_reference ?
            `https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${place.photos[0].photo_reference}&key=AIzaSyCEHjTtVqBclz07ADqbkGjqGIe94Cq-S60` :
            "/img/시나모롤.jpg"; // 사진이 있으면 Google API로 URL 생성, 없으면 기본 이미지 사용

        // ✅ place.types 배열 전체에서 첫 번째로 나오는 타입을 찾습니다
        let placeTypes = place.types.find(type =>
            ['tourist_attraction', 'landmark', 'lodging', 'restaurant'].includes(type)
        ) || "unknown"; // 원하는 타입이 없으면 "unknown"을 기본값으로 설정

        let div = document.createElement("div"); // 새로운 장소 카드 요소 생성
        div.className = "destination-card"; // CSS 클래스 추가 (스타일링용)
        div.innerHTML = `
            <input type="hidden" name="type" value="${placeTypes}">
            <img src="${photoUrl}" alt="${name}">
            <div class="destination-info">
            <h3>${name}</h3> 
            <p class="rating">${rating}</p>
            <p class="review-count">${reviews}</p>
            </div>
        `;

        // 카드 클릭 시 상세 페이지로 이동
        div.addEventListener("click", () => {
            let encodedPlaceId = encodeURIComponent(place.place_id); // place_id를 URL 안전하게 인코딩
            location.href = `/detail/${encodedPlaceId}/${placeTypes}`; // 상세 페이지 URL로 이동
        });

        inDiv.appendChild(div); // 생성한 카드를 컨테이너에 추가
    });
}

const displayAllPlace = places => {
    let container = document.getElementById('search-all-data');
    let div = document.createElement("div");
    container.innerHTML = "";

    div.innerHTML = `
        <h2>${places.main_info.name}</h2>
        <img src="${places.main_info.photo_url}" alt="${places.main_info.name}">
        <p>${places.main_info.description}</p>
        `;
    container.innerHTML = div.innerHTML;

    const categories = [
        { title: '여행지', data: places.tourist_attraction, type: 'tourist_attraction' },
        { title: '관광명소', data: places.landmark, type: 'landmark' },
        { title: '숙박', data: places.lodging, type: 'lodging' },
        { title: '음식점', data: places.restaurant, type: 'restaurant' }
    ];

    categories.forEach(category => {
        const h2 = document.createElement('h2');
        h2.innerText = category.title;
        container.appendChild(h2);
        container.appendChild(otherPlace(category.data, category.type));
    });
}

const otherPlace = (p, category) => {
    let grid = document.createElement("div");
    grid.classList.add('destinations-grid');
    p.forEach(attr => {
        const div = document.createElement('div');
        div.className = 'destination-card';
        div.innerHTML = `
            <input type="hidden" name="type" value="${category}">
            <img src="${attr.photo_url}" alt="${attr.name}">
            <div class="destination-info">
            <h3>${attr.name}</h3> 
            <p class="rating">⭐ ${attr.rating}</p>
            <p class="review-count">${attr.review_count} 리뷰</p>
            </div>
        `;

        div.addEventListener("click", () => {
            let encodedPlaceId = encodeURIComponent(attr.place_id); // place_id를 URL 안전하게 인코딩
            location.href = `/detail/${encodedPlaceId}/${category}`; // 상세 페이지 URL로 이동
        });

        grid.appendChild(div);
    });
    return grid;
};


// 항공 -----------------------------------------------------------------------------

let fpInitialized = false;

let fp = flatpickr(datePickerInput, {
    mode: "range",
    dateFormat: "Y-m-d",
    minDate: "today",
    defaultDate: [new Date(), new Date(new Date().setDate(new Date().getDate() + 7))],
    monthSelectorType: "static",
    showMonths: 2,
    locale: "ko",
    position: "below",
    closeOnSelect: true, // 두 번째 날짜 선택 후 자동으로 닫힘
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
        updateCalendarStyles(selectedDates, instance); // 달력 열릴 때 스타일 초기화
    },
    onChange: function (selectedDates, dateStr, instance) {
        const titleDiv = instance.calendarContainer.querySelector(".calendar-title");
        if (titleDiv) {
            updateTitle(selectedDates, titleDiv);
        }

        // 두 날짜가 선택되면 자동으로 적용
        if (selectedDates.length === 2) {
            const startDate = selectedDates[0].toISOString().split('T')[0]; // YYYY-MM-DD 형식
            const endDate = selectedDates[1].toISOString().split('T')[0];   // YYYY-MM-DD 형식
            const startDateKr = selectedDates[0].toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });
            const endDateKr = selectedDates[1].toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });

            // 입력 필드에 두 형식으로 값 설정
            datePickerInput.value = `${startDate} ~ ${endDate}`; // 데이터 전송용 형식
            datePickerInput.dataset.displayValue = `${startDateKr} ~ ${endDateKr}`; // 사용자 표시용 형식

            // 달력 닫기
            instance.close();
            updateCalendarStyles(selectedDates, instance); // 스타일 업데이트
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

    // 날짜 범위가 2개 이상 선택되었을 때 스타일 적용
    if (selectedDates.length === 2) {
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
    }
}

let style = document.createElement('style');
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

const selection = document.querySelector('.traveler-selection');
const traveler = document.querySelector('.traveler-picker');
const resetBtn = document.querySelector('.reset');
const applyButton = document.querySelector('.apply');
const incrementButtons = document.querySelectorAll('.increment');
const decrementButtons = document.querySelectorAll('.decrement');
const countElements = document.querySelectorAll('.count');
const maxTravelers = 9;

const warningMessage = document.createElement('p');
warningMessage.textContent = "최대 9명까지만 선택할 수 있습니다.";
warningMessage.style.cssText = `
        color: red;
        font-size: 14px;
        margin-top: 8px;
        display: none;
    `;
selection.appendChild(warningMessage);

traveler.addEventListener('click', function (e) {
    e.stopPropagation();
    selection.style.display = 'block';
});

applyButton.addEventListener('click', function () {
    updateTravelerCount();
    selection.style.display = 'none';
});

document.addEventListener('click', function (e) {
    if (!selection.contains(e.target) && e.target !== traveler) {
        selection.style.display = 'none';
    }
});

selection.addEventListener('click', function (e) {
    e.stopPropagation();
});

incrementButtons.forEach(button => {
    button.addEventListener('click', function () {
        let totalTravelers = getTotalTravelers();
        if (totalTravelers < maxTravelers) {
            let countElement = button.previousElementSibling;
            countElement.textContent = parseInt(countElement.textContent) + 1;
        }

    });

    document.getElementsByName('arrivalName')[0].addEventListener('input', function() {
        query = this.value;
        if (query.length >= 1) {
            searchAirports(query, 'arrival-dropdown');
        } else {
            document.getElementById('arrival-dropdown').style.display = 'none';
        }
    });

    // 드롭다운 외부 클릭 시 숨김 처리
    document.addEventListener('click', function(event) {
        if (!event.target.closest('.dropdown-list') && !event.target.matches('input')) {
            document.getElementById('departure-dropdown').style.display = 'none';
            document.getElementById('arrival-dropdown').style.display = 'none';
        }
    });

    document.querySelector('.apply').addEventListener('click', () => {
        updateWarningMessage();
    });

	document.addEventListener('DOMContentLoaded', function() {
	    const moreButton = document.querySelector('#more');
	    const flightItems = document.querySelectorAll('.flight-item'); // 모든 항목

	    // 초기 상태: 5개 이후 항목 숨기기
	    let initialLimit = 5;
	    let index = 0;
	    for (const item of flightItems) {
	        if (index >= initialLimit) {
	            item.classList.add('hidden');
	        }
	        index++;
	    }

	    moreButton.addEventListener('click', function() {
	        const hiddenItems = document.querySelectorAll('.flight-item.hidden');

	        if (hiddenItems.length > 0) {
	            // 숨겨진 항목이 있다면 → 모두 표시
	            for (const item of flightItems) {
	                item.classList.remove('hidden');
	            }
	            this.innerText = '숨기기 ↑'; // 버튼 텍스트 변경
	        } else {
	            // 이미 모두 보이는 경우 → 다시 5개까지만 표시
	            index = 0;
	            for (const item of flightItems) {
	                item.classList.toggle('hidden', index >= initialLimit);
	                index++;
	            }
	            this.innerText = '더보기 ↓'; // 버튼 텍스트 변경
	        }
	    });
	});

	/*<![CDATA[*/
	   var prices = /*[[${flightsOffers.![price]}]]*/ []; // 가격 리스트 가져오기

	   if (prices.length > 0) {
	       var minPrice = Math.min.apply(null, prices); // 최솟값 계산
	       var maxPrice = Math.max.apply(null, prices); // 최댓값 계산

	       document.getElementById("price-range").min = minPrice;
	       document.getElementById("price-range").max = maxPrice;
	       document.getElementById("price-range").value = minPrice;
	       document.getElementById("min-price").textContent = minPrice;
	       document.getElementById("max-price").textContent = maxPrice;
	   }
	   /*]]>*/





});

decrementButtons.forEach(button => {
    button.addEventListener('click', function () {
        let countElement = button.nextElementSibling;
        let currentCount = parseInt(countElement.textContent);
        if (currentCount > 0) {
            countElement.textContent = currentCount - 1;
        }
        updateWarningMessage();
    });
});

resetBtn.addEventListener('click', function () {
    countElements.forEach(countElement => {
        countElement.textContent = '0';
    });
    updateTravelerCount();
});

function getTotalTravelers() {
    return Array.from(countElements).reduce((total, el) => total + parseInt(el.textContent), 0);
}

function updateTravelerCount() {
    let totalTravelers = getTotalTravelers();
    traveler.textContent = `여행자 ${totalTravelers}명`;
    document.querySelector('input[name="travelers"]').value = totalTravelers;
}

function updateWarningMessage() {
    let totalTravelers = getTotalTravelers();
    warningMessage.style.display = totalTravelers >= maxTravelers ? 'block' : 'none';
}

function searchAirports(query, dropdownId) {
    $.ajax({
        url: '/flight/search',
        data: {query: query},
        dataType: 'json',
        success: data => {
            const dropdown = document.getElementById(dropdownId);
            dropdown.innerHTML = ''; // 기존 목록 초기화
            if (data.length > 0) {
                data.forEach(airport => {
                    const li = document.createElement('li');
//                    li.textContent = `${airport.countryKoName}, ${airport.cityKoName}`; // 표시할 값
//					li.textContent += `${airport.airportsKoName} (${airport.iataCode})`;
					li.innerHTML = `${airport.korCountryName}, ${airport.korCityName}<br>${airport.korAirportName} (${airport.iataCode})`;
                    li.dataset.korAirportName = airport.korAirportName + '(' + airport.iataCode + ')'; // 클릭 시 사용
                    li.addEventListener('click', function () {
                        const input = document.getElementsByName(dropdownId.includes('departure') ? 'departureName' : 'arrivalName')[0];
                        input.innerText = airport.korAirportName + '(' + airport.iataCode + ')';
                        input.value = airport.korAirportName + '(' + airport.iataCode + ')'; // 공항 이름 + IATA 코드 입력
                        dropdown.style.display = 'none';
                    });
                    dropdown.appendChild(li);
                });
                dropdown.style.display = 'block';
            } else {
                dropdown.style.display = 'none';
            }
        },
        error: () => console.log("공항 검색 실패")
    });
}

document.getElementsByName('departureName')[0].addEventListener('input', function () {
    query = this.value;
    if (query.length >= 1) {
		setTimeout(() => {
        	searchAirports(query, 'departure-dropdown');
		}, 1000);
    } else {
        document.getElementById('departure-dropdown').style.display = 'none';
    }
});

document.getElementsByName('arrivalName')[0].addEventListener('input', function () {
    query = this.value;
    if (query.length >= 1) {
		setTimeout(()=>{
        	searchAirports(query, 'arrival-dropdown');
		}, 1000);
    } else {
        document.getElementById('arrival-dropdown').style.display = 'none';
    }
});

// 드롭다운 외부 클릭 시 숨김 처리
document.addEventListener('click', function (event) {
    if (!event.target.closest('.dropdown-list') && !event.target.matches('input')) {
        document.getElementById('departure-dropdown').style.display = 'none';
        document.getElementById('arrival-dropdown').style.display = 'none';
    }
});

document.querySelector('.search-btn').addEventListener('click', () => {
    const form = document.querySelector('.search-form');
    form.action = '/flight/flightSearch';
    form.submit();

});