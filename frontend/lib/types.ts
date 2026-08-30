export type Course = {
  courseId: number;
  title: string;
  description: string | null;
  level: string | null;
  duration: number | null;
};

export type Enrollment = {
  enrollmentId: number;
  courseId: number;
  courseTitle: string;
  status: string;
  enrolledAt: string;
};

export type Content = {
  contentId: number;
  courseId: number;
  title: string;
  contentType: string;
  youtubeLink: string | null;
  fileUrl: string | null;
};

export type LiveClass = {
  classId: number;
  courseId: number;
  courseTitle: string;
  meetingLink: string;
  classDate: string;
};

export type PracticeTestSummary = {
  testId: number;
  courseId: number;
  title: string;
  category: string;
  duration: number | null;
  totalMarks: number | null;
};

export type SupportTicket = {
  ticketId: number;
  subject: string;
  message: string;
  status: string;
  createdAt: string;
  studentId: number;
  studentName: string;
  adminId: number | null;
};

export type TeacherCourse = {
  teacherCourseId: number;
  courseId: number;
  courseTitle: string;
  teacherId: number;
  teacherName: string;
  isActive: boolean;
};

export type CourseEnrollment = {
  enrollmentId: number;
  studentId: number;
  studentName: string;
  status: string;
  enrolledAt: string;
};

export type AnswerOption = {
  optionId: number;
  optionText: string;
  isCorrect: boolean | null;
};

export type Question = {
  questionId: number;
  questionText: string;
  skill: string;
  marks: number;
  options: AnswerOption[];
};

export type PracticeTestDetail = {
  testId: number;
  courseId: number;
  title: string;
  category: string;
  duration: number | null;
  totalMarks: number | null;
  questions: Question[];
};

export type Announcement = {
  announcementId: number;
  title: string;
  message: string;
  createdAt: string;
  adminName: string;
};

export type AdminUser = {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  role: "Student" | "Teacher" | "Admin";
  isActive: boolean;
  createdAt: string;
};

export type AdminReport = {
  totalUsers: number;
  totalStudents: number;
  totalTeachers: number;
  totalCourses: number;
  totalEnrollments: number;
  openSupportTickets: number;
};

export type AdminLog = {
  logId: number;
  action: string;
  details: string | null;
  loggedAt: string;
  adminName: string;
};

export type AttemptStart = {
  attemptId: number;
  test: PracticeTestDetail;
};

export type AttemptResult = {
  attemptId: number;
  testId: number;
  testTitle: string;
  startTime: string;
  submitTime: string | null;
  score: number | null;
  overallBand: number | null;
  listening: number | null;
  reading: number | null;
  writing: number | null;
  speaking: number | null;
  feedback: string | null;
};

export type AttemptSummary = {
  attemptId: number;
  testId: number;
  testTitle: string;
  category: string;
  submitTime: string;
  bandScore: number | null;
};

export type Progress = {
  targetBand: number | null;
  currentBand: number | null;
  testsCompleted: number;
  avgListening: number | null;
  avgReading: number | null;
  avgWriting: number | null;
  avgSpeaking: number | null;
  recentAttempts: AttemptSummary[];
};

export type Submission = {
  submissionId: number;
  courseId: number;
  courseTitle: string;
  skill: "Writing" | "Speaking";
  submissionType: string;
  textContent: string | null;
  audioUrl: string | null;
  status: "Pending" | "Graded";
  bandScore: number | null;
  feedback: string | null;
  gradedByName: string | null;
  studentId: number;
  studentName: string;
  submittedAt: string;
  gradedAt: string | null;
};

export type Mission = {
  key: string;
  title: string;
  xpReward: number;
  done: boolean;
};

export type Achievement = {
  key: string;
  title: string;
  icon: string;
  unlocked: boolean;
};

export type GamificationSummary = {
  xp: number;
  coins: number;
  streak: number;
  level: number;
  missions: Mission[];
  achievements: Achievement[];
};
