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

public class LogoUseKeyAndCardActivity extends Activity {
	private static final String TAG = "LogoActivity";
	private SerailPortOpt dtSerialPort;
	private SerailPortOpt kzSerialPort;
	private SerailPortOpt smSerialPort;  //扫描平台
	private static String COMMANDNO;  //最好静态的。
	public static final String ACTION_INTENT_KEYIN = "wait.keyin";
	public static final String ACTION_INTENT_KEYOUT = "wait.keyout";


	protected OutputStream dtOutputStream; //读头
	private InputStream dtInputStream;
	protected OutputStream kzOutputStream; //开闸输入输出流
	private InputStream kzInputStream;
	protected OutputStream smOutputStream; //扫描输入输出流
	private InputStream smInputStream;
	private InReadThread inReadThread;
	private OutReadThread outReadThread;
	
	private SMInReadThread sminReadThread;
	private SMOutReadThread smoutReadThread;
	
	private SendThread mSendThread;
	private SendCloseThread mSendCloseThread;
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
	
	private static final HashMap<String, String> reply=new HashMap<String, String>(){{put("passwordCard","管理手牌");put("insideNoNotExist","卡号不存在");
	put("nonExistCustomerNo","卡号不存在");put("isBK","该卡是补卡状态");put("cardException","卡异常");put("masterCardNoIsNull","主卡为空");
	put("nonExistMasterCardNo","主卡号不存在");put("nonExistTimeCard","时间卡资料不存在");put("nonExistNumCard","次卡资料不存在");put("nonExistGetCard","充值卡资料不存在");
	put("overDayLimit","超过本日限制次数");put("hadInBill","场内已存在记录");put("nonExistChargeFormula","未设置计费公式");put("consumeShouldGreteZero","消费价格应大于零");
	put("notSufficientFunds","余额不足");put("cardOverDue","卡不在有效期内");put("nonExistConsumeType","不存在消费项目");put("nonExistTimeType","不存在该时间场次");put("leftNumNotEnough","次卡余次不足");
	put("todayHoldCard","今日已封卡");put("success","允许通过");put("fail","数据库更新异常");put("cardOutOfTime","卡已超时，余额不足");put("cardNotOutOrIn","该卡不能进出闸机");put("nonExistInBill","该卡未刷进或无效卡(手牌)");put("ticketOverDue","票不在有效期");}};

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
	Intent intent = null;// 创建Intent对象
	String m_strHex;
	
	String    dtIn; //当前时间
	String    dtOut; //当前时间
	String hint="";
	String customerNo="";
	String keyNo="";
	String timeDiff;
	String csMoney;
	String leftOrRight;
	private String dtCom;
	private String kzCom;
	private String outIfChecked;
	private String csIfOut;
	private String ip; //配置ip
	private String inOrOut;
	//超时语音
	private SoundPool sp;
	private int maxStreams = 10;
	private MediaPlayer mp;
	
	private LinearLayout linearLayout1;
	KeyInHandler keyInHandler;
	Thread keyInThread;
	Thread playerThread;
	
	static LogoHandler logoHandler;
	static Thread logoThread;
	
	


	
	//发送入场信息的广播
	static KeyInBroadcastReceiver keyInBroadcastReceiver=new KeyInBroadcastReceiver();
	
	@SuppressWarnings("unused")
	private DBOpenHelper helper;// 创建DBOpenHelper对象
	private SQLiteDatabase db;// 创建SQLiteDatabase对象
	@SuppressWarnings("unused")
	private WaitSetDao waitSetDao;
	
	private String[] strInfos;

	private LinkedList<byte[]> byteLinkedList = new LinkedList<byte[]>();
	private LinkedList<byte[]> tempbyteLinkedList = new LinkedList<byte[]>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 无title
				requestWindowFeature(Window.FEATURE_NO_TITLE);
				// 全屏
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_logo);
		dtSerialPort = new SerailPortOpt();
		kzSerialPort = new SerailPortOpt();
		smSerialPort = new SerailPortOpt();
		keyInHandler = new KeyInHandler();
		logoHandler =  new LogoHandler();

		eTextShowMsg = (EditText) findViewById(R.id.showmsg);
		eTextSendMsg = (EditText) findViewById(R.id.sendmsg);
		eTextKeyInsideNo = (EditText) findViewById(R.id.keyInsiderNo);
		eTextIp = (EditText) findViewById(R.id.ip);
		linearLayout1=(LinearLayout) findViewById(R.id.linearlayout1);
		
		WaitSetDao WaitSetDao = new WaitSetDao(
				LogoUseKeyAndCardActivity.this);
        List<TbWaitSet> lsTbWaitSet=WaitSetDao.getScrollData(0, (int)WaitSetDao.getCount());
        strInfos = new String[lsTbWaitSet.size()];
        int i=0;//事实上只有一条记录
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
        //设置服务器ip
        HttpUtils.setPath(ip);
        
        if(leftOrRight.equals("left"))
        	COMMANDNO="EB 01 40 00 AA";//另外一个命令EB014100AB,分左右方向开闸
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
		
		smSerialPort.mDevNum = Integer.parseInt("1");
		smSerialPort.mSpeed = baudrate[4];
		smSerialPort.mDataBits = databits[3];
		smSerialPort.mStopBits = stopbits[0];
		smSerialPort.mParity = parity[0];
		

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

      

        // 子类必须重写此方法,接受数据

        @Override

        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub

            Log.d("MyHandler", "handleMessage......");

            super.handleMessage(msg);

            // 此处可以更新UI

            Bundle b = msg.getData();

      //      String color = b.getString("color");

   //         MyHandlerActivity.this.button.append(color);
          //  logoThread.stop(); //不提倡。要做安全处理
           // if(!ifChangeBackground)
          //  {
            LogoUseKeyAndCardActivity.this.setContentView(R.layout.activity_keyin);
            //logoThread.interrupt();;  //不推荐，容易出现异常。
          //  LogoActivity.this.linearLayout1.setBackgroundResource(R.drawable.backgroundv);
//           	keyInTime = (TextView)findViewById(R.id.keyin_time);
//           	keyInHint = (TextView)findViewById(R.id.keyinhint);
//           	keyInKeyNo = (TextView)findViewById(R.id.keyno);
//           	keyInCustomerNo = (TextView)findViewById(R.id.customerno);
//           	     
//           	LogoUseKeyAndCardActivity.this.keyInTime.setText(b.getString("dt"));
//           	LogoUseKeyAndCardActivity.this.keyInHint.setText(b.getString("keyHint"));
//           	LogoUseKeyAndCardActivity.this.keyInCustomerNo.setText(b.getString("customerNo"));
//           	LogoUseKeyAndCardActivity.this.keyInKeyNo.setText(b.getString("keyNo"));
           //	logoThread.interrupt();
          // 	LogoThread  n = new LogoThread();//实例一个对象供进场、出场切换布局及背景图片使用         
			  logoThread=new Thread(test);
		       logoThread.start();
        }

    }



    class KeyInThread implements Runnable {

        public void run() {
            
            Message msg = new Message();

            Bundle bundle=new Bundle();
	        bundle.putCharSequence("keyHint", hint);
	        bundle.putCharSequence("dtIn", dtIn);
	        bundle.putCharSequence("keyNo", keyNo);
	        bundle.putCharSequence("customerNo", customerNo);        
	       // intent.putExtras(bundle);
	        msg.setData(bundle);

      //  LogoActivity.this.myHandler.sendEmptyMessage(0);

	        LogoUseKeyAndCardActivity.this.keyInHandler.sendMessage(msg); // 向Handler发送消息,更新UI
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



        // 子类必须重写此方法,接受数据

        @Override

        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub

            Log.d("MyHandler", "handleMessage......");

            super.handleMessage(msg);
          //  Thread.interrupted();
               // if(ifChangeBackground)
              //  if(ifChangeBackground)
              //  {
            LogoUseKeyAndCardActivity.this.setContentView(R.layout.activity_logo);
    			 logoHandler.post(test);
    		//	ifChangeBackground=false;
            //    }

        }

    }



	static Runnable test = new Runnable(){
    	
    	

        public void run() {
        	
       logoHandler.postDelayed(test, 8000);  //不能拥有更新UI线程
      

       
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
	/*	 if (mp!=null){
		        mp.stop();
		        mp.release();
		        mp=null;
		    }*/

		Log.i(TAG, "==>onStop out");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "==>onDestroy in");
		closeSerialPort();
		dtSerialPort = null;
		kzSerialPort = null;
		smSerialPort = null;
//		 if (mp.isPlaying()) {    
//	            mp.stop();    
//	          }    
//           mp.release();    

		super.onDestroy();
		Log.i(TAG, "==>onDestroy out");
	}



	private void openSerialPort() {

		if (smSerialPort.mFd == null || dtSerialPort.mFd == null || kzSerialPort==null) {
			dtSerialPort.openDev(dtSerialPort.mDevNum);
			kzSerialPort.openDev(kzSerialPort.mDevNum);
			smSerialPort.openDev(smSerialPort.mDevNum);

			Log.i("uart port operate", "Mainactivity.java==>uart open");
			dtSerialPort.setSpeed(dtSerialPort.mFd, dtSerialPort.mSpeed);
			kzSerialPort.setSpeed(kzSerialPort.mFd, kzSerialPort.mSpeed);
			smSerialPort.setSpeed(smSerialPort.mFd, smSerialPort.mSpeed);
			Log.i("uart port operate", "Mainactivity.java==>uart set speed..."
					+ dtSerialPort.mSpeed);
			dtSerialPort.setParity(dtSerialPort.mFd, dtSerialPort.mDataBits,
					dtSerialPort.mStopBits, dtSerialPort.mParity);
			kzSerialPort.setParity(kzSerialPort.mFd, kzSerialPort.mDataBits,
					kzSerialPort.mStopBits, kzSerialPort.mParity);
			smSerialPort.setParity(smSerialPort.mFd, smSerialPort.mDataBits,
					smSerialPort.mStopBits, smSerialPort.mParity);
			Log.i("uart port operate",
					"Mainactivity.java==>uart other params..."
							+ dtSerialPort.mDataBits + "..."
							+ dtSerialPort.mStopBits + "..." + dtSerialPort.mParity);

			dtInputStream = dtSerialPort.getInputStream();
			dtOutputStream = dtSerialPort.getOutputStream();
			kzInputStream = kzSerialPort.getInputStream();
			kzOutputStream = kzSerialPort.getOutputStream();
			smInputStream = smSerialPort.getInputStream();
			smOutputStream = smSerialPort.getOutputStream();
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
		if (sminReadThread != null) {
			sminReadThread.interrupt();
			// mReadThread = null;
		}
		if (smoutReadThread != null) {
			smoutReadThread.interrupt();
			// mReadThread = null;
		}

		if (smSerialPort.mFd != null || dtSerialPort.mFd != null || kzSerialPort.mFd != null) {
			Log.i("uart port operate", "Mainactivity.java==>uart stop");
			dtSerialPort.closeDev(dtSerialPort.mFd);
			kzSerialPort.closeDev(kzSerialPort.mFd);
			smSerialPort.closeDev(smSerialPort.mFd);
			Log.i("uart port operate", "Mainactivity.java==>uart stoped");
		}
	}

	private class InReadThread extends Thread {
		byte[] buf = new byte[512];
		 final String inOperatetype="gethyinstatus"; //入场操作类型。
		 String responseCode="";
		 String insideNo="";
		// final String outOperatetype="getkeyoutstatus"; //钥匙出场操作类型。
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			super.run();
			Log.i(TAG, "ReadThread==>buffer:" + buf.length);
			while (!isInterrupted()) {
				int size;
				if (smInputStream == null)
					return;
				size = smSerialPort.readBytes(buf);

				if (size > 0) {
					// new String(buf, 0, size).getBytes()会造成数据错误
					byte[] dest = new byte[size];
					System.arraycopy(buf, 0, dest, 0, size);
					// 使用队列接受数据，解决上版本串口接受
					// 连续大量数据后出现的数据混乱
					//byteLinkedList.offer(new String(buf, 0, size).getBytes());
					byteLinkedList.offer(dest);
					tempbyteLinkedList=byteLinkedList;
					String encode = "utf-8";
					 tempbuf = tempbyteLinkedList.poll();
					int size1 = tempbuf.length;
					 
			  		try{
			  			
			  			 insideNo = new String(tempbuf, 0, size1,"gb2312").trim();
						  
				           params.put("insideno", insideNo);
                         
				           params.put("operate_type", inOperatetype);
				           SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("HH:mm:ss");       
									  		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
									  	    dtIn    =    formatter.format(curDate);
						 responseCode = HttpUtils.sendMessage(params, encode);
							       
							       if (responseCode.equals(""))
							       {
							              hint="网络异常！";
							              customerNo="";
							              keyNo=insideNo;
							      
							       } 
							        else 
							        {		
							        	
						        	
							        	String [] str=responseCode.split("&");
							        	if(str[0].equals("nonExistCustomerNo"))
							    
							        {
							      	  hint=reply.get(str[0]);
							      	
							      	  customerNo="";
						              keyNo=insideNo;
						              dtIn = str[2];
							        }
							        else if(str[0].equals("keyNotActive"))
							        {
							        	 hint="尚未发出！";
							        	 customerNo="";
							              keyNo=str[1];
							              dtIn = str[2];
							        }
							        else if(str[0].equals("keyBeUsed"))
							        {
							        	 hint="手牌已使用！";
							        	 if(str[3].equals("散客"))
							        	 customerNo="散客";
							        	 else
							        	  customerNo=str[2];
							              keyNo=str[1];
							              dtIn = str[4];
							        }
							        else if(str[0].equals("passwordCard"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//继电器短路
							        	mSendThread = new SendThread();
							  			mSendThread.start();
							  			//继电器断路
							  			mSendCloseThread= new SendCloseThread();
							  			mSendCloseThread.start();
							        //	SendMsg();
							        	 hint="管理手牌";
							        	 customerNo="管理人员";
							              keyNo=insideNo;
							              dtIn = str[1];
						
							        }
							        else if(str[0].equals("pleaseComeIn")){
							        	 //  mSendThread = new SendThread();
								  		//	mSendThread.start();
							        	
							        //	SendMsg();
							        	mSendThread = new SendThread();
							  			mSendThread.start();
							  			//继电器断路
							  			mSendCloseThread= new SendCloseThread();
							  			mSendCloseThread.start();
							        	
							        	 hint="允许通过";
							        	 if(str[3].equals("散客"))
								        	 customerNo="散客";
								        else
								        	 customerNo=str[2];
								              keyNo=str[1];
								              
								              dtIn = str[4];
							         }
							        else if(str[0].equals("success"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//  SendMsg();
							        	mSendThread = new SendThread();
							  			mSendThread.start();
							  			//继电器断路
							  			mSendCloseThread= new SendCloseThread();
							  			mSendCloseThread.start();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              
							              dtIn = str[2];
						
							        }
							        else if(str[0].equals("ticketOverDue"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        //	  SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn = str[2];
						
							        }
							        else if(str[0].equals("isBK"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              
							              dtIn = str[2];
						
							        }
							        	
							        else if(str[0].equals("cardException"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("masterCardNoIsNull"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("nonExistMasterCardNo"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("nonExistTimeCard"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("nonExistNumCard"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("nonExistGetCard"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("overDayLimit"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("hadInBill"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("nonExistChargeFormula"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("consumeShouldGreteZero"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("notSufficientFunds"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("cardException"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("cardOverDue"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("nonExistConsumeType"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn = str[2];
							        }
							        	
							        else if(str[0].equals("nonExistTimeType"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("leftNumNotEnough"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("todayHoldCard"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("cardNotOutOrIn"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn = str[2];
							        }
							        else if(str[0].equals("fail"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn = str[1];
							        }
							       }
							   
//							       intent = new Intent(LogoUseKeyAndCardActivity.this, KeyInActivity.class);// 使用Consumehistory窗口初始化Intent
//							        Bundle bundle=new Bundle();
//							        bundle.putCharSequence("keyHint", hint);
//							        bundle.putCharSequence("dtIn", dtIn);
//							        bundle.putCharSequence("keyNo", keyNo);
//							        bundle.putCharSequence("customerNo", customerNo);        
//							        intent.putExtras(bundle);
//									startActivity(intent);// 打开Consumehistory
							   
							       if(KeyInActivity.active==false)
							       {
							       intent = new Intent(LogoUseKeyAndCardActivity.this, KeyInActivity.class);// 使用Consumehistory窗口初始化Intent
							        Bundle bundle=new Bundle();
							        bundle.putCharSequence("keyHint", hint);
							        bundle.putCharSequence("dtIn", dtIn);
							        bundle.putCharSequence("keyNo", keyNo);
							        bundle.putCharSequence("customerNo", customerNo);        
							        intent.putExtras(bundle);
									startActivity(intent);// 打开Consumehistory
							       } else
							       {
							    	//   KeyInActivity.t.interrupt();
							    	//   KeyInActivity.t.destroy();
							    	   
							    	   Intent intent = new Intent(ACTION_INTENT_KEYIN);
							    	  
							           intent.putExtra("keyHint", hint);
							           intent.putExtra("dtIn", dtIn);
							           intent.putExtra("keyNo", keyNo);
							           intent.putExtra("customerNo", customerNo);
							           sendBroadcast(intent);			    	
							    	   
							       }
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
	
	private class SMInReadThread extends Thread {
		byte[] buf = new byte[512];
		 final String inOperatetype="getinstatus"; //入场操作类型。
		 String responseCode="";
		 String consumeName="";
		 String insideNo="";
		// final String outOperatetype="getkeyoutstatus"; //钥匙出场操作类型。
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
					// new String(buf, 0, size).getBytes()会造成数据错误
					byte[] dest = new byte[size];
					System.arraycopy(buf, 0, dest, 0, size);
					// 使用队列接受数据，解决上版本串口接受
					// 连续大量数据后出现的数据混乱
					//byteLinkedList.offer(new String(buf, 0, size).getBytes());
					byteLinkedList.offer(dest);
					tempbyteLinkedList=byteLinkedList;
					String encode = "utf-8";
					 tempbuf = tempbyteLinkedList.poll();
					int size1 = tempbuf.length;
					 
			  		try{
			  			
			  			 insideNo = new String(tempbuf, 0, size1,"gb2312").trim();
						  
				           params.put("insideno", insideNo);
                         
				           params.put("operate_type", inOperatetype);
				           SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("HH:mm:ss");       
									  		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
									  	    dtIn    =    formatter.format(curDate);
						 responseCode = HttpUtils.sendMessage(params, encode);
							       
							       if (responseCode.equals(""))
							       {
							              hint="网络异常！";
							              customerNo=insideNo;
							              keyNo=insideNo;
							      
							       } 
							        else 
							        {		
							        	
							        	
							        	String [] str=responseCode.split("&");
							        	if(str[0].equals("nonExistCustomerNo"))
							    
							        {
							      	  hint=reply.get(str[0]);
							      	
							      	  customerNo="";
						              keyNo=insideNo;
						              dtIn=str[2];
							        }
							        else if(str[0].equals("keyNotActive"))
							        {
							        	 hint="尚未发出！";
							        	 customerNo="";
							              keyNo=str[1];
							              dtIn=str[2];
							        }
							        else if(str[0].equals("keyBeUsed"))
							        {
							        	 hint="手牌已使用！";
							        	 if(str[3].equals("散客"))
							        	 customerNo="散客";
							        	 else
							        	  customerNo=str[2];
							              keyNo=str[1];
							              
							              dtIn=str[4];
							        }
							        else if(str[0].equals("passwordCard"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        //	SendMsg();
							        	mSendThread = new SendThread();
							  			mSendThread.start();
							  			//继电器断路
							  			mSendCloseThread= new SendCloseThread();
							  			mSendCloseThread.start();
							        	 hint="管理手牌";
							        	 customerNo="管理人员";
							        	 consumeName="";
							              keyNo=insideNo;
							              dtIn=str[1];
							        }
							        else if(str[0].equals("pleaseComeIn")){
							        	 //  mSendThread = new SendThread();
								  		//	mSendThread.start();
							        	
							        //	SendMsg();
							        	
							        	mSendThread = new SendThread();
							  			mSendThread.start();
							  			//继电器断路
							  			mSendCloseThread= new SendCloseThread();
							  			mSendCloseThread.start();
							        	
							        	 hint="允许通过";
							        	 if(str[3].equals("散客"))
								        	 customerNo="散客";
								        else
								        	 customerNo=str[2];
								              keyNo=str[1];
								              dtIn=str[4];
							         }
							        else if(str[0].equals("success"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//  SendMsg();
							        	
							        	mSendThread = new SendThread();
							  			mSendThread.start();
							  			//继电器断路
							  			mSendCloseThread= new SendCloseThread();
							  			mSendCloseThread.start();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              consumeName=str[3];
							              dtIn=str[2];
							        }
							        else if(str[0].equals("ticketOverDue"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        //	  SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              consumeName=str[3];
							              dtIn=str[2];
							        }
							        else if(str[0].equals("isBK"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn=str[2];
							        }
							        	

							        else if(str[0].equals("masterCardNoIsNull"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("nonExistMasterCardNo"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("nonExistTimeCard"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("nonExistNumCard"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("nonExistGetCard"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("overDayLimit"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("hadInBill"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("nonExistChargeFormula"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("consumeShouldGreteZero"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("notSufficientFunds"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("cardException"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("cardOverDue"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("nonExistConsumeType"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn=str[2];
							        }
							        	
							        else if(str[0].equals("nonExistTimeType"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("leftNumNotEnough"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("todayHoldCard"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("cardNotOutOrIn"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn=str[2];
							        }
							        else if(str[0].equals("fail"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              dtIn=str[1];
							        }
							        else if(str[0].equals("notExist"))
							        {
							        	//mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	//SendMsg();
							        	 hint="请激活后使用";
							        	 customerNo=insideNo;
							        	 consumeName="";
							              keyNo=str[1];
							              dtIn=str[2];
							        }
							       }
							   
//							       intent = new Intent(LogoUseKeyAndCardActivity.this, KeyInActivity.class);// 使用Consumehistory窗口初始化Intent
//							        Bundle bundle=new Bundle();
//							        bundle.putCharSequence("keyHint", hint);
//							        bundle.putCharSequence("dtIn", dtIn);
//							        bundle.putCharSequence("keyNo", keyNo);
//							        bundle.putCharSequence("customerNo", customerNo);        
//							        intent.putExtras(bundle);
//									startActivity(intent);// 打开Consumehistory
							       if(KeyInActivity.active==false)
							       {
							       intent = new Intent(LogoUseKeyAndCardActivity.this, KeyInActivity.class);// 使用Consumehistory窗口初始化Intent
							        Bundle bundle=new Bundle();
							        bundle.putCharSequence("keyHint", hint);
							        bundle.putCharSequence("dtIn", dtIn);
							      //  bundle.putCharSequence("keyNo", keyNo);
							        bundle.putCharSequence("consumeName", consumeName);
							        bundle.putCharSequence("customerNo", customerNo);        
							        intent.putExtras(bundle);
									startActivity(intent);// 打开Consumehistory
							       } else
							       {
							  	    	   
							    	   Intent intent = new Intent(ACTION_INTENT_KEYIN);
							    	  
							           intent.putExtra("keyHint", hint);
							           intent.putExtra("dtIn", dtIn);
							         //  intent.putExtra("keyNo", keyNo);
							           intent.putExtra("consumeName", consumeName);
							           intent.putExtra("customerNo", customerNo);
							           sendBroadcast(intent);			    		     							    	
							    	   
							       }
				
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
		// final String inOperatetype="getkeyinstatus"; //钥匙入场操作类型。
		 final String outOperatetype="getoutstatus"; //出场操作类型。
		 String responseCode="";
		 String insideNo="";
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
					// new String(buf, 0, size).getBytes()会造成数据错误
					byte[] dest = new byte[size];
					System.arraycopy(buf, 0, dest, 0, size);
					// 使用队列接受数据，解决上版本串口接受
					// 连续大量数据后出现的数据混乱
					//byteLinkedList.offer(new String(buf, 0, size).getBytes());
					byteLinkedList.offer(dest);
					tempbyteLinkedList=byteLinkedList;
					String encode = "utf-8";
					 tempbuf = tempbyteLinkedList.poll();
					int size1 = tempbuf.length;
					 
			  		try{
			  			
			  			  insideNo = new String(tempbuf, 0, size1,"gb2312").trim();
						  
				           params.put("insideno", insideNo);
                        
                           params.put("operate_type", outOperatetype);
                           params.put("outIfChecked", outIfChecked);
                           params.put("csIfOut", csIfOut);
                           
                           String csf = "0";  //超时费。
                         //  params.put("operate_type", inOperatetype);
				           SimpleDateFormat    formatterIn    =   new    SimpleDateFormat    ("HH:mm:ss");       
						   Date    curDateIn    =   new    Date(System.currentTimeMillis());//获取当前时间       
									  dtIn    =    formatterIn.format(curDateIn);
						   SimpleDateFormat    formatterOut    =   new    SimpleDateFormat    ("HH:mm:ss");       
									   Date    curDateOut    =   new    Date(System.currentTimeMillis());//获取当前时间       
												  dtOut    =    formatterOut.format(curDateOut);
						 responseCode = HttpUtils.sendMessage(params, encode);
							       
							       if (responseCode.equals(""))
							       {
							              hint="网络异常！";
							              customerNo="";
							              keyNo=insideNo;
							              timeDiff="0";
							      
							       } 
							        else 
							        {				
							        	String [] str=responseCode.split("&");
							        	if(str[0].equals("nonExistCustomerNo"))
							    
							        {
							      	  hint=reply.get(str[0]);
							      	 customerNo="";
						              keyNo=insideNo;
						              timeDiff="0";
							        }
							        else if(str[0].equals("keyNotActive"))
							        {
							        	 hint="尚未发出！";
							        	 customerNo="";
							              keyNo=str[1];
							              timeDiff="0";
							        }
							        else if(str[0].equals("keyIsChecked"))
							        {
							      //  	mSendThread = new SendThread();
							  	//		mSendThread.start();
							        	
							        	SendMsg();
							        	
							        	 hint="允许通过";
							        	 if(str[3].equals("散客"))
							        	 customerNo="散客";
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
							        	
							        	 hint="管理手牌";
							        	 customerNo="管理人员";
							              keyNo=insideNo;
							              timeDiff="0";
							        }
							        else if(str[0].equals("validKeyButNotIn")){
							        	//   mSendThread = new SendThread();
								  		//	mSendThread.start();   
							        	//SendMsg();
							        	 hint="请先刷进再刷出！";
							        	 if(str[3].equals("散客"))
								        	 customerNo="散客";
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
							             // mMediaPlayer.start();//此构造方法下，不需要parpared
							        	//初始化mp
							        	 	//playerThread = new PlayerThread();
							        	 	//playerThread.start();
							        	 hint="你已超时,不允许出场！";
							        	 if(str[3].equals("散客"))
								        	 customerNo="散客";
								        else
								        	  customerNo = str[2];
								              keyNo      = str[1];
								              timeDiff   = str[4];
								              dtIn       = str[5];
								             if(str[6].equals("csNotChargeFormula"))
								            	csf="不含计费公式";
								             else
								            	csf=str[6];
								              
							        }
							        else if(str[0].equals("CanOut")){
							        	//   mSendThread = new SendThread();
								  		//	mSendThread.start();   

							        	SendMsg();
							        	
							        	if(str[6].equals("outAndChecked"))
							        	 hint="允许通过";
							        	if(str[6].equals("outNotChecked"))
							        	hint="允许通过，请到前台结账";
							        	
							        	 if(str[3].equals("散客"))
								        	 customerNo="散客";
								        else
								        	 customerNo=str[2];
								              keyNo=str[1];
								              timeDiff=str[4];
								              dtIn=str[5];
								              
							            }
							        	
							        else if(str[0].equals("success"))
							        {
							        //	mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	
							        	SendMsg();
							        	
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              timeDiff=str[2];
							        }
							        	
							        else if(str[0].equals("fail"))
							        {
							        //	mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	
							        //	SendMsg();
							        	
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              timeDiff="0";
							        }
							        	
							        else if(str[0].equals("cardOutOfTime"))
							        {
							        //	mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	
							        //	SendMsg();
							        	
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              timeDiff=str[2];
							        }
							        else if(str[0].equals("nonExistInBill"))
							        {
							        //	mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	
							        //	SendMsg();
							        	
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              timeDiff=str[2];
							        }
							        	
							        else if(str[0].equals("cardNotOutOrIn"))
							        {
							        //	mSendThread = new SendThread();
							  		//	mSendThread.start();					        	
							        //	SendMsg();
							        	
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              timeDiff=str[2];
							        }	
							       }
//							        intent = new Intent(LogoUseKeyAndCardActivity.this, KeyOutActivity.class);// 使用Consumehistory窗口初始化Intent
//							        Bundle bundle=new Bundle();
//							        bundle.putCharSequence("keyHint", hint);
//							        bundle.putCharSequence("dtOut", dtOut);
//							        bundle.putCharSequence("dtIn", dtIn);
//							        bundle.putCharSequence("keyNo", keyNo);
//							        bundle.putCharSequence("customerNo", customerNo); 
//							        bundle.putCharSequence("timeDiff", timeDiff+"分钟");
//							        bundle.putCharSequence("csf", csf+"元");
//							        intent.putExtras(bundle);
//									startActivity(intent);// 打开Consumehistory
							       if(KeyOutActivity.active==false)
							       {
							        intent = new Intent(LogoUseKeyAndCardActivity.this, KeyOutActivity.class);// 使用Consumehistory窗口初始化Intent
							        Bundle bundle=new Bundle();
							        bundle.putCharSequence("keyHint", hint);
							        bundle.putCharSequence("dtOut", dtOut);
							        bundle.putCharSequence("dtIn", dtIn);
							        bundle.putCharSequence("keyNo", keyNo);
							        bundle.putCharSequence("customerNo", customerNo); 
							        bundle.putCharSequence("timeDiff", timeDiff);
							        bundle.putCharSequence("csf", csf);
							        intent.putExtras(bundle);
									startActivity(intent);// 打开Consumehistory
							       } else
							       {
							    	   
							    	//  KeyOutActivity.t.destroy();
							    	  
									 Intent intent = new Intent(ACTION_INTENT_KEYOUT);
							    	  
							           intent.putExtra("keyHint", hint);
							           intent.putExtra("dtIn", dtIn);
							           intent.putExtra("keyNo", keyNo);
							           intent.putExtra("customerNo", customerNo);
			
							           intent.putExtra("dtOut", dtOut);
							           intent.putExtra("timeDiff", timeDiff);
							           intent.putExtra("csf", csf);
							           
							           sendBroadcast(intent);
							       }
				
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
	
	private class SMOutReadThread extends Thread {
		byte[] buf = new byte[512];
		// final String inOperatetype="getkeyinstatus"; //钥匙入场操作类型。
		 final String outOperatetype="getoutstatus"; //出场操作类型。
		 String responseCode="";
		 String insideNo="";
		@Override
		public void run() {
			super.run();
			Log.i(TAG, "ReadThread==>buffer:" + buf.length);
			while (!isInterrupted()) {
				int size;
				if (smInputStream == null)
					return;
				size = smSerialPort.readBytes(buf);

				if (size > 0) {
					// new String(buf, 0, size).getBytes()会造成数据错误
					byte[] dest = new byte[size];
					System.arraycopy(buf, 0, dest, 0, size);
					// 使用队列接受数据，解决上版本串口接受
					// 连续大量数据后出现的数据混乱
					//byteLinkedList.offer(new String(buf, 0, size).getBytes());
					byteLinkedList.offer(dest);
					tempbyteLinkedList=byteLinkedList;
					String encode = "utf-8";
					 tempbuf = tempbyteLinkedList.poll();
					int size1 = tempbuf.length;
					 
			  		try{
			  			
			  			  insideNo = new String(tempbuf, 0, size1,"gb2312").trim();
						  
				           params.put("insideno", insideNo);
                        
                           params.put("operate_type", outOperatetype);
                           params.put("outIfChecked", outIfChecked);
                           params.put("csIfOut", csIfOut);
                           
                           String csf = "0";  //超时费。
                         //  params.put("operate_type", inOperatetype);
				           SimpleDateFormat    formatterIn    =   new    SimpleDateFormat    ("HH:mm:ss");       
						   Date    curDateIn    =   new    Date(System.currentTimeMillis());//获取当前时间       
									  dtIn    =    formatterIn.format(curDateIn);
						   SimpleDateFormat    formatterOut    =   new    SimpleDateFormat    ("HH:mm:ss");       
									   Date    curDateOut    =   new    Date(System.currentTimeMillis());//获取当前时间       
												  dtOut    =    formatterOut.format(curDateOut);
						 responseCode = HttpUtils.sendMessage(params, encode);
							       
							       if (responseCode.equals(""))
							       {
							              hint="网络异常！";
							              customerNo="";
							              keyNo=insideNo;
							              timeDiff="0";
							      
							       } 
							        else 
							        {				
							        	String [] str=responseCode.split("&");
							        	if(str[0].equals("nonExistCustomerNo"))
							    
							        {
							      	  hint=reply.get(str[0]);
							      	 customerNo="";
						              keyNo=insideNo;
						              timeDiff="0";
							        }
							        else if(str[0].equals("keyNotActive"))
							        {
							        	 hint="尚未发出！";
							        	 customerNo="";
							              keyNo=str[1];
							              timeDiff="0";
							        }
							        else if(str[0].equals("keyIsChecked"))
							        {
							      //  	mSendThread = new SendThread();
							  	//		mSendThread.start();
							        	
							        	SendMsg();
							        	
							        	 hint="允许通过";
							        	 if(str[3].equals("散客"))
							        	 customerNo="散客";
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
							        	
							        	 hint="管理手牌";
							        	 customerNo="管理人员";
							              keyNo=insideNo;
							              timeDiff="0";
							        }
							        else if(str[0].equals("validKeyButNotIn")){
							        	//   mSendThread = new SendThread();
								  		//	mSendThread.start();   
							        	//SendMsg();
							        	 hint="请先刷进再刷出！";
							        	 if(str[3].equals("散客"))
								        	 customerNo="散客";
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
							             // mMediaPlayer.start();//此构造方法下，不需要parpared
							        	//初始化mp
							        	// 	playerThread = new PlayerThread();
							        	// 	playerThread.start();
							        	 hint="你已超时,不允许出场！";
							        	 if(str[3].equals("散客"))
								        	 customerNo="散客";
								        else
								        	  customerNo = str[2];
								              keyNo      = str[1];
								              timeDiff   = str[4];
								              dtIn       = str[5];
								             if(str[6].equals("csNotChargeFormula"))
								            	csf="不含计费公式";
								             else
								            	csf=str[6];
								              
							        }
							        else if(str[0].equals("CanOut")){
							        	//   mSendThread = new SendThread();
								  		//	mSendThread.start();   

							        	SendMsg();
							        	
							        	if(str[6].equals("outAndChecked"))
							        	 hint="允许通过";
							        	if(str[6].equals("outNotChecked"))
							        	hint="允许通过，请到前台结账";
							        	
							        	 if(str[3].equals("散客"))
								        	 customerNo="散客";
								        else
								        	 customerNo=str[2];
								              keyNo=str[1];
								              timeDiff=str[4];
								              dtIn=str[5];
								              
							            }
							        	
							        else if(str[0].equals("success"))
							        {
							        //	mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	
							        	SendMsg();
							        	
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              timeDiff=str[2];
							        }
							        	
							        else if(str[0].equals("fail"))
							        {
							        //	mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	
							        //	SendMsg();
							        	
							        	 hint=reply.get(str[0]);
							        	 customerNo="";
							              keyNo="";
							              timeDiff="0";
							        }
							        	
							        else if(str[0].equals("cardOutOfTime"))
							        {
							        //	mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	
							        //	SendMsg();
							        	
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              timeDiff=str[2];
							        }
							        else if(str[0].equals("nonExistInBill"))
							        {
							        //	mSendThread = new SendThread();
							  		//	mSendThread.start();
							        	
							        //	SendMsg();
							        	
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              timeDiff=str[2];
							        }
							        	
							        else if(str[0].equals("cardNotOutOrIn"))
							        {
							        //	mSendThread = new SendThread();
							  		//	mSendThread.start();					        	
							        //	SendMsg();
							        	
							        	 hint=reply.get(str[0]);
							        	 customerNo=str[1];
							              keyNo="";
							              timeDiff=str[2];
							        }	
							       }
//							        intent = new Intent(LogoUseKeyAndCardActivity.this, KeyOutActivity.class);// 使用Consumehistory窗口初始化Intent
//							        Bundle bundle=new Bundle();
//							        bundle.putCharSequence("keyHint", hint);
//							        bundle.putCharSequence("dtOut", dtOut);
//							        bundle.putCharSequence("dtIn", dtIn);
//							        bundle.putCharSequence("keyNo", keyNo);
//							        bundle.putCharSequence("customerNo", customerNo); 
//							        bundle.putCharSequence("timeDiff", timeDiff+"分钟");
//							        bundle.putCharSequence("csf", csf+"元");
//							        intent.putExtras(bundle);
//									startActivity(intent);// 打开Consumehistory
							       if(KeyOutActivity.active==false)
							       {
							        intent = new Intent(LogoUseKeyAndCardActivity.this, KeyOutActivity.class);// 使用Consumehistory窗口初始化Intent
							        Bundle bundle=new Bundle();
							        bundle.putCharSequence("keyHint", hint);
							        bundle.putCharSequence("dtOut", dtOut);
							        bundle.putCharSequence("dtIn", dtIn);
							        bundle.putCharSequence("keyNo", keyNo);
							        bundle.putCharSequence("customerNo", customerNo); 
							        bundle.putCharSequence("timeDiff", timeDiff);
							        bundle.putCharSequence("csf", csf);
							        intent.putExtras(bundle);
									startActivity(intent);// 打开Consumehistory
							       } else
							       {	
							    	   
									 Intent intent = new Intent(ACTION_INTENT_KEYOUT);
							    	  
							           intent.putExtra("keyHint", hint);
							           intent.putExtra("dtIn", dtIn);
							           intent.putExtra("keyNo", keyNo);
							           intent.putExtra("customerNo", customerNo);
			
							           intent.putExtra("dtOut", dtOut);
							           intent.putExtra("timeDiff", timeDiff);
							           intent.putExtra("csf", csf);
							           
							           sendBroadcast(intent);
							       }
				
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
	
	private class PlayerThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i(TAG,"SendThread==>run");
			super.run();
			mp=MediaPlayer.create(getApplicationContext(), R.raw.cshint);
        	 if (mp.isPlaying()) {    
 	            mp.stop();    //如果正在播放，要先停止在播放。
 	          }    
        	 mp.start();
		}
	}
	 //定义Handler对象 

    private Handler handler =new Handler(){ 

         @Override 

         //当有消息发送出来的时候就执行Handler的这个方法 

        public void handleMessage(Message msg){ 

             super.handleMessage(msg); 

             // TODO 处理UI 

         }

     };
    
	protected void onDataReceived() {
         
       //   final String operatetype="getkeyinstatus"; //操作类型为身份验证。
		runOnUiThread(new Runnable() {
			public void run() {
				if (eTextShowMsg != null) {
					if (!byteLinkedList.isEmpty()) {
						byte[] buf = tempbuf;
						int size = buf.length;
						if (m_bShowDateType) {
							
							
							try {
								System.out.println("收到的数据" + new String(buf, 0, size,"gb2312"));
								
								eTextShowMsg.append(new String(buf, 0, size,"gb2312"));
								eTextKeyInsideNo.setText(new String(buf, 0, size,"gb2312"));
								

								Log.i("eTextShowMsg ASIIC", new String(buf, 0, size,"gb2312"));
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						} else {
							System.out.println("收到的数据===" + new String(buf, 0, size));
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
	private class SendCloseThread extends Thread{

		private long m_lTimer = 500;	//default 100ms
		private boolean m_bRunFlag = true;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i(TAG,"TimerSendThread==>run");
			super.run();
			//while (m_bRunFlag){  如果为true，相当于一直发送断路命令。
				Log.i(TAG,"TimerSendThread==>"+m_lTimer);
				
			//	if (m_lTimer <= 0){			//must over 0ms
			//		m_lTimer = 500;
			//	}
				try {
					Thread.sleep(m_lTimer);  //睡眠0.5秒。然后断路。
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				SendCloseMsg();
		//	}
		}

		public void setSleepTimer(long timer){
			m_lTimer = timer;
		}
		
		public void stopThread(){
			m_bRunFlag = false;
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
			String src = "00 5A 51 00 01 01 00 00 AD";//另外一个命令EB014100AB,分左右方向开闸。
			if (!m_bSendDateType) {
				//System.out.println("ASCLL" +Rule(src));
				if(Rule(src)){
					src = src.replace(" ", "");
					if(1 == (src.length()%2)){
						src += "0";
					}
					if(null != kzSerialPort.mFd){
						//Log.i(TAG, src);
						kzSerialPort.writeBytes(HexString2Bytes(src));
					}
				}else{
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(LogoUseKeyAndCardActivity.this, R.string.inputwarn,Toast.LENGTH_SHORT).show();
						}
					});
					
				}
			} else {
				//System.out.println("HEX");
				if(null != kzSerialPort.mFd){
					//Log.i(TAG, src);
					kzSerialPort.writeBytes(src.getBytes());
				}
			}
		}
		
		private void SendCloseMsg(){
			
			//	String src = eTextSendMsg.getText().toString();
				String src = "00 5A 51 00 02 01 00 00 AE";//另外一个命令EB014100AB,分左右方向开闸。
				if (!m_bSendDateType) {
					//System.out.println("ASCLL" +Rule(src));
					if(Rule(src)){
						src = src.replace(" ", "");
						if(1 == (src.length()%2)){
							src += "0";
						}
						if(null != kzSerialPort.mFd){
							//Log.i(TAG, src);
							kzSerialPort.writeBytes(HexString2Bytes(src));
						}
					}else{
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(LogoUseKeyAndCardActivity.this, R.string.inputwarn,Toast.LENGTH_SHORT).show();
							}
						});
						
					}
				} else {
					//System.out.println("HEX");
					if(null != kzSerialPort.mFd){
						//Log.i(TAG, src);
						kzSerialPort.writeBytes(src.getBytes());
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
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
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
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
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
					   
					   sminReadThread = new SMInReadThread();
					   sminReadThread.start();
                      } else
                      {
                    	  outReadThread = new OutReadThread();
   					       outReadThread.start();  
   					       
   					      smoutReadThread = new SMOutReadThread();
					       smoutReadThread.start();  
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
						
						sminReadThread.interrupt();
						// mReadThread = null;
						//btnSend.setEnabled(true);
						//togBtnSendPer500ms.setEnabled(true);
						//togBtnSendPer100ms.setEnabled(true);
						//togBtnSendPer1000ms.setEnabled(true);
					}
					if (outReadThread != null) {
						Log.i(TAG, "togBtnReciveDate interrupt mReadThread != null");
						outReadThread.interrupt();
						smoutReadThread.interrupt();
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
