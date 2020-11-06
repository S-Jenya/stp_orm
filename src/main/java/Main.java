import java.sql.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("START stp_ORM");
        String url = "jdbc:mysql://127.0.0.1:3306/misc?serverTimezone=Europe/Minsk&useSSL=false";
        String username = "fred";
        String password = "zap";
        System.out.println("Connecting...");

        Statement statement = null;
        ResultSet result;

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connection successful!");
            String query = "select * from institution";
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            System.out.println(result);
            while(result.next()) {
                System.out.println(result.getString("institution_id") + " " + result.getString("name"));
            }

        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}
