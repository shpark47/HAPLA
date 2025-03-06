document.addEventListener("DOMContentLoaded", function () {
    const tripData = localStorage.getItem("tripData");

    if (tripData) {
        const data = JSON.parse(tripData);

        if (data.city) {
            document.getElementById("tripTitle").textContent = `${data.city.name}, ${data.city.country}`;
        }

        document.getElementById("tripDates").textContent = `📅 ${data.startDate} ~ ${data.endDate}`;

        generateDateList(data.startDate, data.endDate);
        initMap(data.city.lat, data.city.lng);
    }

    // ✅ 패널 닫기 버튼 기능 추가
    document.querySelector(".close-btn").addEventListener("click", function () {
        document.getElementById("side-panel").classList.add("hidden"); // 패널 닫기
        document.getElementById("search-container").classList.add("hidden"); // 검색 UI 숨김
    });
});

// ✅ 날짜별 일정 리스트 생성
function generateDateList(start, end) {
    const dateList = document.getElementById("dateList");
    dateList.innerHTML = "";

    let startDate = new Date(start);
    let endDate = new Date(end);

    while (startDate <= endDate) {
        let formattedDate = startDate.toISOString().split('T')[0];

        let dateContainer = document.createElement("div");
        dateContainer.classList.add("date-container");

        let planDate = document.createElement("div");
        planDate.classList.add("plan-date");
        planDate.textContent = formattedDate;

        let dateItem = document.createElement("div");
        dateItem.classList.add("date-item");

        let addButton = document.createElement("button");
        addButton.classList.add("control-add");
        addButton.textContent = "➕ 추가";

        dateItem.appendChild(addButton);
        dateContainer.appendChild(planDate);
        dateContainer.appendChild(dateItem);
        dateList.appendChild(dateContainer);

        // 📍 추가 버튼 클릭 시 장소 검색 아이콘 생성
        addButton.addEventListener("click", function () {
            if (!dateItem.querySelector(".search-icon")) {
                let searchIcon = document.createElement("span");
                searchIcon.classList.add("search-icon");
                searchIcon.innerHTML = "🔍";
                dateItem.appendChild(searchIcon);

                // 🔍 아이콘 클릭 시 사이드 패널 열기
                searchIcon.addEventListener("click", function () {
                    console.log("🔍 아이콘 클릭됨! 사이드 패널 열림");
                    document.getElementById("side-panel").classList.remove("hidden"); // 패널 보이기
                    document.getElementById("search-container").classList.remove("hidden"); // 검색 UI 보이기
                });
            }
        });

        startDate.setDate(startDate.getDate() + 1);
    }
}

// ✅ Google 지도 초기화 함수
function initMap(lat = 48.8566, lng = 2.3522) {
    new google.maps.Map(document.getElementById("map"), {
        center: { lat: lat, lng: lng },
        zoom: 10,
    });
}
