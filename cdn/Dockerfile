FROM node:8.12.0-jessie
WORKDIR /usr/src/app
COPY ./webrtc /usr/src/app/
EXPOSE 1935
RUN apt update && apt install -y firefox-esr
RUN npm install 
CMD ["node", "server.js"]