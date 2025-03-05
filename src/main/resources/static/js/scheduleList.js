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
       		window.location.href = "/schedule/schedule";
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