# Multi-stage Dockerfile for React + Vite frontend
# Stage 1: Build the React app with Node.js
# Stage 2: Serve with Nginx (production-grade, < 20MB image)

# ─── Stage 1: Build ──────────────────────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files first (layer caching — dependencies only rebuilt on package change)
COPY package.json package-lock.json ./
RUN npm ci --frozen-lockfile

# Copy source and build
COPY . .
RUN npm run build

# ─── Stage 2: Serve with Nginx ────────────────────────────────────────────────
FROM nginx:alpine AS runtime

# Copy built assets from builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy Nginx config for SPA routing
# Without this, refreshing on /dashboard returns 404 from Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
