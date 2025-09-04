import React, { useState, useCallback } from "react";
import Cropper from "react-easy-crop";
import { useDropzone } from "react-dropzone";

const cropWidth = 600;
const cropHeight = 400;

function getCroppedImg(imageSrc, crop) {
    return new Promise((resolve, reject) => {
        const image = new window.Image();
        image.src = imageSrc;
        image.onload = () => {
            const canvas = document.createElement('canvas');
            canvas.width = cropWidth;
            canvas.height = cropHeight;
            const ctx = canvas.getContext('2d');
            // Draw the cropped area of the image onto the canvas
            ctx.drawImage(
                image,
                crop.x, crop.y, crop.width, crop.height, // source crop
                0, 0, cropWidth, cropHeight              // destination size
            );
            canvas.toBlob((blob) => {
                if (blob) {
                    resolve(blob);
                } else {
                    reject(new Error('Canvas is empty'));
                }
            }, 'image/jpeg');
        };
        image.onerror = () => {
            reject(new Error('Failed to load image'));
        };
    });
}

const ImageUploadCropModal = ({ onClose, onSave }) => {
    const [imageSrc, setImageSrc] = useState(null);
    const [crop, setCrop] = useState({ x: 0, y: 0 });
    const [zoom, setZoom] = useState(1);
    const [croppedAreaPixels, setCroppedAreaPixels] = useState(null);

    const onDrop = useCallback((acceptedFiles) => {
        const reader = new FileReader();
        reader.onload = () => setImageSrc(reader.result);
        reader.readAsDataURL(acceptedFiles[0]);
    }, []);
    const { getRootProps, getInputProps } = useDropzone({ onDrop, accept: 'image/*' });

    const onCropComplete = useCallback((_, croppedAreaPixels) => {
        setCroppedAreaPixels(croppedAreaPixels);
    }, []);

    const handleSave = async () => {
        const croppedImage = await getCroppedImg(imageSrc, croppedAreaPixels);
        onSave(croppedImage);
        onClose();
    };

    return (
        <div className="modal show d-block" tabIndex="-1">
            <div className="modal-dialog modal-lg">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">Edit Family Image</h5>
                        <button type="button" className="btn-close" onClick={onClose}></button>
                    </div>
                    <div className="modal-body">
                        {!imageSrc ? (
                            <div {...getRootProps()} className="border p-4 text-center" style={{ cursor: "pointer" }}>
                                <input {...getInputProps()} />
                                <p>Drag & drop image here, or click to browse</p>
                            </div>
                        ) : (
                            <div style={{ position: "relative", width: "100%", height: 400 }}>
                                <Cropper
                                    image={imageSrc}
                                    crop={crop}
                                    zoom={zoom}
                                    aspect={cropWidth / cropHeight}
                                    onCropChange={setCrop}
                                    onZoomChange={setZoom}
                                    onCropComplete={onCropComplete}
                                />
                            </div>
                        )}
                    </div>
                    <div className="modal-footer">
                        <button className="btn btn-secondary" onClick={onClose}>Cancel</button>
                        <button className="btn btn-primary" onClick={handleSave} disabled={!imageSrc}>Save</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ImageUploadCropModal;