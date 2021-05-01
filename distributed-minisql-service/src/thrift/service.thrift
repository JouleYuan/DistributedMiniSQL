namespace java cn.edu.zju.minisql.distributed.service

typedef i16 short
typedef i32 int
typedef i64 long

enum AttributeType {
    INT = 0,
    DOUBLE = 1,
    STRING = 2,
}

struct Attribute {
    1: string name,
    2: AttributeType type,
}

struct Table {
    1: string name,
    2: list<Attribute> attributes,
    3: int primaryKeyIndex,
    4: list<list<string>> rows,
}

service MasterService {
    list<string> showTables(),
    list<string> createTable(1:Table table),
    bool dropTable(1:string tableName),
    list<string> getRegionServers(1:string tableName),
}