namespace java cn.edu.zju.minisql.distributed.server.master.service.thrift

typedef i16 short
typedef i32 int
typedef i64 long

struct Attribute{
    1: string name,
    2: int type,
}

service MasterService{
    bool createTable(
    1:string tableName,
    2:list<Attribute> attributes,
    3:int primaryKeyIndex,
    )

    bool dropTable(
    1:string tableName
    )
}