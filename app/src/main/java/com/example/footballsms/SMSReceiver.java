package com.example.footballsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {
    private static final String PHONE_NUMBER = "666962941";

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SMSReceiver";

    String START_SEQUENCE = "<!##!>";
    String SEPARATING_SEQUENCE = "<##!>";
    String RESULT_SEPARATOR = "-";
    String END_SEQUENCE = "<#!#>";

    String msg, phoneNumber;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Intent received: " + intent.getAction());

        if(!intent.getAction().equals(SMS_RECEIVED))
            return;

        Bundle dataBundle = intent.getExtras();

        if(dataBundle == null)
            return;

        Object[] mypdu = (Object[]) dataBundle.get("pdus");
        final SmsMessage[] message = new SmsMessage[mypdu.length];
        Log.d(TAG, "onReceive: " + mypdu.length);
        for(int i = 0; i < mypdu.length; i++){
            String format = dataBundle.getString("format");
            message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i], format);
            msg = message[i].getMessageBody();
            phoneNumber = message[i].getOriginatingAddress();
            Log.d(TAG, "onReceive: " + msg + " " + phoneNumber);
        }

        if(phoneNumber.contains(PHONE_NUMBER) && msg.contains(START_SEQUENCE) && msg.contains(END_SEQUENCE)){
            try {
                String content = msg.split(START_SEQUENCE)[1].split(END_SEQUENCE)[0];
                String[] contentArray = content.split(SEPARATING_SEQUENCE);
                int idTeam1 = Integer.parseInt(contentArray[0]);
                String result = contentArray[1];
                int idTeam2 = Integer.parseInt(contentArray[2]);
                String[] resultArray = result.split(RESULT_SEPARATOR);
                int team1Goals = Integer.parseInt(resultArray[0]);
                int team2Goals = Integer.parseInt(resultArray[1]);

                updateDatabase(context, idTeam1, idTeam2, team1Goals, team2Goals);
                sendBroadcast(context);
            }
            catch (Exception e){
                Log.d(TAG, "onReceive: exception: " + e.toString());
            }
        }

    }


    private void updateDatabase(Context context, int idTeam1, int idTeam2, int team1Goals, int team2Goals){
        SQLiteHelper sqLiteHelper = new SQLiteHelper(context);
        sqLiteHelper.createMatch(idTeam1, idTeam2, team1Goals, team2Goals);
    }


    private void sendBroadcast(Context context){
        Log.d(TAG, "sendBroadcast: sending broadcast");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.NOTIFY_ACTIVITY_ACTION);

        context.sendBroadcast(broadcastIntent);
        Log.d(TAG, "sendBroadcast: sent broadcast");
    }
}
