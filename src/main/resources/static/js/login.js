let loginImage = '';
let loginName = '';
let loginType = '';
let tokenId = '';

// ✅ 카카오 SDK 초기화
Kakao.init("04cff7eb62f268a2bb506c81bf1de17b");

// ✅ 카카오 로그인 버튼 클릭 시 실행
document.getElementById("kakao-login-btn").addEventListener("click", function () {
    Kakao.Auth.login({
        scope: "profile_nickname, profile_image",
        success: function (authObj) {
            console.log("✅ 로그인 성공! 액세스 토큰:", authObj.access_token);

            // ✅ 액세스 토큰을 이용하여 사용자 정보 가져오기
            fetchUserInfo(authObj.access_token);
        },
        fail: function (err) {
            console.error("❌ 로그인 실패", err);
        },
    });
});

// ✅ 사용자 정보 가져와서 모달 업데이트
function fetchUserInfo(accessToken) {
    Kakao.API.request({
        url: "/v2/user/me",
        success: function (response) {
            console.log("✅ 사용자 정보:", response);
            console.log(response.id)

            loginImage = response.properties.profile_image;
            loginName = response.properties.nickname;
            loginType = "K";
            tokenId = response.id;

            checkUser(tokenId, loginType);
        },
        fail: function (error) {
            console.error("❌ 사용자 정보 가져오기 실패", error);
        },
    });
}

// ✅ 모달창 업데이트 (프로필 이미지 & 닉네임)
function updateModal(loginImage, loginName, loginType, tokenId) {
    const modal = document.getElementById("joinModal");
    const profileImage = document.getElementById("loginImage");
    const nicknameInput = document.getElementById("loginName");
    const type = document.getElementById("loginType");
    const id= document.getElementById('tokenId');
    const profile = document.getElementById('profile');

    // 🔹 사용자 정보 적용
    profileImage.src = loginImage;
    nicknameInput.value = loginName || "사용자";
    type.value = loginType;
    id.value = tokenId;
    profile.value = loginImage;

    // 🔹 모달창 표시
    document.getElementById('loginModal').classList.add('hidden');
    modal.classList.remove("hidden");
}

// Google 로그인 버튼
google.accounts.id.initialize({
    client_id: '157461561471-a5mi8bhvlq4rt4ci11j6ep6h8ftd6tmq.apps.googleusercontent.com',
    callback: handleCredentialResponse
});
google.accounts.id.renderButton(
    document.getElementById("google-login-btn"),
    {theme: "outline", size: "large", width: "100%"}
);

function handleCredentialResponse(response) {
    console.log("Encoded JWT ID token: " + response.credential);
    fetchUserProfile(response.credential);
}

// 사용자 프로필 정보 가져오기
function fetchUserProfile(token) {
    fetch('http://localhost:8080/google-login/verify-token', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ token: token })  // JWT 토큰을 서버로 전송
    })
        .then(response => response.json())
        .then(data => {
            loginImage = data.picture;
            loginName = data.name;
            loginType = "G";
            tokenId = data.userId;
            checkUser(tokenId, loginType);
        })
        .catch(error => {
            console.error('Error fetching user profile:', error);
        });
}

const checkUser = (id, type) => {
    fetch("/checkUser", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            tokenId: id,
            type: type
        }),
    })
        .then((response) => response.json())
        .then((data) => {
            console.log(data);
            if (data.checkResult == '0'){
                updateModal(loginImage, loginName, loginType, tokenId);
            }else{
                document.getElementById('loginModal').classList.add('hidden');
                location.reload();
            }
        })
}