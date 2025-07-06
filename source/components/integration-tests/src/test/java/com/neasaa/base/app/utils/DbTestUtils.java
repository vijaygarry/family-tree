package com.neasaa.base.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbTestUtils {
	
	private static final String URL = "jdbc:postgresql://localhost:5432/your_db";
    private static final String USER = "your_user";
    private static final String PASSWORD = "your_password";

    public static void cleanupTestUser(String userId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
                stmt.setString(1, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test data", e);
        }
    }
    
	private static Connection getDbConnection () throws SQLException {
		Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
		conn.setAutoCommit(true);
		return conn;
	}
	
	public void deleteFamilyMembers (int familyId) throws SQLException {
		Connection dbConnection = null;
		PreparedStatement stmt = null;
		try {
			dbConnection = getDbConnection();
			stmt = dbConnection.prepareStatement("DELETE FROM users WHERE user_id = ?");
            stmt.setInt(1, familyId);
            stmt.executeUpdate();	
		} finally {
			if(stmt != null) {
				stmt.close();
			}
			if(dbConnection != null) {
				dbConnection.close();
			}
		}
	}
	
}
