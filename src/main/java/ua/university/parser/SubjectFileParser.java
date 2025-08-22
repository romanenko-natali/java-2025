package ua.university.parser;

import ua.university.exception.InvalidDataException;
import ua.university.model.Subject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SubjectFileParser {
    private static final Logger logger = Logger.getLogger(SubjectFileParser.class.getName());

    /**
     * Reads subjects from CSV file format: name,credits
     * Example: "Mathematics,5"
     */
    public static List<Subject> parseFromCSV(String filePath) throws InvalidDataException {
        List<Subject> subjects = new ArrayList<>();

        try {
            logger.log(Level.INFO, "Starting to parse subjects from file: {0}", filePath);

            List<String> lines = Files.readAllLines(Path.of(filePath));

            for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
                String line = lines.get(lineNumber).trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    Subject subject = parseSubjectFromLine(line, lineNumber + 1);
                    subjects.add(subject);
                    logger.log(Level.FINE, "Successfully parsed subject from line {0}: {1}",
                            new Object[]{lineNumber + 1, subject.name()});

                } catch (InvalidDataException e) {
                    logger.log(Level.WARNING, "Failed to parse line {0}: {1}",
                            new Object[]{lineNumber + 1, e.getMessage()});
                }
            }

            logger.log(Level.INFO, "Successfully parsed {0} subjects from file", subjects.size());
            return subjects;

        } catch (FileNotFoundException e) {
            String errorMsg = "File not found: " + filePath;
            logger.log(Level.SEVERE, errorMsg, e);
            throw new InvalidDataException(errorMsg, e);

        } catch (IOException e) {
            String errorMsg = "Error reading file: " + filePath;
            logger.log(Level.SEVERE, errorMsg, e);
            throw new InvalidDataException(errorMsg, e);

        } catch (SecurityException e) {
            String errorMsg = "Access denied to file: " + filePath;
            logger.log(Level.SEVERE, errorMsg, e);
            throw new InvalidDataException(errorMsg, e);
        }
    }

    private static Subject parseSubjectFromLine(String line, int lineNumber) throws InvalidDataException {
        String[] parts = line.split(",");

        if (parts.length != 2) {
            throw new InvalidDataException(
                    "Line " + lineNumber + ": Expected format 'name,credits' but got: " + line);
        }

        String name = parts[0].trim();

        try {
            int credits = Integer.parseInt(parts[1].trim());
            return new Subject(name, credits);
        } catch (NumberFormatException e) {
            throw new InvalidDataException(
                    "Line " + lineNumber + ": Invalid credits format: " + parts[1], e);
        }

    }
}
