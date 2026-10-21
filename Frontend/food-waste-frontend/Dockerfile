# Multi-stage Dockerfile for Angular Frontend
FROM node:20-alpine AS builder
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci --legacy-peer-deps || npm install --legacy-peer-deps

# Copy source and build Angular app
COPY . ./
RUN npm run build -- --configuration production

# Production Runtime with Nginx
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Clean default nginx files
RUN rm -rf ./*

# Copy compiled Angular distribution
COPY --from=builder /app/dist/food-waste-frontend/browser ./

# Copy custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
