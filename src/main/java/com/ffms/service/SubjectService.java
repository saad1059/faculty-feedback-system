package com.ffms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ffms.model.Subject;
import com.ffms.model.Faculty;
import com.ffms.model.Student;
import com.ffms.repository.SubjectRepository;
import com.ffms.repository.FacultyRepository;
import com.ffms.repository.StudentRepository;
import com.ffms.exception.RegistrationException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
            .orElseThrow(() -> new RegistrationException("Subject not found"));
    }

    public Subject createSubject(Subject subject) {
        if (subjectRepository.findBySubjectCode(subject.getSubjectCode()) != null) {
            throw new RegistrationException("Subject code already exists");
        }
        return subjectRepository.save(subject);
    }

    public Subject updateSubject(Subject subject) {
        Subject existingSubject = subjectRepository.findById(subject.getId())
            .orElseThrow(() -> new RegistrationException("Subject not found"));

        Subject subjectWithCode = subjectRepository.findBySubjectCode(subject.getSubjectCode());
        if (subjectWithCode != null && !subjectWithCode.getId().equals(subject.getId())) {
            throw new RegistrationException("Subject code already exists");
        }

        return subjectRepository.save(subject);
    }

    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }

    public void assignFacultyToSubject(Long subjectId, Long facultyId) {
        Subject subject = getSubjectById(subjectId);
        Faculty faculty = facultyRepository.findById(facultyId)
            .orElseThrow(() -> new RegistrationException("Faculty not found"));

        subject.getFaculties().add(faculty);
        subjectRepository.save(subject);
    }

    public void removeFacultyFromSubject(Long subjectId, Long facultyId) {
        Subject subject = getSubjectById(subjectId);
        Faculty faculty = facultyRepository.findById(facultyId)
            .orElseThrow(() -> new RegistrationException("Faculty not found"));

        subject.getFaculties().remove(faculty);
        subjectRepository.save(subject);
    }

    public void enrollStudentInSubject(Long subjectId, Long studentId) {
        Subject subject = getSubjectById(subjectId);
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RegistrationException("Student not found"));

        subject.getStudents().add(student);
        subjectRepository.save(subject);
    }

    public void removeStudentFromSubject(Long subjectId, Long studentId) {
        Subject subject = getSubjectById(subjectId);
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RegistrationException("Student not found"));

        subject.getStudents().remove(student);
        subjectRepository.save(subject);
    }

    public List<Subject> getSubjectsByFaculty(Long facultyId) {
        return subjectRepository.findByFacultiesId(facultyId);
    }

    public List<Subject> getSubjectsByStudent(Long studentId) {
        return subjectRepository.findByStudentsId(studentId);
    }

    public Set<Faculty> getFacultiesForSubject(Long subjectId) {
        return getSubjectById(subjectId).getFaculties();
    }

    public Set<Student> getStudentsForSubject(Long subjectId) {
        return getSubjectById(subjectId).getStudents();
    }

    public List<Subject> getAvailableSubjectsForStudent(Long studentId) {
        List<Subject> allSubjects = getAllSubjects();
        List<Subject> enrolledSubjects = getSubjectsByStudent(studentId);
        
        // Filter out enrolled subjects and ensure faculties are loaded
        return allSubjects.stream()
            .filter(subject -> !enrolledSubjects.contains(subject))
            .peek(subject -> {
                // Force load faculties
                subject.getFaculties().size();
            })
            .collect(Collectors.toList());
    }

    public List<Subject> getAvailableSubjectsForFaculty(Long facultyId) {
        List<Subject> allSubjects = getAllSubjects();
        List<Subject> teachingSubjects = getSubjectsByFaculty(facultyId);
        return allSubjects.stream()
            .filter(subject -> !teachingSubjects.contains(subject))
            .collect(Collectors.toList());
    }
} 