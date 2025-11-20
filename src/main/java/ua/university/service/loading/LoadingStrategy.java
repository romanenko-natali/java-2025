package ua.university.service.loading;

import ua.university.repository.*;
import ua.university.service.LoadResult;

@FunctionalInterface
public interface LoadingStrategy {

    LoadResult load(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            GroupRepository groupRepository,
            SubjectRepository subjectRepository,
            DataLoader dataLoader
    );
}