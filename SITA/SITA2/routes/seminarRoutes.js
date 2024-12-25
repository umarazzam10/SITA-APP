const express = require('express');
const { body, query } = require('express-validator');
const router = express.Router();
const seminarController = require('../controllers/seminarController');
const { verifyToken, isLecturer } = require('../middlewares/auth');
const { validate } = require('../middlewares/validation');

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

const approveValidation = [
  body('seminar_date')
    .notEmpty()
    .withMessage('Seminar date is required')
    .isISO8601()
    .withMessage('Invalid date format')
];

const rejectValidation = [
  body('rejection_reason')
    .notEmpty()
    .withMessage('Rejection reason is required')
    .isString()
    .withMessage('Rejection reason must be a string'),
  body('suggested_date')
    .notEmpty()
    .withMessage('Suggested date is required')
    .isISO8601()
    .withMessage('Invalid date format')
];

// Routes
router.get('/',
  verifyToken,
  isLecturer,
  validate(listValidation),
  seminarController.getSeminarList
);

router.get('/:id',
  verifyToken,
  isLecturer,
  seminarController.getSeminarDetail
);

router.get('/:id/guidance-history',
  verifyToken,
  isLecturer,
  seminarController.getGuidanceHistory
);

router.post('/approve/:id',
  verifyToken,
  isLecturer,
  validate(approveValidation),
  seminarController.approveSeminar
);

router.post('/reject/:id',
  verifyToken,
  isLecturer,
  validate(rejectValidation),
  seminarController.rejectSeminar
);

router.get('/:id/thesis-review',
  verifyToken,
  isLecturer,
  seminarController.downloadThesisReview
);

module.exports = router;