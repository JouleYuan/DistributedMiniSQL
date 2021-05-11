namespace java cn.edu.zju.minisql.distributed.service

typedef i16 short
typedef i32 int
typedef i64 long

enum AttributeType {
    INT = 0, FLOAT = 1, CHAR = 2,
}

struct Attribute {
    1: string name,
    2: AttributeType type,
    3: int length,
    4: bool isUnique,
}

struct Table {
    1: string name,
    2: list<Attribute> attributes,
    3: int primaryKeyIndex,
    4: list<list<string>> tuples,
}

struct Index {
    1: string indexName,
    2: string tableName,
    3: string attributeName,
}

service MasterService {
    list<string> showTables(),

    list<string> createTable(1:Table table),

    bool dropTable(1:string tableName),

    list<string> getRegionServers(1:string tableName),
}

service RegionService {
    bool createTable(1:Table table),

    bool dropTable(1:string tableName),

    bool duplicateTable(1:string tableName, 2:string ip, 3:string path),

    bool duplicateCatalog(1:Table table, 2:list<Index> indexes),

    string sqlRequest(1:string request),
}