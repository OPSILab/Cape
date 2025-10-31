FROM node:14.17.0-alpine as builder
ARG BASE_HREF
RUN mkdir -p /app
WORKDIR /app
COPY package.json /app

RUN npm install
COPY . /app
RUN npm run build -- --prod --aot --base-href "/cape-dashboard/"

FROM nginx
EXPOSE 80
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html
COPY --from=builder /app/dist /usr/share/nginx/html/cape-dashboard

