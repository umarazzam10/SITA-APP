const express = require('express');
const { body } = require('express-validator');
const router = express.Router();
const authController = require('../controllers/authController');
const { validate } = require('../middlewares/validation');
const { verifyToken } = require('../middlewares/auth');

// Validation rules
const loginValidation = [
  body('username')
    .notEmpty()
    .withMessage('Username harus diisi'),
  body('password')
    .notEmpty()
    .withMessage('Password harus diisi')
];

// Routes
router.post('/login', validate(loginValidation), authController.login);
router.get('/me', verifyToken, authController.getCurrentUser);

module.exports = router;