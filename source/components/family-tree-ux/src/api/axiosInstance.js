import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
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
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default api;
