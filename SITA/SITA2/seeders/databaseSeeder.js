const fs = require('fs');
const path = require('path');
const bcrypt = require('bcryptjs');
const { sequelize } = require('../config/database');
const { User, Thesis, Seminar, Defense, Logbook } = require('../models');

const copyFileToUploads = (sourcePath, targetDir, filename) => {
  try {
    // Log untuk debugging
    console.log('Copying file:');
    console.log('Source path:', sourcePath);
    console.log('Target dir:', targetDir);
    console.log('Filename:', filename);

    // Buat direktori jika belum ada
    const targetPath = path.join(__dirname, '..', 'uploads', targetDir);
    if (!fs.existsSync(targetPath)) {
      fs.mkdirSync(targetPath, { recursive: true });
      console.log('Created directory:', targetPath);
    }

    // Cek file sumber
    const sourceFile = path.join(__dirname, 'dummy_files', sourcePath);
    if (!fs.existsSync(sourceFile)) {
      console.error('Source file not found:', sourceFile);
      return null;
    }

    // Copy file
    const targetFile = path.join(targetPath, filename);
    fs.copyFileSync(sourceFile, targetFile);
    
    // Return path untuk database (gunakan path relatif dengan forward slashes)
    const dbPath = `uploads/${targetDir}/${filename}`.replace(/\\/g, '/');
    console.log('Saved path:', dbPath);
    return dbPath;

  } catch (error) {
    console.error('Error in copyFileToUploads:', error);
    return null;
  }
};

const seedDatabase = async () => {
  try {
    await sequelize.sync({ force: true });

    // Create dosen
    const dosen = await User.create({
      username: 'dosen1',
      password: 'password123',
      role: 'dosen',
      name: 'Dr. Budi Santoso',
      profile_photo: null
    });

    // Create mahasiswa with profile photos
    const mahasiswa1Profile = copyFileToUploads('profile1.jpeg', 'profiles', '12345678_profile.jpeg');
    console.log('Mahasiswa 1 profile path:', mahasiswa1Profile);
    
    const mahasiswa1 = await User.create({
      username: 'mahasiswa1',
      password: 'password123',
      role: 'mahasiswa',
      name: 'John Doe',
      nim: '12345678',
      profile_photo: mahasiswa1Profile
    });

    const mahasiswa2Profile = copyFileToUploads('profile2.jpeg', 'profiles', '87654321_profile.jpeg');
    console.log('Mahasiswa 2 profile path:', mahasiswa2Profile);
    
    const mahasiswa2 = await User.create({
      username: 'mahasiswa2',
      password: 'password123',
      role: 'mahasiswa',
      name: 'Jane Smith',
      nim: '87654321',
      profile_photo: mahasiswa2Profile
    });

    // Create thesis submissions with files
    const thesis1File = copyFileToUploads('thesis1.pdf', 'thesis', '12345678_thesis.pdf');
    console.log('Thesis 1 file path:', thesis1File);
    
    const thesis1 = await Thesis.create({
      student_id: mahasiswa1.id,
      title: 'Implementasi Machine Learning dalam Prediksi Cuaca',
      research_object: 'Data cuaca historis Indonesia',
      methodology: 'Metode penelitian kuantitatif dengan pendekatan machine learning',
      attachment_file: thesis1File,
      status: 'approved'  // Changed to approved so we can create seminar/defense
    });

    const thesis2File = copyFileToUploads('thesis2.pdf', 'thesis', '87654321_thesis.pdf');
    console.log('Thesis 2 file path:', thesis2File);
    
    const thesis2 = await Thesis.create({
      student_id: mahasiswa2.id,
      title: 'Analisis Keamanan Jaringan IoT',
      research_object: 'Perangkat IoT rumah pintar',
      methodology: 'Penelitian eksperimental dengan pendekatan blackbox testing',
      attachment_file: thesis2File,
      status: 'approved'
    });

    // Create seminar submissions for both students
    const seminar1 = await Seminar.create({
      thesis_id: thesis1.id,
      student_id: mahasiswa1.id,
      title: 'Implementasi Machine Learning dalam Prediksi Cuaca',
      research_object: 'Data cuaca historis Indonesia',
      methodology: 'Metode penelitian kuantitatif dengan pendekatan machine learning',
      seminar_date: new Date('2024-01-10'),
      status: 'approved'  // Changed to approved so we can create defense
    });

    const seminar2 = await Seminar.create({
      thesis_id: thesis2.id,
      student_id: mahasiswa2.id,
      title: 'Analisis Keamanan Jaringan IoT',
      research_object: 'Perangkat IoT rumah pintar',
      methodology: 'Penelitian eksperimental dengan pendekatan blackbox testing',
      seminar_date: new Date('2024-01-15'),
      status: 'approved'
    });

    // Create defense submissions with files for both students
    const defense1File = copyFileToUploads('defense1.pdf', 'defense', '12345678_defense.pdf');
    console.log('Defense 1 file path:', defense1File);

    const defense1 = await Defense.create({
      seminar_id: seminar1.id,
      student_id: mahasiswa1.id,
      title: 'Implementasi Machine Learning dalam Prediksi Cuaca',
      research_object: 'Data cuaca historis Indonesia',
      methodology: 'Metode penelitian kuantitatif dengan pendekatan machine learning',
      defense_date: new Date('2024-01-20'),
      approval_letter_file: defense1File,
      status: 'pending'
    });

    const defense2File = copyFileToUploads('defense1.pdf', 'defense', '87654321_defense.pdf');
    console.log('Defense 2 file path:', defense2File);

    const defense2 = await Defense.create({
      seminar_id: seminar2.id,
      student_id: mahasiswa2.id,
      title: 'Analisis Keamanan Jaringan IoT',
      research_object: 'Perangkat IoT rumah pintar',
      methodology: 'Penelitian eksperimental dengan pendekatan blackbox testing',
      defense_date: new Date('2024-01-25'),
      approval_letter_file: defense2File,
      status: 'pending'
    });

    // Create logbook entries
    await Logbook.create({
      student_id: mahasiswa2.id,
      date: new Date('2024-01-01'),
      activity: 'Diskusi metodologi penelitian dengan dosen pembimbing',
      is_locked: false
    });

    await Logbook.create({
      student_id: mahasiswa2.id,
      date: new Date('2024-01-08'),
      activity: 'Pengumpulan data dan analisis awal',
      is_locked: false
    });

    // Log hasil seeding
    const users = await User.findAll();
    console.log('\nCreated Users:', users.map(u => ({
      id: u.id,
      username: u.username,
      profile_photo: u.profile_photo
    })));

    const theses = await Thesis.findAll();
    console.log('\nCreated Theses:', theses.map(t => ({
      id: t.id,
      title: t.title,
      attachment_file: t.attachment_file
    })));

    const defenses = await Defense.findAll();
    console.log('\nCreated Defenses:', defenses.map(d => ({
      id: d.id,
      title: d.title,
      attachment_file: d.attachment_file
    })));

    console.log('\nDatabase seeded successfully!');
    console.log('\nTest Account Credentials:');
    console.log('Dosen:');
    console.log('Username: dosen1');
    console.log('Password: password123');
    console.log('\nMahasiswa:');
    console.log('Username: mahasiswa1');
    console.log('Password: password123');

  } catch (error) {
    console.error('Error seeding database:', error);
    throw error;
  }
};

// Run seeder
if (require.main === module) {
  seedDatabase()
    .then(() => process.exit())
    .catch(error => {
      console.error(error);
      process.exit(1);
    });
}

module.exports = seedDatabase;