const { User, Thesis, Notification } = require('../models');
const { Op } = require('sequelize');
const fs = require('fs').promises;
const path = require('path');

exports.getThesisList = async (req, res, next) => {
  try {
    const { status, search } = req.query;
    
    // Build where clause
    const where = {};
    if (status) {
      where.status = status;
    }

    // Get thesis submissions with student data
    const submissions = await Thesis.findAll({
      include: [{
        model: User,
        as: 'student',
        attributes: ['id', 'name', 'nim', 'profile_photo'],
        where: search ? {
          [Op.or]: [
            { name: { [Op.like]: `%${search}%` } },
            { nim: { [Op.like]: `%${search}%` } }
          ]
        } : undefined
      }],
      order: [['created_at', 'DESC']]
    });

    res.json({
      success: true,
      data: submissions
    });
  } catch (error) {
    next(error);
  }
};

exports.getThesisDetail = async (req, res, next) => {
  try {
    const { id } = req.params;

    const thesis = await Thesis.findByPk(id, {
      include: [{
        model: User,
        as: 'student',
        attributes: ['id', 'name', 'nim', 'profile_photo']
      }]
    });

    if (!thesis) {
      return res.status(404).json({
        success: false,
        message: 'Thesis submission not found'
      });
    }

    res.json({
      success: true,
      data: thesis
    });
  } catch (error) {
    next(error);
  }
};

exports.approveThesis = async (req, res, next) => {
  try {
    const { id } = req.params;

    const thesis = await Thesis.findByPk(id, {
      include: [{
        model: User,
        as: 'student',
        attributes: ['id', 'name']
      }]
    });

    if (!thesis) {
      return res.status(404).json({
        success: false,
        message: 'Thesis submission not found'
      });
    }

    // Update thesis status
    await thesis.update({ status: 'approved' });

    // Create notification for student
    await Notification.create({
      user_id: thesis.student_id,
      title: 'Pengajuan TA Disetujui',
      message: 'Pengajuan TA Anda telah disetujui. Silakan lanjut ke tahap seminar.'
    });

    res.json({
      success: true,
      message: 'Thesis submission approved successfully',
      data: thesis
    });
  } catch (error) {
    next(error);
  }
};

exports.rejectThesis = async (req, res, next) => {
  try {
    const { id } = req.params;
    const { rejection_reason } = req.body;

    const thesis = await Thesis.findByPk(id, {
      include: [{
        model: User,
        as: 'student',
        attributes: ['id', 'name']
      }]
    });

    if (!thesis) {
      return res.status(404).json({
        success: false,
        message: 'Thesis submission not found'
      });
    }

    // Update thesis status and rejection reason
    await thesis.update({
      status: 'rejected',
      rejection_reason
    });

    // Create notification for student
    await Notification.create({
      user_id: thesis.student_id,
      title: 'Pengajuan TA Ditolak',
      message: `Pengajuan TA Anda ditolak dengan alasan: ${rejection_reason}`
    });

    res.json({
      success: true,
      message: 'Thesis submission rejected successfully',
      data: thesis
    });
  } catch (error) {
    next(error);
  }
};

exports.downloadThesisFile = async (req, res, next) => {
  try {
    const { filename } = req.params;
    const filePath = path.join(__dirname, '..', 'uploads', 'thesis', filename);

    try {
      await fs.access(filePath);
    } catch (error) {
      return res.status(404).json({
        success: false,
        message: 'File not found'
      });
    }

    res.download(filePath);
  } catch (error) {
    next(error);
  }
};