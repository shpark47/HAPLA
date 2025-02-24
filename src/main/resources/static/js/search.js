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
let selectedCategory = "여행지+관광명소+숙박+음식점"; // 기본 카테고리

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

// 장소 검색 함수 (서버로 요청을 보냄)
async function searchPlaces() {
    let city = document.querySelector('#search-input-text').value;
    if (!city) {
        alert("도시명을 입력하세요.");
        return;
    }

    // 서버로 도시 이름과 선택된 카테고리 보내기
    let response = await fetch(`/search?city=${encodeURIComponent(city)}&category=${selectedCategory}`);
    let data = await response.json();
    console.log(data);

    // 서버 응답이 성공적이면 장소 표시
    if (data.status === "OK") {
        displayPlaces(data.results);
    } else {
        alert("검색 결과가 없습니다.");
    }
}

function displayPlaces(places) {
    let container = document.querySelector(".destinations-grid");
    container.innerHTML = "";

    places.forEach(place => {
        console.log(place.photos ? place.photos[0].photo_reference : 'No photo available');

        let name = place.name || "이름 없음";  // 이름이 없는 경우 처리
        let rating = place.rating ? `⭐ ${place.rating}` : "⭐ 없음";
        let reviews = place.user_ratings_total ? `${place.user_ratings_total} 리뷰` : "리뷰 없음";

        // 사진 URL 생성: Google Places API의 photo 엔드포인트를 통해 사진을 가져옵니다.
        let photoUrl = place.photos && place.photos[0].photo_reference ?
            `https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${place.photos[0].photo_reference}&key=AIzaSyCEHjTtVqBclz07ADqbkGjqGIe94Cq-S60` :
            "/img/시나모롤.jpg";  // 기본 이미지

        let div = document.createElement("div");
        div.className = "destination-card";
        div.innerHTML = `
            <img src="${photoUrl}" alt="${name}">
            <div class="destination-info">
            <h3>${name}</h3>
            <p class="rating">${rating}</p>
            <p class="review-count">${reviews}</p>
            <h5>${place.place_id}</h5>
            </div>
        `;

        // 카드 클릭 시 상세 페이지로 이동
        div.addEventListener("click", () => {
            let encodedPlaceId = encodeURIComponent(place.place_id);
            location.href = `/detail/${encodedPlaceId}`;
        });

        container.appendChild(div);
    });
}


// 항공 -----------------------------------------------------------------------------

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
    closeOnSelect: false,
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

                applyButton.addEventListener("click", function () {
                    const selectedDates = instance.selectedDates;
                    if (selectedDates.length === 2) {
                        const startDate = selectedDates[0].toLocaleDateString('ko-KR', {month: 'long', day: 'numeric'});
                        const endDate = selectedDates[1].toLocaleDateString('ko-KR', {month: 'long', day: 'numeric'});
                        datePickerInput.value = `${startDate} → ${endDate}`;
                        instance.close();
                    }
                });

                instance.calendarContainer.appendChild(applyButton);
            }

            instance.calendarContainer.style.cssText += `
                    padding-top: 70px;
                    border-radius: 8px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                `;

            fpInitialized = true;
        }
    },
    onChange: function (selectedDates, dateStr, instance) {
        const titleDiv = instance.calendarContainer.querySelector(".calendar-title");
        if (titleDiv) {
            updateTitle(selectedDates, titleDiv);
        }
    }
});

function updateTitle(selectedDates, titleDiv) {
    if (selectedDates.length === 2) {
        const startDate = selectedDates[0].toLocaleDateString('ko-KR', {month: 'long', day: 'numeric'});
        const endDate = selectedDates[1].toLocaleDateString('ko-KR', {month: 'long', day: 'numeric'});
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
        updateWarningMessage();
    });
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
                    li.textContent = `${airport.airportsKoName} (${airport.iataCode})`; // 표시할 값
                    li.dataset.airportsKoName = airport.airportsKoName + '(' + airport.iataCode + ')'; // 클릭 시 사용
                    li.addEventListener('click', function () {
                        const input = document.getElementsByName(dropdownId.includes('departure') ? 'departureName' : 'arrivalName')[0];
                        input.innerText = airport.airportsKoName + '(' + airport.iataCode + ')';
                        input.value = airport.airportsKoName + '(' + airport.iataCode + ')'; // 공항 이름 + IATA 코드 입력
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
    const query = this.value;
    if (query.length >= 1) {
        searchAirports(query, 'departure-dropdown');
    } else {
        document.getElementById('departure-dropdown').style.display = 'none';
    }
});

document.getElementsByName('arrivalName')[0].addEventListener('input', function () {
    const query = this.value;
    if (query.length >= 1) {
        searchAirports(query, 'arrival-dropdown');
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