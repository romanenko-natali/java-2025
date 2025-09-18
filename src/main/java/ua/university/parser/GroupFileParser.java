package ua.university.parser;

import ua.university.exception.InvalidDataException;
import ua.university.model.Group;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupFileParser {
    private static final Logger logger = Logger.getLogger(GroupFileParser.class.getName());

    /**
     * Parses a single CSV line into a Group object.
     * Example line: "101,Computer Science,2021"
     *
     * @param line CSV line
     * @return parsed Group object
     * @throws InvalidDataException if line has invalid format or data
     */
    public static Group parseGroupFromLine(String line) throws InvalidDataException {
        String[] parts = line.split(",");
        if (parts.length != 3) {
            throw new InvalidDataException(
                    "Expected format 'number,specialty,startYear', got: " + line
            );
        }

        try {
            int number = Integer.parseInt(parts[0].trim());
            String specialty = parts[1].trim();
            int startYear = Integer.parseInt(parts[2].trim());

            return new Group(number, specialty, startYear);
        } catch (NumberFormatException e) {
            throw new InvalidDataException(
                    "Invalid number format in line: " + line, e
            );
        }
    }

    /**
     * Reads groups from a CSV file.
     *
     * @param filePath path to CSV file
     * @return list of parsed Group objects
     * @throws IOException if file cannot be read
     * @throws InvalidDataException if CSV contains invalid data (for individual lines)
     */
    public static List<Group> parseFromCSV(String filePath) throws IOException, InvalidDataException {
        List<Group> groups = new ArrayList<>();
        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }

        logger.log(Level.INFO, "Starting to parse groups from file: {0}", filePath);

        List<String> lines = Files.readAllLines(path);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            try {
                Group group = parseGroupFromLineWithNumber(line, i + 1);
                groups.add(group);
                logger.log(Level.INFO, "Parsed group from line {0}: {1}",
                        new Object[]{i + 1, group.getFullName()});
            } catch (InvalidDataException e) {
                logger.log(Level.WARNING, "Failed to parse line {0}: {1}",
                        new Object[]{i + 1, e.getMessage()});
            }
        }

        logger.log(Level.INFO, "Successfully parsed {0} groups from file", groups.size());
        return groups;
    }

    /**
     * Private helper to parse line with row number for better error messages
     */
    private static Group parseGroupFromLineWithNumber(String line, int lineNumber) throws InvalidDataException {
        try {
            return parseGroupFromLine(line);
        } catch (InvalidDataException e) {
            throw new InvalidDataException("Line " + lineNumber + ": " + e.getMessage(), e);
        }
    }
}
