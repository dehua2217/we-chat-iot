package com.dt.wait.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.dt.wait.activity.KeyInActivity;
import com.dt.wait.activity.LogoActivity;
import com.dt.wait.activity.R;

public class KeyInBroadcastReceiver extends BroadcastReceiver {
	static final String action_keyin="com.dt.wait.keyin";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(action_keyin))
		{
			
			// intent=getIntent();
			Bundle bundle=intent.getExtras();
			// Toast.makeText(context, intent.getStringExtra("dt"), Toast.LENGTH_SHORT).show();
		//	 keyInTime = (TextView)findViewById(R.id.keyin_time);
			// keyInHint = (TextView)findViewById(R.id.keyinhint);
			// keyNo = (TextView)findViewById(R.id.keyno);
		  //   customerNo = (TextView)findViewById(R.id.customerno);
			//KeyInActivity.keyInTime.setText();
			//KeyInActivity.keyInHint.setText();
			//KeyInActivity.customerNo.setText();
			//KeyInActivity.keyNo.setText();
			intent = new Intent(context, KeyInActivity.class);// 使用Consumehistory窗口初始化Intent
	        Bundle bundle1=new Bundle();
	        bundle1.putCharSequence("keyHint", bundle.getString("keyHint"));
	        bundle1.putCharSequence("dt", bundle.getString("dt"));
	        bundle1.putCharSequence("keyNo", bundle.getString("keyNo"));
	        bundle1.putCharSequence("customerNo", bundle.getString("customerNo")); 
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        intent.putExtras(bundle);	          
            context.startActivity(intent);  
		}

	}

}
