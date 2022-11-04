package net.biancheng.www.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import net.biancheng.www.FingerprintDatabase;
import net.biancheng.www.bean.FingerprintStructure;

import java.util.*;

public class test {
    public static void main(String[] args) {
        FingerprintDatabase fingerprint = new FingerprintDatabase("C:\\Users\\asus\\Desktop\\data\\Train0228.txt");
        Set<String> contain = fingerprint.allContain();

        System.out.println(contain.size());
        Iterator it = contain.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            System.out.println(str);
        }
        net.sf.json.JSONObject jsonObjectin = net.sf.json.JSONObject.fromObject("{\"currentUser\":\"zzz\",\"wifi_rssi\":{\"a4:1a:3a:96:36:42\":-37,\"88:df:9e:1d:87:92\":-37,\"88:df:9e:1d:87:91\":-37,\"88:df:9e:1d:87:90\":-37,\"88:df:9e:1d:87:80\":-37,\"88:df:9e:1d:87:82\":-37,\"88:df:9e:1d:87:81\":-37,\"a4:1a:3a:96:4e:5d\":-38,\"a4:1a:3a:95:9c:82\":-43,\"a4:1a:3a:95:9c:84\":-47,\"a4:1a:3a:96:36:44\":-52,\"a4:1a:3a:96:4e:5f\":-55,\"88:df:9e:1d:54:92\":-63,\"88:df:9e:1d:42:d0\":-63,\"88:df:9e:1d:42:d1\":-64,\"88:df:9e:1d:54:91\":-64,\"88:df:9e:1c:61:b0\":-66,\"88:df:9e:1d:42:d2\":-66,\"88:df:9e:1d:54:90\":-67,\"88:df:9e:1c:61:b2\":-72,\"88:df:9e:1c:61:b1\":-72,\"00:24:01:1d:89:f2\":-74,\"34:6b:5b:99:8d:12\":-75,\"78:44:fd:36:ec:dd\":-77,\"88:df:9e:1c:22:f1\":-78,\"88:df:9e:1c:22:f2\":-79,\"88:df:9e:1d:82:11\":-79,\"88:df:9e:1c:22:f0\":-79,\"88:df:9e:1d:82:12\":-81,\"88:df:9e:1d:82:10\":-82}}\n");
        net.sf.json.JSONObject jsonObjectWifi = jsonObjectin.getJSONObject("wifi_rssi");
        Map<String, Integer> params = JSONObject.parseObject(jsonObjectWifi.toString(), new TypeReference<Map<String, Integer>>(){});
        Set<Map.Entry<String ,Integer>> entrySet = params.entrySet();
        Iterator<Map.Entry<String , Integer>> its=entrySet.iterator();
        while(it.hasNext()) {
            Map.Entry<String ,Integer> entry=its.next();
            if(!contain.contains(entry.getKey())){
                it.remove();
            }
        }
        System.out.println(entrySet);
        List<FingerprintStructure> k_nn = fingerprint.get4NN(entrySet);
        float forecast_x=0.0f;
        float forecast_y=0.0f;
        float molecule =0.0f;
        for(FingerprintStructure ff:k_nn){
            molecule =molecule+1/ff.getDistance();
            forecast_x=forecast_x+(1/ff.getDistance())*ff.getX();
            forecast_y=forecast_y+(1/ff.getDistance())* ff.getY();
        }
        forecast_x=forecast_x/molecule;
        forecast_y=forecast_y/molecule;
        System.out.println("X:"+forecast_x+",Y:"+forecast_y);
    }
}
