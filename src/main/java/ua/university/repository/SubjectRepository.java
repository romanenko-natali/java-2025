package ua.university.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.model.Subject;

import java.util.Comparator;
import java.util.List;

public class SubjectRepository extends GenericRepository<Subject> {
    private static final Logger logger = LoggerFactory.getLogger(SubjectRepository.class);
    public SubjectRepository() {
        super(Subject::name, "Subject");
    }

    /**
     * Sort subjects by credits in descending order
     */
    public List<Subject> sortByCreditsDesc() {
        List<Subject> allSubjects = getAll(); // get a copy of items
        allSubjects.sort(Comparator.comparing(Subject::credits).reversed());
        logger.info("Sorted " + "Subject" + " by credits in descending order");
        return allSubjects;
    }

    /**
     * Sort subjects by credits in ascending order (optional)
     */
    public List<Subject> sortByCreditsAsc() {
        List<Subject> allSubjects = getAll();
        allSubjects.sort(Comparator.comparing(Subject::credits));
        logger.info("Sorted " + "Subject" + " by credits in ascending order");
        return allSubjects;
    }
}
