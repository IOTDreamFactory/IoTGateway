package com.iotgatewaybeta;

/**
 * Created by Administrator on 2016/12/5 0005.
 */

public class Data {//数据实体类
    long time;
    String arduinoNum;
    String sensorNum;
    String temperature;
    String humidity;
    public Data(long Time,String ArduinoNum,String sensorNum,String Temperature,String Humidity){
        this.time=Time;
        this.arduinoNum=ArduinoNum;
        this.sensorNum=sensorNum;
        this.temperature=Temperature;
        this.humidity=Humidity;
    }
    @Override
    public String toString(){
        return "Data['time':'"+time+"','arduinoNum':'"+arduinoNum+"','sensorNum':'"+sensorNum+"','temperature':'"+temperature+"','humidity':'"+humidity+"']";
    }
}
