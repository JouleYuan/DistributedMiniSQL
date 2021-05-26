package cn.edu.zju.minisql.distributed.server.region;

import java.io.File;

public class DirectoryManager {
    private static void delFiles(File file){
        if(file.isDirectory()){
            File[] childrenFiles = file.listFiles();
            for (File childFile:childrenFiles){
                delFiles(childFile);
            }
        }
        file.delete();
    }

    public static void init(String filename) {
        File file=new File(filename.substring(0, filename.length() - 1));
        if(file.exists()) delFiles(file);
        file.mkdir();
    }
}
