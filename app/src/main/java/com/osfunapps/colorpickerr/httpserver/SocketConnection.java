package com.osfunapps.colorpickerr.httpserver;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.GestureDetector;

import com.arasthel.asyncjob.AsyncJob;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

/**
 * Created by shabat on 25/05/2017.
 */

public class SocketConnection {

    private final Context context;

    public SocketConnection(Context context, MainCallBack mainCallBack) {
        this.mainCallBack = mainCallBack;
        this.context = context;
    }

    //instances
    private Socket clientSocket;
    private DataOutputStream DOS;
    private CountDownTimer selectTimer;
    private GestureDetector scrollGestureDetector;
    private MainCallBack mainCallBack;


    public AsyncJob.AsyncJobBuilder iniSocketConnection() {
        return new AsyncJob.AsyncJobBuilder<Boolean>()
                .doInBackground(new AsyncJob.AsyncAction<Boolean>() {
                    @Override
                    public Boolean doAsync() {

                        try {
                            SocketAddress socketAddress = new InetSocketAddress(AppBank.computerIp, AppBank.computerPort);
                            clientSocket = new Socket();
                            clientSocket.connect(socketAddress, 2700);

                            //clientSocket = new Socket(AppBank.computerIp,AppBank.computerPort);
                            DOS = new DataOutputStream(clientSocket.getOutputStream());
                            if (clientSocket != null && clientSocket.getInputStream() != null) {
                                System.out.println("client connected!");
                                mainCallBack.onConnected();
                                while(clientSocket != null && clientSocket.getInputStream() != null){
                                        startListening();
                                }
                            }
                            System.out.println("socket open!");
                        } catch (IOException e) {
                            if (e instanceof SocketTimeoutException) {
                                mainCallBack.onConnectionTimeOut();
                            }
                            e.printStackTrace();
                        }

                        return true;
                    }
                });
    }

    private void startListening() {
        try {
            DataInputStream DIS = new DataInputStream(clientSocket.getInputStream());
            mainCallBack.onColorChanged(DIS.readShort(), DIS.readShort(), DIS.readShort());
            //mainCallBack.onColorChanged(DIS.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public interface MainCallBack {
        void onConnected();
        void onConnectionTimeOut();


        void onColorChanged(int newColor);

        void onColorChanged(short r, short g, short b);
    }
}
