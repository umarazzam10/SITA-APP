const express = require('express');
const { body, query } = require('express-validator');
const router = express.Router();
const defenseController = require('../controllers/defenseController');
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
  body('defense_date')
    .notEmpty()
    .withMessage('Defense date is required')
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
  defenseController.getDefenseList
);

router.get('/:id',
  verifyToken,
  isLecturer,
  defenseController.getDefenseDetail
);

router.post('/approve/:id',
  verifyToken,
  isLecturer,
  validate(approveValidation),
  defenseController.approveDefense
);

router.post('/reject/:id',
  verifyToken,
  isLecturer,
  validate(rejectValidation),
  defenseController.rejectDefense
);

router.get('/approval-letter/:id',
  verifyToken,
  isLecturer,
  defenseController.getApprovalLetter
);

module.exports = router;