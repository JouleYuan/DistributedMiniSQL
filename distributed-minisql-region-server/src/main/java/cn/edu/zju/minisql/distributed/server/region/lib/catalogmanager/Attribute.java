package cn.edu.zju.minisql.distributed.server.region.lib.catalogmanager;


public class Attribute {
	String attrName;	//字段名称
	String type;		//字段类型int float char  boolean
	int length;			//字段长度
	boolean isUnique;
	//构造函数
	public Attribute(String attrName, String type, int length, boolean isU){
		this.attrName = attrName;
		this.type=type;
		this.length=length;
		this.isUnique=isU;
	}
}