const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/database');

const Defense = sequelize.define('Defense', {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true
  },
  seminar_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'seminar_submissions',
      key: 'id'
    }
  },
  student_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'users',
      key: 'id'
    }
  },
  title: {
    type: DataTypes.STRING(255),
    allowNull: false,
    validate: {
      notEmpty: true
    }
  },
  research_object: {
    type: DataTypes.TEXT,
    allowNull: false,
    validate: {
      notEmpty: true
    }
  },
  methodology: {
    type: DataTypes.TEXT,
    allowNull: false,
    validate: {
      notEmpty: true
    }
  },
  defense_date: {
    type: DataTypes.DATE,
    allowNull: true
  },
  approval_letter_file: {
    type: DataTypes.STRING(255),
    allowNull: true
  },
  status: {
    type: DataTypes.ENUM('pending', 'approved', 'rejected'),
    defaultValue: 'pending'
  },
  rejection_reason: {
    type: DataTypes.TEXT,
    allowNull: true
  },
  suggested_date: {
    type: DataTypes.DATE,
    allowNull: true
  }
}, {
  tableName: 'defense_submissions',
  timestamps: true,
  underscored: true
});

module.exports = Defense;