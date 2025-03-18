// ✅ Google 지도 초기화 함수
       function initMap(lat = 48.8566, lng = 2.3522) {
           new google.maps.Map(document.getElementById('map'), {
               center: { lat: lat, lng: lng },
               zoom: 10 
           });
       }
       
       // 메뉴바 선택시 
       document.addEventListener("DOMContentLoaded", function(){
       	const menuBtn = document.getElementById("menuBtn");
       	console.log(menuBtn);
       
       	
       	if(menuBtn){
       		menuBtn.addEventListener("click", function(){
       		console.log("일정 목록 페이지로 이동")
       		window.location.href = "/schedule/schedule";
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

// 일정 내용 박스 클릭시 일정 내용으로 페이지 이동
document.addEventListener("DOMContentLoaded", function(){
	const parent = document.querySelector(".date-list");	// 부모요소
	
	parent.addEventListener("click", function(event){
		const target = event.target.closest(".date-container");	//클릭된 요소가 컨테이너
		
		if(target){
			const tripNo = target.getAttribute("data-id");
			if(tripNo){
				window.location.href = '/schedule/detail/' + tripNo;	// 해당 no로 이동
			}
		}
	})
})