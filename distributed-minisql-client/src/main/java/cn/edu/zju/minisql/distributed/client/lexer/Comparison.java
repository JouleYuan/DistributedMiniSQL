package cn.edu.zju.minisql.distributed.client.lexer;

public class Comparison extends Word{
    public Comparison(String s, int tag) {
        super(s, tag);
    }
    public static int parseCompar(Token a){
        String aa=a.toString();
        return Comparison.parseCompar(aa);
    }
    public static int parseCompar(String a){
        if(a.equals("<"))return 2;
        else if(a.equals(">"))return 4;
        else if(a.equals("<="))return 3;
        else if(a.equals(">="))return 5;
        else if(a.equals("=="))return 6;
        else if(a.equals("="))return 6;
        else if(a.equals("<>"))return 7;
        else return -1;
    }
    public static final Comparison
        lt = new Comparison("<", Tag.OP),
        gt = new Comparison(">", Tag.OP),
        eq = new Comparison("==", Tag.OP),
        ne = new Comparison("<>", Tag.OP),
        le = new Comparison("<=", Tag.OP),
        ge = new Comparison(">=", Tag.OP);
}
