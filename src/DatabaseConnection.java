import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection getConnection() {

        String url = "jdbc:mysql://localhost:3306/campus_care";
        String username = "root";
        String password = "S@njana123";

        try {

            Connection con = DriverManager.getConnection(
                    url, username, password
            );

            System.out.println("Database Connected Successfully!");

            return con;

        } catch (Exception e) {

            System.out.println("Database Connection Failed!");
            e.printStackTrace();

            return null;
        }
    }

    public static void main(String[] args) {
        getConnection();
    }
}