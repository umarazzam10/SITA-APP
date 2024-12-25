const { User, Thesis, Seminar, Logbook, Notification } = require('../models');
const { Op } = require('sequelize');
const fs = require('fs').promises;
const path = require('path');

exports.getSeminarList = async (req, res, next) => {
  try {
    const { status, search } = req.query;

    const where = {};
    if (status) {
      where.status = status;
    }

    const seminars = await Seminar.findAll({
      where,
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
      }, {
        model: Thesis,
        as: 'thesis',
        attributes: ['id', 'title', 'research_object', 'methodology']
      }],
      order: [['created_at', 'DESC']]
    });

    res.json({
      success: true,
      data: seminars
    });
  } catch (error) {
    next(error);
  }
};

exports.getSeminarDetail = async (req, res, next) => {
  try {
    const { id } = req.params;

    const seminar = await Seminar.findByPk(id, {
      include: [{
        model: User,
        as: 'student',
        attributes: ['id', 'name', 'nim', 'profile_photo']
      }, {
        model: Thesis,
        as: 'thesis'
      }]
    });

    if (!seminar) {
      return res.status(404).json({
        success: false,
        message: 'Seminar submission not found'
      });
    }

    res.json({
      success: true,
      data: seminar
    });
  } catch (error) {
    next(error);
  }
};

exports.getGuidanceHistory = async (req, res, next) => {
  try {
    const { studentId } = req.params;

    const logbooks = await Logbook.findAll({
      where: { student_id: studentId },
      order: [['date', 'DESC']],
      include: [{
        model: User,
        as: 'student',
        attributes: ['id', 'name', 'nim']
      }]
    });

    res.json({
      success: true,
      data: logbooks
    });
  } catch (error) {
    next(error);
  }
};

exports.approveSeminar = async (req, res, next) => {
  try {
    const { id } = req.params;

    const seminar = await Seminar.findByPk(id, {
      include: [{
        model: User,
        as: 'student',
        attributes: ['id', 'name']
      }]
    });

    if (!seminar) {
      return res.status(404).json({
        success: false,
        message: 'Seminar submission not found'
      });
    }

    // Update seminar status
    await seminar.update({ 
      status: 'approved',
      seminar_date: req.body.seminar_date
    });

    // Create notification
    await Notification.create({
      user_id: seminar.student_id,
      title: 'Pengajuan Seminar Disetujui',
      message: `Pengajuan seminar Anda telah disetujui dengan jadwal: ${req.body.seminar_date}`
    });

    res.json({
      success: true,
      message: 'Seminar submission approved successfully',
      data: seminar
    });
  } catch (error) {
    next(error);
  }
};

exports.rejectSeminar = async (req, res, next) => {
  try {
    const { id } = req.params;
    const { rejection_reason, suggested_date } = req.body;

    const seminar = await Seminar.findByPk(id, {
      include: [{
        model: User,
        as: 'student',
        attributes: ['id', 'name']
      }]
    });

    if (!seminar) {
      return res.status(404).json({
        success: false,
        message: 'Seminar submission not found'
      });
    }

    // Update seminar status
    await seminar.update({
      status: 'rejected',
      rejection_reason,
      suggested_date
    });

    // Create notification
    await Notification.create({
      user_id: seminar.student_id,
      title: 'Pengajuan Seminar Ditolak',
      message: `Pengajuan seminar Anda ditolak dengan alasan: ${rejection_reason}. Saran jadwal: ${suggested_date}`
    });

    res.json({
      success: true,
      message: 'Seminar submission rejected successfully',
      data: seminar
    });
  } catch (error) {
    next(error);
  }
};

exports.downloadThesisReview = async (req, res, next) => {
  try {
    const { id } = req.params;
    
    const seminar = await Seminar.findByPk(id, {
      include: [{
        model: Thesis,
        as: 'thesis',
        attributes: ['attachment_file']
      }]
    });

    if (!seminar || !seminar.thesis) {
      return res.status(404).json({
        success: false,
        message: 'Thesis file not found'
      });
    }

    const filePath = path.join(__dirname, '..', seminar.thesis.attachment_file);

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