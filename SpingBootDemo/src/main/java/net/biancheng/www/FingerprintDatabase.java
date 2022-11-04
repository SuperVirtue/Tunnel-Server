package net.biancheng.www;

//import com.sun.scenario.effect.impl.prism.PrTexture;
import net.biancheng.www.bean.FingerprintStructure;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@PropertySource(value = "classpath:person.properties")//指向对应的配置文件
@Component
@ConfigurationProperties(prefix = "fingerprint-database")
//该文件提供从文件中获取指纹点对象的方法
public class FingerprintDatabase {
    private List<FingerprintStructure> fingerprintDatabase = new ArrayList<>();
    private String fileUrl;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        initialization(fileUrl);
    }

    public  FingerprintDatabase(){
    }
    public  FingerprintDatabase(String FileName){
        this.fileUrl = FileName;
        initialization(FileName);
    }

    public void initialization(){
        initialization(fileUrl);
    }

    public void initialization(Map<String, Integer> params){
        initialization(fileUrl,params);
    }

    private void initialization(String FileName){
        try{
            fingerprintDatabase.clear();
            File file = new File(FileName);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            boolean flag=true;
            FingerprintStructure fingerprintStructure = new FingerprintStructure();
            int count = 0;
            while((line = br.readLine()) != null){
                if(line.equals("")){
                    count++;
                }
                if(!line.equals("")){
                    if(flag){
                        //System.out.println(line);
                        String[] split1 = line.split(" ");
                        fingerprintStructure.setX(Float.parseFloat(split1[0]));
                        fingerprintStructure.setY(Float.parseFloat(split1[1]));
                        flag=false;
                    }else {
                        String[] split2 = line.split("   ");
                        Float te = null;
                        if(split2[1]!=null){
                            te = Float.valueOf(split2[1]);
                        }
                        fingerprintStructure.addWifiRssi(split2[0],te);
                        //System.out.println(split2[0] +","+ te);
                    }
                }else {
                    flag=true;
                    fingerprintDatabase.add(fingerprintStructure);
                    fingerprintStructure=new FingerprintStructure();
                }
            }
            //System.out.println("x:"+fingerprintStructure.getX()+",y:"+fingerprintStructure.getY());
        }catch(FileNotFoundException e) {
            System.out.print("文件不存在！");
        }catch(IOException d) {
            System.out.print(d.getMessage());
        }

    }

    private void initialization(String FileName,Map<String, Integer> params){
        String maxAPmac = "";
        Integer maxAP = -100;
        for (Map.Entry<String, Integer> entry : params.entrySet()) {
            String mapKey = entry.getKey();
            Integer mapValue = entry.getValue();
            if(mapValue>maxAP){
                maxAP=mapValue;
                maxAPmac=mapKey;
            }
        }
        try{
            fingerprintDatabase.clear();
            File file = new File(FileName);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            boolean flag=true;
            FingerprintStructure fingerprintStructure = new FingerprintStructure();
            int count = 0;
            while((line = br.readLine()) != null){
                if(line.equals("")){
                    count++;
                }
                if(!line.equals("")){
                    if(flag){
                        //System.out.println(line);
                        String[] split1 = line.split(" ");
                        fingerprintStructure.setX(Float.parseFloat(split1[0]));
                        fingerprintStructure.setY(Float.parseFloat(split1[1]));
                        flag=false;
                    }else {
                        String[] split2 = line.split("   ");
                        Float te = null;
                        if(split2[1]!=null){
                            te = Float.valueOf(split2[1]);
                        }
                        fingerprintStructure.addWifiRssi(split2[0],te);
                        //System.out.println(split2[0] +","+ te);
                    }
                }else {
                    flag=true;
                    //判断这个位置的最强AP是不是和最强AP为同一个AP，如果是则存到指纹库当中
                    if(maxAPmac.equals(fingerprintStructure.getMaxAPMac())){
                        fingerprintDatabase.add(fingerprintStructure);
                    }
                    fingerprintStructure=new FingerprintStructure();
                }
            }
            //System.out.println("x:"+fingerprintStructure.getX()+",y:"+fingerprintStructure.getY());
        }catch(FileNotFoundException e) {
            System.out.print("文件不存在！");
        }catch(IOException d) {
            System.out.print(d.getMessage());
        }

    }

    public Set<String> allContain(Map<String, Integer> params){
        String maxAPmac = "";
        Integer maxAP = -100;
        for (Map.Entry<String, Integer> entry : params.entrySet()) {
            String mapKey = entry.getKey();
            Integer mapValue = entry.getValue();
            if(mapValue>maxAP){
                maxAP=mapValue;
                maxAPmac=mapKey;
            }
        }
        if(fingerprintDatabase.size()==0){
            return null;
        }else{
            //System.out.println(fingerprintDatabase.size());
            Set<String> resultSet = new HashSet<String>();
            resultSet.addAll(fingerprintDatabase.get(0).getWifiListMapKeys());
            //System.out.println(resultSet.size());
            for(int i = 0; i<fingerprintDatabase.size(); i++){
                Set<String> temp = fingerprintDatabase.get(i).getWifiListMapKeys();
                //System.out.println(temp.size());
                resultSet.retainAll(temp);
            }
            return resultSet;
        }
    }
    public Set<String> allContain(){
        if(fingerprintDatabase.size()==0){
            return null;
        }else{
            //System.out.println(fingerprintDatabase.size());
            Set<String> resultSet = new HashSet<String>();
            resultSet.addAll(fingerprintDatabase.get(0).getWifiListMapKeys());
            //System.out.println(resultSet.size());
            for(int i = 0; i<fingerprintDatabase.size(); i++){
                Set<String> temp = fingerprintDatabase.get(i).getWifiListMapKeys();
                //System.out.println(temp.size());
                resultSet.retainAll(temp);
            }
            return resultSet;
        }
    }

    public List<FingerprintStructure> get4NN(Set<Map.Entry<String,Integer>> mapCurrentPoint){
        List<FingerprintStructure> knnList = new ArrayList<>(4);
        Set<String> allContain = allContain();
        int count=0;
        for(int i=0;i<fingerprintDatabase.size();i++){
            count++;
            //System.out.println("count:"+count);
            Map<String ,Float> finger = fingerprintDatabase.get(i).getFingerprintWifiList();
            Set<Map.Entry<String ,Float>> calculation = finger.entrySet();
            Iterator<Map.Entry<String , Float>> it=calculation.iterator();
            while(it.hasNext()) {
                Map.Entry<String ,Float> entry=it.next();
                if(!allContain.contains(entry.getKey())){
                    it.remove();
                }
            }
            float sum = 0.0f;
            Map<String , Integer> mapFromSet = mapCurrentPoint.stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            for(Map.Entry<String,Float> entry:calculation){
                sum= (float) (sum+Math.pow(entry.getValue()-mapFromSet.get(entry.getKey()),2));
            }
            sum = (float) Math.sqrt(sum);
            fingerprintDatabase.get(i).setDistance(sum);
        }
        fingerprintDatabase.sort(new Comparator<FingerprintStructure>() {
            @Override
            public int compare(FingerprintStructure o1, FingerprintStructure o2) {
                if(o1.getDistance()< o2.getDistance()){
                    return -1;
                }else if(o1.getDistance()> o2.getDistance()){
                    return 1;
                }else
                    return 0;
            }
        });
        for(int i=0;i<4;i++){
            knnList.add(fingerprintDatabase.get(i));
        }
        return knnList;
    }
}
