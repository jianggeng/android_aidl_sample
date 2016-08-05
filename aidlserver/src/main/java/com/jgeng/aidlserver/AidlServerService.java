package com.jgeng.aidlserver; /**
 * Created by jgeng on 8/3/16.
 */
import com.jgeng.aidlserver.IAIDLServer.Stub;
import com.jgeng.aidlserver.IAIDLServer;
import com.jgeng.aidlserver.Data;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class AidlServerService extends Service {
  private static final String TAG = "AidlServerService";
  int count = 0;
  @Override
  public IBinder onBind(Intent intent) {
    switch(intent.getAction()) {
      case ServerAction.AIDL:
        return mAIDLBinder;
      case ServerAction.MESSENGER:
        return mMessenger.getBinder();
      default:
        return null;
    }
  }

  /**
   * 在AIDL文件中定义的接口实现。
   */
  private IAIDLServer.Stub mAIDLBinder = new IAIDLServer.Stub() {

    public Data getData() throws RemoteException {
      Data data = new Data(count++);
      return data;
    }
  };

  final Messenger mMessenger = new Messenger(new IncomingHandler());
  class IncomingHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      Log.e(TAG, "handleMessage " + msg.what);
      switch (msg.what) {
        case ServerMessage.TEST:
          try {
            Message reply = Message.obtain(null, ServerMessage.TEST_ECHO, 0, 0);
            Bundle bundle = new Bundle();
            bundle.putParcelable(ServerMessage.KEY_DATA, new Data(count++));
            reply.setData(bundle);
            msg.replyTo.send(reply);
          } catch (RemoteException e) {
            Log.e(TAG, "send reply error !" + e);
          }
          break;
        default:
          super.handleMessage(msg);
      }
    }
  }
}