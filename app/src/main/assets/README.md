# MindLoop Seed Data — Indian Polity (UPSI)

Real, accurate content for testing the app end-to-end, not placeholder text.

## Files

**notes.csv** (52 notes)
Columns: `note_id, subject, chapter, title, content, time_spent_seconds, revisit_count`
- All 6 Indian Polity chapters we designed the app around: Making of the Constitution,
  Preamble, Fundamental Rights, Directive Principles, Union Executive, State Executive.
- `content` is real factual text (short paragraph form) — use it as the "note text" in
  place of an actual photographed image, or paste it onto a note-image placeholder if
  your test build expects an image field.
- `time_spent_seconds` and `revisit_count` are realistic mock analytics already computed
  (more-revisited notes have proportionally more time logged), so your Study Notes
  dashboard (total time, time by subject, most revisited notes, consistency heatmap)
  has real variation to render.

**questions.csv** (97 questions)
Columns: `question_id, note_id, subject, chapter, type, question, option_a, option_b,
option_c, option_d, correct_answer, times_shown, times_wrong, total_attempts,
avg_time_seconds, last_rating`
- Mix of MCQ (4 options) and True/False, matching the two review templates we designed.
- Every question's `note_id` links it back to its source note in notes.csv — this is
  exactly the CSV-import format we discussed for the "Add Notes" bulk question upload,
  so you should be able to import this file directly through that flow.
- Mock analytics (`times_shown`, `times_wrong`, `total_attempts`, `avg_time_seconds`,
  `last_rating`) are deliberately skewed: Fundamental Rights and Directive Principles
  questions have a higher wrong-rate (35-65%) than other chapters (5-30%), so your
  Mistakes page, accuracy-by-chapter donut chart, and mastery percentages will show a
  believable weak-spot pattern instead of flat/random numbers.

**mistakes.csv** (50 entries, ranked)
Columns: `note_id, concept_title, chapter, total_wrong_attempts, linked_question_ids`
- Pre-aggregated ranking of which concepts have the most wrong attempts, already sorted
  worst-first — this is what should populate your Mistakes screen directly.
- `linked_question_ids` lets "Retest" jump straight to just those questions; `note_id`
  lets "Study Notes" jump straight to that note.

## How to use in AI Studio

1. If your Add Notes / bulk-CSV-import feature is already wired up, import `notes.csv`
   first, then `questions.csv` (since questions reference note_ids).
2. If your backend isn't wired up yet and you're still using mock/local data, you can
   have AI Studio read these CSVs directly as seed data at app startup, or convert them
   to JSON/Room-database seed inserts.
3. `mistakes.csv` doesn't need separate import — it's a derived view for reference /
   for testing that your Mistakes-ranking logic produces something similar once real
   attempt data comes in through normal use.

## Coverage check
- Subjects: 1 (Indian Polity) — matches what we've fully designed so far.
- Chapters: 6, ~8-9 notes each.
- Questions per note: 1-3, roughly matching your "18 Notes • 24 Practice Questions"
  style chapter subtitle pattern from the mockups.
