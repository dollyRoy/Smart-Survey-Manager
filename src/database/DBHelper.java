package database;
import  java.sql.*;

public class DBHelper {
    private static final String URL =
            "jdbc:sqlite:C:/Users/roydo/DSA/Smart Surveying Manager/survey.db";
    public static Connection connect() throws SQLException{
        return DriverManager.getConnection(URL);
    }
    public static void createTable(){
        String sql = "CREATE TABLE IF NOT EXISTS points ("
                + "id INTEGER PRIMARY KEY, "
                + "x REAL, "
                + "y REAL, "
                + "z REAL"
                + ")";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void insertPoint(int id, double x, double y, double z){
        String sql = "INSERT INTO points(id,x,y,z) VALUES (?,?,?,?)";

        try (Connection conn = connect();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,id);
            ps.setDouble(2,x);
            ps.setDouble(3,y);
            ps.setDouble(4,z);

            ps.executeUpdate();
            System.out.println("Data inserted Successfully");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ResultSet getAllPoints(Connection conn) throws SQLException{
        String sql = "SELECT * FROM points";
        return conn.createStatement().executeQuery(sql);
    }

    public static void deletePoint(int id){
        String sql = "DELETE FROM points WHERE id = ?";

        try(Connection conn = connect();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,id);
            ps.executeUpdate();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void clearAll() {
        String sql = "DELETE FROM points";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // updating Query
    public static void updatePoint(int id , double x, double y , double z){
        String sql = "UPDATE points SET x = ? , y = ?, z= ? WHERE id = ?";

        try(Connection conn = connect();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setDouble(1,x);
            ps.setDouble(2,y);
            ps.setDouble(3,z);
            ps.setInt(4,id);
            ps.executeUpdate();
            System.out.println("Update Successfully");

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
