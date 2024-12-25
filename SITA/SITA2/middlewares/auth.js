const jwt = require('jsonwebtoken');
const { User } = require('../models');
const config = require('../config/config');

exports.verifyToken = async (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;
    
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({
        success: false,
        message: 'Token tidak ditemukan'
      });
    }

    const token = authHeader.split(' ')[1];
    
    try {
      const decoded = jwt.verify(token, config.jwt.secret);
      
      const user = await User.findByPk(decoded.id, {
        attributes: { exclude: ['password'] }
      });

      if (!user) {
        return res.status(401).json({
          success: false,
          message: 'User tidak ditemukan'
        });
      }

      req.user = user;
      next();
    } catch (error) {
      if (error.name === 'JsonWebTokenError') {
        return res.status(401).json({
          success: false,
          message: 'Token tidak valid'
        });
      }
      if (error.name === 'TokenExpiredError') {
        return res.status(401).json({
          success: false,
          message: 'Token telah kadaluarsa'
        });
      }
      throw error;
    }
  } catch (error) {
    next(error);
  }
};

exports.isLecturer = (req, res, next) => {
  if (req.user.role !== 'dosen') {
    return res.status(403).json({
      success: false,
      message: 'Akses terbatas untuk dosen'
    });
  }
  next();
};

exports.isStudent = (req, res, next) => {
  if (req.user.role !== 'mahasiswa') {
    return res.status(403).json({
      success: false,
      message: 'Akses terbatas untuk mahasiswa'
    });
  }
  next();
};