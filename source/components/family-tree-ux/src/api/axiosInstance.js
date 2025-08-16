import axios from "axios";

const API_BASE_URL = window.location.origin;
// For development with npm:
// const API_BASE_URL = "http://localhost:8080"
const api = axios.create({
  baseURL: `${API_BASE_URL}/api`,
  withCredentials: true, // 💡 This is critical to send JSESSIONID
});

let hasRedirectedToLogin = false;

// Add interceptor to redirect to login on 401/403
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (
      error.response?.status === 401 &&
      !hasRedirectedToLogin &&
      !window.location.pathname.includes("/login")
    ) {
      hasRedirectedToLogin = true;
      const currentPath = window.location.pathname + window.location.search;
      window.location.href = `/login?redirectTo=${encodeURIComponent(currentPath)}`;
    }
    return Promise.reject(error);
  }
);

export default api;
