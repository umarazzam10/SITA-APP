const express = require('express');
const { body } = require('express-validator');
const router = express.Router();
const userController = require('../controllers/userController');
const { verifyToken } = require('../middlewares/auth');
const { validate } = require('../middlewares/validation');
const { uploadProfilePhoto, handleUploadError } = require('../middlewares/upload');

// Validation rules
const updateProfileValidation = [
  body('username')
    .optional()
    .isLength({ min: 3 })
    .withMessage('Username must be at least 3 characters'),
  body('password')
    .optional()
    .isLength({ min: 6 })
    .withMessage('Password must be at least 6 characters')
];

// Routes
router.put('/profile',
  verifyToken,
  uploadProfilePhoto,
  handleUploadError,
  validate(updateProfileValidation),
  userController.updateProfile
);

router.get('/students/:id/progress',
  verifyToken,
  userController.getStudentProgress
);

module.exports = router;