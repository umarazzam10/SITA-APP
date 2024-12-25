const bcrypt = require('bcryptjs');

async function testPassword() {
  const password = "password123";
  
  // Generate hash baru
  console.log("Password asli:", password);
  const hash = await bcrypt.hash(password, 10);
  console.log("Hash baru dibuat:", hash);
  
  // Test verifikasi dengan hash baru
  const isValid = await bcrypt.compare(password, hash);
  console.log("Hasil verifikasi dengan hash baru:", isValid);
  
  // Test verifikasi dengan hash dari database
  const dbHash = "$2a$10$MtfwKyVqrOqgq1peowZTQu.DPNmCDU0JQ3mGEzSkhHSyy9we9uRo2";
  const isValidDb = await bcrypt.compare(password, dbHash);
  console.log("Hasil verifikasi dengan hash database:", isValidDb);
}

testPassword();