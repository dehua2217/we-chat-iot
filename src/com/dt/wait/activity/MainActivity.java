package com.dt.wait.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.dt.wait.dao.WaitSetDao;
import com.dt.wait.model.TbWaitSet;



public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	private static String inOrOut="waitIn";
	private static String leftOrRight="left";
	private static String outIfChecked="Yes";
	private static String csIfOut="Yes";
	
	private EditText eTextIp; //ip
	private Button btnSave; //配置保存
	private int dtSelectedPosition;
	private int kzSelectedPosition;

	private Spinner spiDtCom; //读头串口号
	private Spinner spiKzCom; //开闸串口号
	private Spinner spiOutIfChecked; //出场是否结账
	private Spinner spiCsIfOut;   //超时是否出场
	
	private static final String[] Sserialport = { "COM0", "COM1", "COM2",
		"COM3", "COM4" };
	private static final String[] YesOrNot = { "是", "否" };
	
	private ArrayAdapter<String> adapSerialPort;
	private ArrayAdapter<String> adapYesOrNot;
	WaitSetDao WaitSetDao = new WaitSetDao(
			MainActivity.this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "==>super onCreate");
		// 无title
				requestWindowFeature(Window.FEATURE_NO_TITLE);
				// 全屏
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		List<TbWaitSet> lsTbWaitSet=WaitSetDao.getScrollData(0,(int)WaitSetDao.getCount());
		if(lsTbWaitSet.size()>0)
		{
		//	Intent intent = new Intent(MainActivity.this, LogoActivity.class);// 使用Consumehistory窗口初始化Intent
			Intent intent = new Intent(MainActivity.this, LogoUseKeyAndCardActivity.class);// 使用Consumehistory窗口初始化Intent
			startActivity(intent);
			finish();
			
		}
		setContentView(R.layout.activity_main);
		
		eTextIp = (EditText) findViewById(R.id.edt_ip);
		
		spiDtCom = (Spinner) findViewById(R.id.dtcom_spinner);
		spiKzCom = (Spinner) findViewById(R.id.kzcom_spinner);
		adapSerialPort = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, Sserialport);
		adapSerialPort
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spiDtCom.setAdapter(adapSerialPort);
		spiKzCom.setAdapter(adapSerialPort);
		
		spiDtCom.setSelection(0);
		spiKzCom.setSelection(2);
		
		dtSelectedPosition= spiDtCom.getSelectedItemPosition();
		kzSelectedPosition= spiKzCom.getSelectedItemPosition();
		
		spiOutIfChecked = (Spinner) findViewById(R.id.outifchecked_spinner);
		spiCsIfOut = (Spinner) findViewById(R.id.csifout_spinner);
		adapYesOrNot = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, YesOrNot);
		adapYesOrNot
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spiOutIfChecked.setAdapter(adapYesOrNot);
		spiCsIfOut.setAdapter(adapYesOrNot);
		
		spiOutIfChecked.setSelection(0);
		spiCsIfOut.setSelection(0);
		
		spiDtCom
		.setOnItemSelectedListener(new SpiOnItemSelectedListenerImpl());
		spiKzCom
		.setOnItemSelectedListener(new SpiOnItemSelectedListenerImpl());
		spiOutIfChecked
		.setOnItemSelectedListener(new SpiOnItemSelectedListenerImpl());
		spiCsIfOut
		.setOnItemSelectedListener(new SpiOnItemSelectedListenerImpl());
		
		btnSave=(Button) findViewById(R.id.save);
		btnSave.setOnClickListener(new OnClickListener() {// 为保存按钮设置监听事件
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String strIp = eTextIp.getText().toString().trim();// 获取ip地址
				if (!strIp.isEmpty()) {// ip不能为空
					
					WaitSetDao WaitSetDao = new WaitSetDao(
							MainActivity.this);
					// 创建Tb_inaccount对象
					TbWaitSet TbWaitSet = new TbWaitSet(
							WaitSetDao.getMaxId() + 1, strIp, inOrOut, spiDtCom.getSelectedItem().toString(),
							spiKzCom.getSelectedItem().toString(),
							outIfChecked,csIfOut,leftOrRight);
					WaitSetDao.add(TbWaitSet);// 添加收入信息
					// 弹出信息提示
					Toast.makeText(MainActivity.this, "配置保存成功！",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(MainActivity.this, LogoUseKeyAndCardActivity.class);// 使用Consumehistory窗口初始化Intent
					startActivity(intent);
					finish();
				} else {
					Toast.makeText(MainActivity.this, "请输入IP地址！",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		//根据ID找到RadioGroup实例
		         RadioGroup group = (RadioGroup)this.findViewById(R.id.radioGroup1);
		        //绑定一个匿名监听器
		        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		             
		             @Override
		             public void onCheckedChanged(RadioGroup arg0, int arg1) {
		                 // TODO Auto-generated method stub
		                 //获取变更后的选中项的ID
		                 int radioButtonId = arg0.getCheckedRadioButtonId();
		                 //根据ID获取RadioButton的实例
		                 RadioButton rb = (RadioButton)findViewById(radioButtonId);
		                 //更新文本内容，以符合选中项
		                 if(rb.getText().toString().equals("进场闸机"))
		                 inOrOut="waitIn";
		                 else
		                	 inOrOut="waitOut";	 
		             }
		         });
		        
		      //根据ID找到RadioGroup实例
		         RadioGroup groupOpen = (RadioGroup)this.findViewById(R.id.radioGroup2);
		        //绑定一个匿名监听器
		        groupOpen.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		             
		             @Override
		             public void onCheckedChanged(RadioGroup arg0, int arg1) {
		                 // TODO Auto-generated method stub
		                 //获取变更后的选中项的ID
		                 int radioButtonId = arg0.getCheckedRadioButtonId();
		                 //根据ID获取RadioButton的实例
		                 RadioButton rb = (RadioButton)findViewById(radioButtonId);
		                 //更新文本内容，以符合选中项
		                 if(rb.getText().toString().equals("左向开闸"))
		                 leftOrRight="left";
		                 else
		                	 leftOrRight="right";	 
		             }
		         });

	}
	
private class SpiOnItemSelectedListenerImpl implements
OnItemSelectedListener {

@Override
public void onItemSelected(AdapterView<?> parent, View view,
		int position, long id) {
	// TODO Auto-generated method stub
	if (parent == spiDtCom) {
		//serialPort.mDevNum = m_iSerialPort[position];
		if(spiDtCom.getSelectedItem().toString().equals(spiKzCom.getSelectedItem().toString()))
		{
			spiDtCom.setSelection(dtSelectedPosition);
			// 弹出信息提示
			Toast.makeText(MainActivity.this, "读头串口号不能和开闸串口号相同！",
					Toast.LENGTH_SHORT).show();
			
			return;
		} else
			dtSelectedPosition=position;
	//	Log.i("uart params set", "ChooseSerialPort_"
		//		+ serialPort.mDevNum);
	} else if (parent == spiKzCom) {
		
		if(spiDtCom.getSelectedItem().toString().equals(spiKzCom.getSelectedItem().toString()))
		{
			// 弹出信息提示
			Toast.makeText(MainActivity.this, "读头串口号不能和开闸串口号相同！",
					Toast.LENGTH_SHORT).show();
			spiKzCom.setSelection(kzSelectedPosition);
			return;
		}else
			kzSelectedPosition=position;
		//serialPort.mSpeed = baudrate[position];
		//Log.i("uart params set", "ChooseBaudRate_" + serialPort.mSpeed);

	} else if (parent == spiOutIfChecked)
	{
		if(spiOutIfChecked.getSelectedItem().toString().equals("是"))
		{
			// 弹出信息提示
			outIfChecked="Yes";
		}else
			outIfChecked="No";
	} else if (parent == spiCsIfOut)
	{
		if(spiCsIfOut.getSelectedItem().toString().equals("是"))
		{
			// 弹出信息提示
			csIfOut="Yes";
		}else
			csIfOut="No";
	}
}

@Override
public void onNothingSelected(AdapterView<?> arg0) {
	// TODO Auto-generated method stub

   }
}

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
		Log.i(TAG, "==>onStop out");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "==>onDestroy in");
		//closeSerialPort();
		//serialPort = null;
		super.onDestroy();
		Log.i(TAG, "==>onDestroy out");
	}



/*	private static final String TAG = "MainActivity";
	private SerailPortOpt serialPort;

	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private SendThread mSendThread;
	private TimerSendThread mTimerSendThread;

	private boolean m_bShowDateType = false;
	private boolean m_bSendDateType = false;

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
	private Button btnSave;

	private ToggleButton togBtnSendPer100ms;
	private ToggleButton togBtnSendPer500ms;
	private ToggleButton togBtnSendPer1000ms;

	private Spinner spiChooseSerialPort;
	private Spinner spiChooseBaudRate;
	private Spinner spiChooseDataBits;
	private Spinner spiChooseStopBits;
	private Spinner spiChooseParity;

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
	String responseCode;
	String keyInsideNo;
	String    dt; //当前时间
	String keyHint=null;
	
	private DBOpenHelper helper;// 创建DBOpenHelper对象
	private SQLiteDatabase db;// 创建SQLiteDatabase对象

	private LinkedList<byte[]> byteLinkedList = new LinkedList<byte[]>();
	private LinkedList<byte[]> tempbyteLinkedList = new LinkedList<byte[]>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//serialPort = new SerailPortOpt();

		eTextShowMsg = (EditText) findViewById(R.id.showmsg);
		eTextSendMsg = (EditText) findViewById(R.id.sendmsg);
		eTextKeyInsideNo = (EditText) findViewById(R.id.keyInsiderNo);
		eTextIp = (EditText) findViewById(R.id.ip);

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

		serialPort.mDevNum = m_iSerialPort[0];
		serialPort.mSpeed = baudrate[4];
		serialPort.mDataBits = databits[3];
		serialPort.mStopBits = stopbits[0];
		serialPort.mParity = parity[0];

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
	}

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
		Log.i(TAG, "==>onStop out");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "==>onDestroy in");
		closeSerialPort();
		serialPort = null;
		super.onDestroy();
		Log.i(TAG, "==>onDestroy out");
	}



	private void openSerialPort() {

		if (serialPort.mFd == null) {
			serialPort.openDev(serialPort.mDevNum);

			Log.i("uart port operate", "Mainactivity.java==>uart open");
			serialPort.setSpeed(serialPort.mFd, serialPort.mSpeed);
			Log.i("uart port operate", "Mainactivity.java==>uart set speed..."
					+ serialPort.mSpeed);
			serialPort.setParity(serialPort.mFd, serialPort.mDataBits,
					serialPort.mStopBits, serialPort.mParity);
			Log.i("uart port operate",
					"Mainactivity.java==>uart other params..."
							+ serialPort.mDataBits + "..."
							+ serialPort.mStopBits + "..." + serialPort.mParity);

			mInputStream = serialPort.getInputStream();
			mOutputStream = serialPort.getOutputStream();
		}
	}

	private void closeSerialPort() {

		if (mReadThread != null) {
			mReadThread.interrupt();
			// mReadThread = null;
		}

		if (serialPort.mFd != null) {
			Log.i("uart port operate", "Mainactivity.java==>uart stop");
			serialPort.closeDev(serialPort.mFd);
			Log.i("uart port operate", "Mainactivity.java==>uart stoped");
		}
	}

	private class ReadThread extends Thread {
		byte[] buf = new byte[512];
		 final String operatetype="getkeyinstatus"; //操作类型为身份验证。
		@Override
		public void run() {
			super.run();
			Log.i(TAG, "ReadThread==>buffer:" + buf.length);
			while (!isInterrupted()) {
				int size;
				if (mInputStream == null)
					return;
				size = serialPort.readBytes(buf);

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
			  			
			  			 keyInsideNo = new String(tempbuf, 0, size1,"gb2312").trim();
						  
				           params.put("key_insideno", keyInsideNo);

				           params.put("operate_type", operatetype);
				           
				           SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy-MM-dd HH:mm:ss");       
					  		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
					  	    dt    =    formatter.format(curDate);
			  		 responseCode = HttpUtils.sendMessage(params, encode);
			       
			       if (responseCode.equals(""))
			    //   {
			              keyHint="网络异常";
			       /*       responseCode = HttpUtils.sendMessage(params, encode);
			              if(responseCode.equals("keyNotExist")) 
					      	  keyHint="钥匙不存在";
					        else if(responseCode.equals("keyNotActive"))
					        	 keyHint="钥匙未激活";
					        else if(responseCode.equals("keyBeUsed"))
					        	 keyHint="钥匙已使用";
					        else if(responseCode.equals("passwordCard"))
					        	 keyHint="管理手牌";
					        else
					        	 keyHint="允许通过";
			       } else
			       {*/
/*			        else  if(responseCode.equals("keyNotExist")) 
			      	  keyHint="钥匙不存在";
			        else if(responseCode.equals("keyNotActive"))
			        	 keyHint="钥匙未激活";
			        else if(responseCode.equals("keyBeUsed"))
			        	 keyHint="钥匙已使用";
			        else if(responseCode.equals("passwordCard"))
			        	 keyHint="管理手牌";
			        else
			        	 keyHint="允许通过";
			   //    }
			  		 }
			        catch (Exception e)
			        {
			            e.printStackTrace();
			        }
			       // handler.sendEmptyMessage(0);
			           
					onDataReceived();
					
					intent = new Intent(MainActivity.this, KeyInActivity.class);// 使用Consumehistory窗口初始化Intent
			        Bundle bundle=new Bundle();
			        bundle.putCharSequence("keyHint", keyHint+responseCode);
			        bundle.putCharSequence("dt", dt);
			        intent.putExtras(bundle);
					 startActivity(intent);// 打开Consumehistory
				//	launchActivity();
		                
					Log.i(TAG, "ReadThread==>" + size);
				}
			 }
			
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
 /*   protected void launchActivity()
    {
    	 new Thread() {

    		   public void run() {



    	
    		   };

    		  }.start();

    }*/
/*	protected void onDataReceived() {
         
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
	
/*	public static String bytesToHexString(byte[] src, int size) {
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
		
		String src = eTextSendMsg.getText().toString();
		if (!m_bSendDateType) {
			//System.out.println("ASCLL" +Rule(src));
			if(Rule(src)){
				src = src.replace(" ", "");
				if(1 == (src.length()%2)){
					src += "0";
				}
				if(null != serialPort.mFd){
					//Log.i(TAG, src);
					serialPort.writeBytes(HexString2Bytes(src));
				}
			}else{
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(MainActivity.this, R.string.inputwarn,Toast.LENGTH_SHORT).show();
					}
				});
				
			}
		} else {
			//System.out.println("HEX");
			if(null != serialPort.mFd){
				//Log.i(TAG, src);
				serialPort.writeBytes(src.getBytes());
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
/**	public static byte uniteBytes(byte src0, byte src1) {
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
/*	public static byte[] HexString2Bytes(String src) {
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
				serialPort.mDevNum = m_iSerialPort[position];
				// tViewSerialPort.setText(Sserialport[position]);
				Log.i("uart params set", "ChooseSerialPort_"
						+ serialPort.mDevNum);
			} else if (parent == spiChooseBaudRate) {
				serialPort.mSpeed = baudrate[position];
				// tViewBaudRate.setText(Sbaudrate[position]);
				Log.i("uart params set", "ChooseBaudRate_" + serialPort.mSpeed);

			} else if (parent == spiChooseDataBits) {
				serialPort.mDataBits = databits[position];
				Log.i("uart params set", "ChooseDataBits_"
						+ serialPort.mDataBits);

			} else if (parent == spiChooseStopBits) {
				serialPort.mStopBits = stopbits[position];
				Log.i("uart params set", "ChooseStopBits_"
						+ serialPort.mStopBits);

			} else if (parent == spiChooseParity) {
				serialPort.mParity = parity[position];
				Log.i("uart params set", "ChooseParity_" + serialPort.mParity);

			}

			if (serialPort.mFd != null) {
				serialPort.setSpeed(serialPort.mFd, serialPort.mSpeed);
				Log.i("uart port operate", "====>here...uart set speed..."
						+ serialPort.mSpeed);
				serialPort.setParity(serialPort.mFd, serialPort.mDataBits,
						serialPort.mStopBits, serialPort.mParity);
				Log.i("uart port operate", "====>here...uart other params..."
						+ serialPort.mDataBits + "..." + serialPort.mStopBits
						+ "..." + serialPort.mParity);
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
					mReadThread = new ReadThread();
					mReadThread.start();
					Log.i("uart port operate", "Mainactivity.java==>ReadThread started");
					//btnSend.setEnabled(false);
					//togBtnSendPer100ms.setEnabled(false);
					//togBtnSendPer500ms.setEnabled(false);
					//togBtnSendPer1000ms.setEnabled(false);
				} else {

					Log.i(TAG, "togBtnReciveDate interrupt in");
					if (mReadThread != null) {
						Log.i(TAG, "togBtnReciveDate interrupt mReadThread != null");
						mReadThread.interrupt();
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
	}*/
}

