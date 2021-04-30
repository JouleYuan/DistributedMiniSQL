namespace java cn.edu.zju.minisql.distributed.server.master.service.thrift

typedef i16 short
typedef i32 int
typedef i64 long

service MasterService{
    bool createTable(
    1:string tableName,
    2:list<int> attributeTypes,
    3:list<string> attributeNames,
    4:int primaryKey,
    )

    bool dropTable(
    1:string tableName
    )
}