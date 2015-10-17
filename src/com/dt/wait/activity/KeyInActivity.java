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

@SuppressWarnings("unused")
public class KeyInActivity extends Activity {
	private static final String TAG = "KeyInActivity";
	 static boolean active = false;
	 public static TextView keyInTime ;
	 public static TextView keyInHint ;
	 public static TextView consumeType ;
	 public static TextView customerNo;
	 
	 Intent intent;
	 Bundle bundle;
	 MyBroadcast broadcastReceiver=null;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 无title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_keyin);
		 intent=getIntent();
		 bundle=intent.getExtras();
		 keyInTime = (TextView)findViewById(R.id.keyin_time);
		 keyInHint = (TextView)findViewById(R.id.keyinhint);
		 consumeType = (TextView)findViewById(R.id.consume_type);
	     customerNo = (TextView)findViewById(R.id.card_no);
	     
	 	keyInTime.setText(bundle.getString("dtIn"));
		keyInHint.setText(bundle.getString("keyHint"));
		customerNo.setText(bundle.getString("customerNo"));
		consumeType.setText(bundle.getString("consumeName"));
		broadcastReceiver=new MyBroadcast();
		  IntentFilter filter = new IntentFilter("wait.keyin");  
		  //注册广播接收器  
		  registerReceiver(broadcastReceiver, filter);  
	   //  onDataReceived();
		  /*
		new Thread(){
			public void run(){
				
				while(true)
				{
					try {
						sleep(8000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					finish();
				}
			}
		}.start();*/
	}
	
	public class MyBroadcast extends BroadcastReceiver {

		  @Override
		  public void onReceive(Context context, Intent intent) {
		   // TODO 自动生成的方法存根
			
			    keyInTime.setText(intent.getExtras().getCharSequence("dtIn"));
	            keyInHint.setText(intent.getExtras().getCharSequence("keyHint"));
	            customerNo.setText(intent.getExtras().getCharSequence("customerNo"));
	            consumeType.setText(intent.getExtras().getCharSequence("consumeName"));
	            
	      
	           
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
		//关闭线程池

	   //     pool.shutdown();

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
		//关闭线程池

     //  pool.shutdown();

	//	active = false;
		Log.i(TAG,"===>pause out");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		 Log.i(TAG,"===>Destroy in");
		super.onDestroy();
		 unregisterReceiver(broadcastReceiver); 
		   //关闭线程池

	    //    pool.shutdown();

		active = false;
		Log.i(TAG,"===>Destroy out");
	}
	 

}
