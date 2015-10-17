package com.dt.wait.activity;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dt.wait.broadcast.KeyInBroadcastReceiver;
import com.dt.wait.dao.DBOpenHelper;
import com.dt.wait.dao.WaitSetDao;
import com.dt.wait.http.HttpUtils;
import com.dt.wait.model.TbWaitSet;
import com.dwin.navy.serialportapi.SerailPortOpt;

public class LogoActivity extends Activity {
	private static final String TAG = "LogoActivity";
	private SerailPortOpt dtSerialPort;
	private SerailPortOpt kzSerialPort;
	private static String COMMANDNO;  //��þ�̬�ġ�

	protected OutputStream dtOutputStream; //��ͷ
	private InputStream dtInputStream;
	protected OutputStream kzOutputStream; //��բ���������
	private InputStream kzInputStream;
	private InReadThread inReadThread;
	private OutReadThread outReadThread;
	private SendThread mSendThread;
	private TimerSendThread mTimerSendThread;

	private boolean m_bShowDateType = false;
	private boolean m_bSendDateType = false;
	
	private static boolean ifChangeBackground=false;

	private static final String[] Sserialport = { "COM0", "COM1", "COM2",
			"COM3", "COM4" };
	private static final int[] m_iSerialPort = { 0, 1, 2, 3, 4 };

	private static final String[] Sbaudrate = { "115200", "57600", "38400",
			"19200", "9600", "4800", "2400", "1200", "300", };
	private static final int[] baudrate = { 115200, 57600, 38400, 19200, 9600,
			4800, 2400, 1200, 300, };

	private static final String[] Sdatabits = { "5", "6", "7", "8" };
	private static final int[] databits = { 5, 6, 7, 8 };

	private static final String[] Sstopbits = { "1", "2", };
	private static final int[] stopbits = { 1, 2, };

	private static final String[] Sparity = { "None", "Odd", "Even", "Mark",
			"Space" };
	private static final int[] parity = { 'n', 'o', 'e', 'm', 's' };
	byte[] tempbuf;//

	TextView tViewSerialPort;
	TextView tViewBaudRate;
	TextView tViewDataBits;
	TextView tViewParity;
	TextView tViewComm;
	EditText eTextShowMsg;
	EditText eTextSendMsg;
	EditText eTextKeyInsideNo;
	EditText eTextIp;

	private ToggleButton togBtnSerial;
	private ToggleButton togBtnReciveDate;
	private ToggleButton togBtnShowDateType;
	private ToggleButton togBtnSendDateType;
	private Button btnClear;
	private Button btnSend;

	private ToggleButton togBtnSendPer100ms;
	private ToggleButton togBtnSendPer500ms;
	private ToggleButton togBtnSendPer1000ms;

	private Spinner spiChooseSerialPort;
	private Spinner spiChooseBaudRate;
	private Spinner spiChooseDataBits;
	private Spinner spiChooseStopBits;
	private Spinner spiChooseParity;
	
	public static TextView keyInTime ;
	public static TextView keyInHint ;
	public static TextView keyInKeyNo ;
	public static TextView keyInCustomerNo;

	private ArrayAdapter<String> adapSerialPort;
	private ArrayAdapter<String> adapBaudRate;
	private ArrayAdapter<String> adapDataBits;
	private ArrayAdapter<String> adapStopBits;
	private ArrayAdapter<String> adapParity;

	String m_strReadThread = "readThread";
	String m_strSendThread = "sendThread";

	String strChoosedSerial = "tty0";
	int nChoosedBaud = 115200;

	Map<String, String> params = new HashMap<String, String>();
	Intent intent = null;// ����Intent����
	String m_strHex;
	String responseCode;
	String keyInsideNo;
	String    dtIn; //��ǰʱ��
	String    dtOut; //��ǰʱ��
	String keyHint="";
	String customerNo="";
	String keyNo="";
	String timeDiff;
	String csMoney;
	String leftOrRight;
	private String dtCom;
	private String kzCom;
	private String outIfChecked;
	private String csIfOut;
	private String ip; //����ip
	private String inOrOut;
	//��ʱ����
	private SoundPool sp;
	private int maxStreams = 10;
	private MediaPlayer mp;
	
	private LinearLayout linearLayout1;
	KeyInHandler keyInHandler;
	Thread keyInThread;
	
	static LogoHandler logoHandler;
	static Thread logoThread;
	
	


	
	//�����볡��Ϣ�Ĺ㲥
	static KeyInBroadcastReceiver keyInBroadcastReceiver=new KeyInBroadcastReceiver();
	
	@SuppressWarnings("unused")
	private DBOpenHelper helper;// ����DBOpenHelper����
	private SQLiteDatabase db;// ����SQLiteDatabase����
	@SuppressWarnings("unused")
	private WaitSetDao waitSetDao;
	
	private String[] strInfos;

	private LinkedList<byte[]> byteLinkedList = new LinkedList<byte[]>();
	private LinkedList<byte[]> tempbyteLinkedList = new LinkedList<byte[]>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��title
				requestWindowFeature(Window.FEATURE_NO_TITLE);
				// ȫ��
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_logo);
		dtSerialPort = new SerailPortOpt();
		kzSerialPort = new SerailPortOpt();
		keyInHandler = new KeyInHandler();
		logoHandler =  new LogoHandler();

		eTextShowMsg = (EditText) findViewById(R.id.showmsg);
		eTextSendMsg = (EditText) findViewById(R.id.sendmsg);
		eTextKeyInsideNo = (EditText) findViewById(R.id.keyInsiderNo);
		eTextIp = (EditText) findViewById(R.id.ip);
		linearLayout1=(LinearLayout) findViewById(R.id.linearlayout1);
		
		WaitSetDao WaitSetDao = new WaitSetDao(
				LogoActivity.this);
        List<TbWaitSet> lsTbWaitSet=WaitSetDao.getScrollData(0, (int)WaitSetDao.getCount());
        strInfos = new String[lsTbWaitSet.size()];
        int i=0;//��ʵ��ֻ��һ����¼
        for(TbWaitSet tbWaitSet:lsTbWaitSet)
        {
        	strInfos[i]=tbWaitSet.getDtComNo()+"|"+tbWaitSet.getKzComNo()+"|"
        	+tbWaitSet.getOutIfChecked()+"|"+tbWaitSet.getCsIfOut()+"|"+tbWaitSet.getInOrOut()
        	+"|"+tbWaitSet.getIp();
        	dtCom=tbWaitSet.getDtComNo();
        	kzCom=tbWaitSet.getKzComNo();
        	outIfChecked=tbWaitSet.getOutIfChecked();
        	csIfOut=tbWaitSet.getCsIfOut();
        	inOrOut=tbWaitSet.getInOrOut();
        	ip=tbWaitSet.getIp();
        	leftOrRight=tbWaitSet.getLeftOrRight();
        	i++;
        }
        //���÷�����ip
        HttpUtils.setPath(ip);
        
        if(leftOrRight.equals("left"))
        	COMMANDNO="EB 01 40 00 AA";//����һ������EB014100AB,�����ҷ���բ
        else
        	COMMANDNO="EB 01 41 00 AB";
		togBtnReciveDate =  (ToggleButton) findViewById(R.id.togBtnReciveDate);
		
		togBtnShowDateType = (ToggleButton) findViewById(R.id.togBtnShowDateType);
		togBtnSendDateType = (ToggleButton) findViewById(R.id.togBtnSendDateType);
		togBtnSerial = (ToggleButton) findViewById(R.id.togBtnSerial);
		btnClear = (Button) findViewById(R.id.clearButton);
		btnSend = (Button) findViewById(R.id.sendButton);

		togBtnSendPer100ms = (ToggleButton) findViewById(R.id.togBtnSendPer100ms);
		togBtnSendPer500ms = (ToggleButton) findViewById(R.id.togBtnSendPer500ms);
		togBtnSendPer1000ms = (ToggleButton) findViewById(R.id.togBtnSendPer1000ms);

		// *****************************************
		// set SerialPort spinner
		// *****************************************
		spiChooseSerialPort = (Spinner) findViewById(R.id.choose_seriaPort_spinner);
		adapSerialPort = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, Sserialport);
		adapSerialPort
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spiChooseSerialPort.setAdapter(adapSerialPort);

		// *****************************************
		// set BaudRate spinner
		// *****************************************
		spiChooseBaudRate = (Spinner) findViewById(R.id.choose_baudRate_spinner);
		adapBaudRate = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, Sbaudrate);
		adapBaudRate
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spiChooseBaudRate.setAdapter(adapBaudRate);

		// *****************************************
		// set DataBit spinner
		// *****************************************
		spiChooseDataBits = (Spinner) findViewById(R.id.choose_databits_spinner);
		adapDataBits = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, Sdatabits);
		adapDataBits
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spiChooseDataBits.setAdapter(adapDataBits);

		// *****************************************
		// set StopBit spinner
		// *****************************************
		spiChooseStopBits = (Spinner) findViewById(R.id.choose_stopbits_spinner);
		adapStopBits = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, Sstopbits);
		adapStopBits
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spiChooseStopBits.setAdapter(adapStopBits);

		// *****************************************
		// set Parity spinner
		// *****************************************
		spiChooseParity = (Spinner) findViewById(R.id.choose_parity_spinner);
		adapParity = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, Sparity);
		spiChooseParity.setAdapter(adapParity);

		spiChooseSerialPort.setSelection(0);
		spiChooseBaudRate.setSelection(4);
		spiChooseDataBits.setSelection(3);
		spiChooseStopBits.setSelection(0);
		spiChooseParity.setSelection(0);

		dtSerialPort.mDevNum = Integer.parseInt(dtCom.substring(3, dtCom.length()));
		dtSerialPort.mSpeed = baudrate[4];
		dtSerialPort.mDataBits = databits[3];
		dtSerialPort.mStopBits = stopbits[0];
		dtSerialPort.mParity = parity[0];
		
		kzSerialPort.mDevNum = Integer.parseInt(kzCom.substring(3, kzCom.length()));
		kzSerialPort.mSpeed = baudrate[4];
		kzSerialPort.mDataBits = databits[3];
		kzSerialPort.mStopBits = stopbits[0];
		kzSerialPort.mParity = parity[0];
		//��ʼ��mp
		mp=MediaPlayer.create(getApplicationContext(), R.raw.cshint);

		spiChooseSerialPort
				.setOnItemSelectedListener(new SpiOnItemSelectedListenerImpl());
		spiChooseBaudRate
				.setOnItemSelectedListener(new SpiOnItemSelectedListenerImpl());
		spiChooseDataBits
				.setOnItemSelectedListener(new SpiOnItemSelectedListenerImpl());
		spiChooseStopBits
				.setOnItemSelectedListener(new SpiOnItemSelectedListenerImpl());
		spiChooseParity
				.setOnItemSelectedListener(new SpiOnItemSelectedListenerImpl());

		togBtnReciveDate
				.setOnCheckedChangeListener(new TogBtnOnCheckedChangeListenerImpl());
		togBtnShowDateType
				.setOnCheckedChangeListener(new TogBtnOnCheckedChangeListenerImpl());
		togBtnSendDateType
				.setOnCheckedChangeListener(new TogBtnOnCheckedChangeListenerImpl());

		togBtnSerial
				.setOnCheckedChangeListener(new TogBtnOnCheckedChangeListenerImpl());

		btnClear.setOnClickListener(new BtnClearOnClickListener());
		btnSend.setOnClickListener(new BtnSendOnClickListener());

		togBtnSendPer100ms
				.setOnCheckedChangeListener(new TogBtnOnCheckedChangeListenerImpl());
		togBtnSendPer500ms
				.setOnCheckedChangeListener(new TogBtnOnCheckedChangeListenerImpl());
		togBtnSendPer1000ms
				.setOnCheckedChangeListener(new TogBtnOnCheckedChangeListenerImpl());
		togBtnSerial.setChecked(true);
		togBtnShowDateType.setChecked(true);
		//togBtnSendDateType.setChecked(true);
		
		togBtnReciveDate.setChecked(true);
		
		
	    
       	
		
	}
	
	class KeyInHandler extends Handler {

        public KeyInHandler() {

        }



        public KeyInHandler(Looper L) {

            super(L);

        }

      

        // ���������д�˷���,��������

        @Override

        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub

            Log.d("MyHandler", "handleMessage......");

            super.handleMessage(msg);

            // �˴����Ը���UI

            Bundle b = msg.getData();

      //      String color = b.getString("color");

   //         MyHandlerActivity.this.button.append(color);
          //  logoThread.stop(); //���ᳫ��Ҫ����ȫ����
           // if(!ifChangeBackground)
          //  {
            LogoActivity.this.setContentView(R.layout.activity_keyin);
            //logoThread.interrupt();;  //���Ƽ������׳����쳣��
          //  LogoActivity.this.linearLayout1.setBackgroundResource(R.drawable.backgroundv);
//           	keyInTime = (TextView)findViewById(R.id.keyin_time);
//           	keyInHint = (TextView)findViewById(R.id.keyinhint);
//           	keyInKeyNo = (TextView)findViewById(R.id.keyno);
//           	keyInCustomerNo = (TextView)findViewById(R.id.customerno);
//           	     
//           	LogoActivity.this.keyInTime.setText(b.getString("dt"));
//           	LogoActivity.this.keyInHint.setText(b.getString("keyHint"));
//           	LogoActivity.this.keyInCustomerNo.setText(b.getString("customerNo"));
//           	LogoActivity.this.keyInKeyNo.setText(b.getString("keyNo"));
           //	logoThread.interrupt();
          // 	LogoThread  n = new LogoThread();//ʵ��һ�����󹩽����������л����ּ�����ͼƬʹ��
            
			  logoThread=new Thread(test);
		       logoThread.start();
           	  //if(test!=null)
         //  	logoHandler.removeCallbacks(logoThread);
         //  	logoHandler =  new LogoHandler();
			//   logoHandler.post(test);
			//  logoHandler.post(logoThread);
          
          //  LogoActivity.this.linearLayout1.setBackgroundResource(R.drawable.backgroundv);
         //   ifChangeBackground=true;
         //   try {
		//		this.wait(8000);
		//	} catch (InterruptedException e) {
				// TODO Auto-generated catch block
		//		e.printStackTrace();
		//	}
            
          //  LogoActivity.this.setContentView(R.layout.activity_logo);


        }

    }



    class KeyInThread implements Runnable {

        public void run() {
            
            Message msg = new Message();

            Bundle bundle=new Bundle();
	        bundle.putCharSequence("keyHint", keyHint);
	        bundle.putCharSequence("dtIn", dtIn);
	        bundle.putCharSequence("keyNo", keyNo);
	        bundle.putCharSequence("customerNo", customerNo);        
	       // intent.putExtras(bundle);
	        msg.setData(bundle);

      //  LogoActivity.this.myHandler.sendEmptyMessage(0);

            LogoActivity.this.keyInHandler.sendMessage(msg); // ��Handler������Ϣ,����UI
       //     try {

            //    Thread.sleep(8000);

        //    } catch (InterruptedException e) {

                // TODO Auto-generated catch block

         //       e.printStackTrace();

          //  }

       //    }
        }

    }
    
	class LogoHandler extends Handler {

        public LogoHandler() {

        }



        public LogoHandler(Looper L) {

            super(L);

        }



        // ���������д�˷���,��������

        @Override

        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub

            Log.d("MyHandler", "handleMessage......");

            super.handleMessage(msg);
          //  Thread.interrupted();
               // if(ifChangeBackground)
              //  if(ifChangeBackground)
              //  {
    			LogoActivity.this.setContentView(R.layout.activity_logo);
    			 logoHandler.post(test);
    		//	ifChangeBackground=false;
            //    }

        }

    }



	static Runnable test = new Runnable(){
    	
    	

        public void run() {

        	// while (!Thread.currentThread().isInterrupted())
        	// {
        	//  try {
			//		Thread.sleep(8000);
					
			//	} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//Thread.currentThread().stop();
			//		e.printStackTrace();
			//	}
        	
        	//  LogoActivity.this.logoHandler.sendEmptyMessage(0);
       
        	// }
        	// LogoActivity.this.logoHandler.sendEmptyMessage(0);
        /*	Timer timer = new Timer();  
        	timer.schedule(new TimerTask() {  
        	                      
        	    @Override  
        	    public void run() {  
        	    	LogoActivity.this.logoHandler.sendEmptyMessage(0); 
        	    }  
        	}, 0, 8000);  */

             
       logoHandler.postDelayed(test, 8000);  //����ӵ�и���UI�߳�
      

       
        }

    };

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.i(TAG, "==>onPause in");
		super.onPause();
		Log.i(TAG, "==>onPause out");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.i(TAG, "==>onStop in");
		super.onStop();
		 if (mp!=null){
		        mp.stop();
		        mp.release();
		        mp=null;
		    }

		Log.i(TAG, "==>onStop out");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "==>onDestroy in");
		closeSerialPort();
		dtSerialPort = null;
		kzSerialPort = null;
		 if (mp.isPlaying()) {    
	            mp.stop();    
	          }    
           mp.release();    

		super.onDestroy();
		Log.i(TAG, "==>onDestroy out");
	}



	private void openSerialPort() {

		if (dtSerialPort.mFd == null || kzSerialPort==null) {
			dtSerialPort.openDev(dtSerialPort.mDevNum);
			kzSerialPort.openDev(kzSerialPort.mDevNum);

			Log.i("uart port operate", "Mainactivity.java==>uart open");
			dtSerialPort.setSpeed(dtSerialPort.mFd, dtSerialPort.mSpeed);
			kzSerialPort.setSpeed(kzSerialPort.mFd, kzSerialPort.mSpeed);
			Log.i("uart port operate", "Mainactivity.java==>uart set speed..."
					+ dtSerialPort.mSpeed);
			dtSerialPort.setParity(dtSerialPort.mFd, dtSerialPort.mDataBits,
					dtSerialPort.mStopBits, dtSerialPort.mParity);
			kzSerialPort.setParity(kzSerialPort.mFd, kzSerialPort.mDataBits,
					kzSerialPort.mStopBits, kzSerialPort.mParity);
			Log.i("uart port operate",
					"Mainactivity.java==>uart other params..."
							+ dtSerialPort.mDataBits + "..."
							+ dtSerialPort.mStopBits + "..." + dtSerialPort.mParity);

			dtInputStream = dtSerialPort.getInputStream();
			dtOutputStream = dtSerialPort.getOutputStream();
			kzInputStream = kzSerialPort.getInputStream();
			kzOutputStream = kzSerialPort.getOutputStream();
		}
	}

	private void closeSerialPort() {

		if (inReadThread != null) {
			inReadThread.interrupt();
			// mReadThread = null;
		}
		if (outReadThread != null) {
			outReadThread.interrupt();
			// mReadThread = null;
		}

		if (dtSerialPort.mFd != null || kzSerialPort.mFd != null) {
			Log.i("uart port operate", "Mainactivity.java==>uart stop");
			dtSerialPort.closeDev(dtSerialPort.mFd);
			kzSerialPort.closeDev(kzSerialPort.mFd);
			Log.i("uart port operate", "Mainactivity.java==>uart stoped");
		}
	}

	private class InReadThread extends Thread {
		byte[] buf = new byte[512];
		 final String inOperatetype="getkeyinstatus"; //Կ���볡�������͡�
		// final String outOperatetype="getkeyoutstatus"; //Կ�׳����������͡�
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			super.run();
			Log.i(TAG, "ReadThread==>buffer:" + buf.length);
			while (!isInterrupted()) {
				int size;
				if (dtInputStream == null)
					return;
				size = dtSerialPort.readBytes(buf);

				if (size > 0) {
					// new String(buf, 0, size).getBytes()��������ݴ���
					byte[] dest = new byte[size];
					System.arraycopy(buf, 0, dest, 0, size);
					// ʹ�ö��н������ݣ�����ϰ汾���ڽ���
					// �����������ݺ���ֵ����ݻ���
					//byteLinkedList.offer(new String(buf, 0, size).getBytes());
					byteLinkedList.offer(dest);
					tempbyteLinkedList=byteLinkedList;
					String encode = "utf-8";
					 tempbuf = tempbyteLinkedList.poll();
					int size1 = tempbuf.length;
					 
			  		try{
			  			
			  			 keyInsideNo = new String(tempbuf, 0, size1,"gb2312").trim();
						  
				           params.put("key_insideno", keyInsideNo);
                         
				           params.put("operate_type", inOperatetype);
				           SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("HH:mm:ss");       
									  		Date    curDate    =   new    Date(System.currentTimeMillis());//��ȡ��ǰʱ��       
									  	    dtIn    =    formatter.format(curDate);
						 responseCode = HttpUtils.sendMessage(params, encode);
							       
							       if (responseCode.equals(""))
							       {
							              keyHint="�����쳣��";
							              customerNo="";
							              keyNo=keyInsideNo;
							      
							       } 
							        else 
							        {				
							        	String [] str=responseCode.split("&");
							        	if(str[0].equals("keyNotExist"))
							    
							        {
							      	  keyHint="��Ч���ƣ�";
							      	 customerNo="";
						              keyNo=keyInsideNo;
							        }
							        else if(str[0].equals("keyNotActive"))
							        {
							        	 keyHint="��δ������";
							        	 customerNo="";
							              keyNo=str[1];
							        }
							        else if(str[0].equals("keyBeUsed"))
							        {
							        	 keyHint="������ʹ�ã�";
							        	 if(str[3].equals("ɢ��"))
							        	 customerNo="ɢ��";
							        	 else
							        	  customerNo=str[2];
							              keyNo=str[1];
							        }
							        else if(str[0].equals("passwordCard"))
							        {
							        //	mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	SendMsg();
							        	 keyHint="��������";
							        	 customerNo="������Ա";
							              keyNo=keyInsideNo;
						
							        }
							        else if(str[0].equals("pleaseComeIn")){
							        	//   mSendThread = new SendThread();
								  		//	mSendThread.start();   
							        	  SendMsg();
							        	 keyHint="����ͨ��";
							        	 if(str[3].equals("ɢ��"))
								        	 customerNo="ɢ��";
								        else
								        	 customerNo=str[2];
								              keyNo=str[1];
							         }
							       }
							     /* if(KeyInActivity.active)
							       {
							    	   IntentFilter intentFilter = new IntentFilter();  

						                intentFilter.addAction("com.dt.wait.keyin");  

						  

						                registerReceiver(keyInBroadcastReceiver, intentFilter);  

						                  

						              //  intent.setAction("com.dt.wait.keyin");  

						                intent.putExtra("keyHint", keyHint); 
						                intent.putExtra("dt", dt);
						                intent.putExtra("keyNo", keyNo);
						                intent.putExtra("customerNo", customerNo);

						                sendBroadcast(intent);  

//						              sendOrderedBroadcast(intent, null);  
							       }
							       else
							       {  */
							       intent = new Intent(LogoActivity.this, KeyInActivity.class);// ʹ��Consumehistory���ڳ�ʼ��Intent
							        Bundle bundle=new Bundle();
							        bundle.putCharSequence("keyHint", keyHint);
							        bundle.putCharSequence("dtIn", dtIn);
							        bundle.putCharSequence("keyNo", keyNo);
							        bundle.putCharSequence("customerNo", customerNo);        
							        intent.putExtras(bundle);
									startActivity(intent);// ��Consumehistory
							    //   }
							     //  ifChangeBackground=false; //��־λ�����ж����л���滹���л�������
							      
							      // LogoThread n = new LogoThread();//ʵ��һ�����󹩽����������л����ּ�����ͼƬʹ��
							       //logoThread=new Thread(n);
							     //  logoThread.start();
							     //  if(!logoThread.isInterrupted())
							   //    keyInThread.stop();
							       
							   //    KeyInThread m = new KeyInThread();//ʵ��һ�����󹩽����������л����ּ�����ͼƬʹ��
								//   keyInThread=new Thread(m);
							  //     keyInThread.start();
							   //    keyInThread.join();
							         //	logoHandler.removeCallbacks(logoThread);
							         //  	logoHandler =  new LogoHandler();
										  
						      //      LogoThread n = new LogoThread();//ʵ��һ�����󹩽����������л����ּ�����ͼƬʹ��
									//   logoThread=new Thread(n);
								  //     logoThread.start();
							       
							      // new Thread(){
						    		//	public void run(){
						    				
						    			//	while(true)
						    			//	{
						    			//		try {
						    			//			sleep(8000);
						    			//		} catch (InterruptedException e) {
						    						// TODO Auto-generated catch block
						    			//			e.printStackTrace();
						    		//			}
						    		
						    		//	}
						    		//}.start();
						    		
						    		 
							      //  new Thread(m).start();
				
			  		 }
			        catch (Exception e)
			        {
			            e.printStackTrace();
			        }
			       // handler.sendEmptyMessage(0);
			           
					onDataReceived();
					
				//	launchActivity();
		                
					Log.i(TAG, "ReadThread==>" + size);
				}
			 }
			
		}
	}
	
	private class OutReadThread extends Thread {
		byte[] buf = new byte[512];
		// final String inOperatetype="getkeyinstatus"; //Կ���볡�������͡�
		 final String outOperatetype="getkeyoutstatus"; //Կ�׳����������͡�
		@Override
		public void run() {
			super.run();
			Log.i(TAG, "ReadThread==>buffer:" + buf.length);
			while (!isInterrupted()) {
				int size;
				if (dtInputStream == null)
					return;
				size = dtSerialPort.readBytes(buf);

				if (size > 0) {
					// new String(buf, 0, size).getBytes()��������ݴ���
					byte[] dest = new byte[size];
					System.arraycopy(buf, 0, dest, 0, size);
					// ʹ�ö��н������ݣ�����ϰ汾���ڽ���
					// �����������ݺ���ֵ����ݻ���
					//byteLinkedList.offer(new String(buf, 0, size).getBytes());
					byteLinkedList.offer(dest);
					tempbyteLinkedList=byteLinkedList;
					String encode = "utf-8";
					 tempbuf = tempbyteLinkedList.poll();
					int size1 = tempbuf.length;
					 
			  		try{
			  			
			  			  keyInsideNo = new String(tempbuf, 0, size1,"gb2312").trim();
						  
				           params.put("key_insideno", keyInsideNo);
                        
                           params.put("operate_type", outOperatetype);
                           params.put("outIfChecked", outIfChecked);
                           params.put("csIfOut", csIfOut);
                           
                           String csf = "0";  //��ʱ�ѡ�
                         //  params.put("operate_type", inOperatetype);
				           SimpleDateFormat    formatterIn    =   new    SimpleDateFormat    ("HH:mm:ss");       
						   Date    curDateIn    =   new    Date(System.currentTimeMillis());//��ȡ��ǰʱ��       
									  dtIn    =    formatterIn.format(curDateIn);
						   SimpleDateFormat    formatterOut    =   new    SimpleDateFormat    ("HH:mm:ss");       
									   Date    curDateOut    =   new    Date(System.currentTimeMillis());//��ȡ��ǰʱ��       
												  dtOut    =    formatterOut.format(curDateOut);
						 responseCode = HttpUtils.sendMessage(params, encode);
							       
							       if (responseCode.equals(""))
							       {
							              keyHint="�����쳣��";
							              customerNo="";
							              keyNo=keyInsideNo;
							              timeDiff="0";
							      
							       } 
							        else 
							        {				
							        	String [] str=responseCode.split("&");
							        	if(str[0].equals("keyNotExist"))
							    
							        {
							      	  keyHint="��Ч���ƣ�";
							      	  customerNo="";
						              keyNo=keyInsideNo;
						              timeDiff="0";
							        }
							        else if(str[0].equals("keyNotActive"))
							        {
							        	 keyHint="��δ������";
							        	 customerNo="";
							              keyNo=str[1];
							              timeDiff="0";
							        }
							        else if(str[0].equals("keyIsChecked"))
							        {
							        	//mSendThread = new SendThread();
							  			//mSendThread.start();
							        	
							        	 SendMsg();
							        	
							        	 keyHint="����ͨ��";
							        	 if(str[3].equals("ɢ��"))
							        	 customerNo="ɢ��";
							        	 else
							        	  customerNo=str[2];
							              keyNo=str[1];
							              timeDiff=str[4];
							              dtIn=str[5];
							        }
							        else if(str[0].equals("passwordCard"))
							        {
							        //	mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	SendMsg();
							        	 keyHint="��������";
							        	 customerNo="������Ա";
							              keyNo=keyInsideNo;
							              timeDiff="0";
							        }
							        else if(str[0].equals("validKeyButNotIn")){
							        	//   mSendThread = new SendThread();
								  		//	mSendThread.start();   
							        	//SendMsg();
							        	 keyHint="����ˢ����ˢ����";
							        	 if(str[3].equals("ɢ��"))
								        	 customerNo="ɢ��";
								        else
								        	 customerNo=str[2];
								              keyNo=str[1];
								              timeDiff=str[4];
								              dtIn=str[5];
								              
							        }
							        else if(str[0].equals("csNotOut")){
							        	//   mSendThread = new SendThread();
								  		//	mSendThread.start();   
							        	//SendMsg();
							        	//final int cshint = sp.load(LogoActivity.this, R.raw.cshint, 0);
							        	// mp.prepare();
							        	// MediaPlayer mMediaPlayer = MediaPlayer.create(this, R.raw.vedio1); 
							             // mMediaPlayer.start();//�˹��췽���£�����Ҫparpared 
							        	 if (mp.isPlaying()) {    
							 	            mp.stop();    //������ڲ��ţ�Ҫ��ֹͣ�ڲ��š�
							 	          }    
							        	 mp.start();
							        	 keyHint="���ѳ�ʱ,�����������";
							        	 if(str[3].equals("ɢ��"))
								        	 customerNo="ɢ��";
								        else
								        	  customerNo = str[2];
								              keyNo      = str[1];
								              timeDiff   = str[4];
								              dtIn       = str[5];
								             if(str[6].equals("csNotChargeFormula"))
								            	csf="�����Ʒѹ�ʽ";
								             else
								            	csf=str[6];
								              
							        }
							        else if(str[0].equals("CanOut")){
							        	//   mSendThread = new SendThread();
								  		//	mSendThread.start();  
							        	
							        	SendMsg();
							        	
							        	if(str[6].equals("outAndChecked"))
							        	 keyHint="����ͨ��";
							        	if(str[6].equals("outNotChecked"))
							        	keyHint="����ͨ�����뵽ǰ̨����";
							        	
							        	 if(str[3].equals("ɢ��"))
								        	 customerNo="ɢ��";
								        else
								        	 customerNo=str[2];
								              keyNo=str[1];
								              timeDiff=str[4];
								              dtIn=str[5];
								              
							        }
							       }
							        intent = new Intent(LogoActivity.this, KeyOutActivity.class);// ʹ��Consumehistory���ڳ�ʼ��Intent
							        Bundle bundle=new Bundle();
							        bundle.putCharSequence("keyHint", keyHint);
							        bundle.putCharSequence("dtOut", dtOut);
							        bundle.putCharSequence("dtIn", dtIn);
							        bundle.putCharSequence("keyNo", keyNo);
							        bundle.putCharSequence("customerNo", customerNo); 
							        bundle.putCharSequence("timeDiff", timeDiff+"����");
							        bundle.putCharSequence("csf", csf+"Ԫ");
							        intent.putExtras(bundle);
									startActivity(intent);// ��Consumehistory
              
				
			  		 }
			        catch (Exception e)
			        {
			            e.printStackTrace();
			        }
			       // handler.sendEmptyMessage(0);
			           
					onDataReceived();
					
				//	launchActivity();
		                
					Log.i(TAG, "ReadThread==>" + size);
				}
			 }
			
		}
	}
	
	 //����Handler���� 

    private Handler handler =new Handler(){ 

         @Override 

         //������Ϣ���ͳ�����ʱ���ִ��Handler��������� 

        public void handleMessage(Message msg){ 

             super.handleMessage(msg); 

             // TODO ����UI 

         }

     };
 /*   protected void launchActivity()
    {
    	 new Thread() {

    		   public void run() {



    	
    		   };

    		  }.start();

    }*/
     
	protected void onDataReceived() {
         
       //   final String operatetype="getkeyinstatus"; //��������Ϊ�����֤��
		runOnUiThread(new Runnable() {
			public void run() {
				if (eTextShowMsg != null) {
					if (!byteLinkedList.isEmpty()) {
						byte[] buf = tempbuf;
						int size = buf.length;
						if (m_bShowDateType) {
							
							
							try {
								System.out.println("�յ�������" + new String(buf, 0, size,"gb2312"));
								
								eTextShowMsg.append(new String(buf, 0, size,"gb2312"));
								eTextKeyInsideNo.setText(new String(buf, 0, size,"gb2312"));
								

								Log.i("eTextShowMsg ASIIC", new String(buf, 0, size,"gb2312"));
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						} else {
							System.out.println("�յ�������===" + new String(buf, 0, size));
							eTextShowMsg.append(bytesToHexString(buf, size));
							eTextKeyInsideNo.setText(bytesToHexString(buf, size));
							Log.i("eTextShowMsg HEX",
									bytesToHexString(buf, size));
						}
					}
				}
			}
		});
		
		
				
	}
    
	/*private class ReadThread extends Thread {
		byte[] buf ;

		@Override
		public void run() {
			super.run();
			//Log.i(TAG, "ReadThread==>buffer:" + buf.length);
			while (!isInterrupted()) {
				int size;
				if (mInputStream == null)
					return;
				if(serialPort != null){
					if(buf == null){
						buf = new byte[1024];
					}
					size = serialPort.readBytes(buf);
					if (size > 0) {
						
						onDataReceived(buf, size);
						buf = null;
						
						Log.i(TAG, "ReadThread==>" + size);
					}
				}
				try {
					Thread.currentThread().sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			
			
		}
	}

	protected void onDataReceived(final byte[] buf, final int size) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (eTextShowMsg != null) {
					if (m_bShowDateType) {
						try {
							eTextShowMsg.append(new String(buf, 0, size,"gb2312").trim().toString());
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						eTextShowMsg.append(bytesToHexString(buf, size));
					}
					try {
						Log.i("eTextShowMsg", new String(buf, 0, size,"gb2312"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}*/

	/*public static String bytesToHexString(byte[] src,int size){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || size <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < size; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}  */
	
	public static String bytesToHexString(byte[] src, int size) {
		String ret = "";
		if (src == null || size <= 0) {
			return null;
		}
		for (int i = 0; i < size; i++) {
			String hex = Integer.toHexString(src[i] & 0xFF);
			Log.i(TAG, hex);
			if (hex.length() < 2) {
				hex = "0" + hex;
			}
			hex += " ";
			ret += hex;
		}
		return ret.toUpperCase();
	}
	
	private class SendThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i(TAG,"SendThread==>run");
			super.run();
			SendMsg();
		}
	}
	
	private class TimerSendThread extends Thread{
		
		private long m_lTimer = 100;	//default 100ms
		private boolean m_bRunFlag = true;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i(TAG,"TimerSendThread==>run");
			super.run();
			while (m_bRunFlag){
				Log.i(TAG,"TimerSendThread==>"+m_lTimer);
				SendMsg();
				if (m_lTimer <= 0){			//must over 0ms
					m_lTimer = 100;
				}
				try {
					Thread.sleep(m_lTimer);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void setSleepTimer(long timer){
			m_lTimer = timer;
		}
		
		public void stopThread(){
			m_bRunFlag = false;
		}
	}
	
	private void SendMsg(){
		
	//	String src = eTextSendMsg.getText().toString();
		//String src = "EB 01 40 00 AA";//����һ������EB014100AB,�����ҷ���բ��
		if (!m_bSendDateType) {
			//System.out.println("ASCLL" +Rule(src));
			if(Rule(COMMANDNO)){
				COMMANDNO = COMMANDNO.replace(" ", "");
				if(1 == (COMMANDNO.length()%2)){
					COMMANDNO += "0";
				}
				if(null != kzSerialPort.mFd){
					//Log.i(TAG, src);
					kzSerialPort.writeBytes(HexString2Bytes(COMMANDNO));
				}
			}else{
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(LogoActivity.this, R.string.inputwarn,Toast.LENGTH_SHORT).show();
					}
				});
				
			}
		} else {
			//System.out.println("HEX");
			if(null != kzSerialPort.mFd){
				//Log.i(TAG, src);
				kzSerialPort.writeBytes(COMMANDNO.getBytes());
			}
		}
	}
	
	public static boolean Rule(String str) {
		boolean result = false;
		
		String reg = "[a-fA-F0-9 ]*";
		if(str.matches(reg)){
			result = true;
		}else{
			result = false;
		}
		  
		return result;
	}
	
	/**
	 * ������ASCII�ַ��ϳ�һ���ֽڣ� �磺"EF"--> 0xEF
	 * 
	 * @param src0
	 *            byte
	 * @param src1
	 *            byte
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * ��ָ���ַ���src����ÿ�����ַ��ָ�ת��Ϊ16������ʽ �磺"2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	 * 0xD9}
	 * 
	 * @param src
	 *            String
	 * @return byte[]
	 */
	public static byte[] HexString2Bytes(String src) {
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < tmp.length / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}
	
	private class SpiOnItemSelectedListenerImpl implements
			OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			if (parent == spiChooseSerialPort) {
				dtSerialPort.mDevNum = m_iSerialPort[position];
				// tViewSerialPort.setText(Sserialport[position]);
				Log.i("uart params set", "ChooseSerialPort_"
						+ dtSerialPort.mDevNum);
			} else if (parent == spiChooseBaudRate) {
				dtSerialPort.mSpeed = baudrate[position];
				// tViewBaudRate.setText(Sbaudrate[position]);
				Log.i("uart params set", "ChooseBaudRate_" + dtSerialPort.mSpeed);

			} else if (parent == spiChooseDataBits) {
				dtSerialPort.mDataBits = databits[position];
				Log.i("uart params set", "ChooseDataBits_"
						+ dtSerialPort.mDataBits);

			} else if (parent == spiChooseStopBits) {
				dtSerialPort.mStopBits = stopbits[position];
				Log.i("uart params set", "ChooseStopBits_"
						+ dtSerialPort.mStopBits);

			} else if (parent == spiChooseParity) {
				dtSerialPort.mParity = parity[position];
				Log.i("uart params set", "ChooseParity_" + dtSerialPort.mParity);

			}

			if (dtSerialPort.mFd != null) {
				dtSerialPort.setSpeed(dtSerialPort.mFd, dtSerialPort.mSpeed);
				Log.i("uart port operate", "====>here...uart set speed..."
						+ dtSerialPort.mSpeed);
				dtSerialPort.setParity(dtSerialPort.mFd, dtSerialPort.mDataBits,
						dtSerialPort.mStopBits, dtSerialPort.mParity);
				Log.i("uart port operate", "====>here...uart other params..."
						+ dtSerialPort.mDataBits + "..." + dtSerialPort.mStopBits
						+ "..." + dtSerialPort.mParity);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	}
	
	private class BtnClearOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			eTextShowMsg.setText("");
			eTextKeyInsideNo.setText("");
		}

	}

	private class BtnSendOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mSendThread = new SendThread();
			mSendThread.start();
		}
	}

	private class TogBtnOnCheckedChangeListenerImpl implements
			OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			Log.i(TAG, "TogBtnOnCheckedChange in");
			// TODO Auto-generated method stub
			if ((ToggleButton) buttonView == togBtnSerial) {
				if (isChecked) {
					openSerialPort();
				} else {
					closeSerialPort();
				}

			} else if ((ToggleButton) buttonView == togBtnShowDateType) {
				if (isChecked) {
					m_bShowDateType = isChecked;
					Log.i(TAG, "TogBtnOnCheckedChange==>m_bShowDateType=true");
				} else {
					m_bShowDateType = isChecked;
					Log.i(TAG, "TogBtnOnCheckedChange==>m_bShowDateType=false");
				}

			} else if ((ToggleButton) buttonView == togBtnSendDateType) {
				if (isChecked) {
					m_bSendDateType = isChecked;
					Log.i(TAG, "TogBtnOnCheckedChange==>m_bSendDateType=true");
				} else {
					m_bSendDateType = isChecked;
					Log.i(TAG, "TogBtnOnCheckedChange==>m_bSendDateType=false");
				}

			} else if ((ToggleButton) buttonView == togBtnSendPer100ms) {
				if (isChecked) {
					mTimerSendThread = new TimerSendThread();
					mTimerSendThread.setSleepTimer(100);
					mTimerSendThread.start();
					btnSend.setEnabled(false);
					togBtnSendPer500ms.setEnabled(false);
					togBtnSendPer1000ms.setEnabled(false);
				} else {
					if(mTimerSendThread != null){
						mTimerSendThread.stopThread();
						mTimerSendThread = null;
						btnSend.setEnabled(true);
						togBtnSendPer500ms.setEnabled(true);
						togBtnSendPer1000ms.setEnabled(true);
					}
				}
			} else if ((ToggleButton) buttonView == togBtnSendPer500ms) {
				if (isChecked) {
					mTimerSendThread = new TimerSendThread();
					mTimerSendThread.setSleepTimer(500);
					mTimerSendThread.start();
					btnSend.setEnabled(false);
					togBtnSendPer100ms.setEnabled(false);
					togBtnSendPer1000ms.setEnabled(false);
				} else {
					if(mTimerSendThread != null){
						mTimerSendThread.stopThread();
						mTimerSendThread = null;
						btnSend.setEnabled(true);
						togBtnSendPer100ms.setEnabled(true);
						togBtnSendPer1000ms.setEnabled(true);
					}
				}
			} else if ((ToggleButton) buttonView == togBtnSendPer1000ms) {
				if (isChecked) {
					mTimerSendThread = new TimerSendThread();
					mTimerSendThread.setSleepTimer(1000);
					mTimerSendThread.start();
					btnSend.setEnabled(false);
					togBtnSendPer500ms.setEnabled(false);
					togBtnSendPer100ms.setEnabled(false);
				} else {
					if(mTimerSendThread != null){
						mTimerSendThread.stopThread();
						mTimerSendThread = null;
						btnSend.setEnabled(true);
						togBtnSendPer500ms.setEnabled(true);
						togBtnSendPer100ms.setEnabled(true);
					}
				}
			} else if ((ToggleButton) buttonView == togBtnReciveDate) {
				if (isChecked) {
					Log.i("uart port operate", "Mainactivity.java==>start ReadThread");
					  if(inOrOut.equals("waitIn"))
                      {
					  inReadThread = new InReadThread();
					   inReadThread.start();
                      } else
                      {
                    	  outReadThread = new OutReadThread();
   					       outReadThread.start();  
                      }
					Log.i("uart port operate", "Mainactivity.java==>ReadThread started");
					//btnSend.setEnabled(false);
					//togBtnSendPer100ms.setEnabled(false);
					//togBtnSendPer500ms.setEnabled(false);
					//togBtnSendPer1000ms.setEnabled(false);
				} else {

					Log.i(TAG, "togBtnReciveDate interrupt in");
					if (inReadThread != null) {
						Log.i(TAG, "togBtnReciveDate interrupt mReadThread != null");
						inReadThread.interrupt();
						// mReadThread = null;
						//btnSend.setEnabled(true);
						//togBtnSendPer500ms.setEnabled(true);
						//togBtnSendPer100ms.setEnabled(true);
						//togBtnSendPer1000ms.setEnabled(true);
					}
					if (outReadThread != null) {
						Log.i(TAG, "togBtnReciveDate interrupt mReadThread != null");
						outReadThread.interrupt();
						// mReadThread = null;
						//btnSend.setEnabled(true);
						//togBtnSendPer500ms.setEnabled(true);
						//togBtnSendPer100ms.setEnabled(true);
						//togBtnSendPer1000ms.setEnabled(true);
					}
					Log.i(TAG, "togBtnReciveDate interrupt out");
				}
			}
			Log.i(TAG, "TogBtnOnCheckedChange out");

		}
	}
}
