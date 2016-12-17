package com.iotgatewaybeta;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.Random;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class MainActivity extends AppCompatActivity {
    public TextView[][] textViews_T=new TextView[7][5];
    public TextView[][] textViews_H=new TextView[7][5];
    public LinearLayout[][] BG_linearLayouts=new LinearLayout[7][5];

    public double WarnHighTemp,WarnHighHumi,WarnLowTemp,WarnLowHumi;//四个警戒值

    ViewData viewData;

    ReloadView reloadView=new ReloadView();
    postData postData=new postData();
    Stater stater=new Stater();
    private String serverUrl="http://192.168.1.105:8080/api/oenv";
    private String [][] UIdata=new String[10][7];
    private mSimpleCache simpleCache=new mSimpleCache(1024);//mSimpleCache是由WeakHashMap和ConcurrentHashMap组成的一个缓存池
    private mSimpleCache jsonCache=new mSimpleCache(1024);
    private mSimpleCache viewCache=new mSimpleCache(128);
    private mSimpleCache heartbeatCache=new mSimpleCache(1024);
    private Boolean isPost=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WarnLowHumi = 5.0;
        WarnLowTemp = 5.0;
        WarnHighHumi = 20.0;
        WarnHighTemp = 20.0;

        textViews_H[0][0]=(TextView)findViewById(R.id.A_1_humi);
        textViews_H[0][1] = (TextView)findViewById(R.id.B_1_humi);
        textViews_H[0][2] = (TextView)findViewById(R.id.C_1_humi);
        textViews_H[0][3]= (TextView)findViewById(R.id.D_1_humi);
        textViews_H[0][4]= (TextView)findViewById(R.id.E_1_humi);
        textViews_H[1][0]= (TextView)findViewById(R.id.A_2_humi);
        textViews_H[1][1]= (TextView)findViewById(R.id.B_2_humi);
        textViews_H[1][2]= (TextView)findViewById(R.id.C_2_humi);
        textViews_H[1][3] = (TextView)findViewById(R.id.D_2_humi);
        textViews_H[1][4] = (TextView)findViewById(R.id.E_2_humi);
        textViews_H[2][0] = (TextView)findViewById(R.id.A_3_humi);
        textViews_H[2][1] = (TextView)findViewById(R.id.B_3_humi);
        textViews_H[2][2] = (TextView)findViewById(R.id.C_3_humi);
        textViews_H[2][3] = (TextView)findViewById(R.id.D_3_humi);
        textViews_H[2][4] = (TextView)findViewById(R.id.E_3_humi);
        textViews_H[3][0] = (TextView)findViewById(R.id.A_4_humi);
        textViews_H[3][1] = (TextView)findViewById(R.id.B_4_humi);
        textViews_H[3][2] = (TextView)findViewById(R.id.C_4_humi);
        textViews_H[3][3] = (TextView)findViewById(R.id.D_4_humi);
        textViews_H[3][4] = (TextView)findViewById(R.id.E_4_humi);
        textViews_H[4][0] = (TextView)findViewById(R.id.A_5_humi);
        textViews_H[4][1] = (TextView)findViewById(R.id.B_5_humi);
        textViews_H[4][2] = (TextView)findViewById(R.id.C_5_humi);
        textViews_H[4][3]  = (TextView)findViewById(R.id.D_5_humi);
        textViews_H[4][4] = (TextView)findViewById(R.id.E_5_humi);
        textViews_H[5][0] = (TextView)findViewById(R.id.A_6_humi);
        textViews_H[5][1] = (TextView)findViewById(R.id.B_6_humi);
        textViews_H[5][2] = (TextView)findViewById(R.id.C_6_humi);
        textViews_H[5][3] = (TextView)findViewById(R.id.D_6_humi);
        textViews_H[5][4] = (TextView)findViewById(R.id.E_6_humi);
        textViews_H[6][0]  = (TextView)findViewById(R.id.A_7_humi);
        textViews_H[6][1] = (TextView)findViewById(R.id.B_7_humi);
        textViews_H[6][2] = (TextView)findViewById(R.id.C_7_humi);
        textViews_H[6][3] = (TextView)findViewById(R.id.D_7_humi);
        textViews_H[6][4] = (TextView)findViewById(R.id.E_7_humi);

        textViews_T[0][0] = (TextView)findViewById(R.id.A_1_temp);
        textViews_T[0][1] = (TextView)findViewById(R.id.B_1_temp);
        textViews_T[0][2] = (TextView)findViewById(R.id.C_1_temp);
        textViews_T[0][3] = (TextView)findViewById(R.id.D_1_temp);
        textViews_T[0][4] = (TextView)findViewById(R.id.E_1_temp);
        textViews_T[1][0] = (TextView)findViewById(R.id.A_2_temp);
        textViews_T[1][1] = (TextView)findViewById(R.id.B_2_temp);
        textViews_T[1][2] = (TextView)findViewById(R.id.C_2_temp);
        textViews_T[1][3] = (TextView)findViewById(R.id.D_2_temp);
        textViews_T[1][4] = (TextView)findViewById(R.id.E_2_temp);
        textViews_T[2][0] = (TextView)findViewById(R.id.A_3_temp);
        textViews_T[2][1] = (TextView)findViewById(R.id.B_3_temp);
        textViews_T[2][2] = (TextView)findViewById(R.id.C_3_temp);
        textViews_T[2][3] = (TextView)findViewById(R.id.D_3_temp);
        textViews_T[2][4] = (TextView)findViewById(R.id.E_3_temp);
        textViews_T[3][0] = (TextView)findViewById(R.id.A_4_temp);
        textViews_T[3][1] = (TextView)findViewById(R.id.B_4_temp);
        textViews_T[3][2] = (TextView)findViewById(R.id.C_4_temp);
        textViews_T[3][3] = (TextView)findViewById(R.id.D_4_temp);
        textViews_T[3][4]  = (TextView)findViewById(R.id.E_4_temp);
        textViews_T[4][0] = (TextView)findViewById(R.id.A_5_temp);
        textViews_T[4][1] = (TextView)findViewById(R.id.B_5_temp);
        textViews_T[4][2] = (TextView)findViewById(R.id.C_5_temp);
        textViews_T[4][3] = (TextView)findViewById(R.id.D_5_temp);
        textViews_T[4][4] = (TextView)findViewById(R.id.E_5_temp);
        textViews_T[5][0] = (TextView)findViewById(R.id.A_6_temp);
        textViews_T[5][1] = (TextView)findViewById(R.id.B_6_temp);
        textViews_T[5][2] = (TextView)findViewById(R.id.C_6_temp);
        textViews_T[5][3] = (TextView)findViewById(R.id.D_6_temp);
        textViews_T[5][4] = (TextView)findViewById(R.id.E_6_temp);
        textViews_T[6][0] = (TextView)findViewById(R.id.A_7_temp);
        textViews_T[6][1] = (TextView)findViewById(R.id.B_7_temp);
        textViews_T[6][2] = (TextView)findViewById(R.id.C_7_temp);
        textViews_T[6][3] = (TextView)findViewById(R.id.D_7_temp);
        textViews_T[6][4] = (TextView)findViewById(R.id.E_7_temp);


        BG_linearLayouts[0][0]= (LinearLayout) findViewById(R.id.backgroundA_1);
        BG_linearLayouts[0][1]= (LinearLayout) findViewById(R.id.backgroundB_1);
        BG_linearLayouts[0][2]= (LinearLayout) findViewById(R.id.backgroundC_1);
        BG_linearLayouts[0][3]= (LinearLayout) findViewById(R.id.backgroundD_1);
        BG_linearLayouts[0][4]= (LinearLayout) findViewById(R.id.backgroundE_1);
        BG_linearLayouts[1][0]= (LinearLayout) findViewById(R.id.backgroundA_2);
        BG_linearLayouts[1][1]= (LinearLayout) findViewById(R.id.backgroundB_2);
        BG_linearLayouts[1][2]= (LinearLayout) findViewById(R.id.backgroundC_2);
        BG_linearLayouts[1][3]= (LinearLayout) findViewById(R.id.backgroundD_2);
        BG_linearLayouts[1][4]= (LinearLayout) findViewById(R.id.backgroundE_2);
        BG_linearLayouts[2][0]= (LinearLayout) findViewById(R.id.backgroundA_3);
        BG_linearLayouts[2][1]= (LinearLayout) findViewById(R.id.backgroundB_3);
        BG_linearLayouts[2][2]= (LinearLayout) findViewById(R.id.backgroundC_3);
        BG_linearLayouts[2][3]= (LinearLayout) findViewById(R.id.backgroundD_3);
        BG_linearLayouts[2][4]= (LinearLayout) findViewById(R.id.backgroundE_3);
        BG_linearLayouts[3][0]= (LinearLayout) findViewById(R.id.backgroundA_4);
        BG_linearLayouts[3][1]= (LinearLayout) findViewById(R.id.backgroundB_4);
        BG_linearLayouts[3][2]= (LinearLayout) findViewById(R.id.backgroundC_4);
        BG_linearLayouts[3][3]= (LinearLayout) findViewById(R.id.backgroundD_4);
        BG_linearLayouts[3][4]= (LinearLayout) findViewById(R.id.backgroundE_4);
        BG_linearLayouts[4][0]= (LinearLayout) findViewById(R.id.backgroundA_5);
        BG_linearLayouts[4][1]= (LinearLayout) findViewById(R.id.backgroundB_5);
        BG_linearLayouts[4][2]= (LinearLayout) findViewById(R.id.backgroundC_5);
        BG_linearLayouts[4][3]= (LinearLayout) findViewById(R.id.backgroundD_5);
        BG_linearLayouts[4][4]= (LinearLayout) findViewById(R.id.backgroundE_5);
        BG_linearLayouts[5][0]= (LinearLayout) findViewById(R.id.backgroundA_6);
        BG_linearLayouts[5][1]= (LinearLayout) findViewById(R.id.backgroundB_6);
        BG_linearLayouts[5][2]= (LinearLayout) findViewById(R.id.backgroundC_6);
        BG_linearLayouts[5][3]= (LinearLayout) findViewById(R.id.backgroundD_6);
        BG_linearLayouts[5][4]= (LinearLayout) findViewById(R.id.backgroundE_6);
        BG_linearLayouts[6][0]= (LinearLayout) findViewById(R.id.backgroundA_7);
        BG_linearLayouts[6][1]= (LinearLayout) findViewById(R.id.backgroundB_7);
        BG_linearLayouts[6][2]= (LinearLayout) findViewById(R.id.backgroundC_7);
        BG_linearLayouts[6][3]= (LinearLayout) findViewById(R.id.backgroundD_7);
        BG_linearLayouts[6][4]= (LinearLayout) findViewById(R.id.backgroundE_7);
        initUiData();
        new Thread(stater).start();
        new Thread(postData).start();
        new Thread(reloadView).start();
    }
    public class Stater implements Runnable{ //启动Netty服务器的线程
        @Override
        public void run() {
            mNetty5Server server = new mNetty5Server();
            try {
                server.start(12345);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public class mNetty5Server { //Netty服务器
        public void start(int port) throws Exception {

            EventLoopGroup bossGroup = new NioEventLoopGroup();

            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {

                ServerBootstrap b = new ServerBootstrap();

                b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)

                        .childHandler(new ChannelInitializer<SocketChannel>() {

                            @Override

                            public void initChannel(SocketChannel ch)

                                    throws Exception {

// register handler

                                ch.pipeline().addLast(new mNetty5ServerHandler());

                            }

                        }).option(ChannelOption.SO_BACKLOG, 128)

                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture f = b.bind(port).sync();

                f.channel().closeFuture().sync();

            } finally {

                workerGroup.shutdownGracefully();

                bossGroup.shutdownGracefully();

            }

        }
    }
    public class mNetty5ServerHandler extends SimpleChannelInboundHandler<Object> {//Netty服务器的Handler类，对数据的读取操作在这里进行

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)//读取收到的msg

                throws Exception {

            System.out.println("HelloServerInHandler.channelRead");
            ByteBuf result=(ByteBuf)msg;
            Logger.d("msg:"+msg);
            byte[] resultB=new byte[result.readableBytes()];
            result.readBytes(resultB);
            String resultStr=binary(resultB,2);
            System.out.println("resultStr:"+resultStr);
            System.out.println("resultB:"+resultB);
            simpleCache.put(Math.abs(new Random().nextInt()%1048576),resultStr);
            for(Object key:simpleCache.getKeySet()){ //处理每条进来的数据
                String resolvingData= (String) simpleCache.get(key);
                String arduinoCallback=dispatchMsg(resolvingData);//arduino需求的回传数据
                System.out.println("key= " + key + " and value= " + resolvingData);
            }
            System.out.println();
            ctx.writeAndFlush(msg);
        }

        @Override

        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override

        protected void messageReceived(ChannelHandlerContext ctx, Object msg)

                throws Exception {

// TODO Auto-generated method stub

            System.out.println("SERVER:msgReceived");

        }

        private String dispatchMsg(String rData){//解析收到的MSG
            long time=new Date().getTime();
            String arduinoNum=null;
            String sensorNum=null;
            char sensorChar='A';
            String temperature="null";
            String humidity="null";

            String startStr=rData.substring(0,4);
            String pullSignalStr=rData.substring(4,28);
            String typeStr=rData.substring(4,6);
            String nodeIdStr=rData.substring(6,9);
            String sensorTypeStr=rData.substring(9,12);
            String sensorStr=rData.substring(12,16);
            String mainDataStr=rData.substring(16,28);
            String mainDataStrB=rData.substring(16,24);
            String mainDataStrD=rData.substring(24,28);
            String endStr=rData.substring(28,32);

            if(startStr.equals("1010")&&endStr.equals("1011")){
                arduinoNum=(Integer.valueOf(nodeIdStr,2)+1)+"";
                switch (typeStr){
                    case "00"://处理并转发数据
                        Logger.d("Dispatching...");
                        sensorChar= (char) (sensorChar+Integer.valueOf(sensorStr,2));
                        Logger.d("the sensorChar="+sensorChar);
                        sensorNum=sensorChar+"";
                        if(sensorTypeStr.equals("000"))
                            temperature=Integer.valueOf(mainDataStrB,2)+"."+Integer.valueOf(mainDataStrD,2);
                        else if(sensorTypeStr.equals("001"))
                            humidity=Integer.valueOf(mainDataStrB,2)+"."+Integer.valueOf(mainDataStrD,2);
                        Gson gson=new Gson();
                        Data envData=new Data(time,arduinoNum,sensorNum,temperature,humidity);
                        ViewData mviewData=new ViewData(arduinoNum,sensorNum,temperature,humidity);
                        String envJson=gson.toJson(envData);
                        String viewJson=gson.toJson(mviewData);
                        viewCache.put(Math.abs(new Random().nextInt()%1048576),viewJson);
                        Logger.d("已获取新的ViewData！");
                        Logger.d("Json为："+envJson);
                        jsonCache.put(Math.abs(new Random().nextInt()%1048576),envJson);
                        Logger.d("CHM:"+jsonCache.getCHM());
                        Logger.d("start:"+startStr+" tpye:"+typeStr+" nodeId:"+nodeIdStr+" sensorType:"+sensorTypeStr+" sensor:"+sensorStr+" mainData:"+mainDataStr+" end:"+endStr);
                        Logger.d("time:"+time+" arduinoNum:"+arduinoNum+" temperature:"+temperature+" humidity: "+humidity);
                        break;
                    case "10"://拉取数据处理阶段
                        break;
                    case "11"://处理心跳阶段
                        Logger.d("收到心跳");
                        break;
                }

            }
            return null;
        }
    }

    public class postData implements Runnable {//post数据到服务器的线程
        private void post() throws InterruptedException {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient okHttpClient = new OkHttpClient();
            String json = null;
            if(!"{}".equals(jsonCache.getCHM()+""))//判定json缓冲池中是否有数据
                for (Object key : jsonCache.getKeySet()) {
                    json = (String) jsonCache.get(key);
                    Logger.d("post中json：" + json);
                    //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
                    RequestBody requestBody = RequestBody.create(JSON, json);
                    //创建一个请求对象
                    Request request = new Request.Builder()
                            .url(serverUrl)
                            .post(requestBody)
                            .build();
                    //发送请求获取响应
                    try {
                        Response response = okHttpClient.newCall(request).execute();
                        //判断请求是否成功
                        if (response.isSuccessful()) {
                            //打印服务端返回结果
                            Logger.d(response.body().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }

        @Override
        public void run() {
            while (true)
                try {
                    post();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }
    public class ReloadView implements Runnable {//刷新界面的线程
        Gson gson = new Gson();
        @Override
        public void run() {
            while (true) {
                try {
                for (Object key : viewCache.getKeySet()) {
                    viewData = gson.fromJson((String) viewCache.get(key), ViewData.class);
                    Logger.d("开始更新界面了"+viewData.ToString());
                    //二维数组第一个数是sensorNum，且奇数是temperature
                    char[] sensorCharArray=viewData.sensorNum.toCharArray();
                    int x=sensorCharArray[0]-'A'+1;
                    int y=Integer.valueOf(viewData.arduinoNum);
                    Logger.d(viewData.temperature+"test");
                    if(!viewData.temperature.equals("null"))
                    {
                        UIdata[2*x-1][y-1]=viewData.temperature;
                        Logger.d("UItemp变化了："+UIdata[2*x-1][y-1]);
                    }
                    if(!viewData.humidity.equals("null"))
                    {
                        UIdata[2*x-2][y-1]=viewData.humidity;
                        Logger.d("UIhum变化了："+UIdata[2*x-2][y-1]);
                    }
                }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Logger.d("UI更新启动");
                            for(int i=0;i<7;i++)
                                for(int j=0;j<5;j++)
                                {
                                    if(Double.valueOf(UIdata[2*j][i]) > WarnHighHumi)
                                        BG_linearLayouts[i][j].setBackgroundColor(Color.CYAN);
                                    else if(Double.valueOf(UIdata[2*j][i]) < WarnLowHumi)
                                        BG_linearLayouts[i][j].setBackgroundColor(Color.GRAY);
                                    if(Double.valueOf(UIdata[2*j+1][i]) > WarnHighTemp)
                                        BG_linearLayouts[i][j].setBackgroundColor(Color.RED);
                                    else if(Double.valueOf(UIdata[2*j+1][i]) < WarnLowTemp)
                                        BG_linearLayouts[i][j].setBackgroundColor(Color.BLUE);
                                    textViews_T[i][j].setText(" "+UIdata[2*j+1][i]+"℃");
                                    textViews_H[i][j].setText(" "+UIdata[2*j][i]+"%");
                                }
                        }
                    });
                    Thread.sleep(5*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            viewData=null;
            }
        }

    }
    public String binary(byte[] bytes, int radix){//解析二进制数组
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }
    public void initUiData(){//初始化UIData数组
        for (int i=0;i<10;i++)
            for (int j=0;j<7;j++){
                if(i%2==0)
                    UIdata[i][j]="20";
                else
                    UIdata[i][j]="10";
            }

    }
}
