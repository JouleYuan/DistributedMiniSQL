package cn.edu.zju.minisql.distributed.server.region.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import cn.edu.zju.minisql.distributed.server.region.lib.catalogmanager.CatalogManager;
import cn.edu.zju.minisql.distributed.server.region.lib.catalogmanager.Attribute;
import cn.edu.zju.minisql.distributed.server.region.lib.catalogmanager.Index;
import cn.edu.zju.minisql.distributed.server.region.lib.catalogmanager.Table;
import cn.edu.zju.minisql.distributed.server.region.lib.recordmanager.ConditionNode;
import cn.edu.zju.minisql.distributed.server.region.lib.recordmanager.Tuple;
import cn.edu.zju.minisql.distributed.server.region.lib.lexer.Comparison;
import cn.edu.zju.minisql.distributed.server.region.lib.lexer.Lexer;
import cn.edu.zju.minisql.distributed.server.region.lib.lexer.Tag;
import cn.edu.zju.minisql.distributed.server.region.lib.lexer.Token;

/*
 * 表名只能是字母开头的东西
 */
public class Interpreter {
	private static Token theToken;//记录当前token
	private static boolean isSynCorrect=true;
	private static boolean isSemaCorrect=true;
	private static String synErrMsg;
	private static String semaErrMsg;

	public static void resetFlags(){
		isSemaCorrect = true;
		isSynCorrect = true;
	}


	public static void main(String []args){
		System.out.println("Welcome to MiniSql.Please enter the command");

		try{
			API.Initialize();
			BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
			Parsing(reader);
		}
		catch(Exception e){
			System.out.println("mini-sql.Interpreter error:"+e.getMessage());
			e.printStackTrace();
		}
	}
	//逐条语句进行解析
	public static void Parsing(BufferedReader reader)throws IOException {
		Lexer lexer = new Lexer(reader);
		while(!lexer.getReaderState()){
			//System.out.print("*");
			if(!isSynCorrect){   //调试通过后再加
				if(theToken.toString().equals(";")){
					System.out.println(synErrMsg);
					isSemaCorrect=true;
					isSynCorrect=true;
					continue;
				}
			}
			theToken = lexer.scan();
			if(theToken.tag==Tag.EXECFILE){
				theToken =lexer.scan();
				// System.out.println(thetoken.toString());
				File file=new File(theToken.toString()+".txt");
				theToken =lexer.scan();
				if(theToken.toString().equals(";")){

					if(file.exists()){
						BufferedReader reader2=new BufferedReader(new FileReader(file));
						Parsing(reader2);
						isSynCorrect=true;//为了奇怪的bug
					}
					else{
						synErrMsg="The file "+file.getName()+" doesn't exist";
						isSynCorrect=false;
					}

				}
				else{
					if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
				}
			}
			else if(theToken.tag==Tag.QUIT){
				theToken =lexer.scan();
				if(theToken.toString().equals(";")){
					System.out.println("Quit the MiniSql. See you next time!");
					API.close();

					reader.close();
					//System.exit(0);
					return;
				}
				else{
					if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
				}

			}
			else if(theToken.tag==Tag.CREATE){
				theToken =lexer.scan();
				/*
				 * create table 语义错误种类
				 * 1 table name已存在
				 * 2 primary key不存在
				 * 3 重复attribute属性
				 * 4 char(n) 的n越界
				 */
				if(theToken.tag==Tag.TABLE){
					theToken =lexer.scan();
					if(theToken.tag==Tag.ID){	//create table 表名
						String tmpTableName= theToken.toString();
						Vector<Attribute>tmpAttributes=new Vector<Attribute>();
						String tmpPrimaryKey=null;
						if(CatalogManager.isTableExist(tmpTableName)){
							semaErrMsg="The table "+tmpTableName+" already exists";
							isSemaCorrect=false;
						}
						theToken =lexer.scan();
						if(theToken.toString().equals("(")){//create table 表名(
							theToken =lexer.scan();
							while(!theToken.toString().equals(")")&&!theToken.toString().equals(";")){
								if(theToken.tag==Tag.ID){ //create table 表名 ( 属性名
									String tmpAttriName= theToken.toString();
									String tmpType;
									int tmpLength;
									boolean tmpIsU=false;
									if(CatalogManager.isAttributeExist(tmpAttributes, tmpAttriName)){
										semaErrMsg="Duplicated attribute names "+tmpAttriName;
										isSemaCorrect=false;
									}
									theToken =lexer.scan();
									if(theToken.tag==Tag.TYPE){//create table 表名 ( 属性名 类型名
										tmpType= theToken.toString();
										if(tmpType.equals("char")){//针对char(n)类型做特殊处理
											theToken =lexer.scan();
											if(theToken.toString().equals("(")){
												theToken =lexer.scan();
												if(theToken.tag==Tag.INTNUM){
													tmpLength = Integer.parseInt(theToken.toString());
													if(tmpLength<1||tmpLength>255){
														semaErrMsg="The length of char should be 1<=n<=255";
														isSemaCorrect=false;
													}
													theToken =lexer.scan();
													if(theToken.toString().equals(")"));
													else{
														if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
														break;
													}
												}
												else{
													if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
													break;
												}
											}
											else{
												if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
												break;
											}

										}
										else{//不是char
											tmpLength=4;

										}
										theToken =lexer.scan();
										if(theToken.tag==Tag.UNIQUE){
											tmpIsU=true;
											theToken =lexer.scan();
										}
										if(theToken.toString().equals(",")){
											tmpAttributes.addElement(new Attribute(tmpAttriName,tmpType,tmpLength,tmpIsU));
										}
										else if(theToken.toString().equals(")")){
											tmpAttributes.addElement(new Attribute(tmpAttriName,tmpType,tmpLength,tmpIsU));
											break;
										}
										else{
											if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
											break;
										}
									}
									else{
										if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
										break;
									}
								}

								else if(theToken.tag==Tag.PRIMARY){
									theToken =lexer.scan();
									if(theToken.tag==Tag.KEY){
										theToken =lexer.scan();
										if(theToken.toString().equals("(")){
											theToken =lexer.scan();
											if(theToken.tag==Tag.ID){
												tmpPrimaryKey= theToken.toString();
												theToken =lexer.scan();
												if(theToken.toString().equals(")")){
													if(!CatalogManager.isAttributeExist(tmpAttributes, tmpPrimaryKey)){
														semaErrMsg="The attribute "+tmpPrimaryKey+" doesn't exist";isSemaCorrect=false;
													}
												}
												else{

													if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
													break;
												}
											}
											else{
												if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
												break;
											}
										}
										else{
											if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
											break;
										}
									}
									else{

										if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
										break;
									}
								}

								else{
									if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
									break;
								}
								theToken =lexer.scan();
							}//end of while")"
							theToken =lexer.scan();

							if(isSynCorrect&& theToken.toString().equals(";")){
								/*
								 * 执行create table 操作
								 * */
								if(tmpPrimaryKey==null){
									synErrMsg="Synthetic error: no primary key defined";isSynCorrect=false;
									continue;
								}
								if(isSemaCorrect){
									if(API.createTable(tmpTableName,new Table(tmpTableName,tmpAttributes,tmpPrimaryKey)))
										System.out.println("create table "+tmpTableName+" succeeded");
									else
										System.out.println("Error: create table failed");

								}
								else{
									System.out.print(semaErrMsg);
									System.out.println(", create table "+tmpTableName+" failed");
									isSemaCorrect=true;
								}
							}
							else{
								//System.out.println("stop here"+isSynCorrect);
								if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
							}
							continue;
						}
					}
					else{
						if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
						continue;
					}
				}
				/*
				 * create index 语义错误种类
				 * 1 index name已存在
				 * 2 table name 不存在
				 * 3 attribute不存在
				 * 4 attribute已经是索引
				 * 5 attribute 不是unique
				 */
				else if(theToken.tag==Tag.INDEX){
					String tmpIndexName,tmpTableName,tmpAttriName;
					theToken =lexer.scan();
					if(theToken.tag==Tag.ID){//create index a
						tmpIndexName= theToken.toString();
						if(CatalogManager.isIndexExist(tmpIndexName)){
							semaErrMsg="The index "+tmpIndexName+" already exist";
							isSemaCorrect=false;
						}
						theToken =lexer.scan();
						if(theToken.tag==Tag.ON){//create index a on
							theToken =lexer.scan();
							if(theToken.tag==Tag.ID){//create index a on b
								tmpTableName= theToken.toString();
								if(!CatalogManager.isTableExist(tmpTableName)){
									semaErrMsg="The table "+tmpTableName+" doesn't exist";
									isSemaCorrect=false;
								}
								theToken =lexer.scan();
								if(theToken.toString().equals("(")){
									theToken =lexer.scan();
									if(theToken.tag==Tag.ID){
										tmpAttriName= theToken.toString();
										if(isSemaCorrect&&!CatalogManager.isAttributeExist(tmpTableName, tmpAttriName)){
											semaErrMsg="The attribute "+tmpAttriName+" doesn't exist on "+tmpTableName;
											isSemaCorrect=false;
										}
										else if(isSemaCorrect&&!CatalogManager.inUniqueKey(tmpTableName, tmpAttriName)){
											semaErrMsg="The attribute "+tmpAttriName+" on "+tmpTableName+" is not unique";
											isSemaCorrect=false;
										}
										else if(isSemaCorrect&&CatalogManager.isIndexKey(tmpTableName, tmpAttriName)){
											semaErrMsg="The attribute "+tmpAttriName+" on "+tmpTableName+" is already an index";
											isSemaCorrect=false;
										}
										theToken =lexer.scan();
										if(theToken.toString().equals(")")&&lexer.scan().toString().equals(";")){//create index a on b;
											/*
											 * 执行create index操作
											 * */
											if(isSemaCorrect){
												if(API.createIndex(new Index(tmpIndexName,tmpTableName,tmpAttriName)))
													System.out.println("create index "+tmpIndexName+" on "+tmpTableName+" ("+tmpAttriName+") succeeded.");
												else
													System.out.println("Error:create index failed");
											}
											else{
												System.out.print(semaErrMsg);
												System.out.println(", create index failed");
												isSemaCorrect=true;
											}
										}
										else{
											if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
											continue;
										}
									}
									else{
										if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
										continue;
									}

								}
								else{
									if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
									continue;
								}
							}
							else{
								if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
								continue;
							}
						}
						else{
							if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
							continue;
						}
					}
					else{
						if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
						continue;
					}
				}
				else{
					if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
					continue;
				}
			}//end of create
			else if(theToken.tag==Tag.DROP){
				theToken =lexer.scan();
				/*
				 * drop table 语义错误种类  1该table不存在
				 */
				if(theToken.tag==Tag.TABLE){
					String tmpTableName;
					theToken =lexer.scan();
					if(theToken.tag==Tag.ID){//drop table a
						tmpTableName= theToken.toString();
						if(!CatalogManager.isTableExist(tmpTableName)){
							semaErrMsg="The table "+tmpTableName+" doesn't exist, ";
							isSemaCorrect=false;
						}
						theToken =lexer.scan();
						if(theToken.toString().equals(";")){//drop table a ;
							/*
							 * 执行drop table
							 * 操作*/
							if(isSemaCorrect){
								API.dropTable(tmpTableName);
								System.out.println("drop table "+tmpTableName+" succeeded");
							}
							else{
								System.out.print(semaErrMsg);
								System.out.println("drop table "+tmpTableName+" failed");
								isSemaCorrect=true;
							}
						}
						else{
							if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
						}

					}
					else{
						if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
					}
				}//end of drop table
				/*
				 * drop index 语义错误种类
				 * 1该index不存在
				 * 2 该index是主键
				 */
				else if(theToken.tag==Tag.INDEX){//drop index
					theToken =lexer.scan();
					if(theToken.tag==Tag.ID){//drop index a
						String tmpIndexName= theToken.toString();
						if(!CatalogManager.isIndexExist(tmpIndexName)){
							semaErrMsg="The index "+tmpIndexName+" doesn't exist, ";
							isSemaCorrect=false;
						}
						if(tmpIndexName.endsWith("_prikey")){
							semaErrMsg="The index "+tmpIndexName+" is a primary key, ";
							isSemaCorrect=false;
						}
						theToken =lexer.scan();
						if(theToken.toString().equals(";")){//drop index a ;
							/*
							 * 执行drop index 操作
							 * */
							if(isSemaCorrect){
								if(API.dropIndex(tmpIndexName))
									System.out.println("drop index "+tmpIndexName+" succeeded.");
							}
							else{
								System.out.print(semaErrMsg);
								System.out.println("drop index "+tmpIndexName+" failed");
								isSemaCorrect=true;
							}
							continue;
						}
						else{
							if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
							continue;
						}
					}
					else{
						if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
						continue;
					}
				}
				else{
					if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
					continue;
				}
			}//end of drop
			/*
			 * insert into 语义错误种类
			 * 1 table 不存在
			 * 2 插入的tuple数量不对
			 * 3 插入的tuple类型（及长度）不对
			 * 4 unique key 有重复插入（未实现,需要record manager配合）
			 */
			else if(theToken.tag==Tag.INSERT){
				theToken =lexer.scan();
				if(theToken.tag==Tag.INTO){//insert into
					theToken =lexer.scan();
					if(theToken.tag==Tag.ID){//insert into 表名
						String tmpTableName= theToken.toString();
						Vector<String>units= new Vector<>();
						if(!CatalogManager.isTableExist(tmpTableName)){
							semaErrMsg="The table "+tmpTableName+" doesn't exist";
							isSemaCorrect=false;
						}

						theToken =lexer.scan();
						if(theToken.tag==Tag.VALUES){
							theToken =lexer.scan();
							if(theToken.toString().equals("(")){
								theToken =lexer.scan();
								String tmpValue ;
								int i=0;//记录unit的index
								while(!theToken.toString().equals(")")){	//insert into 表名 values()
									// System.out.println(thetoken.tag);
									if(isSemaCorrect&&i>=CatalogManager.getTableAttriNum(tmpTableName)){
										isSemaCorrect=false;
										semaErrMsg="The number of values is larger than that of attributes";
									}
									else if(isSemaCorrect){

										tmpValue= theToken.toString();
										int tmpLength=CatalogManager.getLength(tmpTableName, i);
										String tmpType=CatalogManager.getType(tmpTableName, i);
										String tmpAttrName=CatalogManager.getAttriName(tmpTableName, i);

										if(CatalogManager.inUniqueKey(tmpTableName, tmpAttrName)){//对于unique key的判别
											ConditionNode tmpCondition=new ConditionNode(tmpAttrName, "=", theToken.toString());

											if(isSemaCorrect&&API.selectTuples(tmpTableName,null,tmpCondition).size()!=0){
												isSemaCorrect=false;
												semaErrMsg="The value "+ theToken.toString()+" already exists in the unique attrubute "+tmpAttrName;
											}
										}
										if(theToken.tag==Tag.STR){//字符类型

											//if(tmpType.equals("char"))tmpLength/=2;
											if(!tmpType.equals("char")
													||tmpLength<tmpValue.getBytes().length){
												isSemaCorrect=false;
												semaErrMsg="The type of value +"+tmpValue+" should be "+tmpType+"("+tmpLength+"), not char("+tmpValue.getBytes().length+")";
											}
											i++;
											units.add(tmpValue);
										}
										else if(theToken.tag==Tag.INTNUM){//整型

											if(!tmpType.toString().equals("int")
													&&!tmpType.equals("float")){
												isSemaCorrect=false;
												semaErrMsg="The type of value +"+tmpValue+" should be "+tmpType+"("+tmpLength+"), not be int";
											}
											i++;
											units.add(tmpValue);
										}
										else if(theToken.tag==Tag.FLOATNUM){//浮点型

											if(!CatalogManager.getType(tmpTableName, i++).equals("float")){
												isSemaCorrect=false;
												semaErrMsg="The type of value +"+tmpValue+" should be "+tmpType+"("+tmpLength+"), not float";
											}
											units.add(tmpValue);
										}
										else{
											if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
											break;
										}
									}
									theToken =lexer.scan();
									if(theToken.toString().equals(",")) theToken =lexer.scan();
									else if(theToken.toString().equals(")"));
									else{
										if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
										break;
									}
								}
								if(isSemaCorrect && i<CatalogManager.getTableAttriNum(tmpTableName)){
									isSemaCorrect=false;
									semaErrMsg="The number of values is smaller than that of attributes";
								}
								theToken =lexer.scan();
								if(isSynCorrect&& theToken.toString().equals(";")){
									/*
									 * 执行insert 操作
									 * */


									if(isSemaCorrect){
										if(API.insertTuples(tmpTableName,new Tuple(units)))
											System.out.println("insert into "+tmpTableName+" succeeded.");
										else
											System.out.println("Error:insert into "+tmpTableName+" failed.");
									}
									else{
										System.out.print(semaErrMsg);
										System.out.println(", insert failed");
										isSemaCorrect=true;
									}

								}
								else{
									if(isSynCorrect)
										synErrMsg="Synthetic error near: " + theToken.toString();
									isSynCorrect=false;
								}
							}
							else{
								if(isSynCorrect)
									synErrMsg="Synthetic error near: " + theToken.toString();
								isSynCorrect=false;
							}
						}
						else{
							if(isSynCorrect)
								synErrMsg = "Synthetic error near: " + theToken.toString();
							isSynCorrect = false;
						}
					}
					else{
						if(isSynCorrect)
							synErrMsg = "Synthetic error near: " + theToken.toString();
						isSynCorrect = false;
					}
				}
				else{
					if(isSynCorrect)
						synErrMsg = "Synthetic error near: " + theToken.toString();
					isSynCorrect = false;
				}
			}
			//end of insert
			/*
			 * delete 语义错误种类
			 * 1 table 不存在
			 * 2 where 条件有误 见parsingCondition
			 */
			else if(theToken.tag==Tag.DELETE){
				theToken =lexer.scan();
				if(theToken.tag==Tag.FROM){//delete from
					theToken =lexer.scan();
					if(theToken.tag==Tag.ID){
						String tmpTableName= theToken.toString();
						if(!CatalogManager.isTableExist(tmpTableName)){
							semaErrMsg="The table "+tmpTableName+" doesn't exist";
							isSemaCorrect=false;
						}
						theToken =lexer.scan();
						if(theToken.tag==Tag.WHERE){//delete from 表名 where 条件；
							// 添加搜索条件
							ConditionNode tmpConditionNodes=ParsingCondition(lexer,tmpTableName,";");
							if(theToken.toString().equals(";")){//delete from 表名；

								if(isSemaCorrect&&isSynCorrect){
									/*
									 * 执行delete where 操作
									 */
									int deleteNum=API.deleteTuples(tmpTableName, tmpConditionNodes);
									System.out.println("delete "+deleteNum+ " tuples from table "+tmpTableName);
									//System.out.println("delete succeeded");
								}
								else if(!isSynCorrect){
									continue;
								}
								else{
									System.out.println(semaErrMsg+", delete tuples failed");
									isSemaCorrect=true;
								}
							}
							else{
								if(isSynCorrect)
									synErrMsg="Synthetic error near: "+ theToken.toString();
								isSynCorrect=false;
							}
						}
						else if(theToken.toString().equals(";")){//delete from 表名；

							if(isSemaCorrect){
								/*
								 * 执行delete操作
								 */
								int deleteNum=API.deleteTuples(tmpTableName, null);

								System.out.println("delete "+deleteNum+ " tuples from table "+tmpTableName);

							}
							else{
								System.out.println(semaErrMsg+", delete tuples failed");
								isSemaCorrect=true;
							}
						}
						else{

							if(isSynCorrect)
								synErrMsg="Synthetic error near: "+ theToken.toString();
							isSynCorrect=false;
						}
					}
					else{
						if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
						continue;
					}
				}
				else{
					if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
					continue;
				}
			}
			/*
			 * select 语义错误种类
			 * 1 table 不存在
			 * 2 where 条件有误  见parsingCondition
			 */
			else if(theToken.tag==Tag.SELECT){
				Vector<String>tmpAttriNames=ParsingProjection(lexer);
				if(isSynCorrect&& theToken.tag==Tag.FROM){//select * from
					theToken =lexer.scan();
					if(theToken.tag==Tag.ID){
						String tmpTableName= theToken.toString();
						String tmpTableName2="";
						boolean joinflag=false;
						if(isSemaCorrect&&!CatalogManager.isTableExist(tmpTableName)){
							semaErrMsg="The table "+tmpTableName+" doesn't exist";
							isSemaCorrect=false;
						}
						if(tmpAttriNames!=null)//对于投影的属性进行判断
							for(int i=0;i<tmpAttriNames.size();i++){
								if(isSemaCorrect&&!CatalogManager.isAttributeExist(tmpTableName, tmpAttriNames.get(i))){
									semaErrMsg="The attribute "+tmpAttriNames.get(i)+" doesn't exist";
									isSemaCorrect=false;
								}
							}
						theToken =lexer.scan();
						//如果有join
						if(theToken.tag==Tag.JOIN|| theToken.toString().equals(",")){
							joinflag=true;
							theToken =lexer.scan();
							if(theToken.tag==Tag.ID){

								tmpTableName2= theToken.toString();
								if(isSemaCorrect&&!CatalogManager.isTableExist(tmpTableName2)){
									semaErrMsg="The table "+tmpTableName2+" doesn't exist";
									isSemaCorrect=false;
								}
								theToken =lexer.scan();
							}
							else{
								if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
								continue;
							}
						}
						if(isSynCorrect&& theToken.tag==Tag.WHERE){//select * from 表名 where 条件；
							/* 添加搜索条件*/

							if(joinflag){
								theToken =lexer.scan();
								String[]tmpName1=new String[2],tmpName2=new String[2];

								if(theToken.tag==Tag.ID){
									tmpName1= theToken.toString().split("\\.");
									if(isSemaCorrect&&!CatalogManager.isTableExist(tmpName1[0])){
										semaErrMsg="The table "+tmpName1[0]+" doesn't exist";
										isSemaCorrect=false;
									}
									if(isSemaCorrect&&!CatalogManager.isAttributeExist(tmpTableName, tmpName1[1])){
										semaErrMsg="The attribute "+tmpName1[1]+" doesn't exist";
										isSemaCorrect=false;
									}
									theToken =lexer.scan();
									//if(theToken.toString().equals("=")){
									if(theToken.tag==Tag.OP){
										theToken =lexer.scan();
										if(theToken.tag==Tag.ID){
											tmpName2= theToken.toString().split("\\.");
											if(isSemaCorrect&&!CatalogManager.isTableExist(tmpName2[0])){
												semaErrMsg="The table "+tmpName2[0]+" doesn't exist";
												isSemaCorrect=false;
											}
											if(isSemaCorrect&&!CatalogManager.isAttributeExist(tmpTableName, tmpName2[1])){
												semaErrMsg="The attribute "+tmpName2[1]+" doesn't exist";
												isSemaCorrect=false;
											}
											theToken =lexer.scan();
											if(theToken.toString().equals(";")){
												if(isSemaCorrect&&isSynCorrect){
													/*
													 * 执行select join 操作*/
													for(int i=0;i<CatalogManager.getTableAttriNum(tmpTableName);i++){ //输出属性名
														System.out.print("\t"+CatalogManager.getAttriName(tmpTableName, i));
													}
													for(int i=0;i<CatalogManager.getTableAttriNum(tmpTableName2);i++){ //输出属性名
														System.out.print("\t"+CatalogManager.getAttriName(tmpTableName2, i));
													}
													System.out.println();
													Vector<Tuple> selectedTuples=API.join(tmpName1[0],tmpName1[1],tmpName2[0],tmpName2[1]);
													for(int i=0;i<selectedTuples.size();i++){
														System.out.println(selectedTuples.get(i).getString());
													}

												}
												else if(!isSynCorrect) continue;
												else{
													System.out.println(semaErrMsg+", select tuples failed");
													isSemaCorrect=true;
												}
											}
											else{
												if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
												continue;
											}

										}
										else{
											if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
											continue;
										}
									}
									else{
										if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
										continue;
									}
								}
								else{
									if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
									continue;
								}

								continue;
							}
							ConditionNode tmpConditionNode=ParsingCondition(lexer,tmpTableName,";");
							if(theToken.toString().equals(";")){//select from 表名；
								if(isSemaCorrect&&isSynCorrect){
									/*
									 * 执行select where 操作*/

									showSelectRes(tmpTableName,tmpAttriNames, tmpConditionNode,null,false);

								}
								else if(!isSynCorrect) continue;
								else{
									System.out.println(semaErrMsg+", select tuples failed");
									isSemaCorrect=true;
								}

							}
							else if(isSynCorrect&& theToken.tag==Tag.ORDER){
								theToken =lexer.scan();
								if(theToken.tag==Tag.BY){
									theToken =lexer.scan();
									if(theToken.tag==Tag.ID){
										String tmpOrderAttriName= theToken.toString();
										if(isSemaCorrect&&!CatalogManager.isAttributeExist(tmpTableName, tmpOrderAttriName)){
											semaErrMsg="The attribute "+tmpOrderAttriName+" doesn't exist";
											isSemaCorrect=false;
										}
										theToken =lexer.scan();
										if(theToken.toString().equals(";")|| theToken.tag==Tag.ASC|| theToken.tag==Tag.DESC){
											boolean order;
											if(theToken.toString().equals(";")) order=true;
											else {
												order= theToken.tag==Tag.ASC?true:false;
												theToken =lexer.scan();
												if(isSynCorrect&&!theToken.toString().equals(";")){
													synErrMsg="Synthetic error near: "+ theToken.toString();
													isSynCorrect=false;
													continue;
												}
											}
											if(isSemaCorrect){
												/*执行select where order操作*/
												showSelectRes(tmpTableName,tmpAttriNames, tmpConditionNode,tmpOrderAttriName,order);

											}
											else{
												System.out.println(semaErrMsg+", select tuples failed");
												isSemaCorrect=true;
											}
										}
										else{
											if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
											continue;
										}

									} else{
										if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
										continue;
									}
								}
								else{
									if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
									continue;
								}
							}
							else{
								if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
								continue;
							}
						}
						else if(theToken.toString().equals(";")){//select * from 表名；
							if(isSemaCorrect){
								/*执行select 操作*/
								showSelectRes(tmpTableName,tmpAttriNames, null,null,false);
							}
							else{
								System.out.println(semaErrMsg+", select tuples failed");
								isSemaCorrect=true;
							}
						}
						else if(theToken.tag==Tag.ORDER){
							theToken =lexer.scan();
							if(theToken.tag==Tag.BY){
								theToken =lexer.scan();
								if(theToken.tag==Tag.ID){
									String tmpOrderAttriName= theToken.toString();
									if(isSemaCorrect&&!CatalogManager.isAttributeExist(tmpTableName, tmpOrderAttriName)){
										semaErrMsg="The attribute "+tmpOrderAttriName+" doesn't exist";
										isSemaCorrect=false;
									}
									theToken =lexer.scan();
									if(theToken.toString().equals(";")|| theToken.tag==Tag.ASC|| theToken.tag==Tag.DESC){
										boolean order;
										if(theToken.toString().equals(";")) order=true;
										else {
											order= theToken.tag==Tag.ASC?true:false;
											theToken =lexer.scan();
											if(isSynCorrect&&!theToken.toString().equals(";")){
												synErrMsg="Synthetic error near: "+ theToken.toString();
												isSynCorrect=false;
												continue;
											}
										}
										if(isSemaCorrect){
											/*
											 * 执行select order操作
											 */
											showSelectRes(tmpTableName,tmpAttriNames, null,tmpOrderAttriName,order);

										}
										else{
											System.out.println(semaErrMsg+", select tuples failed");
											isSemaCorrect=true;
										}
									}
									else{
										if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
										continue;
									}

								} else{
									if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
									continue;
								}
							}
							else{
								if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
							}
						}
						else{

							if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
						}
					}
					else{
						if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
					}
				}
				else{
					if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
				}


			}
			else if(theToken.tag==Tag.SHOW){
				theToken =lexer.scan();
				if(theToken.toString().equals("tables")){
					theToken =lexer.scan();
					if(theToken.toString().equals(";")){
						API.showTableCatalog();
					} else{
						if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
					}

				}
				else if(theToken.toString().equals("indexes")){
					theToken =lexer.scan();
					if(theToken.toString().equals(";")){
						API.showIndexCatalog();
					} else{
						if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
					}
				}
				else if(theToken.toString().equals("catalog")){
					theToken =lexer.scan();
					if(theToken.toString().equals(";")){
						API.showCatalog();
					} else{
						if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
					}
				}
				else{
					if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
				}
			}
			else{
				if(isSynCorrect)
					synErrMsg = "Synthetic error near: "+ theToken.toString();
				isSynCorrect = false;
			}

		} //end of while


	}

	//显示 选择返回结果
	private static void showSelectRes(String tmpTableName, Vector<String> tmpAttriNames, ConditionNode tmpConditionNode, String tmpOrderAttriName, boolean order){
		if(tmpAttriNames==null)
			for(int i=0;i<CatalogManager.getTableAttriNum(tmpTableName);i++){ //输出属性名
				System.out.print("\t"+CatalogManager.getAttriName(tmpTableName, i));
			}
		else
			for(int i=0;i<tmpAttriNames.size();i++)
				System.out.print("\t"+tmpAttriNames.get(i));
		System.out.println();
		Vector<Tuple> selectedTuples;
		if(tmpOrderAttriName==null)
			selectedTuples=API.selectTuples(tmpTableName,tmpAttriNames, tmpConditionNode);
		else{
			selectedTuples=API.selectTuples(tmpTableName,tmpAttriNames, tmpConditionNode,tmpOrderAttriName,order);
		}
		for(int i=0;i<selectedTuples.size();i++){
			System.out.println(selectedTuples.get(i).getString());
		}
		System.out.println("There are "+selectedTuples.size()+" tuples returned");
	}
	//对project部分语句进行解析
	private static Vector<String> ParsingProjection(Lexer lexer) throws IOException{
		Vector<String>tmpAttrNames=new Vector<String>();
		theToken =lexer.scan();
		if(theToken.toString().equals("*")){
			theToken =lexer.scan();
			return null;
		}
		else{
			while(theToken.tag!=Tag.FROM){
				if(theToken.tag==Tag.ID){
					tmpAttrNames.add(theToken.toString());
					theToken =lexer.scan();
					if(theToken.toString().equals(",")){
						theToken =lexer.scan();
					}
					else if(theToken.tag==Tag.FROM);
					else{
						if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
						break;
					}
				}
				else{
					if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
					break;
				}

			}
			return tmpAttrNames;
		}
	}
	//对条件的每个表达式进行解析
	private static ConditionNode ParsingExpression(Lexer lexer, String tmpTableName) throws IOException{
		String tmpAttriName;Comparison op;String tmpValue;
		boolean constantFlag = false;
		if(theToken.tag==Tag.ID){
			tmpAttriName= theToken.toString();
			if(isSemaCorrect&&!CatalogManager.isAttributeExist(tmpTableName, tmpAttriName)){
				isSemaCorrect=false;
				semaErrMsg="The attribute "+tmpAttriName+" doesn't exist";
			}
			theToken =lexer.scan();
			if(theToken.tag==Tag.OP){
				op=Comparison.parseCompar(theToken);
				theToken =lexer.scan();
				tmpValue= theToken.toString();
				if(isSemaCorrect){
					if(theToken.tag==Tag.STR){
						constantFlag=true;
						String tmpType=CatalogManager.getType(tmpTableName, tmpAttriName);
						int tmpLength=CatalogManager.getLength(tmpTableName, tmpAttriName);

						if(!tmpType.equals("char")
								||tmpLength<tmpValue.getBytes().length){
							isSemaCorrect=false;
							semaErrMsg="The type of value +"+tmpValue+" should be "+tmpType+"("+tmpLength+"), not char("+tmpValue.getBytes().length+")";
						}
					}
					else if(theToken.tag==Tag.INTNUM){
						constantFlag=true;
						String tmpType=CatalogManager.getType(tmpTableName, tmpAttriName);
						int tmpLength=CatalogManager.getLength(tmpTableName, tmpAttriName);

						if(!tmpType.toString().equals("int")
								&&!tmpType.equals("float")){
							isSemaCorrect=false;
							semaErrMsg="The type of value +"+tmpValue+" should be "+tmpType+"("+tmpLength+"), not int";
						}
					}
					else if(theToken.tag==Tag.FLOATNUM){
						constantFlag=true;
						String tmpType=CatalogManager.getType(tmpTableName, tmpAttriName);
						int tmpLength=CatalogManager.getLength(tmpTableName, tmpAttriName);

						if(!tmpType.equals("float")){
							isSemaCorrect=false;
							semaErrMsg="The type of value +"+tmpValue+" should be "+tmpType+"("+tmpLength+"), not float";
						}
					}
					else if(theToken.tag==Tag.ID){//属性间比较
						constantFlag=false;
						String tmpType1=CatalogManager.getType(tmpTableName, tmpAttriName);
						String tmpType2=CatalogManager.getType(tmpTableName, tmpValue);
						//支持float或int或char的属性间比较
						if(!tmpType1.equals(tmpType2)){
							isSemaCorrect=false;
							semaErrMsg="The two attributes are in different types and cannot be compared";
						}
					}
					else{
						if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();
						isSynCorrect=false;

					}
					//return new conditionNode(tmpAttriName,op,tmpValue,constantFlag);

				}
				return new ConditionNode(tmpAttriName,op,tmpValue,constantFlag);

			}
			else{
				if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();
				isSynCorrect=false;
			}
		}
		else{
			if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();
			isSynCorrect=false;
		}
		return null;
	}
	//对条件部分字符串进行解析
	private static ConditionNode ParsingCondition(Lexer lexer, String tmpTableName, String endtoken)throws IOException {
		/*
		 * 语义错误种类
		 * 1 属性名不存在
		 * 2 value 格式不对
		 * 3 操作符不对 char只支持 = <>
		 */
		ConditionNode tmpConditionRoot = null;
		ConditionNode tmpExpresstion = null,tmpConjunction;
		theToken =lexer.scan();
		boolean flag=false;//如果第一个式子是带括号的 flag==true 以保证其完整性
		if(theToken.toString().equals("(")){
			tmpConditionRoot=ParsingCondition(lexer,tmpTableName,")");
			flag=true;
		}
		else if(theToken.tag==Tag.ID){
			tmpConditionRoot=ParsingExpression(lexer,tmpTableName);
		}
		else{
			if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();
			isSynCorrect=false;
		}
		if(tmpConditionRoot==null||!isSynCorrect){
			return null;
		}

		theToken =lexer.scan();
		while(!theToken.toString().equals(endtoken)&& theToken.tag!=Tag.ORDER){
			if(theToken.tag==Tag.AND){
				tmpConjunction=new ConditionNode("and");
				theToken = lexer.scan();
				if(theToken.toString().equals("(")){
					tmpExpresstion=ParsingCondition(lexer,tmpTableName,")");
				}
				else if(theToken.tag==Tag.ID){
					tmpExpresstion=ParsingExpression(lexer,tmpTableName);
				}
				else{
					if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();
					isSynCorrect=false;
				}
				if(tmpExpresstion==null){
					return null;
				}
				//建树
				if(tmpConditionRoot.conjunction=="or"&&flag==false){

					tmpConditionRoot=tmpConditionRoot.linkChildNode(tmpConditionRoot.left, tmpConjunction.linkChildNode(tmpConditionRoot.right, tmpExpresstion));

				}

				else{
					tmpConditionRoot=tmpConjunction.linkChildNode(tmpConditionRoot, tmpExpresstion);
					if(flag) flag=false;
				}



			}
			else if(theToken.tag==Tag.OR){
				tmpConjunction=new ConditionNode("or");
				theToken = lexer.scan();
				if(theToken.toString().equals("(")){
					tmpExpresstion=ParsingCondition(lexer,tmpTableName,")");
				}
				else if(theToken.tag==Tag.ID){
					tmpExpresstion=ParsingExpression(lexer,tmpTableName);
				}
				else{
					if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();
					isSynCorrect=false;
				}

				if(tmpExpresstion==null){
					return null;
				}
				//建树
				tmpConditionRoot=tmpConjunction.linkChildNode(tmpConditionRoot, tmpExpresstion);
			}

			else if(theToken.toString().equals(endtoken)|| theToken.tag==Tag.ORDER);
			else{
				if(isSynCorrect)  synErrMsg="Synthetic error near: "+ theToken.toString();isSynCorrect=false;
				break;
			}
			theToken =lexer.scan();

		}
		return tmpConditionRoot;
	}
}