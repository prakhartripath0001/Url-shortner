// src/services/paymentService.js
import api from "../lib/axios";

export const paymentService = {
  // Create a Razorpay order on our backend
  createOrder: (plan) => api.post("/api/v1/payments/create-order", { plan }),
  // Verify payment signature after successful payment
  verifyPayment: (data) => api.post("/api/v1/payments/verify", data),
  // Get current subscription
  getSubscription: () => api.get("/api/v1/payments/subscription"),
  // Cancel subscription
  cancelSubscription: () => api.post("/api/v1/payments/cancel"),
};

/**
 * Load Razorpay checkout script dynamically
 * We load it only when needed — not on every page load
 */
export const loadRazorpay = () =>
  new Promise((resolve) => {
    if (window.Razorpay) return resolve(true);
    const script = document.createElement("script");
    script.src = "https://checkout.razorpay.com/v1/checkout.js";
    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);
    document.body.appendChild(script);
  });

/**
 * Open Razorpay payment modal
 * @param {Object} order - order from backend (id, amount, currency)
 * @param {Object} user - current user (name, email)
 * @param {Function} onSuccess - called with payment details on success
 * @param {Function} onFailure - called on failure/dismiss
 */
export const openRazorpayCheckout = (order, user, onSuccess, onFailure) => {
  const options = {
    key: import.meta.env.VITE_RAZORPAY_KEY_ID,
    amount: order.amount,
    currency: order.currency || "INR",
    name: "Shortify",
    description: "URL Shortener Premium Plan",
    order_id: order.id,
    prefill: {
      name: user?.username || "",
      email: user?.email || "",
    },
    theme: { color: "#6366f1" },
    handler: (response) => onSuccess(response),
    modal: {
      ondismiss: () => onFailure("Payment cancelled"),
    },
  };
  const rzp = new window.Razorpay(options);
  rzp.open();
};
