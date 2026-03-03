const express = require('express');
const multer = require('multer');
const { uploadImage, editImage, drawOnImage, getAllImages } = require('../controllers/imageController');


const router = express.Router();

const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, './uploads');
    },
    filename: (req, file, cb) => {
        cb(null, `${file.originalname}`);
    },
});
const upload = multer({ storage });

router.post('/upload', upload.single('image'), uploadImage);

router.post('/edit', editImage);
router.post('/draw', drawOnImage);

router.get('/', getAllImages);

module.exports = router;
