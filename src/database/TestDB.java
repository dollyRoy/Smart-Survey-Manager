package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestDB {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            System.out.println("Connected ✅");
        } catch (Exception e) {
            System.out.println("Error ❌");
        }
    }
}
