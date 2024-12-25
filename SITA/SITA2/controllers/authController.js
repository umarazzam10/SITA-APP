const jwt = require('jsonwebtoken');
const User = require('../models/User');
const config = require('../config/config');

exports.login = async (req, res, next) => {
  try {
    const { username, password } = req.body;

    // Find user
    const user = await User.findOne({ 
      where: { username },
      attributes: ['id', 'username', 'password', 'role', 'name', 'nim', 'profile_photo'] 
    });

    if (!user) {
      return res.status(401).json({
        success: false,
        message: 'Username atau password salah'
      });
    }

    // Check password
    const isValidPassword = await user.checkPassword(password);
    
    if (!isValidPassword) {
      return res.status(401).json({
        success: false,
        message: 'Username atau password salah'
      });
    }

    // Generate JWT token
    const token = jwt.sign(
      { id: user.id, role: user.role },
      config.jwt.secret,
      { expiresIn: config.jwt.expiresIn }
    );

    // Remove password from response
    const userData = user.toJSON();
    delete userData.password;

    res.json({
      success: true,
      message: 'Login berhasil',
      data: {
        token,
        user: userData
      }
    });

  } catch (error) {
    next(error);
  }
};

exports.getCurrentUser = async (req, res) => {
  try {
    const userData = req.user.toJSON();
    delete userData.password;
    
    res.json({
      success: true,
      data: userData
    });
  } catch (error) {
    next(error);
  }
};