package net.biancheng.www.mythread;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

public class SqlWriteThread extends Thread{
    private String name;
    private String account;
    private float location_x;
    private float location_y;
    private JdbcTemplate jdbc;

    private int errcode=0;

    public SqlWriteThread(String name, String account, float location_x, float location_y, JdbcTemplate jdbc) {
        this.name = name;
        this.account = account;
        this.location_x = location_x;
        this.location_y = location_y;
        this.jdbc = jdbc;
    }

    public int getErrcode() {
        return errcode;
    }

    @Override
    public void run() {
        String sql ="INSERT INTO location_log ( name,account,location_x,location_y ) VALUES ( '"+name+"', '"+account+"',"+location_x+","+location_y+");";
        System.out.println(sql);
        try {
            errcode = jdbc.update(sql);
        }catch (DuplicateKeyException e){
            System.out.print("Duplicate primary key!!"+e.getMessage());//主键重复
            errcode = -1;
        }catch (Exception ss){
            ss.printStackTrace();
        }
    }
}
