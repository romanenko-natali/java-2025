package ua.university.service.loading;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.university.config.AppConfig;
import ua.university.model.Group;
import ua.university.model.Student;
import ua.university.model.Subject;
import ua.university.model.Teacher;
import ua.university.persistence.PersistenceManager;
import ua.university.repository.GroupRepository;
import ua.university.repository.StudentRepository;
import ua.university.repository.SubjectRepository;
import ua.university.repository.TeacherRepository;

import static org.junit.jupiter.api.Assertions.*;

class DataLoaderTest {

    private DataLoader dataLoader;

    @BeforeEach
    void setUp() {
        AppConfig config = new AppConfig();
        PersistenceManager persistenceManager = new PersistenceManager(config);
        dataLoader = new DataLoader(persistenceManager, "JSON");
    }

    @Test
    void loadEntity_Subjects_ShouldLoadEntitiesIntoRepository() throws Exception {
        SubjectRepository repo = new SubjectRepository();

        int loaded = dataLoader.loadEntity(Subject.class, repo);

        assertEquals(3, loaded);
        assertEquals(3, repo.size());
        assertTrue(repo.findByIdentity("Програмування").isPresent());
        assertTrue(repo.findByIdentity("Математика").isPresent());
        assertTrue(repo.findByIdentity("Фізика").isPresent());
    }

    @Test
    void loadEntity_Teachers_ShouldLoadEntitiesIntoRepository() throws Exception {
        TeacherRepository repo = new TeacherRepository();

        int loaded = dataLoader.loadEntity(Teacher.class, repo);

        assertEquals(2, loaded);
        assertEquals(2, repo.size());
    }

    @Test
    void loadEntity_Groups_ShouldLoadEntitiesIntoRepository() throws Exception {
        GroupRepository repo = new GroupRepository();

        int loaded = dataLoader.loadEntity(Group.class, repo);

        assertEquals(2, loaded);
        assertEquals(2, repo.size());
    }

    @Test
    void loadEntity_Students_ShouldLoadEntitiesIntoRepository() throws Exception {
        StudentRepository repo = new StudentRepository();

        int loaded = dataLoader.loadEntity(Student.class, repo);

        assertEquals(2, loaded);
        assertEquals(2, repo.size());
        assertTrue(repo.findByIdentity("KBN101").isPresent());
        assertTrue(repo.findByIdentity("KBN102").isPresent());
    }

    @Test
    void loadEntity_WithCustomEntityType_ShouldWork() throws Exception {
        SubjectRepository repo = new SubjectRepository();

        int loaded = dataLoader.loadEntity("subjects", Subject.class, repo);

        assertEquals(3, loaded);
    }

    @Test
    void loadEntity_IntoNonEmptyRepository_ShouldAddNewItems() throws Exception {
        SubjectRepository repo = new SubjectRepository();
        repo.add(new Subject("Хімія", 2));

        int loaded = dataLoader.loadEntity(Subject.class, repo);

        assertEquals(3, loaded);
        assertEquals(4, repo.size());
    }

    @Test
    void loadEntity_WithDuplicates_ShouldSkipExisting() throws Exception {
        SubjectRepository repo = new SubjectRepository();
        repo.add(new Subject("Програмування", 5));

        int loaded = dataLoader.loadEntity(Subject.class, repo);

        assertEquals(2, loaded);
        assertEquals(3, repo.size());
    }
}