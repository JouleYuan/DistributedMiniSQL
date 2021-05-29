package cn.edu.zju.minisql.distributed.server.region.minisql;

import com.bethecoder.ascii_table.ASCIITable;
import com.bethecoder.ascii_table.ASCIITableHeader;

import cn.edu.zju.minisql.distributed.server.region.minisql.recordmanager.Tuple;

import java.util.Vector;

public class TableUtils {
    public static void print(Vector<String> attrs, Vector<Tuple> tuples){
        ASCIITableHeader[] headerObjs = new ASCIITableHeader[attrs.size()];
        String[][] data = new String[tuples.size()][];
        for(int i=0; i<attrs.size(); i++){
            headerObjs[i] = new ASCIITableHeader(attrs.get(i), ASCIITable.ALIGN_LEFT);
        }
        if(tuples.size() == 0){
            data = new String[][]{new String[attrs.size()]};
            for(int i=0; i<attrs.size(); i++){
                data[0][i] = "";
            }
        }
        for(int i=0; i<tuples.size(); i++){
            data[i] = tuples.get(i).units.toArray(new String[0]);
        }
        String t = ASCIITable.getInstance().getTable(headerObjs, data);
        System.out.println(t);
    }
}
