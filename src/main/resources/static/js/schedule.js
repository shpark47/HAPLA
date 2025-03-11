// ✅ 일정 페이지가 로드될 때 실행되는 함수
    function initMap() {
        // ✅ localStorage에서 선택한 도시 정보 가져오기
        const storedCity = localStorage.getItem("selectedCity");

        // 기본 지도 위치 설정 (초기값: 파리)
        let mapOptions = {
            center: { lat: 37.5665, lng: 126.9780 }, // 서울 기본 위치
            zoom: 10
        };

        // ✅ 저장된 도시 정보가 있으면 지도 위치 변경
        if (storedCity) {
            const city = JSON.parse(storedCity);
            mapOptions.center = { lat: city.lat, lng: city.lng }; // 선택한 도시 좌표로 이동
            console.log(`📍 지도 위치 변경: ${city.name}, ${city.country}`);
        }

        // ✅ Google 지도 생성
        new google.maps.Map(document.getElementById('map'), mapOptions);
    }
	
	// ✅ 페이지 로드 시 `initMap()` 실행
	document.addEventListener("DOMContentLoaded", function () {
	    initMap();
	});

// +추가 버튼 클릭시 control-btns 보이게 처리
function toggleControls(addButton){
	// 해당 .date-item을 찾기
	const dateItem = addButton.closest('.date-item');
	
	// control-btns와 control-add 찾기
	const controlBtns = dateItem.querySelector('.control-btns');
	const controlAdd = dateItem.querySelector('.control-add');
	
	// control-btns 보이게하고 control-add 숨김
	controlBtns.hidden = false;
	controlAdd.hidden = true;
}


// ✅ 사이드 패널 열기 (숙박 & 장소 검색)
function openSidePanel(panelType) {
    const sidePanel = document.getElementById('side-panel');
    const panelBody = document.getElementById('side-panel-body');
    const searchContainer = document.getElementById("search-container"); // 검색 컨테이너

    if (!sidePanel) {
        console.error("❌ 사이드 패널 요소를 찾을 수 없습니다.");
        return;
    }

    console.log(`🔵 사이드 패널 열기: ${panelType}`);

    // 패널 내용 변경
    if (panelType === 'stay') {
        panelBody.innerHTML = "<p>숙박 정보를 검색하세요.</p>";
    } else if (panelType === 'marker') {
        panelBody.innerHTML = "<p>장소를 검색하세요.</p>";
    }

    // ✅ `hidden` 대신 `display = 'block'`으로 변경
    sidePanel.style.display = 'block';

    // 검색 컨테이너 보이기
    if (searchContainer) {
        searchContainer.style.display = 'block';
    } else {
        console.warn("⚠️ search-container를 찾을 수 없습니다.");
    }
}

// ✅ 사이드 패널 닫기
function closeSidePanel() {
    const sidePanel = document.getElementById("side-panel");
    if (sidePanel) {
        console.log("🔴 사이드 패널 닫기");
        sidePanel.style.display = "none"; // `hidden`이 아닌 `none`으로 변경
    }
}


// Close 버튼 클릭 시, +추가 버튼으로 돌아가게 처리
function closeControls() {
    const dateItem = event.target.closest('.date-item');  // closest()를 이용해 해당 날짜 항목 찾기
    const controlBtns = dateItem.querySelector('.control-btns');
    const controlAdd = dateItem.querySelector('.control-add');
	const sidePanel = document.getElementById("side-panel");

    // .control-btns 숨기기, .add-text 보이게 하기
    controlBtns.hidden = true;
    controlAdd.hidden = false;
	sidePanel.style.display = "none";	// 사이드 패널 닫기
	
}



// Google Places API로 장소 검색(자동 검색)
function searchPlaces(){
	const searchInput = document.getElementById("search-input").value;
	const resultsContainer = document.getElementById("search-results");
	
	if(!searchInput.trim()){
		resultsContainer.innerHTML = "<p>검색어를 입력하세요.</p>";
		return;
	}
	
	console.log(`🔎 검색 실행: ${searchInput}`);
	
	const service = new google.maps.places.PlacesService(document.createElement("div"));

    service.textSearch({ query: searchInput }, function (results, status) {
        resultsContainer.innerHTML = ""; // 기존 결과 초기화

        if (status !== google.maps.places.PlacesServiceStatus.OK || !results.length) {
            resultsContainer.innerHTML = "<p>검색 결과가 없습니다.</p>";
            return;
        }

        results.forEach(place => {
            const placeItem = document.createElement("div");
            placeItem.classList.add("search-result-item");
            placeItem.textContent = place.name;
            placeItem.onclick = function () {
                addPlaceToSchedule(place);
            };
            resultsContainer.appendChild(placeItem);
        });
    });
}

// ✅ 장소를 일정에 추가
function addPlaceToSchedule(place) {
    console.log("선택한 장소:", place.name);
    alert(`선택한 장소: ${place.name}이 일정에 추가되었습니다.`);
}

// ✅ 검색 입력 시 자동으로 검색 실행
document.addEventListener("DOMContentLoaded", function () {
    const searchInputField = document.getElementById("search-input");
    if (searchInputField) {
        searchInputField.addEventListener("input", searchPlaces);
        console.log("✅ 검색 이벤트 리스너 추가됨");
    } else {
        console.error("❌ 검색 입력 필드를 찾을 수 없습니다.");
    }

    // 버튼 이벤트 확인
    document.getElementById("stayBtn")?.addEventListener("click", function () {
        openSidePanel('stay');
    });

    document.getElementById("markerBtn")?.addEventListener("click", function () {
        openSidePanel('marker');
    });
});


        // 메뉴바 선택시 일정 목록으로 페이지 이동
        document.addEventListener("DOMContentLoaded", function(){
           const menuBtn = document.getElementById("menuBtn");
           console.log(menuBtn);
           console.log(menuBtn);
           
           if(menuBtn){
              menuBtn.addEventListener("click", function(){
              console.log("일정 목록 페이지로 이동")
              window.location.href = "/schedule/list";
           });
        }
        });
/*        // ✅ 날짜별 일정 리스트 생성
        function generateDateList(start, end) {
            const dateList = document.getElementById("dateList");
            dateList.innerHTML = ""; // 기존 내용 초기화

            let startDate = new Date(start);
            let endDate = new Date(end);

            while (startDate <= endDate) {
                let formattedDate = startDate.toISOString().split('T')[0];

                let dateContainer = document.createElement("div");
                dateContainer.classList.add("date-container");

                let planDate = document.createElement("div");
                planDate.classList.add("plan-date");
                planDate.textContent = formattedDate; // YYYY-MM-DD 형식으로 출력

                let dateItem = document.createElement("div");
                dateItem.classList.add("date-item");

                let addButton = document.createElement("button");
                addButton.classList.add("control-add");
                addButton.textContent = "➕ 추가";

                dateItem.appendChild(addButton);
                dateContainer.appendChild(planDate);
                dateContainer.appendChild(dateItem);
                dateList.appendChild(dateContainer);
*/

      // 검색 입력 필드 추가 및 Google Place API 연동 가능
      function openSearchInput(parentElement){
         if(!parentElement.querySelector(".search-input")){
            let inputField = document.createElement("input");
            inputField.setAttribute("type", "text");
            inputField.setAttribute("placeholder", "장소 검색...");
            inputField.classList.add("search-input");
            
            let searchButton = document.createElement("button");
            searchButton.textContent = "검색";
            searchButton.classList.add("search-button");
            
            parentElement.appendChild(inputField);
            parentElement.appendChild(searchButton);
            
            // Google Places API 사용 가능(추가 구현 가능)
            searchButton.addEventListener("click", function(){
               let query = inputField.value;
               if(query){
                  console.log('검색어 : ${query}');
                  // Google Place API 연동 가능(추후 추가)
               }
            });
      
         }
      }
      
      
