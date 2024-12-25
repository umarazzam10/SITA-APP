/**
 * Send success response
 * @param {object} res - Express response object
 * @param {string} message - Success message
 * @param {object} data - Response data
 * @param {number} statusCode - HTTP status code (default: 200)
 */
exports.success = (res, message, data = null, statusCode = 200) => {
    const response = {
      success: true,
      message: message
    };
  
    if (data !== null) {
      response.data = data;
    }
  
    return res.status(statusCode).json(response);
  };
  
  /**
   * Send error response
   * @param {object} res - Express response object
   * @param {string} message - Error message
   * @param {array} errors - Array of error details
   * @param {number} statusCode - HTTP status code (default: 500)
   */
  exports.error = (res, message, errors = null, statusCode = 500) => {
    const response = {
      success: false,
      message: message
    };
  
    if (errors !== null) {
      response.errors = errors;
    }
  
    return res.status(statusCode).json(response);
  };
  
  /**
   * Send validation error response
   * @param {object} res - Express response object
   * @param {array} errors - Validation errors array
   */
  exports.validationError = (res, errors) => {
    return res.status(400).json({
      success: false,
      message: 'Validation error',
      errors: errors.array().map(err => ({
        field: err.param,
        message: err.msg
      }))
    });
  };