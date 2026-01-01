// Read ?status=success or ?status=failure
const params = new URLSearchParams(window.location.search);

const messageDiv = document.getElementById("message");
messageDiv.textContent = "This application is only reachable through API";

// Try auto-close after 3 seconds
setTimeout(() => {
    window.close();
}, 3000);