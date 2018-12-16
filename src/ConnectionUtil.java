import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConnectionUtil {

    static {

    }
    public  static Connection  connection  =null;

    public synchronized static Connection getConneciton(){

        if(connection  == null){
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:test","CUST","APP");
                connection.setAutoCommit(false);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return connection;


    }

    public static void main(String[] args) throws ParseException {


    }


}
