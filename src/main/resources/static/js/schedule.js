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
            
            // 추가 버튼 클릭 시 장소 검색 아이콘 생성
            addButton.addEventListener("click", function (){
               addButton.remove();	// 추가 버튼 제거
			   
			   // 장소마커 아이콘
			   
			   let markerIcon = document.createElement("span");
			   markerIcon.classList.add("marker-icon");
			   markerIcon.innerHTML = "📍";
			   dateItem.appendChild(markerIcon);
			   
			   // 숙소 아이콘
			   let hotelIcon = document.createElement("span");
			   hotelIcon.classList.add("hotel-icon");
			   hotelIcon.innerHTML = "🏠";
			   dateItem.appendChild(hotelIcon);
			   
			   // 메모 아이콘
		       let memoIcon = document.createElement("span");
		       memoIcon.classList.add("memo-icon");
		       memoIcon.innerHTML = "📝"; // 메모 이모지
		       dateItem.appendChild(memoIcon);

		       // X 아이콘
		       let closeIcon = document.createElement("span");
		       closeIcon.classList.add("close-icon");
		       closeIcon.innerHTML = "❌"; // X 이모지
		       dateItem.appendChild(closeIcon);
			  
			   // 사이드 패널 열기 및 장소 검색 기능 추가 (장소마커 아이콘 클릭 시)
		       markerIcon.addEventListener("click", function () {
		           openSidePanel("장소 검색");
		       });

		       // 다른 아이콘에 대한 사이드 패널 열기 (예: 숙소, 메모 등)
		       hotelIcon.addEventListener("click", function () {
		           openSidePanel("숙소 검색");
		       });

		       memoIcon.addEventListener("click", function () {
		           openSidePanel("메모 작성");
		       });
			
              // 아이콘 클릭시 장소 검색 기능 실행
              searchIcon.addEventListener("click", function() {
                 openSearchInput(dateItem);
              });
            });

                // 다음 날짜로 이동
                startDate.setDate(startDate.getDate() + 1);
            }
        }*/
      
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