    package com.btl.login.interfaces;

    import com.btl.login.dto.SubjectDTO;
    import com.btl.login.entities.Subject;

    public interface OnSubjectActionListener {
        void onSubjectClick(SubjectDTO subjectDTO);
        void onSubjectDelete(SubjectDTO subjectDTO, int position);
        void onSubjectTagForEdit(SubjectDTO subjectDTO);
    }