package wolverine.example.com.btp_scientist;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Wolverine on 22-09-2015.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    public static final String intentAction = "SEND_PUSH";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GCMMessageHandler.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
