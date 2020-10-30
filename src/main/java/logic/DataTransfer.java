package logic;

import entities.Box;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import dao.BoxProcedures;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataTransfer {
    public static final Logger logger = Logger.getRootLogger();

    //200MB = 209_715_200 bytes
    private static final long MAX_FILE_SIZE = 209_715_200;

    private DataTransfer() {
    }

    public static void save(File file) {

        //check file length
        if (file.length() > MAX_FILE_SIZE) {
            logger.info(String.format("Saving failed. Size of file exceeds limit in 200 mb: %.2f mb.", ((double) file.length() / 1_048_576)));
            return;
        }

        //check file existence
        if (!file.exists()) {
            logger.info("File not found: " + file.toString());
            return;
        }

        //store file in the box entity
        Box box = new Box();
        try {
            box.setName(FilenameUtils.getBaseName(file.toString()));
            box.setFormat(FilenameUtils.getExtension(file.toString()));
            box.setFile(FileUtils.readFileToByteArray(file));
            box.setSize(file.length());
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        //save box entity to the db using stored procedures, alternative : dao.BoxDao.save(box)
        if (BoxProcedures.runProcedureBoxSave(box)) {
            logger.info(box.getName().concat(".").concat(box.getFormat()) + " -> File saved successfully!");
        } else {
            logger.info(box.getName().concat(".").concat(box.getFormat()) + " -> Something went wrong, file not saved!");
        }
    }

    public static void retrieve(String name, String destinationDirectory) {

        //check if name is present
        String filename = FilenameUtils.getBaseName(name);
        if (filename.isEmpty()) {
            logger.info("File name not specified: " + name);
            return;
        }

        //check if format is present
        String fileFormat = FilenameUtils.getExtension(name);
        if (fileFormat.isEmpty()) {
            logger.info("File format not specified: " + name);
            return;
        }

        //check if path exists and is directory
        if (!Files.exists(Paths.get(destinationDirectory)) || !Files.isDirectory(Paths.get(destinationDirectory))) {
            logger.info("Output directory not exist: " + destinationDirectory);
            return;
        }

        //retrieve box with file from db using stored procedure, alternative: dao.BoxDao.retrieve(filename, fileFormat)
        Box box = BoxProcedures.runProcedureBoxRetrieve(filename, fileFormat);

        //check if file found
        if (box.getName() == null) {
            logger.info(filename.concat(".").concat(fileFormat) + " -> File not found in database!");
            return;
        }

        //store file to destination folder
        try {
            File retrievedFile = new File(destinationDirectory, box.getName().concat(".").concat(box.getFormat()));
            FileUtils.writeByteArrayToFile(retrievedFile, box.getFile());
        } catch (IOException e) {
            logger.info("File download failed!");
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        //report about finished job.
        logger.info(String.format("File retrieved successfully! File name: '%s', size %.2f mb., directory '%s'.%n",
                (box.getName().concat(".").concat(box.getFormat())), (double) box.getSize() / 1_048_576, destinationDirectory));

    }
}
