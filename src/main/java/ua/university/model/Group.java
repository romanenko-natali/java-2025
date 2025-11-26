package ua.university.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import ua.university.util.ValidationUtils;
import ua.university.validation.ValidStartYear;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record Group(
        @Positive(message = "Group number must be positive")
        int number,

        @NotBlank(message = "Specialty cannot be null or blank")
        @Pattern(
                regexp = "^[a-zA-Z\\s\\-']{2,100}$",
                message = "Specialty must be 2-100 characters long and contain only letters, spaces, hyphens, or apostrophes"
        )
        String specialty,

        @ValidStartYear(message = "Start year must be within last 10 years")
        int startYear
) implements Comparable<Group> {

    private static final Logger logger = LoggerFactory.getLogger(Group.class);

    public Group {
        specialty = specialty != null ? specialty.trim() : null;
        logger.info("Group created: {} {} started in {}", number, specialty, startYear);
    }

    public static Group createValidGroup(int number, String specialty, int startYear) {
        Group group = new Group(number, specialty, startYear);
        ValidationUtils.validate(group);
        return group;
    }

    @JsonIgnore
    public int getCurrentYear() {
        return LocalDate.now().getYear() - startYear + 1;
    }

    @JsonIgnore
    public String getFullName() {
        return formatGroupFullNumber();
    }

    @JsonIgnore
    public boolean isGraduated() {
        return getCurrentYear() > 4;
    }

    public String formatGroupFullNumber() {
        return specialty().substring(0, 2).toUpperCase() + number() + "-" + startYear() % 100;
    }

    @Override
    public int compareTo(Group o) {
        return this.getFullName().compareTo(o.getFullName());
    }
}