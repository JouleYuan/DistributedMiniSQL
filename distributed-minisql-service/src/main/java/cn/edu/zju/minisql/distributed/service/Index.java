/**
 * Autogenerated by Thrift Compiler (0.14.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package cn.edu.zju.minisql.distributed.service;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.14.1)", date = "2021-05-02")
public class Index implements org.apache.thrift.TBase<Index, Index._Fields>, java.io.Serializable, Cloneable, Comparable<Index> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Index");

  private static final org.apache.thrift.protocol.TField INDEX_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("indexName", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField TABLE_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("tableName", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField ATTRIBUTE_INDEX_FIELD_DESC = new org.apache.thrift.protocol.TField("attributeIndex", org.apache.thrift.protocol.TType.I32, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new IndexStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new IndexTupleSchemeFactory();

  public @org.apache.thrift.annotation.Nullable java.lang.String indexName; // required
  public @org.apache.thrift.annotation.Nullable java.lang.String tableName; // required
  public int attributeIndex; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    INDEX_NAME((short)1, "indexName"),
    TABLE_NAME((short)2, "tableName"),
    ATTRIBUTE_INDEX((short)3, "attributeIndex");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // INDEX_NAME
          return INDEX_NAME;
        case 2: // TABLE_NAME
          return TABLE_NAME;
        case 3: // ATTRIBUTE_INDEX
          return ATTRIBUTE_INDEX;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __ATTRIBUTEINDEX_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.INDEX_NAME, new org.apache.thrift.meta_data.FieldMetaData("indexName", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TABLE_NAME, new org.apache.thrift.meta_data.FieldMetaData("tableName", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ATTRIBUTE_INDEX, new org.apache.thrift.meta_data.FieldMetaData("attributeIndex", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int")));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Index.class, metaDataMap);
  }

  public Index() {
  }

  public Index(
    java.lang.String indexName,
    java.lang.String tableName,
    int attributeIndex)
  {
    this();
    this.indexName = indexName;
    this.tableName = tableName;
    this.attributeIndex = attributeIndex;
    setAttributeIndexIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Index(Index other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetIndexName()) {
      this.indexName = other.indexName;
    }
    if (other.isSetTableName()) {
      this.tableName = other.tableName;
    }
    this.attributeIndex = other.attributeIndex;
  }

  public Index deepCopy() {
    return new Index(this);
  }

  @Override
  public void clear() {
    this.indexName = null;
    this.tableName = null;
    setAttributeIndexIsSet(false);
    this.attributeIndex = 0;
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getIndexName() {
    return this.indexName;
  }

  public Index setIndexName(@org.apache.thrift.annotation.Nullable java.lang.String indexName) {
    this.indexName = indexName;
    return this;
  }

  public void unsetIndexName() {
    this.indexName = null;
  }

  /** Returns true if field indexName is set (has been assigned a value) and false otherwise */
  public boolean isSetIndexName() {
    return this.indexName != null;
  }

  public void setIndexNameIsSet(boolean value) {
    if (!value) {
      this.indexName = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getTableName() {
    return this.tableName;
  }

  public Index setTableName(@org.apache.thrift.annotation.Nullable java.lang.String tableName) {
    this.tableName = tableName;
    return this;
  }

  public void unsetTableName() {
    this.tableName = null;
  }

  /** Returns true if field tableName is set (has been assigned a value) and false otherwise */
  public boolean isSetTableName() {
    return this.tableName != null;
  }

  public void setTableNameIsSet(boolean value) {
    if (!value) {
      this.tableName = null;
    }
  }

  public int getAttributeIndex() {
    return this.attributeIndex;
  }

  public Index setAttributeIndex(int attributeIndex) {
    this.attributeIndex = attributeIndex;
    setAttributeIndexIsSet(true);
    return this;
  }

  public void unsetAttributeIndex() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __ATTRIBUTEINDEX_ISSET_ID);
  }

  /** Returns true if field attributeIndex is set (has been assigned a value) and false otherwise */
  public boolean isSetAttributeIndex() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __ATTRIBUTEINDEX_ISSET_ID);
  }

  public void setAttributeIndexIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __ATTRIBUTEINDEX_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case INDEX_NAME:
      if (value == null) {
        unsetIndexName();
      } else {
        setIndexName((java.lang.String)value);
      }
      break;

    case TABLE_NAME:
      if (value == null) {
        unsetTableName();
      } else {
        setTableName((java.lang.String)value);
      }
      break;

    case ATTRIBUTE_INDEX:
      if (value == null) {
        unsetAttributeIndex();
      } else {
        setAttributeIndex((java.lang.Integer)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case INDEX_NAME:
      return getIndexName();

    case TABLE_NAME:
      return getTableName();

    case ATTRIBUTE_INDEX:
      return getAttributeIndex();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case INDEX_NAME:
      return isSetIndexName();
    case TABLE_NAME:
      return isSetTableName();
    case ATTRIBUTE_INDEX:
      return isSetAttributeIndex();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof Index)
      return this.equals((Index)that);
    return false;
  }

  public boolean equals(Index that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_indexName = true && this.isSetIndexName();
    boolean that_present_indexName = true && that.isSetIndexName();
    if (this_present_indexName || that_present_indexName) {
      if (!(this_present_indexName && that_present_indexName))
        return false;
      if (!this.indexName.equals(that.indexName))
        return false;
    }

    boolean this_present_tableName = true && this.isSetTableName();
    boolean that_present_tableName = true && that.isSetTableName();
    if (this_present_tableName || that_present_tableName) {
      if (!(this_present_tableName && that_present_tableName))
        return false;
      if (!this.tableName.equals(that.tableName))
        return false;
    }

    boolean this_present_attributeIndex = true;
    boolean that_present_attributeIndex = true;
    if (this_present_attributeIndex || that_present_attributeIndex) {
      if (!(this_present_attributeIndex && that_present_attributeIndex))
        return false;
      if (this.attributeIndex != that.attributeIndex)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetIndexName()) ? 131071 : 524287);
    if (isSetIndexName())
      hashCode = hashCode * 8191 + indexName.hashCode();

    hashCode = hashCode * 8191 + ((isSetTableName()) ? 131071 : 524287);
    if (isSetTableName())
      hashCode = hashCode * 8191 + tableName.hashCode();

    hashCode = hashCode * 8191 + attributeIndex;

    return hashCode;
  }

  @Override
  public int compareTo(Index other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetIndexName(), other.isSetIndexName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIndexName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.indexName, other.indexName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetTableName(), other.isSetTableName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTableName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.tableName, other.tableName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetAttributeIndex(), other.isSetAttributeIndex());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAttributeIndex()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.attributeIndex, other.attributeIndex);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("Index(");
    boolean first = true;

    sb.append("indexName:");
    if (this.indexName == null) {
      sb.append("null");
    } else {
      sb.append(this.indexName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("tableName:");
    if (this.tableName == null) {
      sb.append("null");
    } else {
      sb.append(this.tableName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("attributeIndex:");
    sb.append(this.attributeIndex);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class IndexStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public IndexStandardScheme getScheme() {
      return new IndexStandardScheme();
    }
  }

  private static class IndexStandardScheme extends org.apache.thrift.scheme.StandardScheme<Index> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Index struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // INDEX_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.indexName = iprot.readString();
              struct.setIndexNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // TABLE_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.tableName = iprot.readString();
              struct.setTableNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // ATTRIBUTE_INDEX
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.attributeIndex = iprot.readI32();
              struct.setAttributeIndexIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Index struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.indexName != null) {
        oprot.writeFieldBegin(INDEX_NAME_FIELD_DESC);
        oprot.writeString(struct.indexName);
        oprot.writeFieldEnd();
      }
      if (struct.tableName != null) {
        oprot.writeFieldBegin(TABLE_NAME_FIELD_DESC);
        oprot.writeString(struct.tableName);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(ATTRIBUTE_INDEX_FIELD_DESC);
      oprot.writeI32(struct.attributeIndex);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class IndexTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public IndexTupleScheme getScheme() {
      return new IndexTupleScheme();
    }
  }

  private static class IndexTupleScheme extends org.apache.thrift.scheme.TupleScheme<Index> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Index struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetIndexName()) {
        optionals.set(0);
      }
      if (struct.isSetTableName()) {
        optionals.set(1);
      }
      if (struct.isSetAttributeIndex()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetIndexName()) {
        oprot.writeString(struct.indexName);
      }
      if (struct.isSetTableName()) {
        oprot.writeString(struct.tableName);
      }
      if (struct.isSetAttributeIndex()) {
        oprot.writeI32(struct.attributeIndex);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Index struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.indexName = iprot.readString();
        struct.setIndexNameIsSet(true);
      }
      if (incoming.get(1)) {
        struct.tableName = iprot.readString();
        struct.setTableNameIsSet(true);
      }
      if (incoming.get(2)) {
        struct.attributeIndex = iprot.readI32();
        struct.setAttributeIndexIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

