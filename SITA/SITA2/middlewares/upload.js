const multer = require('multer');
const path = require('path');
const fs = require('fs');
const config = require('../config/config');

// Ensure upload directory exists
const uploadDir = config.upload.directory;
if (!fs.existsSync(uploadDir)) {
  fs.mkdirSync(uploadDir, { recursive: true });
}

// Configure storage
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    const folder = getUploadFolder(file.fieldname);
    const fullPath = path.join(uploadDir, folder);
    
    if (!fs.existsSync(fullPath)) {
      fs.mkdirSync(fullPath, { recursive: true });
    }
    
    cb(null, fullPath);
  },
  filename: (req, file, cb) => {
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
    cb(null, uniqueSuffix + path.extname(file.originalname));
  }
});

// File filter
const fileFilter = (req, file, cb) => {
  const allowedMimeTypes = Object.keys(config.upload.allowedMimeTypes);
  
  if (allowedMimeTypes.includes(file.mimetype)) {
    cb(null, true);
  } else {
    cb(new Error('Invalid file type. Allowed types: ' + allowedMimeTypes.join(', ')));
  }
};

// Get upload folder based on fieldname
const getUploadFolder = (fieldname) => {
  switch (fieldname) {
    case 'profile_photo':
      return 'profiles';
    case 'thesis_file':
      return 'thesis';
    case 'approval_letter':
      return 'letters';
    default:
      return 'others';
  }
};

// Create multer upload instance
const upload = multer({
  storage: storage,
  fileFilter: fileFilter,
  limits: {
    fileSize: config.upload.maxFileSize
  }
});

// Middleware for different upload types
exports.uploadProfilePhoto = upload.single('profile_photo');
exports.uploadThesisFile = upload.single('thesis_file');
exports.uploadApprovalLetter = upload.single('approval_letter');

// Error handler middleware
exports.handleUploadError = (err, req, res, next) => {
  if (err instanceof multer.MulterError) {
    if (err.code === 'LIMIT_FILE_SIZE') {
      return res.status(400).json({
        success: false,
        message: `File too large. Maximum size is ${config.upload.maxFileSize / (1024 * 1024)}MB`
      });
    }
    return res.status(400).json({
      success: false,
      message: 'File upload error: ' + err.message
    });
  }
  
  if (err) {
    return res.status(400).json({
      success: false,
      message: err.message
    });
  }
  
  next();
};