FROM node:18.14

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

EXPOSE 4173

CMD npm ci && npm run build && npm run serve
