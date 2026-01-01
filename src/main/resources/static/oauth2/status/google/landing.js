// Read ?status=success or ?status=failure
const params = new URLSearchParams(window.location.search);
const status = params.get("status");

const messageDiv = document.getElementById("message");

if (status === "success") {
    messageDiv.textContent = "Authentication successful! You can close this page now";
    messageDiv.classList.add("success");
} else if (status === "failure") {
    messageDiv.textContent = "Failed to authenticate, please close this page";
    messageDiv.classList.add("failure");
} else {
    messageDiv.textContent = "Unknown status";
}

// Try auto-close after 3 seconds
setTimeout(() => {
    window.close();
}, 3000);