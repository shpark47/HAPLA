// ✈️ 여행 일정 모달 열기
function openTripPlanModal() {
    document.getElementById("tripPlanModal").classList.remove("hidden");
}

// ❌ 여행 일정 모달 닫기
function closeTripPlanModal() {
    document.getElementById("tripPlanModal").classList.add("hidden");
}

// 🖱️ 여행 일정 모달 외부 클릭 시 닫기
document.addEventListener("click", function (event) {
    const modal = document.getElementById("tripPlanModal");
    if (!modal.classList.contains("hidden") && !modal.contains(event.target) && event.target.innerText !== "여행 일정") {
        closeTripPlanModal();
    }

    const modal2 = document.getElementById("citySearchModal");
    console.log(event.target.innerText !== "일정 생성")
    if (!modal2.classList.contains("hidden") && !modal2.contains(event.target) && event.target.innerText !== "여행 일정" && event.target.innerText !== "일정 생성") {
        closeCitySearchModal();
    }
});

// 🌍 도시 검색 모달 열기
function openCitySearchModal() {
	closeTripPlanModal();
    document.getElementById("citySearchModal").classList.remove("hidden");
    //filterCities();
}

// ❌ 도시 검색 모달 닫기
function closeCitySearchModal() {
    document.getElementById("citySearchModal").classList.add("hidden");
}

// 일정 목록 버튼 클릭 시 페이지 이동
document.addEventListener("DOMContentLoaded", function () {
    const planListBtn = document.getElementById("planListBtn");

    if (planListBtn) {
        planListBtn.addEventListener("click", function () {
            console.log("📋 일정 목록 페이지로 이동");
            window.location.href = "/schedule/scheduleList"; 
        });
    }
});

// 🌍 Google Places API 자동완성 검색 적용
function initCitySearch() {
    const input = document.getElementById("searchInput");

    // 브라우저 자동완성 끄기
    input.setAttribute("autocomplete", "off");

    const autocomplete = new google.maps.places.Autocomplete(input, {
        types: ["geocode"], // 도시 자동완성 최소화
        fields: ["name", "formatted_address", "geometry"],
    });

    // ✅ 도시 선택 시 이벤트
    autocomplete.addListener("place_changed", function () {
        const place = autocomplete.getPlace();
        if (!place.geometry) {
            console.error("도시 정보를 찾을 수 없습니다.");
            return;
        }

        let addressParts = place.formatted_address.split(", "); // 🔥 주소를 배열로 변환
        let cityName = addressParts[0]; // 🔥 첫 번째 요소만 저장 (지역명)

        // ✅ 국가명 없이 지역명만 저장하여 넘기기
        selectCity({
            name: cityName,
            lat: place.geometry.location.lat(),
            lng: place.geometry.location.lng(),
        });
    });

    // 검색어 입력 이벤트 추가 (인기 도시 리스트 변경)
    input.addEventListener("input", function () {
        const searchTerm = input.value.trim();
        if (searchTerm === "") {
            displayCityList(); // 검색어가 없으면 다시 인기 도시 출력
        } else {
            filterCities(searchTerm);
        }
    });
}

// ✅ 인기 도시 리스트
const popularCities = [
    { name: "서울", country: "대한민국", lat: 37.5665, lng: 126.9780 },
    { name: "부산", country: "대한민국", lat: 35.1796, lng: 129.0756 },
    { name: "제주", country: "대한민국", lat: 33.4996, lng: 126.5312 },
    { name: "오사카", country: "일본", lat: 34.6937, lng: 135.5023 },
    { name: "도쿄", country: "일본", lat: 35.682839, lng: 139.759455 },
    { name: "방콕", country: "태국", lat: 13.7563, lng: 100.5018 },
];

// 🌆 기본 도시 리스트 출력
function displayCityList() {
    const cityList = document.getElementById("cityList");
    cityList.innerHTML = ""; // 기존 리스트 초기화

    popularCities.forEach((city) => {
        const li = createCityListItem(city);
        cityList.appendChild(li);
    });
}

// 🔍 도시 검색 기능 (검색된 결과 출력)
function filterCities(searchTerm) {
    const service = new google.maps.places.PlacesService(document.createElement("div"));

    const request = {
        query: searchTerm,
        fields: ["name", "formatted_address", "geometry"],
    };

    service.findPlaceFromQuery(request, function (results, status) {
        if (status === google.maps.places.PlacesServiceStatus.OK) {
            const cityList = document.getElementById("cityList");
            cityList.innerHTML = ""; // 기존 리스트 초기화

            results.forEach((place) => {
                if (!place.geometry) return;

                const city = {
                    name: place.name,
                    country: place.formatted_address.split(", ").pop(),
                    lat: place.geometry.location.lat(),
                    lng: place.geometry.location.lng(),
                };

                const li = createCityListItem(city);
                cityList.appendChild(li);
            });
        }
    });
}

// 🌍 도시 리스트 아이템 생성 함수
function createCityListItem(city) {
    const li = document.createElement("li");

    // 📍 아이콘 추가
    const icon = document.createElement("span");
    icon.classList.add("city-icon");
    icon.textContent = "📍";

    // 🌍 도시 이름
    const cityName = document.createElement("span");
    cityName.textContent = city.name;
    cityName.style.fontWeight = "bold";
    cityName.style.marginRight = "10px";

    // 🌎 국가 이름
    const countryName = document.createElement("span");
    countryName.classList.add("country-name");
    countryName.textContent = city.country;

    li.appendChild(icon);
    li.appendChild(cityName);
    li.appendChild(countryName);

    li.onclick = function () {
        selectCity(city);
    };

    return li;
}

// ✅ 도시 선택 시 실행되는 함수 (도시 정보 저장 & 페이지 이동)
function selectCity(city) {
    localStorage.setItem("selectedCity", JSON.stringify(city));
    console.log(`📍 선택한 도시: ${city.name}, ${city.country}`);

    // ✅ 일정 페이지로 이동 (기본 페이지가 /schedule/scheduleCalendar 인 경우)
    window.location.href = "/schedule/scheduleCalendar";
}

// ✅ 페이지 로드 후 Google Places API 초기화 실행
window.onload = function () {
    initCitySearch();
    displayCityList();
};
