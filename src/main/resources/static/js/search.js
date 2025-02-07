// Search category buttons
const categoryButtons = document.querySelectorAll('.search-categories button');
categoryButtons.forEach(button => {
    button.addEventListener('click', () => {
        categoryButtons.forEach(btn => btn.classList.remove('active'));
        button.classList.add('active');
    });
});