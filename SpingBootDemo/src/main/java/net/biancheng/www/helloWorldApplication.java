package net.biancheng.www;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class helloWorldApplication {
    public static void main(String[] args) {
        System.out.println(new Date(System.currentTimeMillis()));
        Connection con;

        String driver="com.mysql.cj.jdbc.Driver";
        String url="jdbc:mysql://localhost:3306/IndoorPositioning?useUnicode=true&characterEncoding=UTF-8";
        String user="root";
        String password="123456";
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
            if (!con.isClosed()) {
                System.out.println("Database connection succeeded!");
            }
            Statement statement = con.createStatement();
            String creat_indoorpositioning_database = "CREATE DATABASE IF NOT EXISTS indoorpositioning\n" +
                    "DEFAULT CHARACTER SET latin1 -- cp1252 West European \n" +
                    "DEFAULT COLLATE latin1_swedish_ci;";
            statement.execute(creat_indoorpositioning_database);
            System.out.println("Database created success!");
            System.out.println("have indoorpositioning database!");
            String table_sql = "show tables;";
            ResultSet table_set = statement.executeQuery(table_sql);
            String str_table;
            List<String> table_list = new ArrayList<>();
            while (table_set.next()){
                str_table = table_set.getString("Tables_in_indoorpositioning");
                //System.out.println(str_table);
                table_list.add(str_table);
            }
            //System.out.println(table_list);
            if(table_list.contains("location_log")){
                System.out.println("have location_log table!");
            }else {//创建location_log表
                String creat_location_log_str = "CREATE TABLE `location_log` (\n" +
                        "  `id` int(4) NOT NULL AUTO_INCREMENT,\n" +
                        "  `account` varchar(30) NOT NULL,\n" +
                        "  `name` varchar(20) CHARACTER SET utf8 NOT NULL,\n" +
                        "  `location_x` float(8,5) NOT NULL,\n" +
                        "  `location_y` float(8,5) NOT NULL,\n" +
                        "  `location_last_time` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ") ENGINE=InnoDB AUTO_INCREMENT=552 DEFAULT CHARSET=latin1;\n" +
                        "\n";
                statement.execute(creat_location_log_str);
                System.out.println("location_log table establish success!- ");
            }
            if(table_list.contains("user")){
                System.out.println("have user table!");
            }else {//创建user表
                String creat_user_str = "CREATE TABLE `user` (\n" +
                        "  `name` varchar(20) CHARACTER SET utf8 NOT NULL,\n" +
                        "  `password` varchar(30) NOT NULL,\n" +
                        "  `account` varchar(30) NOT NULL,\n" +
                        "  `position` int(11) NOT NULL,\n" +
                        "  `iphone` varchar(30) NOT NULL,\n" +
                        "  `last_login_time` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),\n" +
                        "  PRIMARY KEY (`account`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n" +
                        "\n";
                statement.execute(creat_user_str);
                System.out.println("user table establish success! ");
            }

            statement.close();
            con.close();
        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱动没有安装");
        } catch (SQLException e) {
            System.out.println("Database open failed!");
            e.printStackTrace();
        }
        SpringApplication.run(helloWorldApplication.class, args);
    }
}
