import { login } from './api.js';

// --- Functions to switch between login and register forms ---
const register_box = document.getElementById("register-box");
const login_box = document.getElementById("login-box");
const register_button_1 = document.getElementById("register-1");
const login_button_2 = document.getElementById("login-2");

register_button_1.onclick = () => {
    login_box.classList.add('hidden');
    register_box.classList.remove('hidden');
};
login_button_2.onclick = () => {
    register_box.classList.add('hidden');
    login_box.classList.remove('hidden');
};

// --- Login Handler ---
async function handleLogin(event) {
    event.preventDefault(); // Prevent the form from reloading the page
    
    const usernameField = document.getElementById("login-username");
    const passwordField = document.getElementById("login-password");
    
    const success = await login(usernameField.value, passwordField.value);

    if (success) {
        window.location.href = 'index.html'; // Redirect to main page
    } else {
        showModal("Invalid username or password. Please try again.");
    }
}

// Attach the handler to the form's submit event
document.querySelector("#login-box form").addEventListener('submit', handleLogin);


// --- Other Functions ---
function validateRegister() {
    showModal("Registration functionality not implemented yet.");
    return false;
}
// Note: You might need to attach this to the register form in a similar way if you implement it.
