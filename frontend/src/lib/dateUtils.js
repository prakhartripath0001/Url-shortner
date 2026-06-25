// src/lib/dateUtils.js
export function formatDistanceToNow(dateStr) {
  if (!dateStr) return "";
  const diff = Date.now() - new Date(dateStr).getTime();
  const abs = Math.abs(diff);
  const future = diff < 0;
  const prefix = future ? "in " : "";
  const suffix = future ? "" : " ago";
  if (abs < 60000) return "just now";
  if (abs < 3600000) return `${prefix}${Math.floor(abs / 60000)}m${suffix}`;
  if (abs < 86400000) return `${prefix}${Math.floor(abs / 3600000)}h${suffix}`;
  if (abs < 2592000000) return `${prefix}${Math.floor(abs / 86400000)}d${suffix}`;
  if (abs < 31536000000) return `${prefix}${Math.floor(abs / 2592000000)}mo${suffix}`;
  return `${prefix}${Math.floor(abs / 31536000000)}y${suffix}`;
}

export function formatDate(dateStr) {
  if (!dateStr) return "";
  return new Date(dateStr).toLocaleDateString("en-US", { day: "numeric", month: "short", year: "numeric" });
}
