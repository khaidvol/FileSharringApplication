import dao.DatabaseConfigurator;
import logic.DataTransfer;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        DatabaseConfigurator.autoConfiguration();

        //take 2 files (picture, video) from directory "src/main/resources/",
        // save to db and then retrieve to "src/test/resources/"
        File filePicture = new File("src/main/resources/test_source/picture.jpg");
        DataTransfer.save(filePicture);
        DataTransfer.retrieve("picture.jpg", "src/test/resources/test_destination/");

        File video = new File("src/main/resources/test_source/Case Study Overview.mp4");
        DataTransfer.save(video);
        DataTransfer.retrieve("Case Study Overview.mp4", "src/test/resources/test_destination/");

//        this one will fail, file not exist
        File fakeFile = new File("src/main/resources/test_source/Interacting with BigQuery.mp4");
        DataTransfer.save(fakeFile);
        DataTransfer.retrieve("Interacting with BigQuery.mp4", "src/test/resources/test_destination/");

        // check db after run, 3 files are still there.

    }
}
