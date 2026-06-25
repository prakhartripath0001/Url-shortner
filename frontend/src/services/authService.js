// src/services/authService.js
import { authApi } from "../lib/axios";

export const authService = {
  register: (data) => authApi.post("/api/v1/auth/register", data),
  login: (data) => authApi.post("/api/v1/auth/login", data),
  logout: (refreshToken) => authApi.post("/api/v1/auth/logout", { refreshToken }),
  refresh: (refreshToken) => authApi.post("/api/v1/auth/refresh", { refreshToken }),
  forgotPassword: (email) => authApi.post("/api/v1/auth/forgot-password", { email }),
  resetPassword: (data) => authApi.post("/api/v1/auth/reset-password", data),
  getProfile: () => authApi.get("/api/v1/users/me"),
};
