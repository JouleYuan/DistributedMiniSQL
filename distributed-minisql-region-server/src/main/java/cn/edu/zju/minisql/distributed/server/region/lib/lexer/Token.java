package cn.edu.zju.minisql.distributed.server.region.lib.lexer;

public class Token {  
    public final int tag;  
      
    public Token(int t) {  
        this.tag = t;  
    }  
      
    public String toString() {  
        return "" + (char)tag;  
    }  
}  
