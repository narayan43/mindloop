package com.example.data

import com.example.data.entity.NoteEntity
import com.example.data.entity.QuestionEntity
import com.example.data.entity.ReelEntity
import com.example.data.entity.StudySessionEntity

data class SeedMistakeConcept(
    val noteId: Long,
    val conceptTitle: String,
    val chapter: String,
    val totalWrongAttempts: Int,
    val linkedQuestionIds: List<Long>
)

object InitialDataProvider {

    val NOTES_CSV = """
note_id,subject,chapter,title,content,time_spent_seconds,revisit_count
N001,Indian Polity,1. Making of the Constitution,Constituent Assembly Formation,The Constituent Assembly was formed under the Cabinet Mission Plan of 1946. It was not directly elected by the people but by members of the Provincial Legislative Assemblies.,378,9
N002,Indian Polity,1. Making of the Constitution,First Meeting of the Assembly,The first meeting of the Constituent Assembly was held on 9 December 1946. Dr. Sachchidananda Sinha was elected as the temporary/interim President.,82,1
N003,Indian Polity,1. Making of the Constitution,Permanent President,Dr. Rajendra Prasad was elected as the permanent President of the Constituent Assembly on 11 December 1946.,100,2
N004,Indian Polity,1. Making of the Constitution,Drafting Committee,The Drafting Committee was formed on 29 August 1947 with Dr. B.R. Ambedkar as its Chairman. It had 7 members in total.,86,2
N005,Indian Polity,1. Making of the Constitution,Constitutional Advisor,B.N. Rau served as the Constitutional Advisor to the Constituent Assembly and prepared the original draft before the Drafting Committee's work.,78,1
N006,Indian Polity,1. Making of the Constitution,Adoption Date,The Constitution of India was adopted by the Constituent Assembly on 26 November 1949. This day is now celebrated as Constitution Day (Samvidhan Divas).,200,5
N007,Indian Polity,1. Making of the Constitution,Commencement Date,"The Constitution came into force on 26 January 1950, a date chosen to commemorate the 1930 declaration of Purna Swaraj. This day is celebrated as Republic Day.",372,6
N008,Indian Polity,1. Making of the Constitution,Time Taken to Draft,"The Constituent Assembly took 2 years, 11 months, and 18 days to complete the drafting of the Constitution.",36,1
N009,Indian Polity,1. Making of the Constitution,Original Structure,"The original Constitution had 395 Articles, 22 Parts, and 8 Schedules. It has since been amended many times and now has 12 Schedules.",48,1
N010,Indian Polity,1. Making of the Constitution,Borrowed Features,"India's Constitution borrowed features from various countries: Parliamentary system from the UK, Fundamental Rights from the USA, Directive Principles from Ireland, and federal structure from Canada.",134,2
N011,Indian Polity,2. Preamble,Preamble Overview,"The Preamble declares India to be a Sovereign, Socialist, Secular, Democratic Republic, securing Justice, Liberty, Equality, and Fraternity to all citizens.",216,6
N012,Indian Polity,2. Preamble,42nd Amendment Changes,"The words 'Socialist' and 'Secular' were added to the Preamble by the 42nd Constitutional Amendment Act of 1976, along with 'Integrity' after 'Unity'.",235,5
N013,Indian Polity,2. Preamble,Source of Authority,"The Preamble begins with 'We, the people of India', indicating that the ultimate source of authority for the Constitution is the people of India themselves.",711,9
N014,Indian Polity,2. Preamble,Objectives Resolution,The Preamble is based on the 'Objectives Resolution' drafted and moved by Jawaharlal Nehru in the Constituent Assembly on 13 December 1946.,305,5
N015,Indian Polity,2. Preamble,Berubari Case 1960,"In the Berubari Union case (1960), the Supreme Court held that the Preamble is not a part of the Constitution and is therefore not enforceable in a court of law.",126,2
N016,Indian Polity,2. Preamble,Kesavananda Bharati Case 1973,"In the Kesavananda Bharati case (1973), the Supreme Court reversed its earlier stand and held that the Preamble IS a part of the Constitution, though it can be amended, subject to the 'basic structure' doctrine.",312,6
N017,Indian Polity,2. Preamble,Four Ideals in Preamble,"The Preamble mentions four ideals: Justice (social, economic, political), Liberty (of thought, expression, belief, faith, worship), Equality (of status and opportunity), and Fraternity (assuring dignity of the individual and unity of the nation).",83,1
N018,Indian Polity,2. Preamble,Republic Meaning,"The term 'Republic' in the Preamble indicates that the head of the state (the President) is elected, directly or indirectly, for a fixed term, rather than being a hereditary monarch.",158,2
N019,Indian Polity,3. Fundamental Rights,Overview and Location,"Fundamental Rights are enshrined in Part III of the Constitution, covering Articles 12 to 35. They are often called the 'Magna Carta' of India.",168,3
N020,Indian Polity,3. Fundamental Rights,Six Categories,"There are currently six Fundamental Rights: Right to Equality, Right to Freedom, Right against Exploitation, Right to Freedom of Religion, Cultural and Educational Rights, and Right to Constitutional Remedies.",88,2
N021,Indian Polity,3. Fundamental Rights,Right to Property Removed,The Right to Property was originally a Fundamental Right (Article 31) but was removed by the 44th Constitutional Amendment Act of 1978. It now exists only as a legal right under Article 300A.,166,2
N022,Indian Polity,3. Fundamental Rights,Right to Equality Articles,"The Right to Equality is covered under Articles 14 to 18, including equality before law, prohibition of discrimination, equality of opportunity in public employment, and abolition of untouchability and titles.",123,3
N023,Indian Polity,3. Fundamental Rights,Article 17 - Untouchability,Article 17 abolishes 'untouchability' in any form and forbids its practice. Enforcement of any disability arising from untouchability is a punishable offence.,59,1
N024,Indian Polity,3. Fundamental Rights,Right to Freedom - Article 19,"Article 19 guarantees six freedoms to citizens: freedom of speech and expression, assembly, association, movement, residence, and profession/occupation.",57,1
N025,Indian Polity,3. Fundamental Rights,Article 21 - Right to Life,"Article 21 guarantees the Right to Life and Personal Liberty, stating that no person shall be deprived of his life or personal liberty except according to procedure established by law.",219,3
N026,Indian Polity,3. Fundamental Rights,Right against Exploitation,"Articles 23-24 form the Right against Exploitation. Article 23 prohibits traffic in human beings and forced labour. Article 24 prohibits employment of children below 14 years in factories, mines, or hazardous work.",172,2
N027,Indian Polity,3. Fundamental Rights,Right to Constitutional Remedies,Article 32 gives citizens the right to directly approach the Supreme Court for enforcement of Fundamental Rights. Dr. B.R. Ambedkar called it the 'heart and soul' of the Constitution.,81,1
N028,Indian Polity,3. Fundamental Rights,Five Writs,"Under Article 32 and Article 226, courts can issue five types of writs: Habeas Corpus, Mandamus, Prohibition, Certiorari, and Quo Warranto.",276,4
N029,Indian Polity,3. Fundamental Rights,Cultural and Educational Rights,"Articles 29-30 protect the interests of minorities, giving them the right to conserve their distinct language, script, or culture, and the right to establish and administer educational institutions.",59,1
N030,Indian Polity,4. Directive Principles,Overview and Location,"The Directive Principles of State Policy are contained in Part IV of the Constitution, covering Articles 36 to 51. They were borrowed from the Irish Constitution.",70,1
N031,Indian Polity,4. Directive Principles,Non-Justiciable Nature,"Directive Principles are non-justiciable, meaning they cannot be enforced by any court. However, they are declared to be 'fundamental in the governance of the country'.",176,2
N032,Indian Polity,4. Directive Principles,Classification,"DPSPs are broadly classified into three categories: Socialist principles, Gandhian principles, and Liberal-Intellectual principles.",666,9
N033,Indian Polity,4. Directive Principles,Article 39 - Equal Pay,"Article 39 directs the State to ensure equal pay for equal work for both men and women, and equitable distribution of material resources for the common good.",213,3
N034,Indian Polity,4. Directive Principles,Article 40 - Village Panchayats,Article 40 directs the State to take steps to organize village panchayats and endow them with powers to function as units of self-government (a Gandhian principle).,160,2
N035,Indian Polity,4. Directive Principles,Article 44 - Uniform Civil Code,Article 44 directs the State to secure for citizens a Uniform Civil Code throughout the territory of India.,37,1
N036,Indian Polity,4. Directive Principles,Article 45 - Education for Children,"Article 45 originally directed the State to provide free and compulsory education for children until age 14. This has since been strengthened by the 86th Amendment, which added Article 21A as a Fundamental Right.",441,9
N037,Indian Polity,4. Directive Principles,Article 48 - Agriculture and Cattle,"Article 48 directs the State to organize agriculture and animal husbandry on modern scientific lines and to take steps for preserving and improving breeds, prohibiting slaughter of cows, calves, and other milch/draught cattle.",80,2
N038,Indian Polity,4. Directive Principles,Article 51 - International Peace,"Article 51 directs the State to promote international peace and security, and to maintain just and honourable relations between nations.",180,2
N039,Indian Polity,5. Union Executive,President - Head of State,"The President of India is the head of state under Article 52, and formally the head of the Executive, though real executive power lies with the Council of Ministers.",59,1
N040,Indian Polity,5. Union Executive,Presidential Election,The President is elected by an electoral college consisting of elected members of both Houses of Parliament and elected members of the Legislative Assemblies of States (and UTs of Delhi and Puducherry).,128,2
N041,Indian Polity,5. Union Executive,Term of President,"Under Article 56, the President holds office for a term of 5 years from the date on which he/she enters upon the office, and is eligible for re-election.",792,9
N042,Indian Polity,5. Union Executive,Vice President Role,The Vice President is the ex-officio Chairman of the Rajya Sabha (Article 64) and acts as President when there is a vacancy in that office.,135,3
N043,Indian Polity,5. Union Executive,Prime Minister,"The Prime Minister is the real head of the executive, appointed by the President, usually the leader of the majority party in the Lok Sabha.",171,3
N044,Indian Polity,5. Union Executive,Council of Ministers,"Under Article 75, the Council of Ministers, headed by the Prime Minister, is collectively responsible to the Lok Sabha.",154,2
N045,Indian Polity,5. Union Executive,Attorney General,"Under Article 76, the Attorney General of India is the highest law officer of the country, appointed by the President.",158,2
N046,Indian Polity,5. Union Executive,Pardoning Power,"Article 72 gives the President the power to grant pardons, reprieves, respites, or remissions of punishment, including in cases involving death sentences.",684,9
N047,Indian Polity,6. State Executive,Governor - Head of State,"Under Article 153, there shall be a Governor for each state, who acts as the head of the state executive.",73,1
N048,Indian Polity,6. State Executive,Appointment of Governor,"The Governor is appointed by the President under Article 155, and holds office during the pleasure of the President (Article 156), usually for a term of 5 years.",405,9
N049,Indian Polity,6. State Executive,Chief Minister,"The Chief Minister is appointed by the Governor, and is usually the leader of the majority party or coalition in the state Legislative Assembly.",405,5
N050,Indian Polity,6. State Executive,State Council of Ministers,"The Council of Ministers, headed by the Chief Minister, is collectively responsible to the state Legislative Assembly.",90,2
N051,Indian Polity,6. State Executive,Advocate General,"Under Article 165, the Advocate General is the chief law officer of a state, appointed by the Governor.",236,4
N052,Indian Polity,6. State Executive,Reserving Bills for President,"Under Article 200, the Governor may reserve certain bills passed by the state legislature for the consideration of the President.",150,2
N053,Psychology,1. Deception Detection & Behavioral Analysis,Step 1: Establishing the Behavioral Baseline,"You cannot spot a lie until you know what normal looks like. Before analyzing deception, observe the person during relaxed, low-stakes conversation for several minutes to map their baseline across four key dimensions: (1) Speech Cadence & Pitch: normal rhythm, tempo (fast/slow), volume, and natural vocal pitch. (2) Blink Rate: average resting baseline is 9 to 20 blinks per minute; look for changes rather than judging single blinks. (3) Linguistic Style: natural use of personal pronouns (I, we) and common contractions (didn't, won't). (4) Physical Restlessness: standard hand gestures, posture shifts, and facial self-touching. Baseline first, judgement later.",180,3
N054,Psychology,1. Deception Detection & Behavioral Analysis,Step 2: Analyzing Narrative Structure (Truth vs. Fabrication),"True episodic memories are processed differently by the brain than rehearsed or fabricated stories. Real memory is emotional and flexible, not a rigid script. Five core differences: (1) Chronology: Truth starts with emotionally salient climax events and shifts naturally back and forth; Fabrication follows a stiff, step-by-step linear chronology with unnecessary early details to sound authentic. (2) Pronouns: Truth exhibits high personal ownership (I said, I saw, I decided); Fabrication drops the I pronoun (Woke up, went there, left) to reduce cognitive burden and distance from guilt. (3) Contractions: Truth uses natural contractions (didn't, couldn't); Fabrication uses rigid formal non-contractions (I did not do that) to sound technical. (4) Directness: Truth is direct; Fabrication uses psychological distancing, soft euphemisms (take instead of steal), and escape caveats (To the best of my knowledge). (5) Response: Truth directly answers questions; Fabrication offers resume statements touting general good character instead of denying the specific act.",210,4
N055,Psychology,1. Deception Detection & Behavioral Analysis,Step 3: Real-Time Physical & Paralinguistic Indicators,"Track physiological and behavioral shifts occurring Before (B), During (D), or After (A) the statement. Six primary indicators: (1) Blink Rate Spikes (B/D/A): sudden jump from baseline (9-20/min) up to 60+ blinks per minute when working memory is overloaded. (2) Vocal Hesitancy & Pitch Rise (B/D): unnatural pause before answering to buy time, and pitch increase as adrenaline tightens vocal cords. (3) Cognitive Freeze (D): hand and body gestures slow down or stop completely as mental processing is consumed by fabricating details. (4) Shielding & Pacifying Gestures (B/D): unconscious attempts to soothe psychological discomfort, including face touching (nose, mouth, chin), mouth covering, compressed lips holding back info, and pulling feet under the chair. (5) The Confirmation Glance (A): a fraction-of-a-second glance immediately after a claim to check if the listener believes them (seeking social validation). (6) Micro-Incongruence (D): mismatch between words and body language, such as saying yes while subtly shaking the head no. Behaviors are clues, not single conclusive proof.",250,5
N056,Psychology,1. Deception Detection & Behavioral Analysis,Step 4: Increasing Cognitive Load to Test Story Integrity,"Liars prepare for simple linear questions; they rarely rehearse unexpected angles or complex mental tasks. By increasing cognitive load, you can see if their story holds under pressure, because truth survives pressure while fabricated stories collapse. Four interrogation protocols: (1) Reverse Chronology Recall: ask the subject to recount events backwards from the end to the beginning. Truthful episodic memories can be traversed in any order, whereas fabricated narratives quickly break down with hesitation, contradictions, and errors. (2) Sensory Detail Probing: ask for specific sensory details that are hard to fake, including lighting, sounds, weather, smells, textures, or peripheral movements. Truthful memories are rich in sensory context, while fabrications become vague. (3) The Cognitive Freeze Check: introduce unexpected follow-up questions; watch if physical gestures freeze, or if they take prolonged pauses and repeat the question to buy time. (4) Perspective Shift: ask them to recount events from the viewpoint of someone else (a bystander or friend). Liars struggle to maintain consistency across perspective changes.",310,6
N057,Psychology,1. Deception Detection & Behavioral Analysis,Step 5: Scoring via Clustering & The DRS Rule,"Never brand someone a liar based on a single sign. One sign is noise; a cluster is a signal. The Deception Rating Scale (DRS) Rule states that deception is strongly indicated only when you observe a cluster of 11 or more abnormal indicators within a single response cycle. Observe indicators across three timeframes: Before (B - hesitation, preparing), During (D - pitch rise, lack of gestures, blink spike, pronoun drop, non-contractions, micro-incongruence), and After (A - confirmation glance, relief). Five-step Decision Flow: (1) Observe a cue, (2) Consider alternative explanations like nervousness or fatigue, (3) Ask a diagnostic follow-up question, (4) Look for clusters meeting or exceeding 11 DRS points, (5) Draw a probabilistic conclusion. Combining baseline mapping, narrative analysis, physical cues, cognitive load testing, and clustering provides accurate behavioral judgment.",290,5
N058,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),Influence Fundamentals & The Time-Distance Problem,"Influence is communication or behavior performed by one person that causes another person to think or take actions they would not have otherwise taken (Change in thought -> Change in action). People do not just hear your words; they feel your state. The ultimate goal of deep influence is solving the Time-Distance Problem: getting a person to deviate from their normal baseline behavior to an extreme degree (Distance) in a very short amount of time (Time). Deep persuasion achieves maximum behavioral distance with minimum elapsed time. The foundational framework follows the progression: SELF (Master yourself) -> SUBJECT (Profile the subject) -> FOUNDATION (Prepare to change their perception), with the core memory hook: I -> YOU -> SHIFT.",320,6
N059,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),Mastering the Self & The 80/15/5 Rule of Persuasion,"Persuasion is governed by the 80/15/5 rule: Persuasion is 80% who you are, 15% what you do, and only 5% who the other person is. Self-mastery is paramount because if you lack composure or exhibit social anxiety, your nonverbal signals will subconsciously warn the other person not to trust or follow you. Your internal emotional state is always communicated, even in complete silence. Five core components establish authority: (1) Confidence: unwavering certainty, (2) Discipline: controlled emotional stability, (3) Leadership: setting the interactive frame, (4) Gratitude: disarming defensiveness, (5) Enjoyment: positive engagement. Control yourself before you attempt to control a situation; be the person who influences.",340,7
N060,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),The Six-Minute X-Ray (6MX) Subject Profiling Framework,"The Six-Minute X-Ray (6MX) provides rapid subject profiling: 6 minutes to Observe, Understand, and Adapt. Applying influence techniques without profiling yields minimal results. 6MX evaluates four distinct elements: (1) Needs: what the person craves socially, such as Significance (status, importance), Approval (praise, acceptance), or Pity (sympathy, attention). (2) Decisions: how they filter choices, primarily through Conformity (following the group), Novelty (attracted to newness), or Deviance (rebelling against the norm). (3) Values: deep life priorities that dictate what they ultimately want, frequently originating from childhood lack or past emotional experiences. (4) Baseline Behaviors: reading real-time somatic signals including body language, resting blink rate, and facial expressions to detect hidden stress or unvoiced objections.",270,4
""".trimIndent()

    val QUESTIONS_CSV = """
question_id,note_id,subject,chapter,type,question,option_a,option_b,option_c,option_d,correct_answer,times_shown,times_wrong,total_attempts,avg_time_seconds,last_rating
Q001,N001,Indian Polity,1. Making of the Constitution,MCQ,The Constituent Assembly was formed under which plan?,Cabinet Mission Plan 1946,Mountbatten Plan 1947,August Offer 1940,Cripps Mission 1942,Cabinet Mission Plan 1946,14,3,16,11,Medium
Q002,N001,Indian Polity,1. Making of the Constitution,True/False,Members of the Constituent Assembly were directly elected by the general public.,True,False,,,False,3,1,6,25,Medium
Q003,N002,Indian Polity,1. Making of the Constitution,MCQ,Who was the temporary/interim President of the Constituent Assembly?,Dr. Sachchidananda Sinha,Dr. Rajendra Prasad,Dr. B.R. Ambedkar,Jawaharlal Nehru,Dr. Sachchidananda Sinha,6,2,8,21,Easy
Q004,N002,Indian Polity,1. Making of the Constitution,True/False,The first meeting of the Constituent Assembly was held on 9 December 1946.,True,False,,,True,9,2,12,17,Hard
Q005,N003,Indian Polity,1. Making of the Constitution,MCQ,Who was elected as the permanent President of the Constituent Assembly?,Dr. Rajendra Prasad,Dr. Sachchidananda Sinha,C. Rajagopalachari,Dr. B.R. Ambedkar,Dr. Rajendra Prasad,5,1,7,45,Easy
Q006,N004,Indian Polity,1. Making of the Constitution,MCQ,Who chaired the Drafting Committee of the Constitution?,Dr. B.R. Ambedkar,Dr. Rajendra Prasad,B.N. Rau,Jawaharlal Nehru,Dr. B.R. Ambedkar,12,2,13,16,Easy
Q007,N004,Indian Polity,1. Making of the Constitution,True/False,The Drafting Committee had 7 members.,True,False,,,True,4,1,4,17,Medium
Q008,N005,Indian Polity,1. Making of the Constitution,MCQ,Who served as the Constitutional Advisor to the Constituent Assembly?,B.N. Rau,Dr. B.R. Ambedkar,K.M. Munshi,Alladi Krishnaswami Iyer,B.N. Rau,13,2,13,32,Easy
Q009,N006,Indian Polity,1. Making of the Constitution,MCQ,On which date was the Constitution of India adopted?,26 November 1949,26 January 1950,15 August 1947,9 December 1946,26 November 1949,12,4,14,43,Medium
Q010,N006,Indian Polity,1. Making of the Constitution,True/False,26 November is celebrated as Constitution Day (Samvidhan Divas).,True,False,,,True,13,3,15,29,Medium
Q011,N007,Indian Polity,1. Making of the Constitution,MCQ,On which date did the Constitution of India come into force?,26 January 1950,26 November 1949,15 August 1947,1 January 1950,26 January 1950,7,1,10,8,Medium
Q012,N008,Indian Polity,1. Making of the Constitution,True/False,The Constituent Assembly took less than 1 year to draft the Constitution.,True,False,,,False,14,2,15,40,Easy
Q013,N009,Indian Polity,1. Making of the Constitution,MCQ,How many Articles did the original Constitution of India contain?,395,448,470,300,395,13,2,14,17,Hard
Q014,N009,Indian Polity,1. Making of the Constitution,True/False,The original Constitution had 8 Schedules.,True,False,,,True,5,1,5,28,Easy
Q015,N010,Indian Polity,1. Making of the Constitution,MCQ,The Parliamentary system of government was borrowed from which country?,United Kingdom,United States of America,Ireland,Canada,United Kingdom,3,0,5,27,Easy
Q016,N010,Indian Polity,1. Making of the Constitution,MCQ,The concept of Directive Principles of State Policy was borrowed from which country?,Ireland,United Kingdom,United States of America,Canada,Ireland,3,0,3,13,Medium
Q017,N011,Indian Polity,2. Preamble,True/False,"The Preamble describes India as a Sovereign, Socialist, Secular, Democratic Republic.",True,False,,,True,10,3,11,16,Easy
Q018,N012,Indian Polity,2. Preamble,MCQ,Which amendment added the words 'Socialist' and 'Secular' to the Preamble?,42nd Amendment,44th Amendment,86th Amendment,1st Amendment,42nd Amendment,11,1,14,21,Medium
Q019,N012,Indian Polity,2. Preamble,True/False,The word 'Secular' was part of the original 1950 Preamble.,True,False,,,False,14,3,16,33,Hard
Q020,N013,Indian Polity,2. Preamble,MCQ,The Preamble begins with which phrase?,"We, the people of India",In the name of God,"We, the citizens of India","We, the representatives of India","We, the people of India",10,3,13,15,Medium
Q021,N014,Indian Polity,2. Preamble,MCQ,The Preamble is based on the Objectives Resolution moved by whom?,Jawaharlal Nehru,Dr. B.R. Ambedkar,Dr. Rajendra Prasad,Sardar Patel,Jawaharlal Nehru,6,0,6,45,Medium
Q022,N015,Indian Polity,2. Preamble,MCQ,In which case did the Supreme Court hold that the Preamble is NOT part of the Constitution?,Berubari Union case (1960),Kesavananda Bharati case (1973),Golaknath case (1967),Minerva Mills case (1980),Berubari Union case (1960),6,1,6,12,Medium
Q023,N016,Indian Polity,2. Preamble,True/False,The Kesavananda Bharati case (1973) held that the Preamble is a part of the Constitution.,True,False,,,True,6,0,6,29,Easy
Q024,N016,Indian Polity,2. Preamble,MCQ,Which doctrine did the Kesavananda Bharati case establish regarding amendment of the Preamble?,Basic Structure doctrine,Doctrine of Pith and Substance,Doctrine of Colourable Legislation,Doctrine of Severability,Basic Structure doctrine,11,1,14,21,Medium
Q025,N017,Indian Polity,2. Preamble,MCQ,Which of these is NOT one of the four ideals mentioned in the Preamble?,Sovereignty,Justice,Liberty,Equality,Sovereignty,5,1,8,23,Easy
Q026,N018,Indian Polity,2. Preamble,True/False,The term 'Republic' means the head of state is a hereditary monarch.,True,False,,,False,9,1,9,35,Easy
Q027,N019,Indian Polity,3. Fundamental Rights,MCQ,Fundamental Rights are covered under which Part of the Constitution?,Part III,Part IV,Part II,Part V,Part III,9,4,9,14,Hard
Q028,N019,Indian Polity,3. Fundamental Rights,True/False,Fundamental Rights cover Articles 12 to 35.,True,False,,,True,9,5,9,23,Hard
Q029,N020,Indian Polity,3. Fundamental Rights,MCQ,How many categories of Fundamental Rights currently exist?,Six,Seven,Five,Eight,Six,6,3,7,35,Hard
Q030,N021,Indian Polity,3. Fundamental Rights,MCQ,Which amendment removed the Right to Property from Fundamental Rights?,44th Amendment,42nd Amendment,86th Amendment,73rd Amendment,44th Amendment,7,3,7,36,Medium
Q031,N021,Indian Polity,3. Fundamental Rights,True/False,"Right to Property is now a legal right under Article 300A, not a Fundamental Right.",True,False,,,True,4,1,4,13,Medium
Q032,N022,Indian Polity,3. Fundamental Rights,MCQ,The Right to Equality is covered under which Articles?,Articles 14-18,Articles 19-22,Articles 23-24,Articles 25-28,Articles 14-18,5,2,8,21,Easy
Q033,N023,Indian Polity,3. Fundamental Rights,MCQ,Which Article abolishes untouchability?,Article 17,Article 14,Article 21,Article 32,Article 17,3,1,3,32,Hard
Q034,N024,Indian Polity,3. Fundamental Rights,MCQ,How many freedoms are guaranteed under Article 19?,Six,Five,Seven,Four,Six,10,4,13,17,Medium
Q035,N024,Indian Polity,3. Fundamental Rights,True/False,Freedom of speech and expression is guaranteed under Article 19.,True,False,,,True,7,3,7,45,Medium
Q036,N025,Indian Polity,3. Fundamental Rights,MCQ,Which Article guarantees the Right to Life and Personal Liberty?,Article 21,Article 19,Article 32,Article 14,Article 21,11,4,13,11,Medium
Q037,N026,Indian Polity,3. Fundamental Rights,MCQ,Article 24 prohibits employment of children below what age in hazardous work?,14 years,12 years,16 years,18 years,14 years,12,6,13,11,Medium
Q038,N026,Indian Polity,3. Fundamental Rights,True/False,Article 23 prohibits traffic in human beings and forced labour.,True,False,,,True,4,2,4,12,Medium
Q039,N027,Indian Polity,3. Fundamental Rights,MCQ,Who called Article 32 the 'heart and soul' of the Constitution?,Dr. B.R. Ambedkar,Jawaharlal Nehru,Dr. Rajendra Prasad,Sardar Patel,Dr. B.R. Ambedkar,6,3,7,45,Medium
Q040,N028,Indian Polity,3. Fundamental Rights,MCQ,Which of these is NOT one of the five writs under Article 32?,Ordinance,Habeas Corpus,Mandamus,Certiorari,Ordinance,3,2,6,45,Medium
Q041,N028,Indian Polity,3. Fundamental Rights,True/False,Quo Warranto is one of the five constitutional writs.,True,False,,,True,11,5,13,21,Medium
Q042,N029,Indian Polity,3. Fundamental Rights,MCQ,Articles 29-30 protect the interests of whom?,Minorities,Scheduled Castes only,Women,Farmers,Minorities,14,6,16,33,Hard
Q043,N030,Indian Polity,4. Directive Principles,MCQ,Directive Principles of State Policy are covered under which Part?,Part IV,Part III,Part V,Part IVA,Part IV,13,7,16,28,Hard
Q044,N030,Indian Polity,4. Directive Principles,True/False,DPSPs were borrowed from the Irish Constitution.,True,False,,,True,3,1,3,12,Medium
Q045,N031,Indian Polity,4. Directive Principles,True/False,Directive Principles are enforceable by courts.,True,False,,,False,11,5,13,12,Hard
Q046,N032,Indian Polity,4. Directive Principles,MCQ,Which of these is NOT a classification of DPSPs?,Federal principles,Socialist principles,Gandhian principles,Liberal-Intellectual principles,Federal principles,8,3,11,42,Hard
Q047,N033,Indian Polity,4. Directive Principles,MCQ,Article 39 directs the State to ensure what regarding pay?,Equal pay for equal work,Minimum wage only,Maximum wage limits,Pay based on seniority,Equal pay for equal work,12,8,12,43,Hard
Q048,N034,Indian Polity,4. Directive Principles,MCQ,Article 40 relates to organizing which institutions?,Village Panchayats,District Courts,State Legislatures,Municipal Corporations,Village Panchayats,13,5,14,24,Medium
Q049,N034,Indian Polity,4. Directive Principles,True/False,Article 40 reflects a Gandhian principle.,True,False,,,True,4,2,5,25,Hard
Q050,N035,Indian Polity,4. Directive Principles,MCQ,Article 44 directs the State to secure what for citizens?,A Uniform Civil Code,Free legal aid,Right to education,Minimum wages,A Uniform Civil Code,12,5,14,21,Medium
Q051,N036,Indian Polity,4. Directive Principles,True/False,Article 45 originally dealt with free and compulsory education for children up to age 14.,True,False,,,True,13,8,16,24,Hard
Q052,N036,Indian Polity,4. Directive Principles,MCQ,Which amendment added Article 21A as a Fundamental Right to education?,86th Amendment,42nd Amendment,44th Amendment,73rd Amendment,86th Amendment,4,2,6,10,Hard
Q053,N037,Indian Polity,4. Directive Principles,MCQ,Article 48 relates to organizing which sector on modern scientific lines?,Agriculture and animal husbandry,Industry,Education,Judiciary,Agriculture and animal husbandry,8,5,10,18,Medium
Q054,N038,Indian Polity,4. Directive Principles,MCQ,Article 51 directs the State to promote what?,International peace and security,Uniform Civil Code,Village self-government,Free education,International peace and security,10,5,13,43,Hard
Q055,N039,Indian Polity,5. Union Executive,MCQ,Which Article establishes the President as head of state?,Article 52,Article 53,Article 56,Article 72,Article 52,4,0,5,42,Easy
Q056,N040,Indian Polity,5. Union Executive,True/False,The President is directly elected by the general public.,True,False,,,False,8,2,9,35,Medium
Q057,N040,Indian Polity,5. Union Executive,MCQ,Who forms part of the electoral college for the Presidential election?,Elected members of Parliament and State Legislative Assemblies,Only Lok Sabha members,Only Rajya Sabha members,All members of Parliament including nominated ones,Elected members of Parliament and State Legislative Assemblies,3,0,3,30,Easy
Q058,N041,Indian Polity,5. Union Executive,MCQ,What is the term of office of the President under Article 56?,5 years,6 years,4 years,7 years,5 years,13,1,13,30,Medium
Q059,N042,Indian Polity,5. Union Executive,MCQ,The Vice President is the ex-officio Chairman of which House?,Rajya Sabha,Lok Sabha,State Legislative Assembly,State Legislative Council,Rajya Sabha,9,3,10,23,Medium
Q060,N043,Indian Polity,5. Union Executive,True/False,The Prime Minister is the real head of the executive in India.,True,False,,,True,5,1,5,19,Hard
Q061,N044,Indian Polity,5. Union Executive,MCQ,Under which Article is the Council of Ministers collectively responsible to the Lok Sabha?,Article 75,Article 74,Article 76,Article 78,Article 75,9,2,10,25,Medium
Q062,N045,Indian Polity,5. Union Executive,MCQ,Who is the highest law officer of the country under Article 76?,Attorney General,Advocate General,Solicitor General,Chief Justice,Attorney General,14,1,14,38,Easy
Q063,N046,Indian Polity,5. Union Executive,MCQ,Under which Article can the President grant pardons?,Article 72,Article 76,Article 61,Article 56,Article 72,6,2,9,30,Hard
Q064,N046,Indian Polity,5. Union Executive,True/False,The President's pardoning power can apply even to death sentences.,True,False,,,True,6,1,7,33,Hard
Q065,N047,Indian Polity,6. State Executive,MCQ,Under which Article is there a Governor for each state?,Article 153,Article 155,Article 163,Article 200,Article 153,7,2,9,30,Easy
Q066,N048,Indian Polity,6. State Executive,MCQ,By whom is the Governor of a state appointed?,The President,The Prime Minister,The Chief Minister,The state legislature,The President,13,4,15,9,Medium
Q067,N048,Indian Polity,6. State Executive,True/False,The Governor holds office during the pleasure of the President.,True,False,,,True,7,1,9,10,Easy
Q068,N049,Indian Polity,6. State Executive,MCQ,Who appoints the Chief Minister of a state?,The Governor,The President,The state legislature,The Advocate General,The Governor,12,2,14,35,Medium
Q069,N050,Indian Polity,6. State Executive,True/False,The state Council of Ministers is collectively responsible to the state Legislative Assembly.,True,False,,,True,9,2,10,24,Medium
Q070,N051,Indian Polity,6. State Executive,MCQ,Under which Article is the Advocate General of a state appointed?,Article 165,Article 76,Article 200,Article 155,Article 165,14,2,15,31,Easy
Q071,N052,Indian Polity,6. State Executive,MCQ,Under which Article can the Governor reserve a bill for the President's consideration?,Article 200,Article 165,Article 153,Article 163,Article 200,4,1,6,28,Medium
Q072,N003,Indian Polity,1. Making of the Constitution,True/False,Dr. Rajendra Prasad was elected permanent President on 11 December 1946.,True,False,,,True,14,4,16,34,Hard
Q073,N005,Indian Polity,1. Making of the Constitution,True/False,B.N. Rau was a member of the Drafting Committee.,True,False,,,False,9,2,10,20,Easy
Q074,N007,Indian Polity,1. Making of the Constitution,MCQ,Republic Day commemorates which historic 1930 declaration?,Purna Swaraj,Quit India,Non-Cooperation,Dandi March,Purna Swaraj,13,4,14,44,Hard
Q075,N008,Indian Polity,1. Making of the Constitution,MCQ,Approximately how long did it take to draft the Constitution?,Nearly 3 years,6 months,10 years,1 year,Nearly 3 years,9,2,9,27,Hard
Q076,N010,Indian Polity,1. Making of the Constitution,MCQ,The federal structure of the Indian Constitution was borrowed from which country?,Canada,United Kingdom,Ireland,France,Canada,6,1,8,37,Easy
Q077,N010,Indian Polity,1. Making of the Constitution,True/False,Fundamental Rights in the Indian Constitution were influenced by the USA.,True,False,,,True,10,2,13,18,Medium
Q078,N013,Indian Polity,2. Preamble,True/False,"'We, the people of India' indicates the people are the ultimate source of authority.",True,False,,,True,7,1,9,13,Easy
Q079,N015,Indian Polity,2. Preamble,MCQ,Which year did the Berubari Union case occur?,1960,1973,1950,1980,1960,13,2,14,17,Medium
Q080,N017,Indian Polity,2. Preamble,MCQ,"'Fraternity' in the Preamble assures what, alongside unity of the nation?",Dignity of the individual,Freedom of religion,Equal pay,Right to property,Dignity of the individual,3,0,6,12,Easy
Q081,N020,Indian Polity,3. Fundamental Rights,MCQ,Which Fundamental Right is often called the 'heart and soul' of the Constitution?,Right to Constitutional Remedies,Right to Equality,Right to Freedom,Right to Freedom of Religion,Right to Constitutional Remedies,9,6,10,32,Hard
Q082,N022,Indian Polity,3. Fundamental Rights,True/False,"Article 15 prohibits discrimination on grounds of religion, race, caste, sex, or place of birth.",True,False,,,True,9,4,9,14,Hard
Q083,N023,Indian Polity,3. Fundamental Rights,True/False,"Article 18 abolishes titles, except military and academic distinctions.",True,False,,,True,6,2,9,11,Medium
Q084,N025,Indian Polity,3. Fundamental Rights,True/False,Article 21 can be restricted without any procedure established by law.,True,False,,,False,4,2,7,41,Medium
Q085,N027,Indian Polity,3. Fundamental Rights,MCQ,A citizen seeking enforcement of Fundamental Rights can approach which court directly under Article 32?,Supreme Court,District Court,High Court only,Consumer Court,Supreme Court,12,5,15,40,Hard
Q086,N029,Indian Polity,3. Fundamental Rights,MCQ,Article 30 gives minorities the right to establish and administer what?,Educational institutions,Religious trusts only,Political parties,Business enterprises,Educational institutions,11,5,12,38,Hard
Q087,N032,Indian Polity,4. Directive Principles,True/False,"Socialist, Gandhian, and Liberal-Intellectual are recognized classifications of DPSPs.",True,False,,,True,7,4,9,41,Hard
Q088,N035,Indian Polity,4. Directive Principles,True/False,A Uniform Civil Code has been fully implemented across all of India as of the Constitution's directive under Article 44.,True,False,,,False,13,5,16,12,Hard
Q089,N037,Indian Polity,4. Directive Principles,True/False,Article 48 includes a directive on prohibiting slaughter of cows and calves.,True,False,,,True,6,3,8,42,Hard
Q090,N039,Indian Polity,5. Union Executive,True/False,The President of India is formally the head of the Executive.,True,False,,,True,5,0,8,17,Medium
Q091,N041,Indian Polity,5. Union Executive,True/False,The President is eligible for re-election after completing a term.,True,False,,,True,6,0,9,29,Medium
Q092,N042,Indian Polity,5. Union Executive,MCQ,The Vice President acts as President when there is a vacancy in that office due to which Article's mechanism?,Article 65,Article 64,Article 72,Article 76,Article 65,10,2,11,34,Easy
Q093,N044,Indian Polity,5. Union Executive,MCQ,The Prime Minister is usually chosen from which body?,The majority party in the Lok Sabha,The Rajya Sabha only,The Supreme Court,State governors,The majority party in the Lok Sabha,12,3,12,44,Easy
Q094,N047,Indian Polity,6. State Executive,True/False,Every state in India has its own Governor under Article 153.,True,False,,,True,10,1,12,27,Easy
Q095,N049,Indian Polity,6. State Executive,True/False,The Chief Minister is usually the leader of the majority party in the state assembly.,True,False,,,True,9,2,10,39,Medium
Q096,N050,Indian Polity,6. State Executive,MCQ,The state Council of Ministers is headed by whom?,The Chief Minister,The Governor,The Advocate General,The Speaker,The Chief Minister,7,1,7,32,Easy
Q097,N051,Indian Polity,6. State Executive,True/False,The Advocate General is appointed by the Chief Minister.,True,False,,,False,13,3,16,18,Easy
Q098,N053,Psychology,1. Deception Detection & Behavioral Analysis,True/False,"An average person's resting baseline blink rate is typically between 9 and 20 blinks per minute.",True,False,,,True,8,1,10,12,Easy
Q099,N053,Psychology,1. Deception Detection & Behavioral Analysis,True/False,A single isolated behavioral cue such as one throat clear or posture shift is definitive proof of deception.,True,False,,,False,11,4,14,15,Medium
Q100,N053,Psychology,1. Deception Detection & Behavioral Analysis,True/False,Before evaluating deception you should observe the subject in a relaxed low-stakes conversation to map baseline behavior.,True,False,,,True,6,0,7,10,Easy
Q101,N054,Psychology,1. Deception Detection & Behavioral Analysis,True/False,Truthful episodic memories usually start with emotionally salient climax events and shift naturally back and forth.,True,False,,,True,9,2,11,18,Medium
Q102,N054,Psychology,1. Deception Detection & Behavioral Analysis,True/False,"Deceptive stories tend to overuse the personal pronoun 'I' because liars want to take full personal ownership of events.",True,False,,,False,13,5,15,22,Hard
Q103,N054,Psychology,1. Deception Detection & Behavioral Analysis,True/False,"Deceptive narratives often employ formal non-contractions like 'I did not' rather than natural contractions like 'didn't' to sound technically convincing.",True,False,,,True,10,3,12,19,Medium
Q104,N054,Psychology,1. Deception Detection & Behavioral Analysis,True/False,"Soft euphemisms such as saying 'take' instead of 'steal' represent psychological distancing commonly found in fabricated accounts.",True,False,,,True,7,1,8,14,Easy
Q105,N054,Psychology,1. Deception Detection & Behavioral Analysis,True/False,"A resume statement is when a suspect directly and specifically answers an interviewer's question about an alleged offense.",True,False,,,False,12,4,14,20,Medium
Q106,N055,Psychology,1. Deception Detection & Behavioral Analysis,True/False,Under severe cognitive stress and deception a person's blink rate can spike to 60 or more blinks per minute.,True,False,,,True,8,2,10,13,Easy
Q107,N055,Psychology,1. Deception Detection & Behavioral Analysis,True/False,Cognitive freeze occurs when gestures suddenly slow or stop because working memory is overloaded while manufacturing a lie.,True,False,,,True,9,1,11,16,Medium
Q108,N055,Psychology,1. Deception Detection & Behavioral Analysis,True/False,"The confirmation glance is an indicator that occurs before the question is asked.",True,False,,,False,14,5,16,21,Hard
Q109,N055,Psychology,1. Deception Detection & Behavioral Analysis,True/False,"Micro-incongruence occurs when physical body language contradicts spoken statements, such as saying 'yes' while shaking the head 'no'.",True,False,,,True,10,2,12,15,Easy
Q110,N056,Psychology,1. Deception Detection & Behavioral Analysis,True/False,Truthful episodic memories easily break down during reverse chronology recall because memory can only be retrieved forward.,True,False,,,False,13,6,16,25,Hard
Q111,N056,Psychology,1. Deception Detection & Behavioral Analysis,True/False,"Probing for sensory details like ambient lighting, sounds, and weather tests story integrity because truthful memories contain rich sensory context.",True,False,,,True,7,1,8,12,Easy
Q112,N057,Psychology,1. Deception Detection & Behavioral Analysis,True/False,The DRS rule states that deception is strongly indicated when 11 or more points are observed within a single response cycle.,True,False,,,True,11,3,13,17,Medium
Q113,N057,Psychology,1. Deception Detection & Behavioral Analysis,True/False,"In the 3-phase response cycle, indicators are tracked across three timeframes: (B) Before, (D) During, and (A) After.",True,False,,,True,6,0,7,11,Easy
Q114,N058,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),True/False,The Time-Distance Problem in persuasion is how to get a person to deviate from normal behavior to an extreme degree in very little time.,True,False,,,True,8,2,10,16,Medium
Q115,N059,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),True/False,"According to the persuasion law, persuasion is 80% who you are, 15% what you do, and 5% who the other person is.",True,False,,,True,9,1,10,14,Easy
Q116,N060,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),True/False,"In 6MX profiling, the three primary decision filters are Conformity, Novelty, and Deviance.",True,False,,,True,10,2,12,18,Medium
Q117,N060,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),True/False,"The memory hook 'I -> YOU -> SHIFT' stands for Input, Yield, and Output.",True,False,,,False,12,5,15,20,Medium
Q118,N053,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,What is the primary prerequisite before judging any person for deception?,Establish their normal behavioral baseline in a relaxed conversation,Ask high-pressure confrontational questions immediately,Record their biometric pulse rate with a polygraph,Verify their credit history and background,Establish their normal behavioral baseline in a relaxed conversation,12,2,14,14,Easy
Q119,N053,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,What is the normal resting baseline blink rate in an unstressed conversational state?,9 to 20 blinks per minute,40 to 60 blinks per minute,1 to 5 blinks per minute,Over 70 blinks per minute,9 to 20 blinks per minute,10,3,12,13,Easy
Q120,N053,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,Which of the following is NOT one of the 4 key dimensions observed during baseline mapping?,Heart rate variability via chest monitor,Speech cadence and vocal pitch,Resting blink rate,Physical restlessness and hand gestures,Heart rate variability via chest monitor,8,1,9,15,Medium
Q121,N054,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,"Why do fabricated stories frequently drop personal pronouns (e.g., 'Woke up, went there, left')?",To reduce cognitive burden and distance the speaker from guilt,To sound more professional and poetic,Because short-term memory has been permanently wiped,To mimic legal court transcripts,To reduce cognitive burden and distance the speaker from guilt,11,4,13,19,Hard
Q122,N054,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,How is the chronological flow of a truthful episodic memory typically structured?,Begins with emotionally salient events or climax and shifts back and forth,Strictly linear and step-by-step from morning to night,Recited alphabetically by key nouns,Monotonous with excessive early trivial details,Begins with emotionally salient events or climax and shifts back and forth,9,2,11,17,Medium
Q123,N054,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,"Which statement illustrates a 'resume statement' used to avoid answering a specific accusation?","'I have served honorably for 25 years and I am an upstanding community pillar!'","'No, I did not unlock the vault at 3 PM.'","'I left the office at 5 PM with my coworker Sarah.'","'It was raining very heavily outside the building.'","'I have served honorably for 25 years and I am an upstanding community pillar!'",13,3,15,18,Medium
Q124,N054,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,What contraction pattern is characteristic of truthful conversational recall?,Natural spoken contractions such as 'didn't' and 'couldn't',Rigid non-contractions such as 'I did not at any time',Complete omission of all auxiliary verbs,Speaking in third-person Latin phrases,Natural spoken contractions such as 'didn't' and 'couldn't',7,1,8,12,Easy
Q125,N054,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,"What is the deceptive function of using soft words like 'borrow' or 'take' instead of 'steal'?",Psychological distancing to minimize guilt and discomfort,Grammatical elegance in formal speech,Testing the vocabulary level of the interviewer,Demonstrating total lack of emotion,Psychological distancing to minimize guilt and discomfort,10,3,12,16,Medium
Q126,N055,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,Why does a deceptive person's vocal pitch tend to rise under stress?,Adrenaline tightens the vocal cords,Carbon dioxide relaxes the diaphragm,Dopamine decreases blood pressure,The brain shifts into slow-wave sleep,Adrenaline tightens the vocal cords,8,2,10,14,Medium
Q127,N055,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,During which timeframe does the 'confirmation glance' occur?,Immediately after completing a statement (Timeframe: A),Before the question is finished (Timeframe: B),During the middle of the first sentence (Timeframe: D),Thirty minutes after the interview,Immediately after completing a statement (Timeframe: A),14,4,16,21,Hard
Q128,N055,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,What do pacifying gestures like face touching and neck rubbing signify?,Unconscious attempts to soothe psychological anxiety and discomfort,Conscious signaling to a co-conspirator,Biological reflexes to enhance eyesight,Tiredness of facial nerve muscles only,Unconscious attempts to soothe psychological anxiety and discomfort,9,1,10,13,Easy
Q129,N055,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,What does 'cognitive freeze' look like during an interview?,Body gestures slow down or stop completely while speaking,The subject shivers as if in extreme cold,The subject falls asleep instantly,Rapid erratic flailing of both arms,Body gestures slow down or stop completely while speaking,11,3,13,17,Medium
Q130,N055,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,Which of the following is an example of micro-incongruence?,Shaking head 'no' while verbally saying 'yes',Smiling warmly while greeting an old friend,Looking at a document when asked to read it,Sitting upright when invited to take a seat,Shaking head 'no' while verbally saying 'yes',6,0,7,11,Easy
Q131,N056,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,What is the core rationale behind increasing cognitive load during interrogation?,Liars rarely rehearse unexpected angles or complex mental tasks,Liars have superior working memory capacities,Truth-tellers cannot recall events when pressured,Cognitive load forces the suspect to confess immediately,Liars rarely rehearse unexpected angles or complex mental tasks,10,2,12,18,Medium
Q132,N056,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,Why is reverse chronology recall a powerful diagnostic tool?,Truthful memory can be retrieved in any order whereas fabricated scripts collapse,It disorients the human vestibular system,Only guilty people remember events in reverse order,Truthful people are incapable of speaking in chronological order,Truthful memory can be retrieved in any order whereas fabricated scripts collapse,12,4,15,22,Hard
Q133,N056,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,Which question exemplifies sensory detail probing?,What was the lighting and weather like when you walked outside?,What is your social security number?,Did you steal the laptop from room 4B?,How long have you lived in this city?,What was the lighting and weather like when you walked outside?,7,1,8,12,Easy
Q134,N056,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,How does asking a subject to change perspective (e.g. view from a bystander) test integrity?,Fabricators struggle with cognitive flexibility to maintain consistency across viewpoints,Truthful subjects refuse to imagine other viewpoints,Liars immediately run away when asked about bystanders,It activates the subject's mirror neurons to induce sleep,Fabricators struggle with cognitive flexibility to maintain consistency across viewpoints,11,3,13,19,Hard
Q135,N057,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,What does the Deception Rating Scale (DRS) rule mandate for indicating deception?,A cluster of 11 or more abnormal indicators in a single response cycle,A single instance of lip compression or face touching,At least 50 points accumulated across several days,Three vocal pitch increases in one hour,A cluster of 11 or more abnormal indicators in a single response cycle,13,4,15,16,Medium
Q136,N057,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,In the 5-step Decision Flow what should you do immediately after observing an anomalous cue?,Consider alternative explanations like fatigue or nervous anxiety,Conclude the person is guilty beyond reasonable doubt,Order an immediate physical arrest,Stop taking notes and end the meeting,Consider alternative explanations like fatigue or nervous anxiety,8,1,9,14,Easy
Q137,N057,Psychology,1. Deception Detection & Behavioral Analysis,MCQ,An unnatural pause before beginning an answer is mapped to which phase of the response cycle?,Before the answer (B),During the statement (D),After the answer (A),Recovery phase,Before the answer (B),10,2,12,15,Medium
Q138,N058,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),MCQ,How is influence formally defined in communication psychology?,Causing another person to think or take actions they would not have otherwise taken,Forcing physical compliance through intimidation and fear,Presenting mathematical proofs until the listener gives up,Using chemical substances to alter conscious states,Causing another person to think or take actions they would not have otherwise taken,6,0,7,12,Easy
Q139,N058,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),MCQ,In the Time-Distance Problem what does 'Distance' measure?,The degree of deviation from the subject's normal baseline behavior,The spatial distance between speaker and listener in meters,The travel distance required to sign a contract,The length of a written proposal in pages,The degree of deviation from the subject's normal baseline behavior,11,3,13,17,Medium
Q140,N058,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),MCQ,What does the memory hook 'I -> YOU -> SHIFT' represent?,Self (Master self) -> Subject (Profile them) -> Foundation (Shift perception),Idea -> Yield -> Utility,Intention -> Oath -> Understanding,Input -> Output -> System,Self (Master self) -> Subject (Profile them) -> Foundation (Shift perception),9,2,11,16,Medium
Q141,N059,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),MCQ,According to the 80/15/5 rule what determines 80% of persuasion success?,Who you are (internal state and composure),The specific words and scripts you utter,Who the other person is,The timing of the sales presentation,Who you are (internal state and composure),8,1,9,13,Easy
Q142,N059,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),MCQ,Which of the following is NOT one of the 5 components of authority in self-mastery?,Aggressive Dominance,Confidence,Discipline,Gratitude,Aggressive Dominance,12,3,14,18,Medium
Q143,N060,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),MCQ,What does the acronym 6MX stand for?,Six-Minute X-Ray,Six Maximum Extensions,Sixth Method of Exploration,Standard Matrix Experiment,Six-Minute X-Ray,6,1,7,10,Easy
Q144,N060,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),MCQ,In 6MX which quadrant explores social cravings like Significance or Approval?,Needs,Decisions,Values,Baselines,Needs,9,2,11,14,Medium
Q145,N060,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),MCQ,A person who deliberately chooses unconventional options to counter norms has which decision filter?,Deviance,Conformity,Novelty,Approval,Deviance,10,3,12,17,Medium
Q146,N060,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),MCQ,Where do a subject's deepest core 'Values' most frequently originate in 6MX analysis?,Childhood deficits or formative emotional experiences,Recent television commercials,The specific pitch given by the salesperson,Arbitrary random daily moods,Childhood deficits or formative emotional experiences,11,4,13,19,Hard
Q147,N060,Psychology,2. Influence & Persuasion (Foundation & 6MX Profiling),MCQ,Why are baseline behaviors tracked in real-time during 6MX profiling?,To detect sudden stress spikes and unspoken objections during conversation,To measure the subject's blood group,To test if the subject is left or right handed,To determine the subject's physical fitness level,To detect sudden stress spikes and unspoken objections during conversation,8,1,9,14,Easy
""".trimIndent()

    val MISTAKES_CSV = """
note_id,concept_title,chapter,total_wrong_attempts,linked_question_ids
N053,Behavioral Baseline & Blink Rate Norms,1. Deception Detection & Behavioral Analysis,4,Q098;Q119
N054,Narrative Structure & Pronoun Absence,1. Deception Detection & Behavioral Analysis,5,Q102;Q121
N057,DRS Clustering Rule & Thresholds,1. Deception Detection & Behavioral Analysis,6,Q112;Q135
N058,Time-Distance Problem & Persuasion Law,2. Influence & Persuasion (Foundation & 6MX Profiling),4,Q114;Q139
N029,Cultural and Educational Rights,3. Fundamental Rights,11,Q042;Q086
N035,Article 44 - Uniform Civil Code,4. Directive Principles,10,Q050;Q088
N036,Article 45 - Education for Children,4. Directive Principles,10,Q051;Q052
N019,Overview and Location,3. Fundamental Rights,9,Q027;Q028
N020,Six Categories,3. Fundamental Rights,9,Q029;Q081
N026,Right against Exploitation,3. Fundamental Rights,8,Q037;Q038
N027,Right to Constitutional Remedies,3. Fundamental Rights,8,Q039;Q085
N030,Overview and Location,4. Directive Principles,8,Q043;Q044
N033,Article 39 - Equal Pay,4. Directive Principles,8,Q047
N037,Article 48 - Agriculture and Cattle,4. Directive Principles,8,Q053;Q089
N006,Adoption Date,1. Making of the Constitution,7,Q009;Q010
N024,Right to Freedom - Article 19,3. Fundamental Rights,7,Q034;Q035
N028,Five Writs,3. Fundamental Rights,7,Q040;Q041
N032,Classification,4. Directive Principles,7,Q046;Q087
N034,Article 40 - Village Panchayats,4. Directive Principles,7,Q048;Q049
N022,Right to Equality Articles,3. Fundamental Rights,6,Q032;Q082
N025,Article 21 - Right to Life,3. Fundamental Rights,6,Q036;Q084
N003,Permanent President,1. Making of the Constitution,5,Q005;Q072
N007,Commencement Date,1. Making of the Constitution,5,Q011;Q074
N031,Non-Justiciable Nature,4. Directive Principles,5,Q045
N038,Article 51 - International Peace,4. Directive Principles,5,Q054
N042,Vice President Role,5. Union Executive,5,Q059;Q092
N044,Council of Ministers,5. Union Executive,5,Q061;Q093
N048,Appointment of Governor,6. State Executive,5,Q066;Q067
N051,Advocate General,6. State Executive,5,Q070;Q097
N001,Constituent Assembly Formation,1. Making of the Constitution,4,Q001;Q002
N002,First Meeting of the Assembly,1. Making of the Constitution,4,Q003;Q004
N005,Constitutional Advisor,1. Making of the Constitution,4,Q008;Q073
N008,Time Taken to Draft,1. Making of the Constitution,4,Q012;Q075
N012,42nd Amendment Changes,2. Preamble,4,Q018;Q019
N013,Source of Authority,2. Preamble,4,Q020;Q078
N021,Right to Property Removed,3. Fundamental Rights,4,Q030;Q031
N049,Chief Minister,6. State Executive,4,Q068;Q095
N004,Drafting Committee,1. Making of the Constitution,3,Q006;Q007
N009,Original Structure,1. Making of the Constitution,3,Q013;Q014
N011,Preamble Overview,2. Preamble,3,Q017
N015,Berubari Case 1960,2. Preamble,3,Q022;Q079
N023,Article 17 - Untouchability,3. Fundamental Rights,3,Q033;Q083
N046,Pardoning Power,5. Union Executive,3,Q063;Q064
N047,Governor - Head of State,6. State Executive,3,Q065;Q094
N050,State Council of Ministers,6. State Executive,3,Q069;Q096
N010,Borrowed Features,1. Making of the Constitution,3,Q076;Q077
N040,Presidential Election,5. Union Executive,2,Q056
N016,Kesavananda Bharati Case 1973,2. Preamble,1,Q024
N017,Four Ideals in Preamble,2. Preamble,1,Q025
N018,Republic Meaning,2. Preamble,1,Q026
N041,Term of President,5. Union Executive,1,Q058
N043,Prime Minister,5. Union Executive,1,Q060
N045,Attorney General,5. Union Executive,1,Q062
N052,Reserving Bills for President,6. State Executive,1,Q071
""".trimIndent()

    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        for (char in line) {
            when {
                char == '\"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    tokens.add(sb.toString().trim())
                    sb.clear()
                }
                else -> sb.append(char)
            }
        }
        tokens.add(sb.toString().trim())
        return tokens
    }

    fun getInitialNotes(): List<NoteEntity> {
        val list = mutableListOf<NoteEntity>()
        val lines = NOTES_CSV.lines()
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("note_id", ignoreCase = true)) continue
            val tokens = parseCsvLine(trimmed)
            if (tokens.size >= 7) {
                val rawNoteId = tokens[0]
                val idNum = rawNoteId.filter { it.isDigit() }.toLongOrNull() ?: 0L
                val subject = tokens[1]
                val chapter = tokens[2]
                val title = tokens[3]
                val content = tokens[4]
                val timeSpent = tokens[5].toLongOrNull() ?: 120L
                val revisit = tokens[6].toIntOrNull() ?: 1

                val chapNumber = chapter.split(".").firstOrNull()?.trim()?.toIntOrNull() ?: 1

                val sampleImageUri = when (idNum) {
                    1L -> "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?auto=format&fit=crop&w=800&q=80"
                    11L -> "https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=800&q=80"
                    19L -> "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?auto=format&fit=crop&w=800&q=80"
                    30L -> "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=800&q=80"
                    39L -> "https://images.unsplash.com/photo-1541872703-74c5e44368f9?auto=format&fit=crop&w=800&q=80"
                    47L -> "https://images.unsplash.com/photo-1575320181282-9afab399332c?auto=format&fit=crop&w=800&q=80"
                    53L -> "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800&q=80"
                    54L -> "https://images.unsplash.com/photo-1455390582262-044cdead277a?auto=format&fit=crop&w=800&q=80"
                    55L -> "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=800&q=80"
                    56L -> "https://images.unsplash.com/photo-1509228468518-180dd4864904?auto=format&fit=crop&w=800&q=80"
                    57L -> "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&w=800&q=80"
                    58L -> "https://images.unsplash.com/photo-1522071820081-009f0129c71c?auto=format&fit=crop&w=800&q=80"
                    59L -> "https://images.unsplash.com/photo-1517048676732-d65bc937f952?auto=format&fit=crop&w=800&q=80"
                    60L -> "https://images.unsplash.com/photo-1551836022-d5d88e9218df?auto=format&fit=crop&w=800&q=80"
                    else -> null
                }

                list.add(
                    NoteEntity(
                        id = idNum,
                        examId = "UPSI",
                        subjectName = subject,
                        chapterName = chapter,
                        chapterNumber = chapNumber,
                        title = title,
                        summaryText = content,
                        imageUri = sampleImageUri,
                        revisitCount = revisit,
                        timeSpentSeconds = timeSpent
                    )
                )
            }
        }
        return list
    }

    fun getInitialQuestions(): List<QuestionEntity> {
        val list = mutableListOf<QuestionEntity>()
        val lines = QUESTIONS_CSV.lines()
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("question_id", ignoreCase = true)) continue
            val tokens = parseCsvLine(trimmed)
            if (tokens.size >= 16) {
                val rawQId = tokens[0]
                val qIdNum = rawQId.filter { it.isDigit() }.toLongOrNull() ?: 0L
                val rawNoteId = tokens[1]
                val noteIdNum = rawNoteId.filter { it.isDigit() }.toLongOrNull()
                val subject = tokens[2]
                val chapter = tokens[3]
                val rawType = tokens[4]
                val questionText = tokens[5]
                val optA = tokens[6]
                val optB = tokens[7]
                val optC = tokens[8]
                val optD = tokens[9]
                val correctAnswer = tokens[10]
                val timesShown = tokens[11].toIntOrNull() ?: 0
                val timesWrong = tokens[12].toIntOrNull() ?: 0
                val totalAttempts = tokens[13].toIntOrNull() ?: 0
                val avgTimeSec = tokens[14].toLongOrNull() ?: 15L
                val lastRating = tokens[15].uppercase()

                val isTf = rawType.contains("True", ignoreCase = true) || rawType.contains("TF", ignoreCase = true)
                val finalType = if (isTf) "TRUE_FALSE" else "MULTIPLE_CHOICE"

                val finalA: String
                val finalB: String
                val finalC: String
                val finalD: String
                val correctIdx: Int

                if (isTf) {
                    finalA = "True"
                    finalB = "False"
                    finalC = ""
                    finalD = ""
                    correctIdx = if (correctAnswer.equals("True", ignoreCase = true) || correctAnswer.equals("T", ignoreCase = true)) 0 else 1
                } else {
                    finalA = optA
                    finalB = optB
                    finalC = optC
                    finalD = optD
                    correctIdx = when {
                        correctAnswer.equals(optA, ignoreCase = true) -> 0
                        correctAnswer.equals(optB, ignoreCase = true) -> 1
                        correctAnswer.equals(optC, ignoreCase = true) -> 2
                        correctAnswer.equals(optD, ignoreCase = true) -> 3
                        else -> 0
                    }
                }

                val totalTimeSpent = totalAttempts * avgTimeSec
                val isDue = timesWrong > 0 || lastRating == "HARD" || (totalAttempts > 0 && totalAttempts % 3 == 0)

                list.add(
                    QuestionEntity(
                        id = qIdNum,
                        linkedNoteId = noteIdNum,
                        examId = "UPSI",
                        subjectName = subject,
                        chapterName = chapter,
                        questionType = finalType,
                        questionText = questionText,
                        optionA = finalA as String,
                        optionB = finalB as String,
                        optionC = finalC as String,
                        optionD = finalD as String,
                        correctAnswerIndex = correctIdx as Int,
                        timesShown = timesShown,
                        timesWrong = timesWrong,
                        totalAttempts = totalAttempts,
                        totalTimeSpentSeconds = totalTimeSpent,
                        lastRating = lastRating,
                        isDue = isDue,
                        sourceType = "note",
                        sourceId = noteIdNum?.toString() ?: ""
                    )
                )
            }
        }
        // Add comprehensive questions directly linked to micro-learning reels
        list.add(
            QuestionEntity(
                id = 1001L,
                linkedNoteId = null,
                examId = "UPSI",
                subjectName = "Reels Concepts",
                chapterName = "3. Fundamental Rights",
                questionType = "MULTIPLE_CHOICE",
                questionText = "Which writ literally means 'We Command' and is issued to command a public authority to perform its legal duty?",
                optionA = "Habeas Corpus",
                optionB = "Mandamus",
                optionC = "Certiorari",
                optionD = "Quo-Warranto",
                correctAnswerIndex = 1,
                timesShown = 3,
                timesWrong = 1,
                totalAttempts = 3,
                totalTimeSpentSeconds = 45L,
                lastRating = "MEDIUM",
                isDue = true,
                sourceType = "reel",
                sourceId = "2"
            )
        )
        list.add(
            QuestionEntity(
                id = 1002L,
                linkedNoteId = null,
                examId = "UPSI",
                subjectName = "Reels Concepts",
                chapterName = "3. Fundamental Rights",
                questionType = "TRUE_FALSE",
                questionText = "Article 21 (Right to Life & Personal Liberty) can be suspended during a National Emergency.",
                optionA = "True",
                optionB = "False",
                correctAnswerIndex = 1,
                timesShown = 4,
                timesWrong = 2,
                totalAttempts = 4,
                totalTimeSpentSeconds = 50L,
                lastRating = "HARD",
                isDue = true,
                sourceType = "reel",
                sourceId = "1"
            )
        )
        list.add(
            QuestionEntity(
                id = 1003L,
                linkedNoteId = null,
                examId = "UPSI",
                subjectName = "Reels Concepts",
                chapterName = "2. Preamble",
                questionType = "MULTIPLE_CHOICE",
                questionText = "Which three words were added to the Preamble of the Indian Constitution by the 42nd Amendment Act (1976)?",
                optionA = "Socialist, Secular, Integrity",
                optionB = "Sovereign, Democratic, Republic",
                optionC = "Justice, Liberty, Equality",
                optionD = "Fraternity, Dignity, Unity",
                correctAnswerIndex = 0,
                timesShown = 3,
                timesWrong = 1,
                totalAttempts = 3,
                totalTimeSpentSeconds = 40L,
                lastRating = "HARD",
                isDue = true,
                sourceType = "reel",
                sourceId = "3"
            )
        )
        list.add(
            QuestionEntity(
                id = 1004L,
                linkedNoteId = null,
                examId = "UPSI",
                subjectName = "Reels Concepts",
                chapterName = "1. Making of the Constitution",
                questionType = "MULTIPLE_CHOICE",
                questionText = "Who was the Chairman of the Drafting Committee of the Indian Constituent Assembly?",
                optionA = "Dr. Rajendra Prasad",
                optionB = "Dr. B.R. Ambedkar",
                optionC = "Jawaharlal Nehru",
                optionD = "B.N. Rau",
                correctAnswerIndex = 1,
                timesShown = 3,
                timesWrong = 0,
                totalAttempts = 3,
                totalTimeSpentSeconds = 35L,
                lastRating = "EASY",
                isDue = false,
                sourceType = "reel",
                sourceId = "4"
            )
        )
        list.add(
            QuestionEntity(
                id = 1005L,
                linkedNoteId = null,
                examId = "UPSI",
                subjectName = "Reels Concepts",
                chapterName = "4. Directive Principles",
                questionType = "MULTIPLE_CHOICE",
                questionText = "The Directive Principles of State Policy (DPSPs) in Part IV were borrowed from which country's constitution?",
                optionA = "United States",
                optionB = "Ireland",
                optionC = "Australia",
                optionD = "Canada",
                correctAnswerIndex = 1,
                timesShown = 4,
                timesWrong = 2,
                totalAttempts = 4,
                totalTimeSpentSeconds = 48L,
                lastRating = "HARD",
                isDue = true,
                sourceType = "reel",
                sourceId = "5"
            )
        )
        list.add(
            QuestionEntity(
                id = 1006L,
                linkedNoteId = null,
                examId = "UPSI",
                subjectName = "Reels Concepts",
                chapterName = "5. Union Executive",
                questionType = "MULTIPLE_CHOICE",
                questionText = "Under which Article does the President possess the power to promulgate Ordinances during recess of Parliament?",
                optionA = "Article 72",
                optionB = "Article 123",
                optionC = "Article 143",
                optionD = "Article 352",
                correctAnswerIndex = 1,
                timesShown = 5,
                timesWrong = 2,
                totalAttempts = 5,
                totalTimeSpentSeconds = 55L,
                lastRating = "MEDIUM",
                isDue = true,
                sourceType = "reel",
                sourceId = "6"
            )
        )
        list.add(
            QuestionEntity(
                id = 1007L,
                linkedNoteId = null,
                examId = "UPSI",
                subjectName = "Reels Concepts",
                chapterName = "1. Introduction & Research Methods",
                questionType = "MULTIPLE_CHOICE",
                questionText = "In Pavlov's classical conditioning experiment, the meat powder that naturally caused dogs to salivate is termed as:",
                optionA = "Conditioned Stimulus (CS)",
                optionB = "Unconditioned Stimulus (UCS)",
                optionC = "Conditioned Response (CR)",
                optionD = "Extinction Trial",
                correctAnswerIndex = 1,
                timesShown = 4,
                timesWrong = 1,
                totalAttempts = 4,
                totalTimeSpentSeconds = 42L,
                lastRating = "HARD",
                isDue = true,
                sourceType = "reel",
                sourceId = "7"
            )
        )
        list.add(
            QuestionEntity(
                id = 1008L,
                linkedNoteId = null,
                examId = "UPSI",
                subjectName = "Reels Concepts",
                chapterName = "2. Biological Bases of Behavior",
                questionType = "MULTIPLE_CHOICE",
                questionText = "According to Maslow's Hierarchy of Needs pyramid, what is the pinnacle level of human psychological growth?",
                optionA = "Safety Needs",
                optionB = "Esteem Needs",
                optionC = "Self-Actualization",
                optionD = "Belongingness Needs",
                correctAnswerIndex = 2,
                timesShown = 2,
                timesWrong = 0,
                totalAttempts = 2,
                totalTimeSpentSeconds = 30L,
                lastRating = "EASY",
                isDue = false,
                sourceType = "reel",
                sourceId = "8"
            )
        )
        list.add(
            QuestionEntity(
                id = 1009L,
                linkedNoteId = null,
                examId = "UPSI",
                subjectName = "Reels Concepts",
                chapterName = "3. Sensation, Perception & Consciousness",
                questionType = "MULTIPLE_CHOICE",
                questionText = "In Freud's tripartite model of mind, which component operates strictly according to the 'Pleasure Principle'?",
                optionA = "Ego",
                optionB = "Superego",
                optionC = "Id",
                optionD = "Ego Ideal",
                correctAnswerIndex = 2,
                timesShown = 4,
                timesWrong = 2,
                totalAttempts = 4,
                totalTimeSpentSeconds = 50L,
                lastRating = "HARD",
                isDue = true,
                sourceType = "reel",
                sourceId = "9"
            )
        )
        list.add(
            QuestionEntity(
                id = 1010L,
                linkedNoteId = null,
                examId = "UPSI",
                subjectName = "Reels Concepts",
                chapterName = "4. Learning & Conditioning",
                questionType = "MULTIPLE_CHOICE",
                questionText = "In Skinner's operant conditioning, negative reinforcement specifically involves:",
                optionA = "Applying an aversive stimulus to discourage behavior",
                optionB = "Removing an aversive stimulus to strengthen a desired response",
                optionC = "Withholding all positive rewards",
                optionD = "Random punishment intervals",
                correctAnswerIndex = 1,
                timesShown = 3,
                timesWrong = 1,
                totalAttempts = 3,
                totalTimeSpentSeconds = 44L,
                lastRating = "HARD",
                isDue = true,
                sourceType = "reel",
                sourceId = "10"
            )
        )
        return list
    }

    fun getInitialReels(): List<ReelEntity> {
        return listOf(
            ReelEntity(
                id = 1L,
                reelId = 1L,
                exam = "UPSI",
                subject = "Indian Polity",
                chapter = "3. Fundamental Rights",
                title = "Article 21: Right to Life & Personal Liberty",
                description = "Understanding procedure established by law vs due process of law in Maneka Gandhi case (1978).",
                videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
                durationSeconds = 35,
                watchCount = 1420,
                timesWatched = 3,
                totalWatchTimeSeconds = 105L,
                uploadedBy = "admin"
            ),
            ReelEntity(
                id = 2L,
                reelId = 2L,
                exam = "UPSI",
                subject = "Indian Polity",
                chapter = "3. Fundamental Rights",
                title = "The 5 Constitutional Writs Explained",
                description = "Quick breakdown of Habeas Corpus, Mandamus, Prohibition, Certiorari, and Quo-Warranto under Article 32.",
                videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/friday.mp4",
                durationSeconds = 45,
                watchCount = 2150,
                timesWatched = 4,
                totalWatchTimeSeconds = 180L,
                uploadedBy = "admin"
            ),
            ReelEntity(
                id = 3L,
                reelId = 3L,
                exam = "UPSI",
                subject = "Indian Polity",
                chapter = "2. Preamble",
                title = "Preamble Keywords: 42nd Amendment",
                description = "How Socialist, Secular, and Integrity were added to the Preamble in 1976.",
                videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
                durationSeconds = 30,
                watchCount = 980,
                timesWatched = 2,
                totalWatchTimeSeconds = 60L,
                uploadedBy = "admin"
            ),
            ReelEntity(
                id = 4L,
                reelId = 4L,
                exam = "UPSI",
                subject = "Indian Polity",
                chapter = "1. Making of the Constitution",
                title = "Constituent Assembly & Drafting Committee",
                description = "Key facts on Dr. B.R. Ambedkar, Cabinet Mission Plan, and Samvidhan Divas.",
                videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/friday.mp4",
                durationSeconds = 40,
                watchCount = 1640,
                timesWatched = 2,
                totalWatchTimeSeconds = 80L,
                uploadedBy = "admin"
            ),
            ReelEntity(
                id = 5L,
                reelId = 5L,
                exam = "UPSI",
                subject = "Indian Polity",
                chapter = "4. Directive Principles",
                title = "DPSPs: Articles 36 to 51",
                description = "Non-justiciable fundamental principles of governance borrowed from Ireland.",
                videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
                durationSeconds = 32,
                watchCount = 890,
                timesWatched = 1,
                totalWatchTimeSeconds = 32L,
                uploadedBy = "admin"
            ),
            ReelEntity(
                id = 6L,
                reelId = 6L,
                exam = "UPSI",
                subject = "Indian Polity",
                chapter = "5. Union Executive",
                title = "Presidential Ordinance & Pardoning Powers",
                description = "Article 123 (Ordinance making power) and Article 72 (Pardoning power) compared.",
                videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/friday.mp4",
                durationSeconds = 38,
                watchCount = 1120,
                timesWatched = 2,
                totalWatchTimeSeconds = 76L,
                uploadedBy = "admin"
            ),
            ReelEntity(
                id = 7L,
                reelId = 7L,
                exam = "Self-Study",
                subject = "Psychology",
                chapter = "1. Introduction & Research Methods",
                title = "Classical Conditioning: Pavlov's Experiment",
                description = "Unconditioned stimulus, conditioned response, and stimulus generalization in 45s.",
                videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
                durationSeconds = 45,
                watchCount = 780,
                timesWatched = 2,
                totalWatchTimeSeconds = 90L,
                uploadedBy = "admin"
            ),
            ReelEntity(
                id = 8L,
                reelId = 8L,
                exam = "Self-Study",
                subject = "Psychology",
                chapter = "2. Biological Bases of Behavior",
                title = "Maslow's Hierarchy of Needs Pyramid",
                description = "From physiological safety to self-actualization: A visual memory aid.",
                videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/friday.mp4",
                durationSeconds = 35,
                watchCount = 650,
                timesWatched = 1,
                totalWatchTimeSeconds = 35L,
                uploadedBy = "admin"
            ),
            ReelEntity(
                id = 9L,
                reelId = 9L,
                exam = "Self-Study",
                subject = "Psychology",
                chapter = "3. Sensation, Perception & Consciousness",
                title = "Id, Ego, Superego: Freud's Tripartite Model",
                description = "The pleasure principle vs reality principle vs morality ego ideal.",
                videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
                durationSeconds = 40,
                watchCount = 820,
                timesWatched = 2,
                totalWatchTimeSeconds = 80L,
                uploadedBy = "admin"
            ),
            ReelEntity(
                id = 10L,
                reelId = 10L,
                exam = "Self-Study",
                subject = "Psychology",
                chapter = "4. Learning & Conditioning",
                title = "Operant Conditioning: Skinner Box",
                description = "Positive reinforcement, negative reinforcement, and punishment schedules.",
                videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/friday.mp4",
                durationSeconds = 38,
                watchCount = 940,
                timesWatched = 1,
                totalWatchTimeSeconds = 38L,
                uploadedBy = "admin"
            )
        )
    }

    fun getInitialMistakes(): List<SeedMistakeConcept> {
        val list = mutableListOf<SeedMistakeConcept>()
        val lines = MISTAKES_CSV.lines()
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("note_id", ignoreCase = true)) continue
            val tokens = parseCsvLine(trimmed)
            if (tokens.size >= 5) {
                val rawNoteId = tokens[0]
                val noteIdNum = rawNoteId.filter { it.isDigit() }.toLongOrNull() ?: 0L
                val conceptTitle = tokens[1]
                val chapter = tokens[2]
                val totalWrong = tokens[3].toIntOrNull() ?: 0
                val linkedQIds = tokens[4].split(";").mapNotNull { it.trim().filter { c -> c.isDigit() }.toLongOrNull() }

                list.add(
                    SeedMistakeConcept(
                        noteId = noteIdNum,
                        conceptTitle = conceptTitle,
                        chapter = chapter,
                        totalWrongAttempts = totalWrong,
                        linkedQuestionIds = linkedQIds
                    )
                )
            }
        }
        return list
    }

    fun getInitialSessions(): List<StudySessionEntity> {
        return listOf(
            StudySessionEntity(dayOfWeek = "Mon", subjectName = "Indian Polity", durationMinutes = 55, questionsAttempted = 42, dateStr = "2026-09-01"),
            StudySessionEntity(dayOfWeek = "Tue", subjectName = "Indian Polity", durationMinutes = 75, questionsAttempted = 65, dateStr = "2026-09-02"),
            StudySessionEntity(dayOfWeek = "Wed", subjectName = "Indian Polity", durationMinutes = 45, questionsAttempted = 38, dateStr = "2026-09-03"),
            StudySessionEntity(dayOfWeek = "Thu", subjectName = "Indian Polity", durationMinutes = 70, questionsAttempted = 55, dateStr = "2026-09-04"),
            StudySessionEntity(dayOfWeek = "Fri", subjectName = "Indian Polity", durationMinutes = 90, questionsAttempted = 72, dateStr = "2026-09-05"),
            StudySessionEntity(dayOfWeek = "Sat", subjectName = "Indian Polity", durationMinutes = 35, questionsAttempted = 25, dateStr = "2026-09-06"),
            StudySessionEntity(dayOfWeek = "Sun", subjectName = "Indian Polity", durationMinutes = 20, questionsAttempted = 10, dateStr = "2026-09-07"),
            StudySessionEntity(dayOfWeek = "Sat", subjectName = "Psychology", durationMinutes = 45, questionsAttempted = 28, dateStr = "2026-09-06")
        )
    }
}
