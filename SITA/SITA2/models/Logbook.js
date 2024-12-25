const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/database');

const Logbook = sequelize.define('Logbook', {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true
  },
  student_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'users',
      key: 'id'
    }
  },
  date: {
    type: DataTypes.DATE,
    allowNull: false
  },
  activity: {
    type: DataTypes.TEXT,
    allowNull: false,
    validate: {
      notEmpty: true
    }
  },
  is_locked: {
    type: DataTypes.BOOLEAN,
    defaultValue: false
  },
  lecturer_notes: {
    type: DataTypes.TEXT,
    allowNull: true
  }
}, {
  tableName: 'logbooks',
  timestamps: true,
  underscored: true
});

module.exports = Logbook;