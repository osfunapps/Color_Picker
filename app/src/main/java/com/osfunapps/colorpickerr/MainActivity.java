package com.osfunapps.colorpickerr;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.arasthel.asyncjob.AsyncJob;
import com.osfunapps.colorpickerr.httpserver.SocketConnection;


public class MainActivity extends AppCompatActivity implements SocketConnection.MainCallBack {


    private SocketConnection socketConnection;
    private TextView someTxt;
    private Handler mainLooperHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        prepareFrag().create().start();
    }

    private AsyncJob.AsyncJobBuilder prepareFrag() {
        return new AsyncJob.AsyncJobBuilder<Boolean>()
                .doInBackground(new AsyncJob.AsyncAction<Boolean>() {
                    @Override
                    public Boolean doAsync() {
                        setInstances();
                        setViews();
                        return true;
                    }
                }).doWhenFinished(new AsyncJob.AsyncResultAction() {
                    @Override
                    public void onResult(Object o) {
                        socketConnection.iniSocketConnection().create().start();
                    }
                });
    }

    private void setViews() {
        someTxt = (TextView) findViewById(R.id.some_txt);
    }

    private void setInstances() {
        socketConnection = new SocketConnection(this,this);
        mainLooperHandler = new Handler(getMainLooper());
    }



    @Override
    public void onConnected() {

    }

    @Override
    public void onConnectionTimeOut() {

    }

    @Override
    public void onColorChanged(final int newColor) {
        mainLooperHandler.post(new Runnable() {
            @Override
            public void run() {
                someTxt.setTextColor(newColor);
            }
        });
    }

    @Override
    public void onColorChanged(final short r, final short g, final short b) {
        mainLooperHandler.post(new Runnable() {
            @Override
            public void run() {
                someTxt.setTextColor(Color.rgb(r,g,b));
            }
        });
    }

    public TextView getSomeTxt() {
        return someTxt;
    }
}

