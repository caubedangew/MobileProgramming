package com.btl.login.dto;

import androidx.room.Embedded;
import com.btl.login.entities.Subject;

public class SubjectDTO {
    @Embedded
    private Subject subject; // Thông tin môn học

    // Constructor
    public SubjectDTO(Subject subject) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject cannot be null");
        }
        this.subject = subject;
    }

    // Getter & Setter
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject cannot be null");
        }
        this.subject = subject;
    }

    // Utility Method
    public String getSubjectDetails() {
        return "Tên môn: " + subject.getSubjectName() + ", Số tín chỉ: " + subject.getCreditNumber();
    }
}