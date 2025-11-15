function toggleApplicationForm() {
    var form = document.getElementById('applicationForm');
    if (form.style.display === 'none') {
        form.style.display = 'block';
    } else {
        form.style.display = 'none';
    }
}

function confirmAction(message) {
    return confirm(message);
}