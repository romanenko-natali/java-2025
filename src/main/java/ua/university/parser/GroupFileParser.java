package ua.university.parser;

import ua.university.exception.InvalidDataException;
import ua.university.model.Group;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class GroupFileParser {
    private static final Logger logger = Logger.getLogger(GroupFileParser.class.getName());

    /**
     * Reads groups from CSV file format: number,specialty,startYear
     * Example: "101,Computer Science,2021"
     */
    public static List<Group> parseFromCSV(String filePath) throws InvalidDataException {
        List<Group> groups = new ArrayList<>();

        try {
            logger.log(Level.INFO, "Starting to parse groups from file: {0}", filePath);

            List<String> lines = Files.readAllLines(Path.of(filePath));

            for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
                String line = lines.get(lineNumber).trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    Group group = parseGroupFromLine(line, lineNumber + 1);
                    groups.add(group);
                    logger.log(Level.FINE, "Successfully parsed group from line {0}: {1}",
                            new Object[]{lineNumber + 1, group.getFullName()});

                } catch (InvalidDataException e) {
                    logger.log(Level.WARNING, "Failed to parse line {0}: {1}",
                            new Object[]{lineNumber + 1, e.getMessage()});
                }
            }

            logger.log(Level.INFO, "Successfully parsed {0} groups from file", groups.size());
            return groups;

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

    private static Group parseGroupFromLine(String line, int lineNumber) throws InvalidDataException {
        String[] parts = line.split(",");

        if (parts.length != 3) {
            throw new InvalidDataException(
                    "Line " + lineNumber + ": Expected format 'number,specialty,startYear' but got: " + line);
        }

        try {
            int number = Integer.parseInt(parts[0].trim());
            String specialty = parts[1].trim();
            int startYear = Integer.parseInt(parts[2].trim());

            return new Group(number, specialty, startYear);

        } catch (NumberFormatException e) {
            throw new InvalidDataException(
                    "Line " + lineNumber + ": Invalid number format in: " + line, e);
        }
    }
}