package com.jgeng.aidlclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jgeng.aidlserver.Data;
import com.jgeng.aidlserver.IAIDLServer;
import com.jgeng.aidlserver.ServerAction;
import com.jgeng.aidlserver.ServerMessage;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = "MainActivity";
  TextView mAIDLResult;
  TextView mMessengerResult;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mAIDLResult = (TextView) findViewById(R.id.aidl_result);
    mMessengerResult = (TextView) findViewById(R.id.messenger_result);

    Button btnTestAIDL = (Button)findViewById(R.id.btn_test_aidl);
    btnTestAIDL.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mAIDLServer == null) {
          Intent intent = new Intent(ServerAction.AIDL);
          intent.setPackage("com.jgeng.aidlserver");
          bindService(intent, mAIDLConnection, BIND_AUTO_CREATE);
        } else {
          testAIDL();
        }
      }
    });

    Button btnTestMessenger = (Button)findViewById(R.id.btn_test_messenger);
    btnTestMessenger.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mServerMessenger == null) {
          Intent intent = new Intent(ServerAction.MESSENGER);
          intent.setPackage("com.jgeng.aidlserver");
          bindService(intent, mMessengerConnection, BIND_AUTO_CREATE);
        } else {
          testMessenger();
        }
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mAIDLServer != null) {
      unbindService(mAIDLConnection);
    }

    if (mServerMessenger != null) {
      unbindService(mMessengerConnection);
    }
  }

  IAIDLServer mAIDLServer = null;
  ServiceConnection mAIDLConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      Log.e(TAG, "aidl onServiceConnected");
      mAIDLServer = IAIDLServer.Stub.asInterface(iBinder);
      testAIDL();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      mAIDLServer = null;
    }
  };

  private void testAIDL() {
    try {
      mAIDLResult.setText(mAIDLServer.getData().getName());
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  Messenger mServerMessenger;
  ServiceConnection mMessengerConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      Log.e(TAG, "messenger onServiceConnected");
      mServerMessenger = new Messenger(iBinder);
      testMessenger();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      mServerMessenger = null;
    }
  };

  final Messenger mClientMessenger = new Messenger(new IncomingHandler());
  class IncomingHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case ServerMessage.TEST_ECHO:
          Data data;
          try {
            Bundle bundle = msg.getData();
            bundle.setClassLoader(this.getClass().getClassLoader());
            data= bundle.getParcelable(ServerMessage.KEY_DATA);
          } catch (Exception e) {
            Log.e(TAG, "message format error! " + e);
            return;
          }
          mMessengerResult.setText("messenger: " + data.getName());
          break;
        default:
          super.handleMessage(msg);
      }
    }
  }

  private void testMessenger() {
    Message msg = Message.obtain(null, ServerMessage.TEST, 0, 0);
    msg.replyTo = mClientMessenger;
    try {
      mServerMessenger.send(msg);
    } catch (RemoteException e) {
      Log.e(TAG, "send reply error !" + e);
    }
  }
}
