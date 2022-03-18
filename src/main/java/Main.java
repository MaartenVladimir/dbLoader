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
        insertActivitiesIntoDatabase(dir, conn);
    }

    public static void insertActivitiesIntoDatabase(File dir, Connection conn) throws IOException, SQLException {
        if(dir.isDirectory()) {
            File[] files = dir.listFiles();
            Arrays.stream(files).forEach(x-> {
                try {
                    insertActivitiesIntoDatabase(x, conn);
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
                jsoNclass.imageSource = imageSource.replace(".json", ".png");
                insertJSON(jsoNclass, conn);
                System.out.println(a++);
            }
        }
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
