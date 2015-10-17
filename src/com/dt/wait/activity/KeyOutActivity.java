package com.dt.wait.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class KeyOutActivity  extends Activity {
	static boolean active = false;
	 private static final String TAG = "KeyOUtActivity";
	 
	 TextView keyOutTime ;
	 TextView keyInTime ;
	 TextView keyOutHint;
	 TextView customerNo;
	 TextView keyNo ;
	 TextView timeDiff ;
	 TextView csf; 
	 public static Thread t;
	 MyBroadcast broadcastReceiver=null;
	 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 无title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_keyout);
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		 keyOutTime = (TextView)findViewById(R.id.keyout_time);
		 keyOutHint = (TextView)findViewById(R.id.outhint);
		 customerNo = (TextView)findViewById(R.id.consume_type);
		 keyNo = (TextView)findViewById(R.id.card_no);
		 timeDiff = (TextView)findViewById(R.id.time_diff);
		
		keyOutTime.setText(bundle.getString("dtOut"));
		//keyInTime.setText(bundle.getString("dtIn"));
		keyOutHint.setText(bundle.getString("keyHint"));
		customerNo.setText(bundle.getString("customerNo"));
		keyNo.setText(bundle.getString("keyNo"));
		timeDiff.setText(bundle.getString("timeDiff")+"分钟");
	//	csf.setText(bundle.getString("csf")+"元");
		broadcastReceiver=new MyBroadcast();
		  IntentFilter filter = new IntentFilter("wait.keyout");  
		  //注册广播接收器  
		  registerReceiver(broadcastReceiver, filter);  
//		new Thread(){
//			public void run(){
//				
//				while(true)
//				{
//					try {
//						sleep(8000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					finish();
//				}
//			}
//		}.start();
	}
	public class MyBroadcast extends BroadcastReceiver {

		  @Override
		  public void onReceive(Context context, Intent intent) {
		   // TODO 自动生成的方法存根
			   // keyInTime.setText(intent.getExtras().getCharSequence("dtIn"));
			    keyOutTime.setText(intent.getExtras().getCharSequence("dtOut"));
	            customerNo.setText(intent.getExtras().getCharSequence("customerNo"));
	            keyNo.setText(intent.getExtras().getCharSequence("keyNo"));
	            keyOutHint.setText(intent.getExtras().getCharSequence("keyHint"));
	            timeDiff.setText(intent.getExtras().getCharSequence("timeDiff")+"分钟");
	         //   csf.setText(intent.getExtras().getCharSequence("csf")+"元");
	            
	        //    if(t.isInterrupted()) 
	         //   	t.start();
	            
		  
		  }

	}
	
	 public void onStart() {
		 Log.i(TAG,"===>start in");
		 super.onStart();
		active = true;
		Log.i(TAG,"===>start out");
		} 

	 public void onStop() {
		 Log.i(TAG,"===>stop in");
		 super.onStop();
	//	active = false;
		Log.i(TAG,"===>stop out");
		}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.i(TAG,"===>restart in");
		super.onRestart();
		active = true;
		Log.i(TAG,"===>restart out");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.i(TAG,"===>resume in");
		super.onResume();
		active = true;
		Log.i(TAG,"===>resume out");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.i(TAG,"===>pause in");
		super.onPause();
	//	active = false;
		Log.i(TAG,"===>pause out");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		 Log.i(TAG,"===>Destroy in");
		super.onDestroy();
		 unregisterReceiver(broadcastReceiver); //注销广播

		active = false;
		Log.i(TAG,"===>Destroy out");
	}
	 
}
