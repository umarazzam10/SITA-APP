const { body, param, query } = require('express-validator');

/**
 * Common validation rules
 */
exports.rules = {
  // User validation rules
  username: () => 
    body('username')
      .notEmpty().withMessage('Username is required')
      .isLength({ min: 3 }).withMessage('Username must be at least 3 characters'),
  
  password: () =>
    body('password')
      .notEmpty().withMessage('Password is required')
      .isLength({ min: 6 }).withMessage('Password must be at least 6 characters'),

  // Thesis validation rules
  thesisTitle: () =>
    body('title')
      .notEmpty().withMessage('Title is required')
      .isLength({ min: 10 }).withMessage('Title must be at least 10 characters'),

  researchObject: () =>
    body('research_object')
      .notEmpty().withMessage('Research object is required'),

  methodology: () =>
    body('methodology')
      .notEmpty().withMessage('Methodology is required'),

  // Date validation rules
  seminarDate: () =>
    body('seminar_date')
      .notEmpty().withMessage('Seminar date is required')
      .isISO8601().withMessage('Invalid date format'),

  defenseDate: () =>
    body('defense_date')
      .notEmpty().withMessage('Defense date is required')
      .isISO8601().withMessage('Invalid date format'),

  // Common validation rules
  id: () =>
    param('id')
      .isNumeric().withMessage('Invalid ID format'),

  status: () =>
    query('status')
      .optional()
      .isIn(['pending', 'approved', 'rejected'])
      .withMessage('Invalid status value'),

  search: () =>
    query('search')
      .optional()
      .isString()
      .withMessage('Search query must be a string'),

  note: () =>
    body('note')
      .notEmpty().withMessage('Note is required')
      .isString().withMessage('Note must be a string'),

  rejectionReason: () =>
    body('rejection_reason')
      .notEmpty().withMessage('Rejection reason is required')
      .isString().withMessage('Rejection reason must be a string')
};

/**
 * Custom validation functions
 */
exports.custom = {
  // Check if date is in the future
  isFutureDate: (value) => {
    const date = new Date(value);
    const now = new Date();
    if (date <= now) {
      throw new Error('Date must be in the future');
    }
    return true;
  },

  // Check if file exists
  isFileExist: (value) => {
    if (!value) {
      throw new Error('File is required');
    }
    return true;
  },

  // Check if value exists in database
  existsInDb: async (Model, field, value) => {
    const item = await Model.findOne({ where: { [field]: value } });
    if (!item) {
      throw new Error(`${field} not found in database`);
    }
    return true;
  }
};

/**
 * Error formatting
 */
exports.formatError = (error) => ({
  field: error.param,
  message: error.msg
});