// User profile dropdown functionality
const userProfile = document.querySelector('.user-profile');
const dropdown = document.querySelector('.dropdown');

userProfile.addEventListener('click', () => {
    dropdown.classList.toggle('active');
});

// Close dropdown when clicking outside
document.addEventListener('click', (e) => {
    if (!userProfile.contains(e.target) && !dropdown.contains(e.target)) {
        dropdown.classList.remove('active');
    }
});

const modalOpenButton = document.getElementById('login');
const join = document.getElementById('join-modal');
const loginModal = document.getElementById('loginModal');
const joinModal = document.getElementById('joinModal');

modalOpenButton.addEventListener('click', () => {
    loginModal.classList.remove('hidden');
});

document.querySelectorAll('.close-btn').forEach((button) => {
    button.addEventListener('click', () => {
        loginModal.classList.add('hidden');
        joinModal.classList.add('hidden');
    });
});

join.addEventListener('click', () => {
    loginModal.classList.add('hidden');
    joinModal.classList.remove('hidden');
});

const openModal = () => {
    dropdown.classList.remove('active');
    document.getElementById('editModal').classList.remove('hidden');
}

const closeModal = () =>{
    document.getElementById('editModal').classList.add('hidden');
}

document.getElementById('imageUpload').addEventListener('change', function(e) {
    if (e.target.files && e.target.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('profileImage').src = e.target.result;
        };
        reader.readAsDataURL(e.target.files[0]);
    }
});

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