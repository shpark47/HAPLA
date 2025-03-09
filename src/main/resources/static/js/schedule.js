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


// 사이드 패널 열기
function openSidePanel(panelType) {
    const sidePanel = document.getElementById('side-panel');
    const panelBody = document.getElementById('side-panel-body');
    
    // 사이드 패널 내용 변경
    if (panelType == 'memo') {
        panelBody.innerHTML = "<p>메모를 추가할 수 있습니다.</p>";
    } else if (panelType == 'stay') {
        panelBody.innerHTML = "<p>숙박 정보를 추가할 수 있습니다.</p>";
    } else if (panelType == 'marker') {
        panelBody.innerHTML = "<p>마커를 추가할 수 있습니다.</p>";
    }

    
	    sidePanel.style.display = 'block';  // `hidden` 대신 `display` 속성으로 보이게 설정
	}


// Close 버튼 클릭 시, +추가 버튼으로 돌아가게 처리
function closeControls() {
    const dateItem = event.target.closest('.date-item');  // closest()를 이용해 해당 날짜 항목 찾기
    const controlBtns = dateItem.querySelector('.control-btns');
    const controlAdd = dateItem.querySelector('.control-add');

    // .control-btns 숨기기, .add-text 보이게 하기
    controlBtns.hidden = true;
    controlAdd.hidden = false;
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
      
      
