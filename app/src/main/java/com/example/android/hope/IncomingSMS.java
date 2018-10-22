package com.example.android.hope;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class IncomingSMS extends BroadcastReceiver {
    private final String TAG ="IncomingSMS";
    private final String SMSbUNDLE = "pdus";

   public  IncomingSMS()
    {}
    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle= intent.getExtras();
        try
        {
            if(bundle!=null)
            {
                final Object[] pdusObj =(Object[]) bundle.get(SMSbUNDLE);
                if(pdusObj!=null)
                {
                    for(Object aPdusObj : pdusObj)
                    {
                        SmsMessage currSMS = SmsMessage.createFromPdu((byte[]) aPdusObj);
                        //not needed bodhe
                        String currSender = currSMS.getDisplayOriginatingAddress();
                        String message = currSMS.getDisplayMessageBody();

                        try
                        {
                            Intent int2 = new Intent("Message Receiver");
                            int2.putExtra("msgs",message);
                            context.sendBroadcast(int2);

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                           //Toast.makeText(this,"error!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
}
