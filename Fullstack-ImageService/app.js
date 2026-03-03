const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');
const cors = require('cors');
const multer = require('multer');
const sharp = require('sharp');
const fs = require('fs');

const imageRoutes = require('./routes/imageRoutes');

const app = express();

app.use(cors());

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));
app.use('/drawn-uploads', express.static(path.join(__dirname, 'drawn-', 'uploads')));
app.use('/edited-uploads', express.static(path.join(__dirname, 'edited-', 'uploads')));

app.use('/api/images', imageRoutes);

const PORT = 3000;
app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});
