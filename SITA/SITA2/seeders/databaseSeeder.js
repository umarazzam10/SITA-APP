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
      username: 'husnilK',
      password: 'husnil123',
      role: 'dosen',
      name: 'Husnil Kamil',
      profile_photo: null
    });

       // Create mahasiswa with profile photos
       const mahasiswa1Profile = copyFileToUploads('profile1.jpeg', 'profiles', '12345678_profile.jpeg');
       const mahasiswa1 = await User.create({
           username: 'mahasiswa1',
           password: 'password123',
           role: 'mahasiswa',
           name: 'Muhammad Zaki',
           nim: '2211523031',
           profile_photo: mahasiswa1Profile
       });

       const mahasiswa2Profile = copyFileToUploads('profile2.jpeg', 'profiles', '87654321_profile.jpeg');
       const mahasiswa2 = await User.create({
           username: 'mahasiswa2',
           password: 'password123',
           role: 'mahasiswa',
           name: 'Umar Abdullah',
           nim: '2211521019',
           profile_photo: mahasiswa2Profile
       });

       const mahasiswa3Profile = copyFileToUploads('profile3.jpeg', 'profiles', '56785678_profile.jpeg');
       const mahasiswa3 = await User.create({
           username: 'mahasiswa3',
           password: 'password123',
           role: 'mahasiswa',
           name: 'Ghifari Rizki',
           nim: '2211522011',
           profile_photo: mahasiswa3Profile
       });

       // Create thesis submissions
       const thesis1File = copyFileToUploads('thesis1.pdf', 'thesis', '12345678_thesis.pdf');
       const thesis1 = await Thesis.create({
           student_id: mahasiswa1.id,
           title: 'Implementasi AI dalam Optimisasi Lalu Lintas',
           research_object: 'Sistem transportasi di Jakarta',
           methodology: 'Analisis kuantitatif berbasis simulasi AI',
           attachment_file: thesis1File,
           status: 'pending'
       });

       const thesis2File = copyFileToUploads('thesis2.pdf', 'thesis', '87654321_thesis.pdf');
       const thesis2 = await Thesis.create({
           student_id: mahasiswa2.id,
           title: 'Analisis Keamanan Data pada Sistem Cloud',
           research_object: 'Penyimpanan data berbasis cloud',
           methodology: 'Studi eksperimental menggunakan metode penetration testing',
           attachment_file: thesis2File,
           status: 'pending'
       });

       const thesis3File = copyFileToUploads('thesis3.pdf', 'thesis', '56785678_thesis.pdf');
       const thesis3 = await Thesis.create({
           student_id: mahasiswa3.id,
           title: 'Pemanfaatan Blockchain untuk Voting Elektronik',
           research_object: 'Sistem e-voting nasional',
           methodology: 'Metode pengembangan berbasis prototipe',
           attachment_file: thesis3File,
           status: 'pending'
       });

       // Create seminar submissions
       const seminar1 = await Seminar.create({
           thesis_id: thesis1.id,
           student_id: mahasiswa1.id,
           title: thesis1.title,
           research_object: thesis1.research_object,
           methodology: thesis1.methodology,
           seminar_date: new Date('2024-01-10'),
           status: 'pending'
       });

       const seminar2 = await Seminar.create({
           thesis_id: thesis2.id,
           student_id: mahasiswa2.id,
           title: thesis2.title,
           research_object: thesis2.research_object,
           methodology: thesis2.methodology,
           seminar_date: new Date('2024-01-15'),
           status: 'pending'
       });

       const seminar3 = await Seminar.create({
           thesis_id: thesis3.id,
           student_id: mahasiswa3.id,
           title: thesis3.title,
           research_object: thesis3.research_object,
           methodology: thesis3.methodology,
           seminar_date: new Date('2024-01-20'),
           status: 'pending'
       });

       // Create defense submissions
       const defense1File = copyFileToUploads('defense1.pdf', 'defense', '12345678_defense.pdf');
       const defense1 = await Defense.create({
           seminar_id: seminar1.id,
           student_id: mahasiswa1.id,
           title: seminar1.title,
           research_object: seminar1.research_object,
           methodology: seminar1.methodology,
           defense_date: new Date('2024-01-25'),
           approval_letter_file: defense1File,
           status: 'pending'
       });

       const defense2File = copyFileToUploads('defense2.pdf', 'defense', '87654321_defense.pdf');
       const defense2 = await Defense.create({
           seminar_id: seminar2.id,
           student_id: mahasiswa2.id,
           title: seminar2.title,
           research_object: seminar2.research_object,
           methodology: seminar2.methodology,
           defense_date: new Date('2024-01-30'),
           approval_letter_file: defense2File,
           status: 'pending'
       });

       const defense3File = copyFileToUploads('defense3.pdf', 'defense', '56785678_defense.pdf');
       const defense3 = await Defense.create({
           seminar_id: seminar3.id,
           student_id: mahasiswa3.id,
           title: seminar3.title,
           research_object: seminar3.research_object,
           methodology: seminar3.methodology,
           defense_date: new Date('2024-02-05'),
           approval_letter_file: defense3File,
           status: 'pending'
       });

       // Create logbook entries
       await Logbook.bulkCreate([
           {
               student_id: mahasiswa1.id,
               date: new Date('2024-01-01'),
               activity: 'Pengumpulan data awal untuk penelitian',
               is_locked: false
           },
           {
               student_id: mahasiswa2.id,
               date: new Date('2024-01-08'),
               activity: 'Diskusi hasil analisis dengan pembimbing',
               is_locked: false
           },
           {
               student_id: mahasiswa3.id,
               date: new Date('2024-01-15'),
               activity: 'Menyusun laporan penelitian',
               is_locked: false
           }
       ]);

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