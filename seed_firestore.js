/**
 * Firestore CSV Seed Script for MindLoop
 * 
 * Usage:
 *   1. npm install firebase-admin
 *   2. Export GOOGLE_APPLICATION_CREDENTIALS="path/to/serviceAccountKey.json"
 *   3. node seed_firestore.js <USER_ID>
 * 
 * Default user_id is "user_personal_default" matching MindLoop's default configuration.
 */

const admin = require('firebase-admin');
const fs = require('fs');
const readline = require('readline');
const path = require('path');

// Initialize Firebase Admin
if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.applicationDefault(),
    projectId: 'mindloop-80c94'
  });
}

const db = admin.firestore();
const TARGET_USER_ID = process.argv[2] || 'user_personal_default';

console.log(`Starting Firestore Seeding for User: ${TARGET_USER_ID}`);

function parseCsvLine(line) {
  const values = [];
  let current = '';
  let inQuotes = false;
  for (let i = 0; i < line.length; i++) {
    const char = line[i];
    if (char === '"') {
      inQuotes = !inQuotes;
    } else if (char === ',' && !inQuotes) {
      values.push(current.trim());
      current = '';
    } else {
      current += char;
    }
  }
  values.push(current.trim());
  return values;
}

async function seedStudySessions(filePath) {
  if (!fs.existsSync(filePath)) {
    console.log(`Study sessions CSV file not found at ${filePath}. Skipping.`);
    return;
  }
  const fileStream = fs.createReadStream(filePath);
  const rl = readline.createInterface({ input: fileStream, crlfDelay: Infinity });

  const sessionsCol = db.collection(`users/${TARGET_USER_ID}/study_sessions`);
  let isHeader = true;
  let count = 0;
  let batch = db.batch();

  for await (const line of rl) {
    if (!line.trim()) continue;
    if (isHeader) {
      isHeader = false;
      continue;
    }

    const tokens = parseCsvLine(line);
    // Expected: session_id, note_id, subject, chapter, started_at, ended_at, duration_seconds, mode
    const [sessId, noteId, subject, chapter, startedAt, endedAt, durationSecs, mode] = tokens;

    const docRef = sessionsCol.doc(); // auto-generated document ID via .doc() or matching session_id
    batch.set(docRef, {
      session_id: sessId || docRef.id,
      note_id: noteId ? parseInt(noteId, 10) : 1,
      subject: subject || 'Indian Polity',
      chapter: chapter || 'Fundamental Rights',
      started_at: startedAt ? parseInt(startedAt, 10) : Date.now(),
      ended_at: endedAt ? parseInt(endedAt, 10) : Date.now(),
      duration_seconds: durationSecs ? parseInt(durationSecs, 10) : 1800,
      mode: mode || 'chapter_wise'
    });

    count++;
    if (count % 400 === 0) {
      await batch.commit();
      batch = db.batch();
    }
  }

  await batch.commit();
  console.log(`Successfully seeded ${count} study sessions.`);
}

async function seedQuestionAttempts(filePath) {
  if (!fs.existsSync(filePath)) {
    console.log(`Question attempts CSV file not found at ${filePath}. Skipping.`);
    return;
  }
  const fileStream = fs.createReadStream(filePath);
  const rl = readline.createInterface({ input: fileStream, crlfDelay: Infinity });

  const attemptsCol = db.collection(`users/${TARGET_USER_ID}/question_attempts`);
  let isHeader = true;
  let count = 0;
  let batch = db.batch();

  for await (const line of rl) {
    if (!line.trim()) continue;
    if (isHeader) {
      isHeader = false;
      continue;
    }

    const tokens = parseCsvLine(line);
    // Expected: attempt_id, question_id, note_id, subject, chapter, question_type, shown_at, answered_at, time_taken_seconds, selected_answer, is_correct, self_rating
    const [attId, questionId, noteId, subject, chapter, qType, shownAt, answeredAt, timeTaken, selectedAns, isCorrect, rating] = tokens;

    const docRef = attemptsCol.doc();
    batch.set(docRef, {
      attempt_id: attId || docRef.id,
      question_id: questionId ? parseInt(questionId, 10) : 1,
      note_id: noteId ? parseInt(noteId, 10) : null,
      subject: subject || 'Indian Polity',
      chapter: chapter || 'Fundamental Rights',
      question_type: qType || 'MCQ',
      shown_at: shownAt ? parseInt(shownAt, 10) : Date.now(),
      answered_at: answeredAt ? parseInt(answeredAt, 10) : Date.now(),
      time_taken_seconds: timeTaken ? parseInt(timeTaken, 10) : 20,
      selected_answer: selectedAns || '',
      is_correct: isCorrect === 'true' || isCorrect === '1',
      self_rating: rating || 'GOOD'
    });

    count++;
    if (count % 400 === 0) {
      await batch.commit();
      batch = db.batch();
    }
  }

  await batch.commit();
  console.log(`Successfully seeded ${count} question attempts.`);
}

async function run() {
  try {
    const studySessionsCsv = process.env.STUDY_SESSIONS_CSV || path.join(__dirname, 'study_sessions_log.csv');
    const questionAttemptsCsv = process.env.QUESTION_ATTEMPTS_CSV || path.join(__dirname, 'question_attempts_log.csv');

    console.log(`Importing study sessions from: ${studySessionsCsv}`);
    await seedStudySessions(studySessionsCsv);

    console.log(`Importing question attempts from: ${questionAttemptsCsv}`);
    await seedQuestionAttempts(questionAttemptsCsv);

    console.log('Firestore seed completed successfully!');
  } catch (err) {
    console.error('Error during Firestore seeding:', err);
    process.exit(1);
  }
}

run();
