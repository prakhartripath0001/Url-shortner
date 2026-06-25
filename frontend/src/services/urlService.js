// src/services/urlService.js
import api from "../lib/axios";

export const urlService = {
  createUrl: (data) => api.post("/api/v1/urls", data),
  listUrls: (page = 0, size = 20) => api.get(`/api/v1/urls?page=${page}&size=${size}`),
  getUrl: (shortCode) => api.get(`/api/v1/urls/${shortCode}`),
  deleteUrl: (shortCode) => api.delete(`/api/v1/urls/${shortCode}`),
  getQrCode: (shortCode, size = 300) =>
    api.get(`/api/v1/urls/${shortCode}/qr?size=${size}`, { responseType: "blob" }),
};
