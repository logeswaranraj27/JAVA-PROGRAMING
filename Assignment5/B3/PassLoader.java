import java.sql.*;

public class PassLoader {
    private static final String URL =
            "jdbc:mysql://localhost:3306/bus_pass_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "change-me";

    public static void insertStatement(Connection connection, int count) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (int i = 1; i <= count; i++) {
                statement.executeUpdate(
                        "INSERT INTO bus_pass VALUES ('P" + i + "','R" + i + "','Name " + i
                                + "','R12','Main Gate','2026-12-31',2500.00)");
            }
        }
    }

    public static void insertPrepared(Connection connection, int count) throws SQLException {
        String sql = "INSERT INTO bus_pass VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= count; i++) {
                statement.setString(1, "PP" + i);
                statement.setString(2, "R" + i);
                statement.setString(3, "Name " + i);
                statement.setString(4, "R12");
                statement.setString(5, "Main Gate");
                statement.setDate(6, Date.valueOf("2026-12-31"));
                statement.setDouble(7, 2500.00);
                statement.executeUpdate();
            }
        }
    }

    public static void insertBatch(Connection connection, int count) throws SQLException {
        String sql = "INSERT INTO bus_pass VALUES (?,?,?,?,?,?,?)";
        connection.setAutoCommit(false);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= count; i++) {
                statement.setString(1, "PB" + i);
                statement.setString(2, "R" + i);
                statement.setString(3, "Name " + i);
                statement.setString(4, "R12");
                statement.setString(5, "Main Gate");
                statement.setDate(6, Date.valueOf("2026-12-31"));
                statement.setDouble(7, 2500.00);
                statement.addBatch();
                if (i % 1000 == 0) statement.executeBatch();
            }
            statement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public static void main(String[] args) throws Exception {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            DatabaseMetaData meta = connection.getMetaData();
            System.out.println(meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            insertBatch(connection, 1000);
        }
    }
}
