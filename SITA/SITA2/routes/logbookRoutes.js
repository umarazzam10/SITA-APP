const express = require('express');
const { body, query } = require('express-validator');
const router = express.Router();
const logbookController = require('../controllers/logbookController');
const { verifyToken, isLecturer } = require('../middlewares/auth');
const { validate } = require('../middlewares/validation');

// Validation rules
const studentListValidation = [
  query('search')
    .optional()
    .isString()
    .withMessage('Search query must be a string')
];

const noteValidation = [
  body('note')
    .notEmpty()
    .withMessage('Note is required')
    .isString()
    .withMessage('Note must be a string')
];

// Routes
router.get('/students',
  verifyToken,
  isLecturer,
  validate(studentListValidation),
  logbookController.getStudentList
);

router.get('/student/:studentId',
  verifyToken,
  isLecturer,
  logbookController.getStudentLogbook
);

router.post('/lock/:studentId',
  verifyToken,
  isLecturer,
  logbookController.lockLogbook
);

router.post('/note/:id',
  verifyToken,
  isLecturer,
  validate(noteValidation),
  logbookController.addNote
);

router.get('/download/:studentId',
  verifyToken,
  isLecturer,
  logbookController.downloadLogbook
);

module.exports = router;