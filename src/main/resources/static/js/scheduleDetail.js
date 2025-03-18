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

                dateItem.appendChild(addButton);
                dateContainer.appendChild(planDate);
                dateContainer.appendChild(dateItem);
                dateList.appendChild(dateContainer);
				
				// 각 날짜 컨테이너 클릭 시 side-pannel 표시
				dateContainer.addEventListener("click", function(){
					document.getElementById("side-panel").style.display = "block";
				});

                // 다음 날짜로 이동
                startDate.setDate(startDate.getDate() + 1);
            }
        }
		
		// ✅ 패널 닫기 버튼 기능 추가
		document.addEventListener("DOMContentLoaded", function () {
		    const closeButton = document.querySelector(".close-btn");
		    if (closeButton) {
		        closeButton.addEventListener("click", function () {
		            document.getElementById("side-panel").style.display = "none";
		        });
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

// 사이드 패널 열기 / 닫기 기능
for (const button of document.querySelectorAll('.panel-open-btn')) {
    button.addEventListener('click', () => {
        document.getElementById('side-panel').classList.add('active');
    });
}


document.querySelector('.close-btn').addEventListener('click', () => {
    document.getElementById('side-panel').classList.remove('active');
});