FROM node:18.14

WORKDIR /app

COPY package*.json ./

RUN yarn install --pure-lockfile

COPY . .

RUN npm run build

EXPOSE 4173

CMD ["npm", "run", "serve"]