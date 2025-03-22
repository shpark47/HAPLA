let trip;
window.onload = () => {
	trip = window.trip;
	console.log("trip : " + trip.cityName);
}

		function initMap(){
			// 'tripTitle'요소에서 'data-city'속성 가져오기
			const cityElement = document.getElementById("tripTitle");
			const cityName = cityElement ? cityElement.getAttribute("data-city") : null;
			
			console.log("선택한 도시 : " + cityName);
			
			// 기본 위치(서울)
			let defaultLocation = { lat: 37.5665, lng:126.9780};
			
			// Google Maps Geocoder 생성 (도시명을 좌표로 변환)
		    const geocoder = new google.maps.Geocoder();

		    if (cityName) {
		        geocoder.geocode({ address: cityName }, function(results, status) {
		            if (status == "OK") {
		                defaultLocation = results[0].geometry.location;
		                console.log("📌 변환된 좌표:", defaultLocation);

		                // Google 지도 생성
		                const map = new google.maps.Map(document.getElementById("map"), {
		                    center: defaultLocation,
		                    zoom: 12
		                });

		                // 마커 추가
		                new google.maps.Marker({
		                    position: defaultLocation,
		                    map: map,
		                    title: cityName
		                });

		            } else {
		                console.error("📍 도시 좌표 변환 실패:", status);
		            }
		        });
		    } else {
		        console.warn("🚨 도시 정보 없음! 기본 위치 사용");

		        // Google 지도 기본값 (서울)
		        const map = new google.maps.Map(document.getElementById("map"), {
		            center: defaultLocation,
		            zoom: 12
		        });
		    }
		}		
		
		// Google Places API를 사용하여 placeId로 장소 이름 가져오는 함수
		function getPlaceNameById(placeId, callback) {
		    const service = new google.maps.places.PlacesService(document.createElement('div'));

		    const request = {
		        placeId: placeId,
		        fields: ['name'] // 장소 이름만 요청
		    };

		    service.getDetails(request, (place, status) => {
		        if (status === google.maps.places.PlacesServiceStatus.OK && place && place.name) {
		            callback(place.name); // 성공 시 이름 전달
		        } else {
		            callback("이름 불러오기 실패"); // 실패 시 대체 텍스트
		        }
		    });
		}
		
		// 날짜 범위(start ~ end) 기준으로 일정 리스트를 생성하는 함수
		function generateDateList(start, end) {
		    const dateList = document.getElementById("dateList"); // HTML에 있는 일정 출력 영역
		    dateList.innerHTML = ""; // 기존에 있던 일정들 초기화

		    let startDate = new Date(start); // 시작 날짜 객체
		    let endDate = new Date(end);     // 종료 날짜 객체

		    // 시작일부터 종료일까지 반복
		    while (startDate <= endDate) {
		        const formattedDate = startDate.toISOString().split("T")[0]; // 날짜 포맷: yyyy-MM-dd

		        // 이 날짜에 해당하는 detail 데이터 필터링
		        const dailyDetails = window.detailList.filter(d => d.selectDate === formattedDate);

		        // 해당 날짜의 placeId 목록 (중복 제거, null 제거)
		        const placeIds = [...new Set(dailyDetails.map(d => d.placeId).filter(Boolean))];

		        // 해당 날짜에 저장된 메모가 있으면 가져오기 (없으면 기본 메시지)
		        const memo = dailyDetails.find(d => d.content)?.content || "메모 없음";

		        // 일정 하나를 표시할 컨테이너 요소 생성
		        const dateContainer = document.createElement("div");
		        dateContainer.classList.add("date-container"); // 날짜별 일정 묶음

		        // 날짜 표시 요소
		        const planDate = document.createElement("div");
		        planDate.classList.add("plan-date");
		        planDate.textContent = formattedDate;

		        // 일정 내용 표시 영역
		        const dateItem = document.createElement("div");
		        dateItem.classList.add("date-item");

		        // 메모 / 장소 내용을 담는 div
		        const contentDiv = document.createElement("div");
		        contentDiv.classList.add("plan-content");

				// ✅ 메모 DOM 요소 먼저 만들어두기
				const memoEl = document.createElement("div");
				memoEl.innerHTML = `<strong>📝 메모:</strong> ${memo}`;
				contentDiv.appendChild(memoEl); // 메모는 마지막에 붙이기

				// ✅ 장소 먼저 삽입 (메모 위에)
				if (placeIds.length > 0) {
				    placeIds.forEach(placeId => {
				        getPlaceNameById(placeId, (placeName) => {
				            const placeEl = document.createElement("div");
				            placeEl.innerHTML = `<strong>📍 장소:</strong> ${placeName}`;
				            // 📍 장소를 메모 위에 삽입
				            contentDiv.insertBefore(placeEl, memoEl);
				        });
				    });
				} else {
				    const placeEl = document.createElement("div");
				    placeEl.innerHTML = `<strong>📍 장소:</strong> 없음`;
				    contentDiv.insertBefore(placeEl, memoEl); // 장소 없을 때도 메모 위로
				}

		        // 모든 요소 조립해서 DOM에 추가
		        dateItem.appendChild(contentDiv);
		        dateContainer.appendChild(planDate);
		        dateContainer.appendChild(dateItem);
		        dateList.appendChild(dateContainer);

		        // 다음 날짜로 이동
		        startDate.setDate(startDate.getDate() + 1);
		    }
		}
		
		document.addEventListener("DOMContentLoaded", function () {
			    const trip = window.trip;
			
			    if (trip && trip.startDate && trip.endDate) {
			        const start = new Date(trip.startDate);
			        const end = new Date(trip.endDate);
			        generateDateList(start, end);
			    }
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

		// 말풍선 메뉴 토글
		document.getElementById("editBtn").addEventListener("click", function (e) {
		    e.stopPropagation(); // 다른 클릭 막기
		    const menu = document.getElementById("editMenu");
		    menu.style.display = menu.style.display === "block" ? "none" : "block";
		});

		// 바깥 클릭하면 닫기
		window.addEventListener("click", function () {
		    const menu = document.getElementById("editMenu");
		    if (menu) menu.style.display = "none";
		});

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

		    // 모든 하위 패널 숨기기
		    memoPanel.style.display = 'none';
		    stayPanel.style.display = 'none';

		    // panelType에 따라 해당 패널만 보이게 설정
		    if (panelType == 'memo') {
		        memoPanel.style.display = 'block';
		        p = 0;
		    } else if (panelType == 'stay') {
		        stayPanel.style.display = 'block';
		        p = 1;
		    }
		    displayPlaceList(p, place);
		}

		// Close 버튼 클릭 시, +추가 버튼으로 돌아가게 처리
		function closeControls(event) {
		    const dateItem = event.target.closest('.date-item');  // 해당 날짜 항목 찾기
		    const controlBtns = dateItem.querySelector('.control-btns');
		    const controlAdd = dateItem.querySelector('.control-add');
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
		function filterPlaces(searchTerm, place) {
		    const autocompleteService = new google.maps.places.AutocompleteService();

		    autocompleteService.getPlacePredictions({
		        input: searchTerm,
		        location: place,
		        rankby: 30000,
		        types: ['museum', 'park', 'restaurant', 'lodging', 'tourist_attraction'] // 장소 유형 추가
		    }, function (predictions, status) {
		        const resultsList = document.getElementById("search-results");
		        resultsList.innerHTML = ""; // 기존 리스트 초기화

		        if (status != google.maps.places.PlacesServiceStatus.OK || !predictions) {
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
		let places = [];
		let placeMap = new Map();
		let selDate, activeDateItem;
		// 선택한 장소 사이드바 일정('addDetail)에 추가하기
		function selectPlace(place) {
		    console.log("선택한 장소:", place);

		    // 현재 선택된 날짜('.date-item')찾기
		    activeDateItem = document.querySelector(".date-item.active"); // 현재 활성화된 날짜
		    selDate = activeDateItem.previousElementSibling.getElementsByClassName('selectDate')[0].value;

		    if (placeMap.has(selDate)){
		        let pla = placeMap.get(selDate);
		        if(!pla.some(p => place.placeId.includes(p))){
		            pla.push(place.placeId);
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
		            placeItem.setAttribute("data-place-id", place.placeId);
		            placeItem.innerHTML = `
			        <span class="place-name">${place.name}</span>
			        <input type="hidden" value="${place.placeId}"/>
			        <button class="remove-btn" onclick="removePlace(this)">X</button>
			    `;

		            // `addDetail`에 장소 추가
		            addDetail.appendChild(placeItem);
		        }
		        placeMap.set(selDate, pla);
		    }else{
		        places = [];
		        places.push(place.placeId);
		        placeMap.set(selDate, places);
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
		        placeItem.setAttribute("data-place-id", place.placeId);
		        placeItem.innerHTML = `
			        <span class="place-name">${place.name}</span>
			        <input type="hidden" value="${place.placeId}"/>
			        <button class="remove-btn" onclick="removePlace(this)">X</button>
			    `;

		        // `addDetail`에 장소 추가
		        addDetail.appendChild(placeItem);
		    }
		}

		// 장소 삭제 기능 추가
		function removePlace(button) {
		    let date = button.parentElement.parentElement.parentElement.previousElementSibling.querySelector('.selectDate').value;
		    if (placeMap.has(date)){
		        let places = placeMap.get(date);
		        places = places.filter((e) => e !== button.previousElementSibling.value);
		        placeMap.set(date, places);
		    }
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

		let memos = [];
		let memoMap = new Map();
		function saveMemo() {
		    activeDateItem = document.querySelector(".date-item.active"); // 현재 활성화된 날짜
		    selDate = activeDateItem.previousElementSibling.getElementsByClassName('selectDate')[0].value;
		    // 메모 입력값 가져오기
		    const memoText = document.getElementById("memo-text").value.trim();

		    if (memoText == "") {
		        alert("메모를 입력하세요!");
		        return;
		    }

		    if (memoMap.has(selDate)){
		        let me = memoMap.get(selDate);
		        me.push(memoText);
		        memoMap.set(selDate, me);
		    }else{
		        memos = [];
		        memos.push(memoText);
		        memoMap.set(selDate, memos);
		    }

		    // ✅ 선택한 날짜의 `addMemo` 요소 찾기 (없으면 생성)
		    let addMemo = activeDateItem.querySelector(".addMemo");
		    if (!addMemo) {
		        addMemo = document.createElement("div");
		        addMemo.classList.add("addMemo");
		        activeDateItem.appendChild(addMemo);
		    }

		    // ✅ 메모 요소 추가 (HTML 요소 생성)
		    const memoItem = document.createElement("div");
		    memoItem.classList.add("memo-item");
		    memoItem.innerHTML = `
		        <span class="memo-text">${memoText}</span>
		        <button class="remove-btn" onclick="removeMemo(this)">X</button>
		    `;

		    // `addMemo`에 메모 추가
		    addMemo.appendChild(memoItem);

		    // 메모 입력창 초기화
		    document.getElementById("memo-text").value = "";
		}

		//saveDetail(memoText, null);
		document.getElementById("memo-text").value = "";

		// ✅ 메모 삭제 기능 추가
		function removeMemo(button) {
		    let date = button.parentElement.parentElement.parentElement.previousElementSibling.querySelector('.selectDate').value;
		    if (memoMap.has(date)){
		        let memos = memoMap.get(date);
		        memos = memos.filter((e) => e !== button.previousElementSibling.innerText);
		        memoMap.set(date, memos);
		    }
		    button.parentElement.remove(); // 부모 요소 (`memo-item`) 삭제
		}

		function saveDetail() {
		    const placeObj = Object.fromEntries(placeMap);
		    const memoObj = Object.fromEntries(memoMap);

		    // const data = [placeObj, memoObj];
			const data = {
			        tripNo: parseInt(tripNo),
			        datas: [placeObj, memoObj]
			    };

		    fetch('/schedule/saveDetail', {
		        method: 'POST',
		        headers: {
		            'Content-Type': 'application/json' // ✅ 올바른 Content-Type 설정
		        },
		        body: JSON.stringify(data)
		    })
		        .then(response => response.text())
		        .then(message => {
		            console.log("서버 응답:", message);
		            alert(message); // ✅ 알림 추가 (저장 완료 메시지)
		        })
		        .catch(error => console.error("에러 발생:", error));
		}


		// 메뉴바 선택시 일정 목록으로 페이지 이동
		document.addEventListener("DOMContentLoaded", function () {
		    const menuBtn = document.getElementById("menuBtn");
		    console.log(menuBtn);


		    if (menuBtn) {
		        menuBtn.addEventListener("click", function () {
		            console.log("일정 목록 페이지로 이동")
		            window.location.href = "/schedule/list";
		        });
		    }
		});