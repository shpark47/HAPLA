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
	document.addEventListener("DOMContentLoaded", function() {
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
		// 모든 사이드 패널을 숨깁니다.
		const allPanels = document.querySelectorAll('.side-panel');
		allPanels.forEach(panel => panel.classList.add('hidden')); // 모든 패널 숨기기
	
		// 클릭된 패널만 보이게 합니다.
		const panel = document.getElementById(panelType + '-panel');
		if (panel) {
			panel.classList.remove('hidden'); // 해당 패널 보이게 설정
		}
	
		// +추가 버튼 숨기고, control-btns 보이게 처리
	
		const controlBtns = dateItem.querySelector('.control-btns');
		const controlAdd = dateItem.querySelector('.control-add');
		controlBtns.hidden = false;
		controlAdd.hidden = true;
	}
	
	// 사이드 패널 열기 함수
	function openSidePanel(panelType) {
	
		const memoPanel = document.getElementById('memo-panel');
		const stayPanel = document.getElementById('stay-panel');
		const markerPanel = document.getElementById('marker-panel');
	
		// 모든 하위 패널 숨기기
		memoPanel.style.display = 'none';
		stayPanel.style.display = 'none';
		markerPanel.style.display = 'none';
	
		// panelType에 따라 해당 패널만 보이게 설정
		if (panelType === 'memo') {
			memoPanel.style.display = 'block';
		} else if (panelType === 'stay') {
			stayPanel.style.display = 'block';
		} else if (panelType === 'marker') {
			markerPanel.style.display = 'block';
		}
	
		// 사이드 패널 컨테이너 보이기
		/*sidePanel.style.display = 'block';*/
	}
	
	// 사이드 패널 닫기 함수
	function closeSidePanel() {
	
		sidePanel.style.display = 'none';
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
//	function initPlaceSearch() {
//		console.log("initPlaceSearch 실행됨!");
//
//	    // 브라우저 자동완성 끄기
//	    input.setAttribute("autocomplete", "off");
//
//	    const autocomplete = new google.maps.places.Autocomplete(input, {
//	        // 도시 제한 대신 모든 장소 검색 (필요 시 types 옵션 추가 가능)
//	        fields: ["name", "formatted_address", "geometry", "types"],
//	    });
//
//	    // ✅ 장소 선택 시 이벤트
//	    autocomplete.addEventListener("place_changed", function () {
//	        const place = autocomplete.getPlace();
//	        if (!place.geometry || !place.formatted_address) {
//	            console.error("장소 정보를 찾을 수 없습니다.");
//	            return;
//	        }
//	        selectPlace({
//	            name: place.name,
//	            address: place.formatted_address,
//	            lat: place.geometry.location.lat(),
//	            lng: place.geometry.location.lng(),
//	        });
//	    }); 
//	}
	
	// 검색어 입력 이벤트 (인기 장소 리스트 또는 검색 결과 변경)
	    input.addEventListener("input", function () {
			console.log("121111111111111111111");
	        const searchTerm = input.value.trim();
	        if (searchTerm == "") {
	            displayPlaceList(); // 검색어가 없으면 인기 장소 출력
	        } else {
	            filterPlaces(searchTerm);
	        }
	    });
		
	// ✅ 인기 장소 리스트 (예시)
	const popularPlaces = [
	    { name: "롯데월드타워", address: "서울, 대한민국", lat: 37.512, lng: 127.102 },
	    { name: "에펠탑", address: "파리, 프랑스", lat: 48.8584, lng: 2.2945 },
	];

	// 🌆 기본 장소 리스트 출력
	function displayPlaceList() {
	    const resultsList = document.getElementById("search-results");
	    resultsList.innerHTML = ""; // 기존 리스트 초기화

	    popularPlaces.forEach((place) => {
	        const li = createPlaceListItem(place);
	        resultsList.appendChild(li);
	    });
	}

	// 🔍 장소 검색 기능 (검색 예측 결과 출력)
	function filterPlaces(searchTerm) {
	    const autocompleteService = new google.maps.places.AutocompleteService();

	    autocompleteService.getPlacePredictions({
	        input: searchTerm,
	        // 모든 장소 검색: types 옵션 생략 또는 필요에 따라 추가
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
	                fields: ["name", "formatted_address", "geometry"]
	            }, function (place, status) {
	                if (status !== google.maps.places.PlacesServiceStatus.OK || !place.geometry) {
	                    console.error("장소 상세 정보를 가져오지 못했습니다.");
	                    return;
	                }
	                selectPlace({
	                    name: place.name,
	                    address: place.formatted_address,
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

	// 선택한 장소 처리 함수
	function selectPlace(place) {
	    // 예시: 선택한 장소 정보를 콘솔에 출력하거나, 지도 업데이트 등 원하는 동작을 수행합니다.
	    console.log("선택한 장소:", place);
	    // 추가 동작을 여기에 구현하세요.
	}
	
	
	// 메뉴바 선택시 일정 목록으로 페이지 이동
	document.addEventListener("DOMContentLoaded", function() {
		const menuBtn = document.getElementById("menuBtn");
		console.log(menuBtn);
		console.log(menuBtn);
	
		if (menuBtn) {
			menuBtn.addEventListener("click", function() {
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
	
	
