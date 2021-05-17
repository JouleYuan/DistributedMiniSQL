package cn.edu.zju.minisql.distributed.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) {
        System.out.println("Welcome to MiniSql.");

        API.init();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            Interpreter.parsing(reader);
        }
        catch(Exception e){
            System.out.println("Interpreter error:"+e.getMessage());
            e.printStackTrace();
        }
    }
}
