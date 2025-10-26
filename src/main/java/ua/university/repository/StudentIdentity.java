package ua.university.repository;

import ua.university.model.Person;

public class StudentIdentity extends Person implements Identity{

    private String studentId;

    public StudentIdentity(){
        super(null, null, null);
    }
    @Override
    public String getIdentity() {
        return studentId;
    }
}


class StudentRepo extends GenericRepositoryForInterface<StudentIdentity>{

    public StudentRepo(){
        super("Student identity");
    }


}