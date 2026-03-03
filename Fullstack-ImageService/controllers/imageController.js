const path = require('path');
const sharp = require('sharp');
const fs = require('fs');

exports.uploadImage = (req, res) => {
    if (!req.file) {
        return res.status(400).json({ message: 'No image uploaded' });
    }
    res.json({ filePath: `/uploads/${req.file.filename}` });
};

exports.drawOnImage = async (req, res) => {
    const { filePath, drawingData } = req.body;

    if (!filePath || !drawingData) {
        return res.status(400).json({ message: "Missing required parameters" });
    }

    const inputPath = path.join(__dirname, '../', filePath);
    const outputPath = path.join(__dirname, '../', `drawn-${filePath}`);

    try {
        const metadata = await sharp(inputPath).metadata();
        const { width, height } = metadata;

        if (!width || !height) {
            throw new Error('Could not retrieve image dimensions');
        }

        console.log(`Original image dimensions: Width=${width}, Height=${height}`);

        const drawingBuffer = Buffer.from(drawingData, "base64");

        const scaledDrawingBuffer = await sharp(drawingBuffer)
            .resize(width, height, { fit: 'inside' })
            .toBuffer();

        await sharp(inputPath)
            .composite([
                {
                    input: scaledDrawingBuffer,
                    top: 0,
                    left: 0,
                },
            ])
            .toFile(outputPath);

        res.json({ drawnFilePath: `/uploads/drawn-${filePath}` });
    } catch (err) {
        console.error("Error drawing on image:", err);
        res.status(500).json({ message: "Failed to draw on image", error: err.message });
    }
};


exports.editImage = async (req, res) => {
    const { filePath, text, x, y } = req.body;

    if (!filePath || !text || !x || !y) {
        return res.status(400).json({ message: 'Missing required parameters' });
    }

    const inputPath = path.join(__dirname, '../', filePath);
    const outputPath = path.join(__dirname, '../', `edited-${filePath}`);

    try {
        const metadata = await sharp(inputPath).metadata();
        const { width, height } = metadata;

        if (!width || !height) {
            throw new Error('Could not retrieve image dimensions');
        }

        console.log(`Original image dimensions: Width=${width}, Height=${height}`);

        const svg = `
            <svg width="${width}" height="${height}">
                <text x="${x}" y="${y}" font-size="50" fill="red" font-family="Arial">${text}</text>
            </svg>
        `;

        await sharp(inputPath)
            .composite([
                {
                    input: Buffer.from(svg),
                    top: 0,
                    left: 0,
                },
            ])
            .toFile(outputPath);

        console.log("Image edited successfully");
        console.log(svg);

        res.json({ editedFilePath: `/uploads/edited-${filePath}` });
    } catch (err) {
        console.error('Error editing image:', err.message);
        res.status(500).json({ message: 'Failed to edit image', error: err.message });
    }
};


exports.getAllImages = (req, res) => {
    const uploadsPath = path.join(__dirname, '../uploads');
    const drawnUploadsPath = path.join(__dirname, '../drawn-', 'uploads');
    const editedUploadsPath = path.join(__dirname, '../edited-', 'uploads');

    const getImagesFromFolder = (folderPath) => {
        return new Promise((resolve, reject) => {
            fs.readdir(folderPath, (err, files) => {
                if (err) {
                    reject(err);
                } else {
                    const images = files.filter(file => ['.jpg', '.jpeg', '.png', '.gif'].includes(path.extname(file).toLowerCase()));
                    resolve(images.map(image => `/uploads/${image}`));
                }
            });
        });
    };

    Promise.all([
        getImagesFromFolder(uploadsPath),
        getImagesFromFolder(drawnUploadsPath),
        getImagesFromFolder(editedUploadsPath)
    ])
        .then(([uploadsImages, drawnImages, editedImages]) => {
            res.json({
                uploads: uploadsImages,
                drawn: drawnImages,
                edited: editedImages
            });
        })
        .catch(err => {
            console.error('Error loading images:', err);
            res.status(500).json({ message: 'Failed to load images', error: err.message });
        });
};
