import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Arrays;
import org.codehaus.jackson.map.ObjectMapper;

public class Main {
    public static int a = 0;
    public static void main(String[] args) throws SQLException, IOException {
        Connection conn = getConnection(args[0]);
        String pathToActivities = args[1];
        File dir = new File(pathToActivities);
        insertActivitiesIntoDatabase(dir, conn, null);
    }

    public static void insertActivitiesIntoDatabase(File dir, Connection conn, File parent) throws IOException, SQLException {
        if(dir.isDirectory()) {
            File[] files = dir.listFiles();
            Arrays.stream(files).forEach(x-> {
                try {
                    insertActivitiesIntoDatabase(x, conn, dir);
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            });
        }
        if (dir.isFile()) {
            if (dir.getAbsolutePath().contains("json")) {
                InputStream inJson = new FileInputStream(dir.getAbsolutePath());
                JSONclass jsoNclass = new ObjectMapper().readValue(inJson, JSONclass.class);
                String imageSource = dir.getAbsolutePath();
                File[] siblings = parent.listFiles();
                String extension = determineExtension(siblings, dir);
                jsoNclass.imageSource = imageSource.replace(".json", extension);
                insertJSON(jsoNclass, conn);
                System.out.println(a++);
            }
        }
    }

    public static String determineExtension(File[] siblings, File child) {
        String childName = child.getName().replace(".json", "");
        for (File sibling : siblings) {
            if (sibling.getName().contains(childName)) {
                if (sibling.getName().contains(".json")) {
                    continue;
                }
                return sibling.getName().replace(childName, "");
            }
        }
        System.out.println("Cant find image associated with : " + childName);
        return null;
    }
    public static void insertJSON(JSONclass jsoNclass, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO ACTIVITY VALUES (NEXT VALUE FOR ACT_SEQ, ?, ?, ?, ?)"
        );
        preparedStatement.setLong(1, jsoNclass.getConsumption_in_wh());
        preparedStatement.setString(2, jsoNclass.imageSource);
        preparedStatement.setString(3, jsoNclass.getSource());
        preparedStatement.setString(4, jsoNclass.getTitle());
        preparedStatement.executeUpdate();
    }

    public static Connection getConnection(String path) throws SQLException {
        return DriverManager.getConnection(
                "jdbc:h2:file:" + path + "\\quizzzz", "sa", "");
    }


}
