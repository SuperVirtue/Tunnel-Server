package net.biancheng.www.bean;

import java.util.*;

public class FingerprintStructure {

    private float distance;
    private float x;
    private float y;
    private Map<String, Float> FingerprintWifiList = new HashMap<>();

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setDistance(float distance){
        this.distance=distance;
    }

    public float getDistance(){
        return this.distance;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Map<String, Float> getFingerprintWifiList() {
        return FingerprintWifiList;
    }

    public Set<String> getWifiListMapKeys() {
        if(FingerprintWifiList.size()!=0){
            return FingerprintWifiList.keySet();
        }
        return null;
    }

    public String getMaxAPMac(){
        String maxApMac = "";
        double maxApRssi=-100.0;
        if(FingerprintWifiList.size()!=0){
            for(Map.Entry<String, Float> entry : FingerprintWifiList.entrySet()){
                String mapKey = entry.getKey();
                Float mapValue = entry.getValue();
                if(mapValue>maxApRssi){
                    maxApRssi=mapValue;
                    maxApMac=mapKey;
                }
            }
        }
        return maxApMac;
    }

    public void setFingerprintWifiList(Map<String, Float> fingerprintWifiList) {
        FingerprintWifiList = fingerprintWifiList;
    }

    public void addWifiRssi(String mac,Float level){
        FingerprintWifiList.put(mac, level);
    }

    public FingerprintStructure() {
    }

    public FingerprintStructure(float x, float y, Map<String, Float> fingerprintWifiList) {
        this.x = x;
        this.y = y;
        FingerprintWifiList = fingerprintWifiList;
    }

    @Override
    public String toString() {
        return "FingerprintStructure{" +
                "distance=" + distance +
                ", x=" + x +
                ", y=" + y +
                ", FingerprintWifiList=" + FingerprintWifiList +
                '}';
    }
}
