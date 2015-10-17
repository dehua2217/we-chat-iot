package com.dt.wait.model;

public class TbWaitSet {
	private int _id;// 存储配置信息编号
	private String ip;// 存储ip信息
	private String inOrOut;//是进还是出
	private String dtComNo;//读头端口号
	private String kzComNo;//开闸端口号
	private String outIfChecked;//出场是否结账
	private String csIfOut;//超时是否开闸
	private String leftOrRight;//左向还是右向开闸
	

	public TbWaitSet()// 默认构造函数
	{
		super();
	}


	public TbWaitSet(int _id, String ip, String inOrOut, String dtComNo,
			String kzComNo, String outIfChecked, String csIfOut,String leftOrRight) {
		super();
		this._id = _id;
		this.ip = ip;
		this.inOrOut = inOrOut;
		this.dtComNo = dtComNo;
		this.kzComNo = kzComNo;
		this.outIfChecked = outIfChecked;
		this.csIfOut = csIfOut;
		this.leftOrRight = leftOrRight;
	}

	public String getOutIfChecked() {
		return outIfChecked;
	}

	public void setOutIfChecked(String outIfChecked) {
		this.outIfChecked = outIfChecked;
	}

	public String getCsIfOut() {
		return csIfOut;
	}

	public void setCsIfOut(String csIfOut) {
		this.csIfOut = csIfOut;
	}


	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getInOrOut() {
		return inOrOut;
	}

	public void setInOrOut(String inOrOut) {
		this.inOrOut = inOrOut;
	}

	public String getDtComNo() {
		return dtComNo;
	}

	public void setDtComNo(String dtComNo) {
		this.dtComNo = dtComNo;
	}

	public String getKzComNo() {
		return kzComNo;
	}

	public void setKzComNo(String kzComNo) {
		this.kzComNo = kzComNo;
	}
	public String getLeftOrRight() {
		return leftOrRight;
	}

	public void setLeftOrRight(String leftOrRight) {
		this.leftOrRight = leftOrRight;
	}

	
}
