package cn.edu.zju.minisql.distributed.server.region.lib.indexmanager;

import java.util.Vector;

public class OffsetInfo {
	public Vector<Integer> offsetInfile;
	public Vector<Integer> offsetInBlock;
	public int length;
	public OffsetInfo(){
		offsetInfile = new Vector<Integer>();
		offsetInBlock = new Vector<Integer>();
		length=0;
	}
}