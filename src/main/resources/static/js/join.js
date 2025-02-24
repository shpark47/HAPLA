document.querySelector('#joinBtn').addEventListener('click', () => {

    const form = document.getElementById('joinForm');
    const formData = new FormData(form);

    fetch("/join", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(Object.fromEntries(formData)),
    })
        .then((response) => response.json())
        .then((data) => {
            if (data.success){
                console.log('fdsafa');
                document.getElementById("joinModal").classList.add('hidden');
                document.getElementById('loginModal').classList.remove('hidden');
            }else{
                alert('회원가입에 실패하였습니다.');
            }
        })
});