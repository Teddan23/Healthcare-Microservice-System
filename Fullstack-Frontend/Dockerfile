# 1. Använd en Node-baserad bild för att bygga projektet
FROM node:18 AS builder

# 2. Sätt arbetskatalogen i containern
WORKDIR /app

# 3. Kopiera package.json och package-lock.json för att installera beroenden
COPY package.json package-lock.json ./

# 4. Installera beroenden
RUN npm install

# 5. Kopiera över hela projektkoden till arbetskatalogen
COPY . .

# 6. Bygg projektet för produktion
RUN npm run build

# 7. Använd en lättviktsbas för produktion (som nginx för att tjäna byggda filer)
FROM nginx:alpine

# 8. Kopiera byggda filer från builder-steg till nginx webroot
COPY --from=builder /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/nginx.conf

# 9. Exponera porten nginx kommer att lyssna på
EXPOSE 80

# 10. Starta nginx server
CMD ["nginx", "-g", "daemon off;"]
