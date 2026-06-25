// src/services/analyticsService.js
import api from "../lib/axios";

export const analyticsService = {
  getUrlStats: (shortCode, period = "7d") =>
    api.get(`/api/v1/analytics/${shortCode}?period=${period}`),
  getDashboardStats: () =>
    api.get("/api/v1/analytics/dashboard"),
  getClicksByDay: (shortCode, period = "30d") =>
    api.get(`/api/v1/analytics/${shortCode}/clicks-by-day?period=${period}`),
  getClicksByCountry: (shortCode) =>
    api.get(`/api/v1/analytics/${shortCode}/clicks-by-country`),
  getClicksByDevice: (shortCode) =>
    api.get(`/api/v1/analytics/${shortCode}/clicks-by-device`),
};
