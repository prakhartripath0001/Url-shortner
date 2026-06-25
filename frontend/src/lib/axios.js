// src/lib/axios.js
// Central Axios instance — all API calls go through here
// WHY? Single place to configure base URL, interceptors, auth headers

import axios from "axios";
import { useAuthStore } from "../store/authStore";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8083",
  headers: { "Content-Type": "application/json" },
  timeout: 10000,
});

// Request interceptor — attach JWT to every request
api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor — handle 401 (token expired) globally
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      try {
        const refreshToken = useAuthStore.getState().refreshToken;
        const res = await axios.post(
          `${import.meta.env.VITE_AUTH_BASE_URL || "http://localhost:8082"}/api/v1/auth/refresh`,
          { refreshToken }
        );
        const { accessToken } = res.data;
        useAuthStore.getState().setTokens(accessToken, refreshToken);
        original.headers.Authorization = `Bearer ${accessToken}`;
        return api(original);
      } catch {
        useAuthStore.getState().logout();
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

// Auth service client (different base URL)
export const authApi = axios.create({
  baseURL: import.meta.env.VITE_AUTH_BASE_URL || "http://localhost:8082",
  headers: { "Content-Type": "application/json" },
  timeout: 10000,
});

authApi.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default api;
