package ticketmanagement.jdbc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCDemo {
    public static void main(String[] args) {


        String url="jdbc:mysql://localhost:3306/workordermanagementsystemdb";
        String user="x";
        String password="x";

        try{ Class.forName("com.mysql.cj.jdbc.Driver");


            Connection connection = DriverManager.getConnection(url,user,password);
            System.out.println("Connected to database");

        }


      catch (ClassNotFoundException e){
            e.printStackTrace();
      }
        catch (SQLException throwables){
            throwables.printStackTrace();
        }
    }

}

