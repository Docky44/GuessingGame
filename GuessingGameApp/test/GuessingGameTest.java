import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;

public class GuessingGameTest {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/GuessingGameDB";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    @BeforeAll
    public static void setupDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = connection.createStatement()) {
            String createTable = "CREATE TABLE IF NOT EXISTS Scores (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "player_name VARCHAR(50)," +
                    "attempts INT," +
                    "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            stmt.execute(createTable);
        }
    }

    @BeforeEach
    public void clearDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM Scores");
        }
    }

    @Test
    public void testSaveScore() {
        String playerName = "TestPlayer";
        int attempts = 5;

        GuessingGame.saveScore(playerName, attempts);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Scores WHERE player_name = ?")) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();

            assertTrue(rs.next(), "A record should be found");
            assertEquals(playerName, rs.getString("player_name"), "Player name should match");
            assertEquals(attempts, rs.getInt("attempts"), "Attempts should match");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("SQLException should not occur");
        }
    }
}
