const modalOpenButton = document.getElementById('login');
const loginModal = document.getElementById('loginModal');
const joinModal = document.getElementById('joinModal');

// User profile dropdown functionality
const userProfile = document.querySelector('.user-profile');
const dropdown = document.querySelector('.header-dropdown');

userProfile.addEventListener('click', () => {
    dropdown.classList.toggle('active');
});

// Close dropdown when clicking outside
document.addEventListener('click', (e) => {
    if (!userProfile.contains(e.target) && !dropdown.contains(e.target)) {
        dropdown.classList.remove('active');
    }
});

document.querySelectorAll('.header-close-btn').forEach((button) => {
    button.addEventListener('click', () => {
        loginModal.classList.add('hidden');
        joinModal.classList.add('hidden');
    });
});

const openModal = () => {
    dropdown.classList.remove('active');
    document.getElementById('editModal').classList.remove('hidden');
}

const closeModal = () =>{
    document.getElementById('editModal').classList.add('hidden');
}

modalOpenButton.addEventListener('click', () => {
    loginModal.classList.remove('hidden');
});

document.getElementById('imageUpload').addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('image', file);

    const profileImage = document.getElementById('profileImage');
    if (!profileImage) {
        console.error("profileImage 요소를 찾을 수 없습니다.");
        return;
    }

    fetch('/upload', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) throw new Error('업로드 실패: ' + response.status);
            return response.json();
        })
        .then(data => {
            console.log("받은 데이터:", data);
            if (data.imagePath) {
                profileImage.src = data.imagePath + '?t=' + new Date().getTime(); // 캐시 방지
                profileImage.onload = () => console.log("이미지 로드 성공");
                profileImage.onerror = () => console.error("이미지 로드 실패: " + data.imagePath);
                document.getElementById('profileImg').value = data.imagePath;
            }
        })
        .catch(error => {
            console.error('이미지 업로드 에러:', error);
            profileImage.src = '/img/시나모롤.jpg';
            alert('이미지 업로드에 실패했습니다. 다시 시도해주세요.');
        });
});

document.querySelector('#save').addEventListener('click', ()=>{
    let check = false;
    const nickname = document.querySelector('#nickname').value;
    const formData = new FormData();
    formData.append('nickname', nickname);
    fetch('/checkNickname',{
        method: 'POST',
        body: formData
    }).then(response => {return response.json()})
        .then(data => {
            if (data.result == false){
                check = false;
                alert('중복된 닉네임입니다.');
            }else{
                check = true;
                alert('사용 가능한 닉네임입니다.')
            }
        })
        .catch(error => console.log(error))

    if (check){
        fetch('/updateUser', {

        }).then(response => {return response.json()})
            .then(data => {

            })
            .catch(error => console.log(error))
    }else{
        alert('중복된 닉네임입니다.');
    }
})

document.querySelector('.edit-form').addEventListener('submit', function(e) {
    e.preventDefault();
    // 여기에 폼 제출 로직 추가
    alert('프로필이 업데이트되었습니다!');
    closeModal();
});

// 회원 탈퇴 처리
document.querySelector('.delete-account-btn').addEventListener('click', function() {
    if (confirm('정말로 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
        // 여기에 회원 탈퇴 로직 추가
        alert('회원 탈퇴가 완료되었습니다.');
        closeModal();
    }
});