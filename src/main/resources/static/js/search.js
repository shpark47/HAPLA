const categoryButtons = document.querySelectorAll('.search-categories button');
const inputText = document.querySelector('input[type=text]');
const searchBar = document.querySelector('.search-bar'); // 기본 검색창
const flightSearchBar = document.querySelector('.flight-search-bar'); // 항공권 검색창

// Placeholder 매핑 객체
const placeholderMap = {
    '여행지': '여행지',
    '관광명소': '관광명소, 액티비티 또는 여행지',
    '숙박': '호텔 이름 또는 여행지',
    '음식점': '음식점 또는 여행지'
};

categoryButtons.forEach(button => {
    button.addEventListener('click', () => {
        // 모든 버튼의 'active' 제거 후 현재 버튼에 추가
        categoryButtons.forEach(btn => btn.classList.remove('active'));
        button.classList.add('active');

        // 버튼에 해당하는 placeholder 적용
        inputText.placeholder = placeholderMap[button.innerText] || '여행지, 즐길거리, 호텔 등';

        // 검색창 표시 설정
        const isFlightSearch = button.innerText === '항공권';
        searchBar.style.display = isFlightSearch ? 'none' : '';
        flightSearchBar.style.display = isFlightSearch ? 'block' : 'none';
    });
});
