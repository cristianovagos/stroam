FROM node:8.12.0-jessie as builder
WORKDIR /usr/src/angular-app
COPY . /usr/src/angular-app/
RUN npm install -g @angular/cli
RUN npm install
RUN npm run build

FROM nginx:1.13.9-alpine
COPY --from=builder /usr/src/angular-app/dist/AuthClient /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]