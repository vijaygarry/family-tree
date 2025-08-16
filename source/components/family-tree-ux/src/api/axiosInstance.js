import axios from "axios";

// List of pages to ignore 401 redirect
const IGNORE_401_PAGES = new Set([
  "/login",
  "/requestForgotPassword",
  "/requestSignUp"
]);

//const API_BASE_URL = window.location.origin;
// For development with npm:
const API_BASE_URL = "http://localhost:8080"
const api = axios.create({
  baseURL: `${API_BASE_URL}/api`,
  withCredentials: true, // 💡 This is critical to send JSESSIONID
});

let hasRedirectedToLogin = false;

// Add interceptor to redirect to login on 401/403
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const currentPath = window.location.pathname;
    if (
      error.response?.status === 401 &&
      !hasRedirectedToLogin &&
      ![...IGNORE_401_PAGES].some(page => currentPath.includes(page))
    ) {
      hasRedirectedToLogin = true;
      const redirectPath = currentPath + window.location.search;
      window.location.href = `/login?redirectTo=${encodeURIComponent(redirectPath)}`;
    }
    return Promise.reject(error);
  }
);

export default api;
