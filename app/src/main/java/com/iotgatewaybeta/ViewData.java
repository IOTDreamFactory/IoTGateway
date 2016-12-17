package com.iotgatewaybeta;

/**
 * Created by Administrator on 2016/12/6 0006.
 */

public class ViewData {//UI数据实体类
    String arduinoNum;
    String sensorNum;
    String temperature;
    String humidity;
    public ViewData(String arduinoNum,String sensorNum,String temperature,String humidity){
        this.arduinoNum=arduinoNum;
        this.sensorNum=sensorNum;
        this.temperature=temperature;
        this.humidity=humidity;
    }
    public String ToString(){
        return "arduinoNUM:"+arduinoNum+" sensorNum:"+sensorNum+" temperature:"+temperature+" humidity:"+humidity;
    }
}
