import React, { useRef } from "react";

const Canvas = ({ imagePath, onSave }) => {
    const canvasRef = useRef(null);

    const handleMouseDown = (e) => {
        const canvas = canvasRef.current;
        const ctx = canvas.getContext("2d");
        ctx.beginPath();
        ctx.moveTo(e.nativeEvent.offsetX, e.nativeEvent.offsetY);
        canvas.isDrawing = true;
    };

    const handleMouseMove = (e) => {
        if (!canvasRef.current.isDrawing) return;
        const canvas = canvasRef.current;
        const ctx = canvas.getContext("2d");
        ctx.lineTo(e.nativeEvent.offsetX, e.nativeEvent.offsetY);
        ctx.stroke();
    };

    const handleMouseUp = () => {
        canvasRef.current.isDrawing = false;
    };

    const handleSave = () => {
        const canvas = canvasRef.current;
        const drawingData = canvas.toDataURL("image/png").split(",")[1]; // Base64
        onSave(drawingData);
    };

    return (
        <div>
            <canvas
                ref={canvasRef}
                width={800}
                height={600}
                style={{ border: "1px solid black", background: `url(${imagePath}) center/contain no-repeat` }}
                onMouseDown={handleMouseDown}
                onMouseMove={handleMouseMove}
                onMouseUp={handleMouseUp}
            />
            <button onClick={handleSave}>Save Drawing</button>
        </div>
    );
};

export default Canvas;
