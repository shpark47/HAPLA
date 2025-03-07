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