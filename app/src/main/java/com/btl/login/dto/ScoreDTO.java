package com.btl.login.dto;
public class ScoreDTO {
    private String studentFullName;   // Họ và tên sinh viên
    private String teacherFullName;   // Họ và tên giáo viên
    private String subjectName;       // Tên môn học
    private String semesterName;      // Tên học kỳ
    private String academicYearName;  // Tên năm học
    private double processScore;      // Điểm Quá trình
    private double midtermScore;      // Điểm Giữa kỳ
    private double finalScore;        // Điểm Cuối kỳ

    // Constructor
    public ScoreDTO(String studentFullName, String teacherFullName, String subjectName,
                    String semesterName, String academicYearName,
                    double processScore, double midtermScore, double finalScore) {
        this.studentFullName = studentFullName;
        this.teacherFullName = teacherFullName;
        this.subjectName = subjectName;
        this.semesterName = semesterName;
        this.academicYearName = academicYearName;
        this.processScore = processScore;
        this.midtermScore = midtermScore;
        this.finalScore = finalScore;
    }

    // Getters và Setters
    public String getStudentFullName() { return studentFullName; }
    public void setStudentFullName(String studentFullName) { this.studentFullName = studentFullName; }

    public String getTeacherFullName() { return teacherFullName; }
    public void setTeacherFullName(String teacherFullName) { this.teacherFullName = teacherFullName; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSemesterName() { return semesterName; }
    public void setSemesterName(String semesterName) { this.semesterName = semesterName; }

    public String getAcademicYearName() { return academicYearName; }
    public void setAcademicYearName(String academicYearName) { this.academicYearName = academicYearName; }

    public double getProcessScore() { return processScore; }
    public void setProcessScore(double processScore) { this.processScore = processScore; }

    public double getMidtermScore() { return midtermScore; }
    public void setMidtermScore(double midtermScore) { this.midtermScore = midtermScore; }

    public double getFinalScore() { return finalScore; }
    public void setFinalScore(double finalScore) { this.finalScore = finalScore; }
}