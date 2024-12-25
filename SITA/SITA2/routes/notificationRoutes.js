const express = require('express');
const router = express.Router();
const notificationController = require('../controllers/notificationController');
const { verifyToken } = require('../middlewares/auth');

// Routes
router.get('/',
  verifyToken,
  notificationController.getNotifications
);

router.put('/:id/read',
  verifyToken,
  notificationController.markAsRead
);

router.put('/mark-all-read',
  verifyToken,
  notificationController.markAllAsRead
);

router.get('/unread-count',
  verifyToken,
  notificationController.getUnreadCount
);

module.exports = router;