FROM node:8.12.0-jessie
WORKDIR /usr/src/angular-app
COPY . /usr/src/angular-app/
EXPOSE 4200
RUN npm install -g @angular/cli
RUN npm install
CMD ["ng", "serve"]