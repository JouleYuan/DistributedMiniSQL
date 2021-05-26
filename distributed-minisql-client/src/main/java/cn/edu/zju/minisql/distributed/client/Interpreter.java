package cn.edu.zju.minisql.distributed.client;

import cn.edu.zju.minisql.distributed.service.Attribute;
import cn.edu.zju.minisql.distributed.service.AttributeType;
import cn.edu.zju.minisql.distributed.service.Table;

import cn.edu.zju.minisql.distributed.client.lexer.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Interpreter {
    private static Token currentToken;//记录当前token
    private static boolean isSynCorrect = true;
    private static boolean isSemaCorrect = true;
    private static String synErrMsg;
    private static String semaErrMsg;
    private static final StringBuilder sql = new StringBuilder();

    //逐条语句进行解析
    public static void parsing(BufferedReader reader) throws IOException {
        Lexer lexer = new Lexer(reader);

        while (!lexer.getReaderState()) {
            System.out.print("miniSQL> ");
            if (!isSynCorrect) {
                while (!currentToken.toString().equals(";")) {
                    currentToken = lexer.scan();
                }
                System.out.println(synErrMsg);
                isSemaCorrect = true;
                isSynCorrect = true;
                sql.delete(0,sql.length());
                continue;
            }

            currentToken = lexer.scan();
            sql.append(currentToken);

            if (currentToken.tag == Tag.EXECFILE) {
                currentToken = lexer.scan();
                File file = new File(currentToken.toString() + ".txt");
                currentToken = lexer.scan();
                if (currentToken.toString().equals(";")) {
                    if (file.exists()) {
                        BufferedReader reader2 = new BufferedReader(new FileReader(file));
                        parsing(reader2);
                        isSynCorrect = true;
                    } else {
                        synErrMsg = "The file " + file.getName() + " doesn't exist";
                        isSynCorrect = false;
                    }
                } else {
                    if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                    isSynCorrect = false;
                }
            }
            else if (currentToken.tag == Tag.QUIT) {
                currentToken = lexer.scan();
                if (currentToken.toString().equals(";")) {
                    System.out.println("Quit the MiniSql. See you next time!");
                    API.close();
                    reader.close();
                    System.exit(0);
                } else {
                    if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                    isSynCorrect = false;
                }
            }
            else if (currentToken.tag == Tag.CREATE) {
                currentToken = lexer.scan();
                sql.append(" ");
                sql.append(currentToken);
                /*
                 * create table 语义错误种类
                 * 1 table name已存在
                 * 2 primary key不存在
                 * 3 重复attribute属性
                 * 4 char(n) 的n越界
                 */
                if (currentToken.tag == Tag.TABLE) {
                    currentToken = lexer.scan();
                    if (currentToken.tag == Tag.ID) {    //create table 表名
                        String tmpTableName = currentToken.toString();
                        List<Attribute> tmpAttributes = new ArrayList<>();
                        String tmpPrimaryKey = null;

                        currentToken = lexer.scan();

                        if (currentToken.toString().equals("(")) {//create table 表名(
                            currentToken = lexer.scan();

                            while (!currentToken.toString().equals(")") && !currentToken.toString().equals(";")) {
                                if (currentToken.tag == Tag.ID) { //create table 表名 ( 属性名
                                    String tmpAttrName = currentToken.toString();
                                    String tmpType;
                                    AttributeType attrType;
                                    int tmpLength;
                                    boolean tmpIsU = false;

                                    currentToken = lexer.scan();
                                    if (currentToken.tag == Tag.TYPE) {//create table 表名 ( 属性名 类型名
                                        tmpType = currentToken.toString();
                                        if (tmpType.equals("char")) {//针对char(n)类型做特殊处理
                                            currentToken = lexer.scan();
                                            if (currentToken.toString().equals("(")) {
                                                currentToken = lexer.scan();
                                                if (currentToken.tag == Tag.INTNUM) {
                                                    tmpLength = Integer.parseInt(currentToken.toString());
                                                    attrType = AttributeType.CHAR;
                                                    if (tmpLength < 1 || tmpLength > 255) {
                                                        semaErrMsg = "The length of char should be 1<=n<=255";
                                                        isSemaCorrect = false;
                                                    }
                                                    currentToken = lexer.scan();
                                                    if (!currentToken.toString().equals(")")) {
                                                        if (isSynCorrect) {
                                                            synErrMsg = "Synthetic error near: " + currentToken.toString();
                                                            isSynCorrect = false;
                                                        }
                                                        break;
                                                    }
                                                } else {
                                                    if (isSynCorrect) {
                                                        synErrMsg = "Synthetic error near: " + currentToken.toString();
                                                        isSynCorrect = false;
                                                    }
                                                    break;
                                                }
                                            } else {
                                                if (isSynCorrect) {
                                                    synErrMsg = "Synthetic error near: " + currentToken.toString();
                                                    isSynCorrect = false;
                                                }
                                                break;
                                            }
                                        } else if (tmpType.equals("int")) {//不是char
                                            tmpLength = 4;
                                            attrType = AttributeType.INT;
                                        } else {
                                            tmpLength = 4;
                                            attrType = AttributeType.FLOAT;
                                        }

                                        currentToken = lexer.scan();
                                        if (currentToken.tag == Tag.UNIQUE) {
                                            tmpIsU = true;
                                            currentToken = lexer.scan();
                                        }

                                        if (currentToken.toString().equals(",")) {
                                            tmpAttributes.add(new Attribute(tmpAttrName, attrType, tmpLength, tmpIsU));
                                        } else if (currentToken.toString().equals(")")) {
                                            tmpAttributes.add(new Attribute(tmpAttrName, attrType, tmpLength, tmpIsU));
                                            break;
                                        } else {
                                            if (isSynCorrect) {
                                                synErrMsg = "Synthetic error near: " + currentToken.toString();
                                                isSynCorrect = false;
                                            }
                                            break;
                                        }
                                    } else {
                                        if (isSynCorrect) {
                                            synErrMsg = "Synthetic error near: " + currentToken.toString();
                                            isSynCorrect = false;
                                        }
                                        break;
                                    }
                                } else if (currentToken.tag == Tag.PRIMARY) {
                                    currentToken = lexer.scan();
                                    if (currentToken.tag == Tag.KEY) {
                                        currentToken = lexer.scan();
                                        if (currentToken.toString().equals("(")) {
                                            currentToken = lexer.scan();
                                            if (currentToken.tag == Tag.ID) {
                                                tmpPrimaryKey = currentToken.toString();
                                                currentToken = lexer.scan();
                                                if (!currentToken.toString().equals(")")) {
                                                    if (isSynCorrect) {
                                                        synErrMsg = "Synthetic error near: " + currentToken.toString();
                                                        isSynCorrect = false;
                                                    }
                                                    break;
                                                }
                                            } else {
                                                if (isSynCorrect) {
                                                    synErrMsg = "Synthetic error near: " + currentToken.toString();
                                                    isSynCorrect = false;
                                                }
                                                break;
                                            }
                                        } else {
                                            if (isSynCorrect) {
                                                synErrMsg = "Synthetic error near: " + currentToken.toString();
                                                isSynCorrect = false;
                                            }
                                            break;
                                        }
                                    } else {
                                        if (isSynCorrect) {
                                            synErrMsg = "Synthetic error near: " + currentToken.toString();
                                            isSynCorrect = false;
                                        }
                                        break;
                                    }
                                } else {
                                    if (isSynCorrect) {
                                        synErrMsg = "Synthetic error near: " + currentToken.toString();
                                        isSynCorrect = false;
                                    }
                                    break;
                                }
                                currentToken = lexer.scan();
                            }//end of while ")"

                            currentToken = lexer.scan();

                            if (isSynCorrect && currentToken.toString().equals(";")) {
                                /*
                                 * 执行create table 操作
                                 * */
                                if (tmpPrimaryKey == null) {
                                    synErrMsg = "Synthetic error: no primary key defined";
                                    isSynCorrect = false;
                                    sql.delete(0,sql.length());
                                    continue;
                                }
                                if (isSemaCorrect) {
                                    boolean find = false;
                                    for (int index = 0; index < tmpAttributes.size(); index++) {
                                        if (tmpAttributes.get(index).getName().equals(tmpPrimaryKey)) {
                                            API.createTable(tmpTableName, new Table(tmpTableName, tmpAttributes, index, null));
                                            find = true;
                                        }
                                    }
                                    if (!find){
                                        System.out.println("Synthetic error: no field named" + tmpPrimaryKey + ", create table " + tmpTableName + " failed");
                                    }
                                } else {
                                    System.out.print(semaErrMsg);
                                    System.out.println(", create table " + tmpTableName + " failed");
                                    isSemaCorrect = true;
                                }
                                sql.delete(0,sql.length());
                            } else {
                                if (isSynCorrect) {
                                    synErrMsg = "Synthetic error near: " + currentToken.toString();
                                    isSynCorrect = false;
                                }
                            }
                        }
                    } else {
                        if (isSynCorrect) {
                            synErrMsg = "Synthetic error near: " + currentToken.toString();
                            isSynCorrect = false;
                        }
                    }
                }

                // create index
                else if (currentToken.tag == Tag.INDEX) {
                    String tmpTableName;

                    currentToken = lexer.scan();
                    sql.append(" ");
                    sql.append(currentToken);

                    if (currentToken.tag == Tag.ID) {//create index a
                        currentToken = lexer.scan();
                        sql.append(" ");
                        sql.append(currentToken);
                        if (currentToken.tag == Tag.ON) {//create index a on
                            currentToken = lexer.scan();
                            sql.append(" ");
                            sql.append(currentToken);
                            if (currentToken.tag == Tag.ID) {//create index a on b
                                tmpTableName = currentToken.toString();

                                currentToken = lexer.scan();
                                sql.append(" ");
                                sql.append(currentToken);
                                if (currentToken.toString().equals("(")) {
                                    currentToken = lexer.scan();
                                    sql.append(" ");
                                    sql.append(currentToken);
                                    if (currentToken.tag == Tag.ID) {
                                        currentToken = lexer.scan();
                                        sql.append(" ");
                                        sql.append(currentToken);
                                        if (currentToken.toString().equals(")") && lexer.scan().toString().equals(";")) {//create index a on b;
                                            /*
                                             * 执行create index操作
                                             * */
                                            if (isSemaCorrect) {
                                                sql.append(";");
                                                API.regionSQL(tmpTableName, sql.toString(), true);
                                            } else {
                                                System.out.println(semaErrMsg + ", create index failed");
                                                isSemaCorrect = true;
                                            }
                                            sql.delete(0,sql.length());
                                        } else {
                                            if (isSynCorrect)
                                                synErrMsg = "Synthetic error near: " + currentToken.toString();
                                            isSynCorrect = false;
                                        }
                                    } else {
                                        if (isSynCorrect)
                                            synErrMsg = "Synthetic error near: " + currentToken.toString();
                                        isSynCorrect = false;
                                    }
                                } else {
                                    if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                                    isSynCorrect = false;
                                }
                            } else {
                                if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                                isSynCorrect = false;
                            }
                        } else {
                            if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                            isSynCorrect = false;
                        }
                    } else {
                        if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                        isSynCorrect = false;
                    }
                }
                else {
                    if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                    isSynCorrect = false;
                }
            }//end of create
            else if (currentToken.tag == Tag.DROP) {
                currentToken = lexer.scan();
                sql.append(" ");
                sql.append(currentToken);
                /*
                 * drop table 语义错误种类  1该table不存在
                 */
                if (currentToken.tag == Tag.TABLE) {
                    String tmpTableName;
                    currentToken = lexer.scan();
                    if (currentToken.tag == Tag.ID) {//drop table a
                        tmpTableName = currentToken.toString();

                        currentToken = lexer.scan();
                        if (currentToken.toString().equals(";")) {//drop table a ;
                            /*
                             * 执行drop table
                             */
                            if (isSemaCorrect) {
                                API.dropTable(tmpTableName);
                            } else {
                                System.out.print(semaErrMsg);
                                System.out.println("drop table " + tmpTableName + " failed");
                                isSemaCorrect = true;
                            }
                        } else {
                            if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                            isSynCorrect = false;
                        }
                        sql.delete(0, sql.length());
                    } else {
                        if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                        isSynCorrect = false;
                    }
                }//end of drop table
                /*
                 * drop index 语义错误种类
                 * 1 该index不存在
                 * 2 该index是主键
                 */
                else if (currentToken.tag == Tag.INDEX) {//drop index
                    currentToken = lexer.scan();
                    sql.append(" ");
                    sql.append(currentToken);
                    if (currentToken.tag == Tag.ID) {//drop index a
                        String tmpIndexName = currentToken.toString();

                        currentToken = lexer.scan();
                        sql.append(" ");
                        sql.append(currentToken);
                        if (currentToken.tag == Tag.ON) { // drop index a on
                            currentToken = lexer.scan();
                            sql.append(" ");
                            sql.append(currentToken);

                            String tmpTableName = currentToken.toString(); // drop index a on b
                            currentToken = lexer.scan();
                            sql.append(" ");
                            sql.append(currentToken);
                            if (!tmpTableName.equals("") && currentToken.toString().equals(";")) {//drop index a on b;
                                /*
                                 * 执行drop index 操作
                                 * */
                                if (isSemaCorrect) {
                                    API.regionSQL(tmpTableName, sql.toString(), true);
                                } else {
                                    System.out.print(semaErrMsg);
                                    System.out.println("drop index " + tmpIndexName + " failed");
                                    isSemaCorrect = true;
                                }
                                sql.delete(0,sql.length());
                            } else {
                                if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                                isSynCorrect = false;
                            }
                        } else {
                            if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                            isSynCorrect = false;
                        }
                    } else {
                        if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                        isSynCorrect = false;
                    }
                } else {
                    if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                    isSynCorrect = false;
                }
            }//end of drop
            /*
             * insert into 语义错误种类
             * 1 table 不存在
             * 2 插入的tuple数量不对
             * 3 插入的tuple类型（及长度）不对
             * 4 unique key 有重复插入（未实现,需要record manager配合）
             */
            else if (currentToken.tag == Tag.INSERT) {
                currentToken = lexer.scan();
                sql.append(" ");
                sql.append(currentToken);
                if (currentToken.tag == Tag.INTO) {//insert into
                    currentToken = lexer.scan();
                    sql.append(" ");
                    sql.append(currentToken);
                    if (currentToken.tag == Tag.ID) {//insert into 表名
                        String tmpTableName = currentToken.toString();

                        currentToken = lexer.scan();
                        sql.append(" ");
                        sql.append(currentToken);
                        if (currentToken.tag == Tag.VALUES) {
                            currentToken = lexer.scan();
                            sql.append(" ");
                            sql.append(currentToken);
                            String str;
                            do{
                                str = lexer.readLine().trim();
                                sql.append(" ");
                                sql.append(str);
                            } while(!str.endsWith(";"));

                            if (isSynCorrect) {
                                /*
                                 * 执行insert 操作
                                 * */
                                if (isSemaCorrect) {
                                    API.regionSQL(tmpTableName, sql.toString(),true);
                                } else {
                                    System.out.print(semaErrMsg);
                                    System.out.println(", insert failed");
                                    isSemaCorrect = true;
                                }
                                sql.delete(0, sql.length());
                            }
                        } else {
                            if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                            isSynCorrect = false;
                        }
                    } else {
                        if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                        isSynCorrect = false;
                    }
                } else {
                    if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                    isSynCorrect = false;
                }
            }//end of insert
            /*
             * delete 语义错误种类
             * 1 table 不存在
             * 2 where 条件有误 见parsingCondition
             */
            else if (currentToken.tag == Tag.DELETE) {
                currentToken = lexer.scan();
                sql.append(" ");
                sql.append(currentToken);
                if (currentToken.tag == Tag.FROM) {//delete from
                    currentToken = lexer.scan();
                    sql.append(" ");
                    sql.append(currentToken);
                    if (currentToken.tag == Tag.ID) {
                        String tmpTableName = currentToken.toString();

                        currentToken = lexer.scan();
                        sql.append(" ");
                        sql.append(currentToken);
                        if (currentToken.tag == Tag.WHERE) {//delete from 表名 where 条件；
                            // 添加搜索条件
                            String str;
                            do{
                                str = lexer.readLine().trim();
                                sql.append(" ");
                                sql.append(str);
                            } while(!str.endsWith(";"));

                            /*
                             * 执行delete 操作
                             * */
                            if (isSemaCorrect && isSynCorrect) {
                                /*
                                 * 执行delete where 操作
                                 */
                                API.regionSQL(tmpTableName, sql.toString(), true);
                            } else if (isSynCorrect) {
                                System.out.println(semaErrMsg + ", delete tuples failed");
                                isSemaCorrect = true;
                            }
                            sql.delete(0, sql.length());
                        } else if (currentToken.toString().equals(";")) {//delete from 表名；
                            if (isSemaCorrect) {
                                /*
                                 * 执行delete操作
                                 */
                                API.regionSQL(tmpTableName, sql.toString(), true);
                            } else {
                                System.out.println(semaErrMsg + ", delete tuples failed");
                                isSemaCorrect = true;
                            }
                            sql.delete(0, sql.length());
                        } else {
                            if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                            isSynCorrect = false;
                        }
                    } else {
                        if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                        isSynCorrect = false;
                    }
                } else {
                    if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                    isSynCorrect = false;
                }
            }
            /*
             * select 语义错误种类
             * 1 table 不存在
             * 2 where 条件有误  见parsingCondition
             */
            else if (currentToken.tag == Tag.SELECT) {
                ParsingProjection(lexer);
                if (isSynCorrect && currentToken.tag == Tag.FROM) {//select * from
                    currentToken = lexer.scan();
                    sql.append(" ");
                    sql.append(currentToken);
                    if (currentToken.tag == Tag.ID) {
                        String tmpTableName = currentToken.toString();
                        currentToken = lexer.scan();
                        sql.append(" ");
                        sql.append(currentToken);

                        if (isSynCorrect && currentToken.tag == Tag.WHERE) {//select * from 表名 where 条件；
                            String str;
                            do{
                                str = lexer.readLine().trim();
                                sql.append(" ");
                                sql.append(str);
                            } while(!str.endsWith(";"));

                            if (isSemaCorrect && isSynCorrect) {
                                /*
                                 * 执行select where 操作
                                 * */
                                API.regionSQL(tmpTableName, sql.toString(), false);
                            } else if (isSynCorrect) {
                                System.out.println(semaErrMsg + ", select tuples failed");
                                isSemaCorrect = true;
                            }
                            sql.delete(0, sql.length());
                        } else if (currentToken.toString().equals(";")) {//select * from 表名；
                            if (isSemaCorrect) {
                                /*执行select 操作*/
                                API.regionSQL(tmpTableName, sql.toString(), true);
                            } else {
                                System.out.println(semaErrMsg + ", select tuples failed");
                                isSemaCorrect = true;
                            }
                            sql.delete(0, sql.length());
                        } else if (currentToken.tag == Tag.ORDER) {
                            String str;
                            do{
                                str = lexer.readLine().trim();
                                sql.append(" ");
                                sql.append(str);
                            } while(!str.endsWith(";"));

                            if (isSemaCorrect) {
                                /*
                                 * 执行select order操作
                                 */
                                API.regionSQL(tmpTableName, sql.toString(), true);
                            } else {
                                System.out.println(semaErrMsg + ", select tuples failed");
                                isSemaCorrect = true;
                            }
                            sql.delete(0, sql.length());
                        } else {
                            if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                            isSynCorrect = false;
                        }
                    } else {
                        if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                        isSynCorrect = false;
                    }
                } else {
                    if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                    isSynCorrect = false;
                }
            } else if (currentToken.tag == Tag.SHOW) {
                currentToken = lexer.scan();
                if (currentToken.toString().equals("tables")) {
                    currentToken = lexer.scan();
                    if (currentToken.toString().equals(";")) {
                        API.showTables();
                    } else {
                        if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                        isSynCorrect = false;
                    }
                    sql.delete(0, sql.length());
                } else {
                    if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                    isSynCorrect = false;
                }
            } else {
                if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                isSynCorrect = false;
            }
        } //end of while
    }

    //对project部分语句进行解析
    private static void ParsingProjection(Lexer lexer) throws IOException {
        currentToken = lexer.scan();
        sql.append(" ");
        sql.append(currentToken);
        if (currentToken.toString().equals("*")) {
            currentToken = lexer.scan();
            sql.append(" ");
            sql.append(currentToken);
        } else {
            while (currentToken.tag != Tag.FROM) {
                if (currentToken.tag == Tag.ID) {
                    currentToken = lexer.scan();
                    sql.append(" ");
                    sql.append(currentToken);
                    if (currentToken.toString().equals(",")) {
                        currentToken = lexer.scan();
                        sql.append(" ");
                        sql.append(currentToken);
                    } else if (currentToken.tag == Tag.FROM) return;
                    else {
                        if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                        isSynCorrect = false;
                        break;
                    }
                } else {
                    if (isSynCorrect) synErrMsg = "Synthetic error near: " + currentToken.toString();
                    isSynCorrect = false;
                    break;
                }
            }
        }
    }
}
