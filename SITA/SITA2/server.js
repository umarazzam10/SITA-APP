const express = require('express');
const cors = require('cors');
const path = require('path');
const { testConnection } = require('./config/database');
const config = require('./config/config');

// Import routes
const authRoutes = require('./routes/authRoutes');
const userRoutes = require('./routes/userRoutes');
const thesisRoutes = require('./routes/thesisRoutes');
const seminarRoutes = require('./routes/seminarRoutes');
const defenseRoutes = require('./routes/defenseRoutes');
const logbookRoutes = require('./routes/logbookRoutes');
const notificationRoutes = require('./routes/notificationRoutes');
// Hapus atau komentari baris ini jika tidak digunakan
// const studentRoutes = require('./routes/studentRoutes');

const app = express();

// Test database connection
testConnection();

// Middleware
app.use(cors(config.cors.options));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Static file serving
app.use('/uploads', express.static(path.join(__dirname, config.upload.directory)));

// Routes
app.use('/api/auth', authRoutes);
app.use('/api/users', userRoutes);
app.use('/api/thesis', thesisRoutes);
app.use('/api/seminars', seminarRoutes);
app.use('/api/defense', defenseRoutes);
app.use('/api/logbooks', logbookRoutes);
app.use('/api/notifications', notificationRoutes);
// Hapus atau komentari baris ini jika tidak digunakan
// app.use('/api/students', studentRoutes);

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({
    success: false,
    message: 'Internal server error',
    errors: process.env.NODE_ENV === 'development' ? [err.message] : undefined
  });
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({
    success: false,
    message: 'API endpoint not found'
  });
});

const PORT = config.port;

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});

