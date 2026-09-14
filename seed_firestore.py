#!/usr/bin/env python3
"""
Python CSV Seed Script for MindLoop Cloud Firestore
Using Google Cloud / Firebase Admin SDK

Requirements:
    pip install firebase-admin

Usage:
    export GOOGLE_APPLICATION_CREDENTIALS="path/to/serviceAccountKey.json"
    python seed_firestore.py [user_id]
"""

import sys
import os
import csv
import time
import firebase_admin
from firebase_admin import credentials, firestore

TARGET_USER_ID = sys.argv[1] if len(sys.argv) > 1 else "user_personal_default"

def init_firestore():
    if not firebase_admin._apps:
        # Uses GOOGLE_APPLICATION_CREDENTIALS environment variable by default
        cred = credentials.ApplicationDefault()
        firebase_admin.initialize_app(cred, {
            'projectId': 'mindloop-80c94'
        })
    return firestore.client()

def seed_study_sessions(db, csv_path):
    if not os.path.exists(csv_path):
        print(f"[!] {csv_path} not found. Skipping study sessions.")
        return

    sessions_col = db.collection(f"users/{TARGET_USER_ID}/study_sessions")
    batch = db.batch()
    count = 0

    with open(csv_path, mode="r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            doc_ref = sessions_col.document()
            data = {
                "session_id": row.get("session_id", doc_ref.id),
                "note_id": int(row["note_id"]) if row.get("note_id") else 1,
                "subject": row.get("subject", "Indian Polity"),
                "chapter": row.get("chapter", "Fundamental Rights"),
                "started_at": int(row["started_at"]) if row.get("started_at") else int(time.time() * 1000),
                "ended_at": int(row["ended_at"]) if row.get("ended_at") else int(time.time() * 1000),
                "duration_seconds": int(row["duration_seconds"]) if row.get("duration_seconds") else 1800,
                "mode": row.get("mode", "chapter_wise")
            }
            batch.set(doc_ref, data)
            count += 1
            if count % 400 == 0:
                batch.commit()
                batch = db.batch()

    batch.commit()
    print(f"[✓] Seeded {count} study sessions successfully into users/{TARGET_USER_ID}/study_sessions.")

def seed_question_attempts(db, csv_path):
    if not os.path.exists(csv_path):
        print(f"[!] {csv_path} not found. Skipping question attempts.")
        return

    attempts_col = db.collection(f"users/{TARGET_USER_ID}/question_attempts")
    batch = db.batch()
    count = 0

    with open(csv_path, mode="r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            doc_ref = attempts_col.document()
            is_corr_val = str(row.get("is_correct", "true")).strip().lower()
            is_correct = is_corr_val in ("true", "1", "yes")

            data = {
                "attempt_id": row.get("attempt_id", doc_ref.id),
                "question_id": int(row["question_id"]) if row.get("question_id") else 1,
                "note_id": int(row["note_id"]) if row.get("note_id") and row["note_id"] != "null" else None,
                "subject": row.get("subject", "Indian Polity"),
                "chapter": row.get("chapter", "Fundamental Rights"),
                "question_type": row.get("question_type", "MCQ"),
                "shown_at": int(row["shown_at"]) if row.get("shown_at") else int(time.time() * 1000),
                "answered_at": int(row["answered_at"]) if row.get("answered_at") else int(time.time() * 1000),
                "time_taken_seconds": int(row["time_taken_seconds"]) if row.get("time_taken_seconds") else 20,
                "selected_answer": row.get("selected_answer", ""),
                "is_correct": is_correct,
                "self_rating": row.get("self_rating", "GOOD")
            }
            batch.set(doc_ref, data)
            count += 1
            if count % 400 == 0:
                batch.commit()
                batch = db.batch()

    batch.commit()
    print(f"[✓] Seeded {count} question attempts successfully into users/{TARGET_USER_ID}/question_attempts.")

def main():
    print(f"Connecting to Cloud Firestore for User: {TARGET_USER_ID}...")
    db = init_firestore()

    sessions_csv = os.environ.get("STUDY_SESSIONS_CSV", "study_sessions_log.csv")
    attempts_csv = os.environ.get("QUESTION_ATTEMPTS_CSV", "question_attempts_log.csv")

    seed_study_sessions(db, sessions_csv)
    seed_question_attempts(db, attempts_csv)
    print("Done!")

if __name__ == "__main__":
    main()
