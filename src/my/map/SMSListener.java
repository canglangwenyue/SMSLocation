package my.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSListener extends BroadcastReceiver{

	private static final String ACTION = 
				"android.provider.Telephony.SMS_RECEIVED";
	static String body,longitudeString,latitudeString;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent != null && intent.getAction()!=null && 
				ACTION.compareToIgnoreCase(intent.getAction())==0) {
			Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
			SmsMessage[] messages = new SmsMessage[pduArray.length];
			for (int i = 0; i < pduArray.length; i++) {
				messages[i] = SmsMessage.createFromPdu((byte[])pduArray[i]);
				
				body = messages[i].getMessageBody();
				longitudeString = body.substring(body.indexOf("#")+1, body.indexOf("*"));
				latitudeString  = body.substring(body.indexOf("*")+1);
				if (latitudeString!= null && longitudeString!=null) {
					Toast.makeText(context, "请进行定位", Toast.LENGTH_LONG).show();
				}
				
			}
		}
	}

}
