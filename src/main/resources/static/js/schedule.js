// ✅ 일정 페이지가 로드될 때 실행되는 함수
        document.addEventListener("DOMContentLoaded", function () {
            const tripData = localStorage.getItem("tripData");

            if (tripData) {
                const data = JSON.parse(tripData);

                // ✅ 선택한 도시 정보 적용
                if (data.city) {
                    document.getElementById("tripTitle").textContent = `${data.city.name}, ${data.city.country}`;
                }

                // ✅ 선택한 날짜 정보 적용
                document.getElementById("tripDates").textContent = `📅 ${data.startDate} ~ ${data.endDate}`;

                // ✅ 날짜별 일정 리스트 생성
                generateDateList(data.startDate, data.endDate);

                // ✅ 지도 위치 변경
                initMap(data.city.lat, data.city.lng);
            }
        });

        // ✅ 날짜별 일정 리스트 생성
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

                // 다음 날짜로 이동
                startDate.setDate(startDate.getDate() + 1);
            }
        }

        // ✅ Google 지도 초기화 함수
        function initMap(lat = 48.8566, lng = 2.3522) {
            new google.maps.Map(document.getElementById('map'), {
                center: { lat: lat, lng: lng },
                zoom: 10 
            });
        }
        
        // 메뉴바 선택시 일정 목록으로 페이지 이동
        document.addEventListener("DOMContentLoaded", function(){
        	const menuBtn = document.getElementById("menuBtn");
        	console.log(menuBtn);
        	console.log(menuBtn);
        	
        	if(menuBtn){
        		menuBtn.addEventListener("click", function(){
        		console.log("일정 목록 페이지로 이동")
        		window.location.href = "/schedule/scheduleList";
        	});
        }
        });

// Google Maps initialization
/*function initMap() {
    const map = new google.maps.Map(document.getElementById('map'), {
        center: { lat: 46.603354, lng: 1.888334 }, // Center of France
        zoom: 6,
        styles: [
            {
                featureType: 'water',
                elementType: 'geometry',
                stylers: [{ color: '#b3d1ff' }]
            },
            {
                featureType: 'landscape',
                elementType: 'geometry',
                stylers: [{ color: '#e8f0e8' }]
            },
            {
                featureType: 'road',
                elementType: 'geometry',
                stylers: [{ color: '#ffffff' }]
            },
            {
                featureType: 'poi',
                elementType: 'geometry',
                stylers: [{ color: '#d4e8d4' }]
            }
        ],
        mapTypeControl: false,
        streetViewControl: false,
        fullscreenControl: false
    });

    // Add markers for example
    const markers = [
        {
            position: { lat: 48.8566, lng: 2.3522 },
            title: 'Paris'
        },
        {
            position: { lat: 43.2965, lng: 5.3698 },
            title: 'Marseille'
        }
    ];

    markers.forEach(markerInfo => {
        new google.maps.Marker({
            position: markerInfo.position,
            map: map,
            title: markerInfo.title
        });
    });
}*/

// 사이드 패널 열기 / 닫기 기능
for (const button of document.querySelectorAll('.panel-open-btn')) {
    button.addEventListener('click', () => {
        document.getElementById('side-panel').classList.add('active');
    });
}


document.querySelector('.close-btn').addEventListener('click', () => {
    document.getElementById('side-panel').classList.remove('active');
});