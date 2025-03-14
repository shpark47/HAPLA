let map;
let mapOptions;
let activeDateItem;
let currentLatitude;
let currentLongitude;



function initMap() {
    const storedCity = localStorage.getItem("selectedCity");

    mapOptions = {
        center: { lat: 37.5665, lng: 126.9780 },
        zoom: 10
    };

    if (storedCity) {
        const city = JSON.parse(storedCity);
        mapOptions.center = { lat: city.lat, lng: city.lng };
        console.log(` 지도 위치 변경: ${city.name}, ${city.country}`);
    }

    map = new google.maps.Map(document.getElementById('map'), mapOptions);
    displayPlaceList();
    initPlaceSearch();
}

document.addEventListener("DOMContentLoaded", function () {
    initMap();
});

function toggleControls(addButton) {
    const dateItem = addButton.closest('.date-item');
    const controlBtns = dateItem.querySelector('.control-btns');
    const controlAdd = dateItem.querySelector('.control-add');

    controlBtns.style.display = 'flex';
    controlAdd.style.display = 'none';
    activeDateItem = dateItem;
}

function openSidePanel(panelType) {
    const sidePanels = document.querySelectorAll('.side-panel');
    sidePanels.forEach(panel => panel.style.display = 'none');

    const panel = document.getElementById(panelType + '-panel');
    if (panel) {
        panel.style.display = 'block';
		
	// 활성화된 날짜 항목의 위치 정보를 사용하여 displayPlaceList() 호출
	        if (activeDateItem) {
	            location = map.getCenter(); // 지도 중심 좌표 사용 (필요에 따라 변경)
	            displayPlaceList(location);
	        }
	    }
	}

function closeControls(event) {
    const dateItem = event.target.closest('.date-item');
    const controlBtns = dateItem.querySelector('.control-btns');
    const controlAdd = dateItem.querySelector('.control-add');
    const sidePanels = document.querySelectorAll(".side-panel");

    sidePanels.forEach(panel => panel.style.display = "none");

    controlBtns.style.display = 'none';
    controlAdd.style.display = 'block';
    activeDateItem = null;
}

const input = document.getElementById("searchInput");
const searchResults = document.getElementById("search-results"); // 검색 결과 목록 요소

function initPlaceSearch() {
    input.setAttribute("autocomplete", "off");

	input.addEventListener("input", function () {
	        const searchTerm = input.value.trim();
	        if (searchTerm == "") {
	            searchResults.innerHTML = ""; // 검색어가 없으면 결과 목록 초기화
	        } else {
	            filterPlaces(searchTerm);
	        }
	    });
	}

function filterPlaces(searchTerm) {
    const autocompleteService = new google.maps.places.AutocompleteService();

    autocompleteService.getPlacePredictions({ input: searchTerm }, (predictions, status) => {
        if (status == google.maps.places.PlacesServiceStatus.OK) {
            displayCustomSearchResults(predictions);
        } else {
            searchResults.innerHTML = ""; // 오류 발생 시 결과 목록 초기화
        }
    });
}

function displayCustomSearchResults(predictions) {
    searchResults.innerHTML = ""; // 기존 결과 목록 초기화
    predictions.forEach(prediction => {
        const li = createPlaceListItem({
            description: prediction.description,
            place_id: prediction.place_id
        });
        searchResults.appendChild(li);
    });
}

function displayPlaceList(location) {
    if (!map) {
        console.error("지도 객체(map)가 초기화되지 않았습니다.");
        return;
    }

    if (!location) {
        location = map.getCenter();
		console.log('location : ' + location);
        if (!location) {
            console.error("지도 중심 좌표를 가져올 수 없습니다.");
            return;
        }
    }

    console.log("displayPlaceList location:", location);
	
    console.log("displayPlaceList latitude:", location.lat());
    console.log("displayPlaceList longitude:", location.lng());

    let latitude = location.lat();
    let longitude = location.lng();

//    let center = new google.maps.LatLng(latitude,longitude);

    let service = new google.maps.places.PlacesService(map);

    service.nearbySearch({
        location: mapOptions.center,
        radius: 10000,
        type: ['tourist_attraction']
    }, (results, status) => {
        console.log("nearbySearch status:", status);
        console.log("nearbySearch results:", results);

        if (status == google.maps.places.PlacesServiceStatus.OK) {
            // ... (결과 처리)
        } else {
            console.error("nearbySearch 오류:", status);
            alert("인기 장소를 가져오는 데 실패했습니다. Status: " + status);
        }
    });
}

function createPlaceListItem(item) {
    const li = document.createElement("li");
    li.classList.add("place-item");

    const icon = document.createElement("span");
    icon.classList.add("place-icon");
    icon.textContent = "";

    const textSpan = document.createElement("span");
    textSpan.classList.add("place-text");

    if (item.description && item.place_id) {
        textSpan.textContent = item.description;
        li.onclick = function () {
            const service = new google.maps.places.PlacesService(document.createElement("div"));
            service.getDetails({
                placeId: item.place_id,
                fields: ["name", "geometry", "place_id"]
            }, (place, status) => {
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
        textSpan.textContent = `${item.name}, ${item.address}`;
        li.onclick = function () {
            selectPlace(item);
        };
    }

    li.appendChild(icon);
    li.appendChild(textSpan);
    return li;
}

function selectPlace(place) {
    if (!activeDateItem) {
        alert("날짜를 먼저 선택하세요!");
        return;
    }

    let addDetail = activeDateItem.querySelector(".addDetail");
    if (!addDetail) {
        addDetail = document.createElement("div");
        addDetail.classList.add("addDetail");
        activeDateItem.appendChild(addDetail);
    }

    const placeItem = document.createElement("div");
    placeItem.classList.add("place-item");
    placeItem.innerHTML = `
        <span class="place-name">${place.name}</span>
        <button class="remove-btn" onclick="removePlace(this)">X</button>
    `;

    addDetail.appendChild(placeItem);
}

function removePlace(button) {
    button.parentElement.remove();
}

document.querySelectorAll(".date-item").forEach(item => {
    item.addEventListener("click", function () {
        document.querySelectorAll(".date-item").forEach(el => el.classList.remove("active"));
        this.classList.add("active");
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const menuBtn = document.getElementById("menuBtn");
    if (menuBtn) {
        menuBtn.addEventListener("click", function () {
            window.location.href = "/schedule/list";
        });
    }
});