const { User } = require('../models');
const { Op } = require('sequelize');
const fs = require('fs').promises;
const path = require('path');

exports.updateProfile = async (req, res, next) => {
  try {
    const { username, password } = req.body;
    const userId = req.user.id;

    // Check if username is already taken
    if (username) {
      const existingUser = await User.findOne({
        where: { 
          username,
          id: { [Op.ne]: userId }
        }
      });

      if (existingUser) {
        return res.status(400).json({
          success: false,
          message: 'Username sudah digunakan'
        });
      }
    }

    // Update user data
    const updateData = {};
    if (username) updateData.username = username;
    if (password) updateData.password = password; // Will be hashed by hook

    // Handle profile photo upload if exists
    if (req.file) {
      // Delete old profile photo if exists
      if (req.user.profile_photo) {
        const oldPhotoPath = path.join(__dirname, '..', req.user.profile_photo);
        try {
          await fs.unlink(oldPhotoPath);
        } catch (error) {
          console.error('Error deleting old profile photo:', error);
        }
      }
      updateData.profile_photo = req.file.path.replace(/\\/g, '/');
    }

    // Update user
    await req.user.update(updateData);

    // Get updated user data
    const updatedUser = await User.findByPk(userId, {
      attributes: { exclude: ['password'] }
    });

    res.json({
      success: true,
      message: 'Profile berhasil diupdate',
      data: updatedUser
    });

  } catch (error) {
    next(error);
  }
};

exports.getStudentProgress = async (req, res, next) => {
  try {
    const { studentId } = req.params;
    const student = await User.findOne({
      where: {
        id: studentId,
        role: 'mahasiswa'
      },
      include: [
        {
          model: Thesis,
          as: 'theses',
          include: {
            model: Seminar,
            as: 'seminar',
            include: {
              model: Defense,
              as: 'defense'
            }
          }
        }
      ],
      attributes: { exclude: ['password'] }
    });

    if (!student) {
      return res.status(404).json({
        success: false,
        message: 'Mahasiswa tidak ditemukan'
      });
    }

    // Format progress data
    const thesis = student.theses[0];
    const progress = {
      thesis: thesis ? {
        status: thesis.status,
        submitted_at: thesis.created_at
      } : null,
      seminar: thesis?.seminar ? {
        status: thesis.seminar.status,
        date: thesis.seminar.seminar_date,
        submitted_at: thesis.seminar.created_at
      } : null,
      defense: thesis?.seminar?.defense ? {
        status: thesis.seminar.defense.status,
        date: thesis.seminar.defense.defense_date,
        submitted_at: thesis.seminar.defense.created_at
      } : null
    };

    res.json({
      success: true,
      data: {
        student: {
          id: student.id,
          name: student.name,
          nim: student.nim,
          profile_photo: student.profile_photo
        },
        progress
      }
    });

  } catch (error) {
    next(error);
  }
};