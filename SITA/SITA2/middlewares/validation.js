const { validationResult } = require('express-validator');

exports.validate = (validations) => {
  return async (req, res, next) => {
    // Execute all validations
    await Promise.all(validations.map(validation => validation.run(req)));

    // Check for validation errors
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({
        success: false,
        message: 'Validation error',
        errors: errors.array().map(err => ({
          field: err.param,
          message: err.msg
        }))
      });
    }
    next();
  };
};

// Custom validation error messages
exports.messages = {
  required: '{field} is required',
  string: '{field} must be a string',
  email: 'Invalid email format',
  min: '{field} must be at least {min} characters',
  max: '{field} must not exceed {max} characters',
  enum: '{field} must be one of: {values}',
  date: 'Invalid date format',
  numeric: '{field} must be a number',
  boolean: '{field} must be true or false',
  exists: '{field} not found',
  unique: '{field} already exists'
};