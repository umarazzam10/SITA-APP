const User = require('./User');
const Thesis = require('./Thesis');
const Seminar = require('./Seminar');
const Defense = require('./Defense');
const Logbook = require('./Logbook');
const Notification = require('./Notification');

// User Relations
User.hasMany(Thesis, { foreignKey: 'student_id', as: 'theses' });
User.hasMany(Seminar, { foreignKey: 'student_id', as: 'seminars' });
User.hasMany(Defense, { foreignKey: 'student_id', as: 'defenses' });
User.hasMany(Logbook, { foreignKey: 'student_id', as: 'logbooks' });
User.hasMany(Notification, { foreignKey: 'user_id', as: 'notifications' });

// Thesis Relations
Thesis.belongsTo(User, { foreignKey: 'student_id', as: 'student' });
Thesis.hasOne(Seminar, { foreignKey: 'thesis_id', as: 'seminar' });

// Seminar Relations
Seminar.belongsTo(User, { foreignKey: 'student_id', as: 'student' });
Seminar.belongsTo(Thesis, { foreignKey: 'thesis_id', as: 'thesis' });
Seminar.hasOne(Defense, { foreignKey: 'seminar_id', as: 'defense' });

// Defense Relations
Defense.belongsTo(User, { foreignKey: 'student_id', as: 'student' });
Defense.belongsTo(Seminar, { foreignKey: 'seminar_id', as: 'seminar' });

// Logbook Relations
Logbook.belongsTo(User, { foreignKey: 'student_id', as: 'student' });

// Notification Relations
Notification.belongsTo(User, { foreignKey: 'user_id', as: 'user' });

module.exports = {
  User,
  Thesis,
  Seminar,
  Defense,
  Logbook,
  Notification
};