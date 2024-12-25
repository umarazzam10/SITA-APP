const { User, Logbook, Notification } = require('../models');
const { Op } = require('sequelize');
const PDFDocument = require('pdfkit');
const fs = require('fs');
const path = require('path');

exports.getStudentList = async (req, res, next) => {
  try {
    const { search } = req.query;

    const whereClause = {
      role: 'mahasiswa'
    };

    if (search) {
      whereClause[Op.or] = [
        { name: { [Op.like]: `%${search}%` } },
        { nim: { [Op.like]: `%${search}%` } }
      ];
    }

    const students = await User.findAll({
      where: whereClause,
      attributes: ['id', 'name', 'nim', 'profile_photo'],
      include: [{
        model: Logbook,
        as: 'logbooks',
        attributes: ['id']
      }]
    });

    res.json({
      success: true,
      data: students
    });
  } catch (error) {
    next(error);
  }
};

exports.getStudentLogbook = async (req, res, next) => {
  try {
    const { studentId } = req.params;

    const student = await User.findByPk(studentId, {
      attributes: ['id', 'name', 'nim', 'profile_photo'],
      include: [{
        model: Logbook,
        as: 'logbooks',
        order: [['date', 'DESC']]
      }]
    });

    if (!student) {
      return res.status(404).json({
        success: false,
        message: 'Student not found'
      });
    }

    res.json({
      success: true,
      data: {
        student,
        logbooks: student.logbooks
      }
    });
  } catch (error) {
    next(error);
  }
};

exports.lockLogbook = async (req, res, next) => {
  try {
    const { studentId } = req.params;

    // Update all logbooks for the student
    await Logbook.update(
      { is_locked: true },
      { where: { student_id: studentId } }
    );

    // Create notification
    await Notification.create({
      user_id: studentId,
      title: 'Logbook Dikunci',
      message: 'Logbook Anda telah dikunci oleh dosen pembimbing.'
    });

    res.json({
      success: true,
      message: 'Logbook locked successfully'
    });
  } catch (error) {
    next(error);
  }
};

exports.addNote = async (req, res, next) => {
  try {
    const { id } = req.params;
    const { note } = req.body;

    const logbook = await Logbook.findByPk(id, {
      include: [{
        model: User,
        as: 'student',
        attributes: ['id', 'name']
      }]
    });

    if (!logbook) {
      return res.status(404).json({
        success: false,
        message: 'Logbook entry not found'
      });
    }

    // Update logbook note
    await logbook.update({ lecturer_notes: note });

    // Create notification
    await Notification.create({
      user_id: logbook.student_id,
      title: 'Catatan Logbook Baru',
      message: 'Dosen pembimbing telah memberikan catatan pada logbook Anda.'
    });

    res.json({
      success: true,
      message: 'Note added successfully',
      data: logbook
    });
  } catch (error) {
    next(error);
  }
};

exports.downloadLogbook = async (req, res, next) => {
  try {
    const { studentId } = req.params;

    const student = await User.findByPk(studentId, {
      attributes: ['id', 'name', 'nim'],
      include: [{
        model: Logbook,
        as: 'logbooks',
        order: [['date', 'ASC']]
      }]
    });

    if (!student) {
      return res.status(404).json({
        success: false,
        message: 'Student not found'
      });
    }

    // Create PDF document
    const doc = new PDFDocument();
    const filename = `logbook_${student.nim}_${Date.now()}.pdf`;
    const filePath = path.join(__dirname, '..', 'uploads', 'logbooks', filename);

    // Pipe PDF to file
    doc.pipe(fs.createWriteStream(filePath));

    // Add content to PDF
    doc.fontSize(16).text('Logbook Bimbingan Tugas Akhir', { align: 'center' });
    doc.moveDown();
    doc.fontSize(12).text(`Nama: ${student.name}`);
    doc.text(`NIM: ${student.nim}`);
    doc.moveDown();

    student.logbooks.forEach((logbook) => {
      doc.text(`Tanggal: ${new Date(logbook.date).toLocaleDateString()}`);
      doc.text('Aktivitas:', { underline: true });
      doc.text(logbook.activity);
      if (logbook.lecturer_notes) {
        doc.text('Catatan Dosen:', { underline: true });
        doc.text(logbook.lecturer_notes);
      }
      doc.moveDown();
    });

    // Finalize PDF
    doc.end();

    // Send file
    res.download(filePath, filename, (err) => {
      if (err) {
        next(err);
      }
      // Delete file after download
      fs.unlink(filePath, (unlinkError) => {
        if (unlinkError) {
          console.error('Error deleting temporary file:', unlinkError);
        }
      });
    });
  } catch (error) {
    next(error);
  }
};

module.exports = exports;