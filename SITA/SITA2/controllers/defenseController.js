const { User, Thesis, Seminar, Defense, Notification } = require('../models');
const { Op } = require('sequelize');
const fs = require('fs').promises;
const path = require('path');

exports.getDefenseList = async (req, res, next) => {
  try {
    const { status, search } = req.query;

    const where = {};
    if (status) {
      where.status = status;
    }

    const defenses = await Defense.findAll({
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
        model: Seminar,
        as: 'seminar',
        include: [{
          model: Thesis,
          as: 'thesis',
          attributes: ['id', 'title', 'research_object', 'methodology']
        }]
      }],
      order: [['created_at', 'DESC']]
    });

    res.json({
      success: true,
      data: defenses
    });
  } catch (error) {
    next(error);
  }
};

exports.getDefenseDetail = async (req, res, next) => {
  try {
    const { id } = req.params;

    const defense = await Defense.findByPk(id, {
      include: [{
        model: User,
        as: 'student',
        attributes: ['id', 'name', 'nim', 'profile_photo']
      }, {
        model: Seminar,
        as: 'seminar',
        include: [{
          model: Thesis,
          as: 'thesis',
          attributes: ['id', 'title', 'research_object', 'methodology', 'attachment_file']
        }]
      }]
    });

    if (!defense) {
      return res.status(404).json({
        success: false,
        message: 'Defense submission not found'
      });
    }

    res.json({
      success: true,
      data: defense
    });
  } catch (error) {
    next(error);
  }
};

exports.approveDefense = async (req, res, next) => {
  try {
    const { id } = req.params;
    const { defense_date } = req.body;

    const defense = await Defense.findByPk(id, {
      include: [{
        model: User,
        as: 'student',
        attributes: ['id', 'name']
      }]
    });

    if (!defense) {
      return res.status(404).json({
        success: false,
        message: 'Defense submission not found'
      });
    }

    // Update defense data
    await defense.update({
      status: 'approved',
      defense_date
    });

    // Create notification
    await Notification.create({
      user_id: defense.student_id,
      title: 'Pengajuan Sidang Disetujui',
      message: `Pengajuan sidang Anda telah disetujui dengan jadwal: ${defense_date}`
    });

    res.json({
      success: true,
      message: 'Defense submission approved successfully',
      data: defense
    });
  } catch (error) {
    next(error);
  }
};

exports.rejectDefense = async (req, res, next) => {
  try {
    const { id } = req.params;
    const { rejection_reason, suggested_date } = req.body;

    const defense = await Defense.findByPk(id, {
      include: [{
        model: User,
        as: 'student',
        attributes: ['id', 'name']
      }]
    });

    if (!defense) {
      return res.status(404).json({
        success: false,
        message: 'Defense submission not found'
      });
    }

    // Update defense data
    await defense.update({
      status: 'rejected',
      rejection_reason,
      suggested_date
    });

    // Create notification
    await Notification.create({
      user_id: defense.student_id,
      title: 'Pengajuan Sidang Ditolak',
      message: `Pengajuan sidang Anda ditolak dengan alasan: ${rejection_reason}. Saran jadwal: ${suggested_date}`
    });

    res.json({
      success: true,
      message: 'Defense submission rejected successfully',
      data: defense
    });
  } catch (error) {
    next(error);
  }
};

exports.getApprovalLetter = async (req, res, next) => {
  try {
    const { id } = req.params;

    const defense = await Defense.findByPk(id);
    if (!defense || !defense.approval_letter_file) {
      return res.status(404).json({
        success: false,
        message: 'Approval letter not found'
      });
    }

    const filePath = path.join(__dirname, '..', defense.approval_letter_file);

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