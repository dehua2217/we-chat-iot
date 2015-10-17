package com.dt.wait.model;

public class TbWaitSet {
	private int _id;// �洢������Ϣ���
	private String ip;// �洢ip��Ϣ
	private String inOrOut;//�ǽ����ǳ�
	private String dtComNo;//��ͷ�˿ں�
	private String kzComNo;//��բ�˿ں�
	private String outIfChecked;//�����Ƿ����
	private String csIfOut;//��ʱ�Ƿ�բ
	private String leftOrRight;//����������բ
	

	public TbWaitSet()// Ĭ�Ϲ��캯��
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
