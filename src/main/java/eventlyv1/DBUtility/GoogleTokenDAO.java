package eventlyv1.DBUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GoogleTokenDAO {

    public static void saveOrUpdateToken(GoogleToken token) {
        String sql = "INSERT INTO google_tokens (user_id, access_token, refresh_token, token_type, expiry_time) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (user_id) DO UPDATE SET " +
                "access_token = EXCLUDED.access_token, " +
                "refresh_token = EXCLUDED.refresh_token, " +
                "token_type = EXCLUDED.token_type, " +
                "expiry_time = EXCLUDED.expiry_time;";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Database connection not initialized.getting null");
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, token.userId);
                ps.setString(2, token.accessToken);
                ps.setString(3, token.refreshToken);
                ps.setString(4, token.tokenType);
                ps.setLong(5, token.expiryTime);
                ps.executeUpdate();
            }catch (SQLException e) {
                System.err.println("Error while saving/updating token: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static GoogleToken getToken(String userId) throws SQLException {
        String sql = "SELECT access_token, refresh_token, token_type, expiry_time FROM google_tokens WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new GoogleToken(userId,
                        rs.getString("access_token"),
                        rs.getString("refresh_token"),
                        rs.getString("token_type"),
                        rs.getLong("expiry_time"));
            } else {
                return null;
            }
        }
    }
}
