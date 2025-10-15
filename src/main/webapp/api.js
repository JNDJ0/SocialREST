export const API_BASE_URL = 'https://socialrest.onrender.com/api';

// --------------------- Token Storage ---------------------
export const ACCESS_TOKEN_KEY = 'access_token';
export const REFRESH_TOKEN_KEY = 'refresh_token';
export const USER_TOKEN_KEY = 'current_user';

function getAccessToken() {
  return sessionStorage.getItem(ACCESS_TOKEN_KEY);
}
function setAccessToken(t) {
  if (t) sessionStorage.setItem(ACCESS_TOKEN_KEY, t);
}
function getRefreshToken() {
  return sessionStorage.getItem(REFRESH_TOKEN_KEY);
}
function setRefreshToken(t) {
  if (t) sessionStorage.setItem(REFRESH_TOKEN_KEY, t);
}

// --------------------- Auth Functions ---------------------
export function logout() {
    sessionStorage.removeItem(ACCESS_TOKEN_KEY);
    sessionStorage.removeItem(USER_TOKEN_KEY);
    sessionStorage.removeItem(REFRESH_TOKEN_KEY);
    if (typeof window !== 'undefined') {
        window.location.href = 'login.html';
    }
}

function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } 
    catch (e) {
        console.error("Failed to parse JWT", e);
        return null;
    }
}

export async function JWTLogin(username, password) {
    let res;
    try {
        res = await fetch(`${API_BASE_URL}/jwt/token`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                grant_type: 'password',
                username,
                password
            })
        });
    } 
    catch (netErr) {
        throw new Error(`Unable to contact server (${netErr?.message || 'network error'}). Ensure ${API_BASE_URL} is reachable and CORS is configured.`);
    }

    if (!res.ok) {
        let bodyText = '';
        try { bodyText = await res.text(); } catch {}
        throw new Error(`HTTP ${res.status} ${res.statusText}${bodyText ? ` - ${bodyText}` : ''}`);
    }

    let data;
    try { data = await res.json(); }
    catch {
        throw new Error('Invalid response from server (expected JSON).');
    }

    alert(JSON.stringify(data));
    setAccessToken(data.access_token);
    setRefreshToken(data.refresh_token);
    const tokenPayload = parseJwt(data.access_token);
    if (tokenPayload && tokenPayload.sub) {
        try {
            const currentUser = await getUserById(tokenPayload.uid);
            
            if (currentUser) {
                sessionStorage.setItem(USER_TOKEN_KEY, JSON.stringify(currentUser));
            } 
            else {
                console.warn("JWT successful, but couldn't find user details for subject:", tokenPayload.sub);
            }
        } 
        catch (error) {
            console.error("Failed to fetch user details after JWT login:", error);
        }
    }
    
    return data;
}

export async function basicLogin(email, password) {
    const response = await fetch(`${API_BASE_URL}/users/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    });

    if (response.ok) {
        const authHeader = response.headers.get('Authorization');
        const basicToken = 'Basic ' + btoa(`${email}:${password}`);
            
        alert(authHeader);

        sessionStorage.setItem(ACCESS_TOKEN_KEY, authHeader || basicToken);

        const user = await response.json();
        sessionStorage.setItem(USER_TOKEN_KEY, JSON.stringify(user));
        return user;
    } 
    else {
        sessionStorage.removeItem(ACCESS_TOKEN_KEY);
        sessionStorage.removeItem(USER_TOKEN_KEY);
        return null;
    }
}

export function isAuthenticated() {
    return !!getAccessToken();
}
export function requireAuth(redirectTo = 'login.html') {
    if (!isAuthenticated() && typeof window !== 'undefined') {
        window.location.href = redirectTo;
    }
}

export function checkAdminRole() {
    const token = sessionStorage.getItem(ACCESS_TOKEN_KEY);
    if (!token) {
        window.location.href = 'login.html';
        return false;
    }

    const userString = sessionStorage.getItem(USER_TOKEN_KEY);
    if (!userString) {
        console.error("User is authenticated but user details are not in session storage.");
        return false;
    }

    try {
        const user = JSON.parse(userString);
        if (user.roles && user.roles.includes('ADMIN')) {
            return true;
        }
    } 
    catch (e) {
        console.error("Failed to parse user data from session storage:", e);
    }
    
    return false;
}

// --------------------- API Functions ---------------------
export async function getAllUsers(name) {
    let path = '/users';
    if (name) path += `?name=${encodeURIComponent(name)}`;
    return apiFetch(path);
}

export async function getUserById(userId) {
    return apiFetch(`/users/${userId}`);
}

export async function createUser(userData) {
    let response = await fetch(`${API_BASE_URL}/users/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
    });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    return response.json();
}

export async function updateUser(userId, userData) {
    return apiFetch(`/users/${userId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData),
    });
}

export async function deleteUser(userId) {
    return apiFetch(`/users/${userId}`, { method: 'DELETE' });
}

export async function getAllPosts(authorId) {
    let path = '/posts';
    if (authorId) path += `?authorId=${authorId}`;
    return apiFetch(path);
}

export async function getPostById(postId) {
    return apiFetch(`/posts/${postId}`);
}

export async function createPost(postData) {
    return apiFetch('/posts', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(postData),
    });
}

export async function getCommentsByPost(postId) {
    return apiFetch(`/posts/${postId}/comments`);
}

export async function deletePost(postId) {
    return apiFetch(`/posts/${postId}`, { method: 'DELETE' })
}

export async function createComment(postId, commentData) {
    return apiFetch(`/posts/${postId}/comments`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(commentData),
    });
}

export async function deleteComment(postId, commentId){
    return apiFetch(`/posts/${postId}/comments/${commentId}`, {
        method: 'DELETE'
    });
}

export async function likePost(postId) {
    return apiFetch(`/posts/${postId}/likes`, { method: 'POST' });
}

// --------------------- Aux Functions ---------------------
async function tryRefreshAndReplay(originalConfig) {
    const refresh_token = getRefreshToken();
    if (!refresh_token) return null;

    const r = await fetch(`${API_BASE_URL}/jwt/token`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            grant_type: 'refresh_token',
            refresh_token
        })
    });

    if (!r.ok) return null;

    const data = await r.json();
    setAccessToken(data.access_token);
    setRefreshToken(data.refresh_token);

    const { url, initFactory } = originalConfig;
    return fetch(url, initFactory());
}

async function apiFetch(path, init = {}) {
    const url = `${API_BASE_URL}${path}`;
    const token = getAccessToken();

    const headers = new Headers(init.headers || {});
    if (token) {
        if (token.startsWith('Basic')) {
            headers.set('Authorization', token);
        } 
        else {
            headers.set('Authorization', `Bearer ${token}`);
        }
    }
  
    const initWithAuth = () => ({ ...init, headers });

    let res = await fetch(url, initWithAuth());
    if (res.status === 401) {
        const retried = await tryRefreshAndReplay({ url, initFactory: initWithAuth });
        if (retried) res = retried;
    }
    
    if (!res.ok) {
        throw new Error(res.status);
    }
    const contentType = res.headers.get("content-type");
    if (contentType && contentType.indexOf("application/json") !== -1) {
        return res.json();
    }
    return;
}

export function showModal(message, onConfirm, isAlert = true) {
    const existingModal = document.querySelector('.modal-backdrop');
    if (existingModal) existingModal.remove();

    const modal = document.createElement('div');
    modal.className = 'modal-backdrop fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50 opacity-0';
    
    const modalContent = document.createElement('div');
    modalContent.className = 'modal-content bg-gray-800 rounded-lg p-6 shadow-xl text-center max-w-sm w-full transform scale-95';
    
    setTimeout(() => {
        modal.style.opacity = '1';
        modalContent.style.transform = 'scale(1)';
    }, 10);
    
    modalContent.innerHTML = `<p class="mb-6">${message}</p>`;

    const buttonContainer = document.createElement('div');
    buttonContainer.className = 'flex justify-center gap-4';

    const closeModal = () => {
        modal.style.opacity = '0';
        modalContent.style.transform = 'scale(0.95)';
        setTimeout(() => modal.remove(), 300);
    };

    if (!isAlert) {
        const cancelBtn = document.createElement('button');
        cancelBtn.textContent = 'Cancel';
        cancelBtn.className = 'py-2 px-4 bg-gray-600 hover:bg-gray-500 rounded-md transition';
        cancelBtn.onclick = closeModal;
        buttonContainer.appendChild(cancelBtn);

        const confirmBtn = document.createElement('button');
        confirmBtn.textContent = 'Delete';
        confirmBtn.className = 'py-2 px-4 bg-red-600 hover:bg-red-500 rounded-md transition';
        confirmBtn.onclick = () => {
            onConfirm();
            closeModal();
        };
        buttonContainer.appendChild(confirmBtn);
    } 
    else {
        const okBtn = document.createElement('button');
        okBtn.textContent = 'OK';
        okBtn.className = 'py-2 px-4 bg-blue-600 hover:bg-blue-500 rounded-md transition';
        okBtn.onclick = closeModal;
        buttonContainer.appendChild(okBtn);
    }
    
    modalContent.appendChild(buttonContainer);
    modal.appendChild(modalContent);
    document.body.appendChild(modal);
}