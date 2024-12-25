const express = require('express');
const { body, query } = require('express-validator');
const router = express.Router();
const thesisController = require('../controllers/thesisController');
const { verifyToken, isLecturer } = require('../middlewares/auth');
const { validate } = require('../middlewares/validation');
const { uploadThesisFile, handleUploadError } = require('../middlewares/upload');

// Validation rules
const listValidation = [
  query('status')
    .optional()
    .isIn(['pending', 'approved', 'rejected'])
    .withMessage('Invalid status value'),
  query('search')
    .optional()
    .isString()
    .withMessage('Search query must be a string')
];

const rejectValidation = [
  body('rejection_reason')
    .notEmpty()
    .withMessage('Rejection reason is required')
    .isString()
    .withMessage('Rejection reason must be a string')
];

// Routes
router.get('/',
  verifyToken,
  isLecturer,
  validate(listValidation),
  thesisController.getThesisList
);

router.get('/:id',
  verifyToken,
  isLecturer,
  thesisController.getThesisDetail
);

router.post('/approve/:id',
  verifyToken,
  isLecturer,
  thesisController.approveThesis
);

router.post('/reject/:id',
  verifyToken,
  isLecturer,
  validate(rejectValidation),
  thesisController.rejectThesis
);

router.get('/file/:filename',
  verifyToken,
  isLecturer,
  thesisController.downloadThesisFile
);

module.exports = router;