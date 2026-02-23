// Read ?status=success or ?status=failure
const params = new URLSearchParams(window.location.search);

const messageDiv = document.getElementById("message");
messageDiv.textContent = "Oops, something went wrong here";

// Try auto-close after 3 seconds
setTimeout(() => {
    window.close();
}, 3000);