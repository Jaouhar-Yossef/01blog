# Build stage
FROM node:20 AS build

WORKDIR /app

# Copy only Angular app
COPY my-app/package*.json ./
RUN npm install

COPY my-app/ .

RUN npm run build

# Production stage
FROM nginx:alpine

COPY --from=build /app/dist/my-app/browser/ /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]