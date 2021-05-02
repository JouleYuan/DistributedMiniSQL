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

enum Operator {
    AND = 0, OR = 1, LESS_THAN = 2, LESS_THAN_OR_EQUAL = 3, GREATER_THAN = 4,
    GREATER_THAN_OR_EQUAL = 5, EQUAL = 6, NOT_EQUAL = 7,
}

struct ConditionNode {
    1: string leftElement,
    2: string rightElement,
    3: bool isRightElementConstant,
    4: Operator operator,
    5: ConditionNode leftNode,
    6: ConditionNode rightNode,
}

struct Index {
    1: string indexName,
    2: string tableName,
    3: int attributeIndex,
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

    Table select(
    1: string tableName,
    2: list<string> attributeNames,
    3: ConditionNode conditions,
    4: string orderKeyAttributeName,
    5: bool isIncreased),

    bool insert(1:string tableName, 2:list<string> tuple),

    int deleteRecords(1:string tableName, 2:ConditionNode conditions),

    bool createIndex(1:Index index),

    bool dropIndex(1:string indexName),
}