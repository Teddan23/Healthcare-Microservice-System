FROM node:21.1

WORKDIR /app

# Kopiera package.json och package-lock.json om de finns
COPY package*.json ./

# Installera beroenden
RUN npm install --legacy-peer-deps

# Kopiera hela projektet
COPY . .

# Bygg projektet om det behövs (om det inte är en Node.js app som bara körs direkt)
# Om du använder något byggverktyg som React eller TypeScript, använd npm run build
#RUN npm run build 

# Exponera porten applikationen kör på
EXPOSE 3000

# Starta applikationen
CMD ["node", "app.js"]
