let mapOptions;
let lo;
let p;
let place;

// ✅ 일정 페이지가 로드될 때 실행되는 함수
function initMap() {
    // ✅ localStorage에서 선택한 도시 정보 가져오기
    const storedCity = localStorage.getItem("selectedCity");

    // 기본 지도 위치 설정 (초기값: 파리)
    mapOptions = {
        center: {lat: 37.5665, lng: 126.9780}, // 서울 기본 위치
        zoom: 10
    };

    // ✅ 저장된 도시 정보가 있으면 지도 위치 변경
    if (storedCity) {
        const city = JSON.parse(storedCity);

        mapOptions.center = {lat: city.lat, lng: city.lng}; // 선택한 도시 좌표로 이동
        lo = mapOptions.center;
        place = city.name;
        console.log(`📍 지도 위치 변경: ${city.name}, ${city.country}`);
    }

    // ✅ Google 지도 생성( 전역 변수 map에 할당)
    map = new google.maps.Map(document.getElementById('map'), mapOptions);
}


// ✅ 페이지 로드 시 `initMap()` 실행
document.addEventListener("DOMContentLoaded", function () {
    initMap();
});


const dateItem = document.querySelector('.date-item');  // 해당 날짜 항목 찾기
const sidePanel = document.getElementsByClassName('side-panel');

// +추가 버튼 클릭시 control-btns 보이게 처리
function toggleControls(addButton) {
    // 해당 .date-item을 찾기
    const dateItem = addButton.closest('.date-item');

    // control-btns와 control-add 찾기
    const controlBtns = dateItem.querySelector('.control-btns');
    const controlAdd = dateItem.querySelector('.control-add');

    // control-btns 보이게하고 control-add 숨김
    controlBtns.hidden = false;
    controlAdd.hidden = true;
}


// 사이드 패널 열기 함수
function openSidePanel(panelType) {

    const memoPanel = document.getElementById('memo-panel');
    const stayPanel = document.getElementById('stay-panel');
    //		const markerPanel = document.getElementById('marker-panel');

    // 모든 하위 패널 숨기기
    memoPanel.style.display = 'none';
    stayPanel.style.display = 'none';
    //markerPanel.style.display = 'none';

    // panelType에 따라 해당 패널만 보이게 설정
    if (panelType == 'memo') {
        memoPanel.style.display = 'block';
        p=0;
    } else if (panelType == 'stay') {
        stayPanel.style.display = 'block';
        p=1;
    }
    displayPlaceList(p, place);
}

// Close 버튼 클릭 시, +추가 버튼으로 돌아가게 처리
function closeControls(event) {
    const dateItem = event.target.closest('.date-item');  // 해당 날짜 항목 찾기
    const controlBtns = dateItem.querySelector('.control-btns');
    const controlAdd = dateItem.querySelector('.control-add');
    //		const sidePanels = document.getElementsByClassName("side-panel");
    const sidePanels = document.querySelectorAll(".side-panel")

    // ✅ 모든 사이드 패널을 닫기
    sidePanels.forEach(panel => {
        panel.style.display = "none"; // ✅ 패널이 정상적으로 선택되었는지 확인
    });

    // control-btns 숨기고 control-add 보이게 하기
    controlBtns.hidden = true;
    controlAdd.hidden = false;
    sidePanels.style.display = "none";
}


const input = document.getElementById("searchInput");

// 🌍 Google Places API 자동완성 장소 검색 적용
function initPlaceSearch() {
    console.log("initPlaceSearch 실행됨!");

    // 브라우저 자동완성 끄기
    input.setAttribute("autocomplete", "off");

    const autocomplete = new google.maps.places.Autocomplete(input, {
        types: ['establishment'] // 장소(업체)만 검색, 지역(주소) 정보는 제외
    });

    // ✅ 장소 선택 시 이벤트
    autocomplete.addEventListener("place_changed", function () {
        const place = autocomplete.getPlace();
        if (!place.geometry) {
            /*	        if (!place.geometry || !place.formatted_address) {*/
            console.error("장소 정보를 찾을 수 없습니다.");
            return;
        }
        selectPlace({
            name: place.name,
            placeId: place.place_id,
            lat: place.geometry.location.lat(),
            lng: place.geometry.location.lng(),
        });
    });
}

// 검색어 입력 이벤트 (인기 장소 리스트 또는 검색 결과 변경)
input.addEventListener("input", function () {
    const searchTerm = input.value.trim();
    if (input && input.value.trim() == "") {
        displayPlaceList(p, place); // 검색어가 없으면 인기 장소 출력
    } else {
        console.log('input!');
        filterPlaces(searchTerm);
    }
});


// 🌆 기본 장소 리스트 출력
function displayPlaceList(p, place) {
    filterPlaces(place)
    sidePanel[p].style.display = 'block';
}


// 🔍 장소 검색 기능 (검색 예측 결과 출력)
function filterPlaces(searchTerm) {
    const autocompleteService = new google.maps.places.AutocompleteService();

    autocompleteService.getPlacePredictions({
        input: searchTerm,
        rankby: 30000,
        types: ['tourist_attraction'] // 장소만 검색
    }, function (predictions, status) {
        const resultsList = document.getElementById("search-results");
        resultsList.innerHTML = ""; // 기존 리스트 초기화

        if (status !== google.maps.places.PlacesServiceStatus.OK || !predictions) {
            console.error("장소 검색 결과가 없습니다.");
            return;
        }

        predictions.forEach(function (prediction) {
            const li = createPlaceListItem(prediction);
            resultsList.appendChild(li);
        });
    });
}

// 🌍 장소 리스트 아이템 생성 함수 (인기 장소와 검색 예측 모두 지원)
// 인기 장소 객체: { name, address, lat, lng }
// 예측 객체: { description, place_id }
function createPlaceListItem(item) {
    const li = document.createElement("li");
    li.classList.add("place-item");

    // 📍 아이콘 추가
    const icon = document.createElement("span");
    icon.classList.add("place-icon");
    icon.textContent = "📍";

    // 장소 정보 텍스트
    const textSpan = document.createElement("span");
    textSpan.classList.add("place-text");

    if (item.description && item.place_id) {
        // 검색 예측 결과
        textSpan.textContent = item.description;
        li.onclick = function () {
            // place_id를 이용해 상세 정보를 가져옴
            const service = new google.maps.places.PlacesService(document.createElement("div"));
            service.getDetails({
                placeId: item.place_id,
                fields: ["name", "geometry", "place_id"] //vicinity : 짧은 주소
            }, function (place, status) {
                if (status !== google.maps.places.PlacesServiceStatus.OK || !place.geometry) {
                    console.error("장소 상세 정보를 가져오지 못했습니다.");
                    return;
                }
                selectPlace({
                    name: place.name,
                    placeId: place.place_id,
                    lat: place.geometry.location.lat(),
                    lng: place.geometry.location.lng(),
                });

            });
        };

    } else if (item.name && item.address) {
        // 인기 장소 객체
        textSpan.textContent = `${item.name}, ${item.address}`;
        li.onclick = function () {
            selectPlace(item);
        };
    }

    li.appendChild(icon);
    li.appendChild(textSpan);
    return li;
}

// 선택한 장소 사이드바 일정('addDetail)에 추가하기
function selectPlace(place) {
    console.log("선택한 장소:", place);

    // 현재 선택된 날짜('.date-item')찾기
    const activeDateItem = document.querySelector(".date-item.active"); // 현재 활성화된 날짜

    if (!activeDateItem) {
        alert("날짜를 먼저 선택하세요!");
        return;
    }

    // ✅ 선택한 날짜의 `addDetail` 요소 찾기 (없으면 생성)
    let addDetail = activeDateItem.querySelector(".addDetail");
    if (!addDetail) {
        addDetail = document.createElement("div");
        addDetail.classList.add("addDetail");
        activeDateItem.appendChild(addDetail); // `.date-item`에 추가
    }

    // ✅ 장소 정보 추가 (HTML 요소 생성)
    const placeItem = document.createElement("div");
    placeItem.classList.add("place-item");
    placeItem.innerHTML = `
	        <span class="place-name">${place.name}</span>
	        <button class="remove-btn" onclick="removePlace(this)">X</button>
	    `;

    // `addDetail`에 장소 추가
    addDetail.appendChild(placeItem);
}

// 장소 삭제 기능 추가
function removePlace(button) {
    button.parentElement.remove();	// 부모 요소 (`place-item`) 삭제
}

document.querySelectorAll(".date-item").forEach(item => {
    item.addEventListener("click", function () {
        // 기존 'active'제거
        document.querySelectorAll(".date-item").forEach(el => el.classList.remove("active"));

        // 클릭한 `date-item`에 `active` 추가
        this.classList.add("active");
    });
});

// 메뉴바 선택시 일정 목록으로 페이지 이동
document.addEventListener("DOMContentLoaded", function () {
    const menuBtn = document.getElementById("menuBtn");
    console.log(menuBtn);
    console.log(menuBtn);

    if (menuBtn) {
        menuBtn.addEventListener("click", function () {
            console.log("일정 목록 페이지로 이동")
            window.location.href = "/schedule/list";
        });
    }
});


