import java.io.*;
import java.sql.*;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

public class Main {
    public static int a = 0;
    public static void main(String[] args) throws IOException {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println("Give the path to your quizzzz.mv.db file (example: c:/user/example/repository-template) :");
        String pathToDatabase = userInput.readLine();
        checkIfFileExists(pathToDatabase);
        System.out.println("Given path found (does not guarantee correctness)");
        System.out.println("Give the path to the folder containing the activities.json (example: c:/user/example/activities_folder");
        System.out.println("Note! do not include the activities.json at the end of the path.");
        String pathToActivities = userInput.readLine();
        checkIfFileExists(pathToActivities);

        prepareInsertion(pathToActivities, pathToDatabase);
    }

    public static void checkIfFileExists(String path) {
        File testFile = new File(path);
        if (!testFile.exists()) {
            System.out.println("The given path does not exist. terminating program");
            System.exit(1);
        }
    }
    public static void prepareInsertion(String pathToActivities, String pathToDatabase) {
        Connection conn = null;
        try {
            conn = getConnection(pathToDatabase);
        } catch (SQLException e) {
            System.out.println("FATAL ERROR: Failed to make a connection to the .mv.db file; Make sure that the path is as specified");
            e.printStackTrace();
        }
        File dir = new File(pathToActivities);
        try {
            insertActivitiesIntoDatabase(dir, conn);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static File findJsonFile(File startPoint) {
        File[] childFiles = startPoint.listFiles();
        for (File file : childFiles) {
            if (file.getAbsolutePath().contains("activities.json")) {
                return file;
            }
        }
        return null;
    }

    public static void insertActivitiesIntoDatabase(File pathToActivities, Connection conn) throws IOException, SQLException {
        File jsonFile = findJsonFile(pathToActivities);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationConfig.Feature.USE_BIG_INTEGER_FOR_INTS, true);
        InputStream inJson = new FileInputStream(jsonFile.getAbsolutePath());
        Activity[] activities = mapper.readValue(inJson, Activity[].class);
        int counter = 0;
        for (Activity activity : activities) {
            File fileToImage = new File(pathToActivities, activity.getImage_path());
            if (!fileToImage.exists()) {
                System.out.println("found defect image, continuing without it");
                continue;
            }
            counter++;
            activity.setImage_path(fileToImage.getAbsolutePath());
            insertJSON(activity, conn);
        }

        System.out.println("Inserted " + counter + " activities");
    }

    public static void insertJSON(Activity activity, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO ACTIVITY VALUES (NEXT VALUE FOR ACT_SEQ, ?, ?, ?, ?)"
        );
        preparedStatement.setLong(1, activity.getConsumption_in_wh());
        preparedStatement.setString(2, activity.image_path);
        preparedStatement.setString(3, activity.getSource());
        preparedStatement.setString(4, activity.getTitle());
        preparedStatement.executeUpdate();
    }

    public static Connection getConnection(String path) throws SQLException {
        return DriverManager.getConnection(
                "jdbc:h2:file:" + path + "\\quizzzz", "sa", "");
    }


}
