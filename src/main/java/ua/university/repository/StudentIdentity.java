package ua.university.repository;

public class StudentIdentity implements Identity{

    private String studentId;
    @Override
    public String getIdentity() {
        return studentId;
    }
}
