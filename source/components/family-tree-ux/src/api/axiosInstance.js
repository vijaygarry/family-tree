import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // 💡 This is critical to send JSESSIONID
});

// Add interceptor to redirect to login on 401/403
api.interceptors.response.use(
  (response) => response,
  (error) => {
    //const isLoginApi = error.config?.url?.includes("/login");

    // if (!isLoginApi && [401].includes(error.response?.status)) {
    if (error.response?.status === 401) {
      //window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default api;
