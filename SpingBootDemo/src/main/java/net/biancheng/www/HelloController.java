package net.biancheng.www;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import net.biancheng.www.bean.FingerprintStructure;
import net.biancheng.www.bean.Person;
import net.biancheng.www.bean.user;
import net.biancheng.www.bean.userDemo;
import net.biancheng.www.mythread.SqlWriteThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

//使用指纹算法完成的定位

@Controller
public class HelloController {

    @Autowired //自动装配并不会调用到初始化函数
    private FingerprintDatabase fingerprintDatabase;

    @Autowired
    private Person person;
    @ResponseBody
    @RequestMapping("/hello")
    public Person hello() {
        System.out.print("hello!!");
        return person;
    }

    @PostMapping(value = "/hello")
    @ResponseBody
    public String hello(HttpServletRequest request) {
        System.out.println("post hello!");
        Date date=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Current Time:"+format.format(date));

        ServletInputStream is = null;
        try {
            is = request.getInputStream();
            StringBuilder sb = new StringBuilder();
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, len));
            }
            System.out.println(sb.toString());
            String str = "hello baby!";
            return str;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @PostMapping(value = "/login")
    @ResponseBody
    public int login(HttpServletRequest request) {
        //login登录接口
        //从数据库中获取数据与请求进行匹配
        System.out.println("hello login post!");
        userDemo userdemo = null;
        int resultInt=-1;
        ServletInputStream is = null;
        try {
            is = request.getInputStream();
            StringBuilder sb = new StringBuilder();
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, len));
            }
            System.out.println(sb.toString());
            userdemo=JSON.parseObject(sb.toString(),userDemo.class);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String sqlQuery = "select name,iphone,position,account from user where account = '"+userdemo.getAccount()+"' and password = '"+userdemo.getPassword()+"';";
        System.out.println(sqlQuery);
        List<Map<String, Object>> list =  jdbcTemplate.queryForList(sqlQuery);
        System.out.println("list count:"+list.size());
        if(list.size()>=1){
            resultInt = list.size();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String sqlUpdate = "UPDATE user SET last_login_time='"+timestamp+"'  WHERE account = '"+userdemo.getAccount()+"' and password = '"+userdemo.getPassword()+"'; ";
            System.out.println(sqlUpdate);
            jdbcTemplate.update(sqlUpdate);
        }
        for (Map<String, Object> map : list) {
            Set<Entry<String, Object>> entries = map.entrySet( );
            if(entries != null) {
                Iterator<Entry<String, Object>> iterator = entries.iterator( );
                while(iterator.hasNext( )) {
                    Entry<String, Object> entry =(Entry<String, Object>) iterator.next( );
                    Object key = entry.getKey( );
                    Object value = entry.getValue();
                    System.out.println(key+":"+value);
                }
            }
        }

        return resultInt;
    }

    @PostMapping(value = "/register")
    @ResponseBody
    public int register(HttpServletRequest request) {
        //注册新用户
        //往数据库里写入
        System.out.println("hello register post!");
        user User = null;
        int errcode;
        ServletInputStream is = null;
        try {
            is = request.getInputStream();
            StringBuilder sb = new StringBuilder();
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, len));
            }
            System.out.println(sb.toString());
            User= JSON.parseObject(sb.toString(), user.class);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String name = (String) User.getName();
        String password = (String) User.getPassword();
        String account = (String) User.getAccount();
        String phone = (String) User.getIphone();
        int position = (int) User.getPosition();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        User.setLast_login_time(timestamp.toString());
        String sqlInsert = "insert into user(name,iphone,account,password,position,last_login_time) values('"+name+"','"+phone+"','"+account+"','"+password+"',"+position+",'"+timestamp+"');";
        System.out.println(sqlInsert);
        try {
            errcode = jdbcTemplate.update(sqlInsert);
        }catch (DuplicateKeyException e){
            System.out.print("Duplicate primary key!!"+e.getMessage());//主键重复
            errcode = -1;
        }

        //jdbcTemplate.execute(sqlInsert);
        return errcode;
    }
    @PostMapping(value = "/location")
    @ResponseBody
    public JSONObject location(HttpServletRequest request){
        //从请求中获取json数据，然后计算，然后再打包成json原路返回
        //1. 将接收到的数据转为json
        System.out.println("hello location post!");
        net.sf.json.JSONObject jsonObject = null;
        int resultInt=-1;
        ServletInputStream is = null;
        try {
            is = request.getInputStream();
            StringBuilder sb = new StringBuilder();
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, len));
            }
            System.out.println(sb.toString());
            jsonObject = net.sf.json.JSONObject.fromObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //2. 拿出其中的currentAccount，去数据库拿这个用户的相关信息保存起来，后面会需要用到。
        String currentUser = jsonObject.getString("currentUser");
        String queryCurrentUserInfo = "select name,account,position,iphone from user where account = '"+currentUser+"'";
        System.out.println("sql query current user:"+queryCurrentUserInfo);
        List<Map<String, Object>> list =  jdbcTemplate.queryForList(queryCurrentUserInfo);
        //System.out.println(list.get(0).keySet());
        //遍历整个Map
//        for(Map<String,Object> map:list){
//            System.out.println(map.toString());
//        }
        //3. 查询到该用户职位信息，如果是管理员则返回最近一段时间的全部人员定时数据
        JSONObject resultParamJson = new JSONObject();
        if(list.get(0).get("position").toString().equals("1")){
            //查询数据库中最近一段时间的全部定位信息然后返回
            String sql_other_location ="select account,name,location_x,location_y from location_log  where location_last_time>=CURRENT_TIMESTAMP - INTERVAL 10 MINUTE  and account!='"+list.get(0).get("account").toString()+"'  group by account;";
            List<Map<String, Object>> other_location_list =  jdbcTemplate.queryForList(sql_other_location);
            if(other_location_list.size()==0){
                resultParamJson.put("other_location",new JSONObject());
            }else {
                JSONArray all_other_location = new JSONArray();
                for (Map<String, Object> map : other_location_list) {
                    Set<Entry<String, Object>> entries = map.entrySet( );
                    JSONObject one_json_location = new JSONObject();
                    if(entries != null) {
                        Iterator<Entry<String, Object>> iterator = entries.iterator( );
                        while(iterator.hasNext( )) {
                            Entry<String, Object> entry =(Entry<String, Object>) iterator.next( );
                            Object key = entry.getKey( );
                            Object value = entry.getValue();
                            one_json_location.put(key.toString(),value);
                        }
                    }
                    all_other_location.add(one_json_location);
                }
                resultParamJson.put("other_location",all_other_location);
            }
        }else {
            resultParamJson.put("other_location",new JSONObject());
        }
        //4. 定位计算
        Map<String, Integer> params = JSONObject.parseObject(jsonObject.getString("wifi_rssi"), new TypeReference<Map<String, Integer>>(){});
        fingerprintDatabase.initialization(params);
        Set<String> contain = fingerprintDatabase.allContain();
        System.out.println(contain);
        Set<Map.Entry<String ,Integer>> entrySet = params.entrySet();
        Iterator<Map.Entry<String , Integer>> it=entrySet.iterator();
        while(it.hasNext()) {
            Map.Entry<String ,Integer> entry=it.next();
            if(!contain.contains(entry.getKey())){
                it.remove();
            }
        }
        Map<String , Integer> mapFromSet = entrySet.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if(mapFromSet.size()!=9){
            for(String k:contain){
                if(!mapFromSet.keySet().contains(k)){
                    mapFromSet.put(k,-94);
                }
            }
        }
        Set<Entry<String, Integer>> entrys = mapFromSet.entrySet();
        //System.out.println(entrys);
        List<FingerprintStructure> k_nn = fingerprintDatabase.get4NN(entrys);
//        String disStr="";
//        for(FingerprintStructure fi:k_nn){
//            disStr=disStr+" "+fi.getDistance();
//        }
//        System.out.println(disStr);
        float forecast_x=0.0f;
        float forecast_y=0.0f;
        float molecule =0.0f;
        for(FingerprintStructure ff:k_nn){
            molecule = molecule+(float)1/ff.getDistance();
            forecast_x = forecast_x+((float)1/ff.getDistance())*ff.getX();
            forecast_y = forecast_y+((float)1/ff.getDistance())* ff.getY();
        }
        //System.out.println("forecast_x:"+forecast_x+",forecast_y:"+forecast_y+",molecule:"+molecule);
        forecast_x=forecast_x/(float) molecule;
        forecast_y=forecast_y/(float)molecule;
        //System.out.println("forecast_x:"+forecast_x+",forecast_y:"+forecast_y);
        //5. 返回数据
        JSONObject ownLocation = new JSONObject();
        ownLocation.put("forecast_x",forecast_x);
        ownLocation.put("forecast_y",forecast_y);
        resultParamJson.put("current",list.get(0).get("name"));
        resultParamJson.put("own_location",ownLocation);
        System.out.println("forecast_x:"+forecast_x+",forecast_y:"+forecast_y);
        SqlWriteThread sqlWriteThread = new SqlWriteThread(list.get(0).get("name").toString(),list.get(0).get("account").toString(),forecast_x,forecast_y,jdbcTemplate);
        sqlWriteThread.start();
        System.out.println(resultParamJson);
        return resultParamJson;
    }
}