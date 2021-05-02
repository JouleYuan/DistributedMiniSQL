/**
 * Autogenerated by Thrift Compiler (0.14.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package cn.edu.zju.minisql.distributed.service;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.14.1)", date = "2021-05-02")
public class Table implements org.apache.thrift.TBase<Table, Table._Fields>, java.io.Serializable, Cloneable, Comparable<Table> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Table");

  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField ATTRIBUTES_FIELD_DESC = new org.apache.thrift.protocol.TField("attributes", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField PRIMARY_KEY_INDEX_FIELD_DESC = new org.apache.thrift.protocol.TField("primaryKeyIndex", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField TUPLES_FIELD_DESC = new org.apache.thrift.protocol.TField("tuples", org.apache.thrift.protocol.TType.LIST, (short)4);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new TableStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new TableTupleSchemeFactory();

  public @org.apache.thrift.annotation.Nullable java.lang.String name; // required
  public @org.apache.thrift.annotation.Nullable java.util.List<Attribute> attributes; // required
  public int primaryKeyIndex; // required
  public @org.apache.thrift.annotation.Nullable java.util.List<java.util.List<java.lang.String>> tuples; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    NAME((short)1, "name"),
    ATTRIBUTES((short)2, "attributes"),
    PRIMARY_KEY_INDEX((short)3, "primaryKeyIndex"),
    TUPLES((short)4, "tuples");

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
        case 1: // NAME
          return NAME;
        case 2: // ATTRIBUTES
          return ATTRIBUTES;
        case 3: // PRIMARY_KEY_INDEX
          return PRIMARY_KEY_INDEX;
        case 4: // TUPLES
          return TUPLES;
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
  private static final int __PRIMARYKEYINDEX_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData("name", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ATTRIBUTES, new org.apache.thrift.meta_data.FieldMetaData("attributes", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Attribute.class))));
    tmpMap.put(_Fields.PRIMARY_KEY_INDEX, new org.apache.thrift.meta_data.FieldMetaData("primaryKeyIndex", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int")));
    tmpMap.put(_Fields.TUPLES, new org.apache.thrift.meta_data.FieldMetaData("tuples", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
                new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Table.class, metaDataMap);
  }

  public Table() {
  }

  public Table(
    java.lang.String name,
    java.util.List<Attribute> attributes,
    int primaryKeyIndex,
    java.util.List<java.util.List<java.lang.String>> tuples)
  {
    this();
    this.name = name;
    this.attributes = attributes;
    this.primaryKeyIndex = primaryKeyIndex;
    setPrimaryKeyIndexIsSet(true);
    this.tuples = tuples;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Table(Table other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetName()) {
      this.name = other.name;
    }
    if (other.isSetAttributes()) {
      java.util.List<Attribute> __this__attributes = new java.util.ArrayList<Attribute>(other.attributes.size());
      for (Attribute other_element : other.attributes) {
        __this__attributes.add(new Attribute(other_element));
      }
      this.attributes = __this__attributes;
    }
    this.primaryKeyIndex = other.primaryKeyIndex;
    if (other.isSetTuples()) {
      java.util.List<java.util.List<java.lang.String>> __this__tuples = new java.util.ArrayList<java.util.List<java.lang.String>>(other.tuples.size());
      for (java.util.List<java.lang.String> other_element : other.tuples) {
        java.util.List<java.lang.String> __this__tuples_copy = new java.util.ArrayList<java.lang.String>(other_element);
        __this__tuples.add(__this__tuples_copy);
      }
      this.tuples = __this__tuples;
    }
  }

  public Table deepCopy() {
    return new Table(this);
  }

  @Override
  public void clear() {
    this.name = null;
    this.attributes = null;
    setPrimaryKeyIndexIsSet(false);
    this.primaryKeyIndex = 0;
    this.tuples = null;
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getName() {
    return this.name;
  }

  public Table setName(@org.apache.thrift.annotation.Nullable java.lang.String name) {
    this.name = name;
    return this;
  }

  public void unsetName() {
    this.name = null;
  }

  /** Returns true if field name is set (has been assigned a value) and false otherwise */
  public boolean isSetName() {
    return this.name != null;
  }

  public void setNameIsSet(boolean value) {
    if (!value) {
      this.name = null;
    }
  }

  public int getAttributesSize() {
    return (this.attributes == null) ? 0 : this.attributes.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<Attribute> getAttributesIterator() {
    return (this.attributes == null) ? null : this.attributes.iterator();
  }

  public void addToAttributes(Attribute elem) {
    if (this.attributes == null) {
      this.attributes = new java.util.ArrayList<Attribute>();
    }
    this.attributes.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<Attribute> getAttributes() {
    return this.attributes;
  }

  public Table setAttributes(@org.apache.thrift.annotation.Nullable java.util.List<Attribute> attributes) {
    this.attributes = attributes;
    return this;
  }

  public void unsetAttributes() {
    this.attributes = null;
  }

  /** Returns true if field attributes is set (has been assigned a value) and false otherwise */
  public boolean isSetAttributes() {
    return this.attributes != null;
  }

  public void setAttributesIsSet(boolean value) {
    if (!value) {
      this.attributes = null;
    }
  }

  public int getPrimaryKeyIndex() {
    return this.primaryKeyIndex;
  }

  public Table setPrimaryKeyIndex(int primaryKeyIndex) {
    this.primaryKeyIndex = primaryKeyIndex;
    setPrimaryKeyIndexIsSet(true);
    return this;
  }

  public void unsetPrimaryKeyIndex() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __PRIMARYKEYINDEX_ISSET_ID);
  }

  /** Returns true if field primaryKeyIndex is set (has been assigned a value) and false otherwise */
  public boolean isSetPrimaryKeyIndex() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __PRIMARYKEYINDEX_ISSET_ID);
  }

  public void setPrimaryKeyIndexIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __PRIMARYKEYINDEX_ISSET_ID, value);
  }

  public int getTuplesSize() {
    return (this.tuples == null) ? 0 : this.tuples.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<java.util.List<java.lang.String>> getTuplesIterator() {
    return (this.tuples == null) ? null : this.tuples.iterator();
  }

  public void addToTuples(java.util.List<java.lang.String> elem) {
    if (this.tuples == null) {
      this.tuples = new java.util.ArrayList<java.util.List<java.lang.String>>();
    }
    this.tuples.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<java.util.List<java.lang.String>> getTuples() {
    return this.tuples;
  }

  public Table setTuples(@org.apache.thrift.annotation.Nullable java.util.List<java.util.List<java.lang.String>> tuples) {
    this.tuples = tuples;
    return this;
  }

  public void unsetTuples() {
    this.tuples = null;
  }

  /** Returns true if field tuples is set (has been assigned a value) and false otherwise */
  public boolean isSetTuples() {
    return this.tuples != null;
  }

  public void setTuplesIsSet(boolean value) {
    if (!value) {
      this.tuples = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((java.lang.String)value);
      }
      break;

    case ATTRIBUTES:
      if (value == null) {
        unsetAttributes();
      } else {
        setAttributes((java.util.List<Attribute>)value);
      }
      break;

    case PRIMARY_KEY_INDEX:
      if (value == null) {
        unsetPrimaryKeyIndex();
      } else {
        setPrimaryKeyIndex((java.lang.Integer)value);
      }
      break;

    case TUPLES:
      if (value == null) {
        unsetTuples();
      } else {
        setTuples((java.util.List<java.util.List<java.lang.String>>)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case NAME:
      return getName();

    case ATTRIBUTES:
      return getAttributes();

    case PRIMARY_KEY_INDEX:
      return getPrimaryKeyIndex();

    case TUPLES:
      return getTuples();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case NAME:
      return isSetName();
    case ATTRIBUTES:
      return isSetAttributes();
    case PRIMARY_KEY_INDEX:
      return isSetPrimaryKeyIndex();
    case TUPLES:
      return isSetTuples();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof Table)
      return this.equals((Table)that);
    return false;
  }

  public boolean equals(Table that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_name = true && this.isSetName();
    boolean that_present_name = true && that.isSetName();
    if (this_present_name || that_present_name) {
      if (!(this_present_name && that_present_name))
        return false;
      if (!this.name.equals(that.name))
        return false;
    }

    boolean this_present_attributes = true && this.isSetAttributes();
    boolean that_present_attributes = true && that.isSetAttributes();
    if (this_present_attributes || that_present_attributes) {
      if (!(this_present_attributes && that_present_attributes))
        return false;
      if (!this.attributes.equals(that.attributes))
        return false;
    }

    boolean this_present_primaryKeyIndex = true;
    boolean that_present_primaryKeyIndex = true;
    if (this_present_primaryKeyIndex || that_present_primaryKeyIndex) {
      if (!(this_present_primaryKeyIndex && that_present_primaryKeyIndex))
        return false;
      if (this.primaryKeyIndex != that.primaryKeyIndex)
        return false;
    }

    boolean this_present_tuples = true && this.isSetTuples();
    boolean that_present_tuples = true && that.isSetTuples();
    if (this_present_tuples || that_present_tuples) {
      if (!(this_present_tuples && that_present_tuples))
        return false;
      if (!this.tuples.equals(that.tuples))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetName()) ? 131071 : 524287);
    if (isSetName())
      hashCode = hashCode * 8191 + name.hashCode();

    hashCode = hashCode * 8191 + ((isSetAttributes()) ? 131071 : 524287);
    if (isSetAttributes())
      hashCode = hashCode * 8191 + attributes.hashCode();

    hashCode = hashCode * 8191 + primaryKeyIndex;

    hashCode = hashCode * 8191 + ((isSetTuples()) ? 131071 : 524287);
    if (isSetTuples())
      hashCode = hashCode * 8191 + tuples.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(Table other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetName(), other.isSetName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.name, other.name);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetAttributes(), other.isSetAttributes());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAttributes()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.attributes, other.attributes);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetPrimaryKeyIndex(), other.isSetPrimaryKeyIndex());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPrimaryKeyIndex()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.primaryKeyIndex, other.primaryKeyIndex);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetTuples(), other.isSetTuples());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTuples()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.tuples, other.tuples);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("Table(");
    boolean first = true;

    sb.append("name:");
    if (this.name == null) {
      sb.append("null");
    } else {
      sb.append(this.name);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("attributes:");
    if (this.attributes == null) {
      sb.append("null");
    } else {
      sb.append(this.attributes);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("primaryKeyIndex:");
    sb.append(this.primaryKeyIndex);
    first = false;
    if (!first) sb.append(", ");
    sb.append("tuples:");
    if (this.tuples == null) {
      sb.append("null");
    } else {
      sb.append(this.tuples);
    }
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

  private static class TableStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TableStandardScheme getScheme() {
      return new TableStandardScheme();
    }
  }

  private static class TableStandardScheme extends org.apache.thrift.scheme.StandardScheme<Table> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Table struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.name = iprot.readString();
              struct.setNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // ATTRIBUTES
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct.attributes = new java.util.ArrayList<Attribute>(_list0.size);
                @org.apache.thrift.annotation.Nullable Attribute _elem1;
                for (int _i2 = 0; _i2 < _list0.size; ++_i2)
                {
                  _elem1 = new Attribute();
                  _elem1.read(iprot);
                  struct.attributes.add(_elem1);
                }
                iprot.readListEnd();
              }
              struct.setAttributesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // PRIMARY_KEY_INDEX
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.primaryKeyIndex = iprot.readI32();
              struct.setPrimaryKeyIndexIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // TUPLES
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list3 = iprot.readListBegin();
                struct.tuples = new java.util.ArrayList<java.util.List<java.lang.String>>(_list3.size);
                @org.apache.thrift.annotation.Nullable java.util.List<java.lang.String> _elem4;
                for (int _i5 = 0; _i5 < _list3.size; ++_i5)
                {
                  {
                    org.apache.thrift.protocol.TList _list6 = iprot.readListBegin();
                    _elem4 = new java.util.ArrayList<java.lang.String>(_list6.size);
                    @org.apache.thrift.annotation.Nullable java.lang.String _elem7;
                    for (int _i8 = 0; _i8 < _list6.size; ++_i8)
                    {
                      _elem7 = iprot.readString();
                      _elem4.add(_elem7);
                    }
                    iprot.readListEnd();
                  }
                  struct.tuples.add(_elem4);
                }
                iprot.readListEnd();
              }
              struct.setTuplesIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, Table struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.name != null) {
        oprot.writeFieldBegin(NAME_FIELD_DESC);
        oprot.writeString(struct.name);
        oprot.writeFieldEnd();
      }
      if (struct.attributes != null) {
        oprot.writeFieldBegin(ATTRIBUTES_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.attributes.size()));
          for (Attribute _iter9 : struct.attributes)
          {
            _iter9.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(PRIMARY_KEY_INDEX_FIELD_DESC);
      oprot.writeI32(struct.primaryKeyIndex);
      oprot.writeFieldEnd();
      if (struct.tuples != null) {
        oprot.writeFieldBegin(TUPLES_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.LIST, struct.tuples.size()));
          for (java.util.List<java.lang.String> _iter10 : struct.tuples)
          {
            {
              oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, _iter10.size()));
              for (java.lang.String _iter11 : _iter10)
              {
                oprot.writeString(_iter11);
              }
              oprot.writeListEnd();
            }
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TableTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TableTupleScheme getScheme() {
      return new TableTupleScheme();
    }
  }

  private static class TableTupleScheme extends org.apache.thrift.scheme.TupleScheme<Table> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Table struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetName()) {
        optionals.set(0);
      }
      if (struct.isSetAttributes()) {
        optionals.set(1);
      }
      if (struct.isSetPrimaryKeyIndex()) {
        optionals.set(2);
      }
      if (struct.isSetTuples()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetName()) {
        oprot.writeString(struct.name);
      }
      if (struct.isSetAttributes()) {
        {
          oprot.writeI32(struct.attributes.size());
          for (Attribute _iter12 : struct.attributes)
          {
            _iter12.write(oprot);
          }
        }
      }
      if (struct.isSetPrimaryKeyIndex()) {
        oprot.writeI32(struct.primaryKeyIndex);
      }
      if (struct.isSetTuples()) {
        {
          oprot.writeI32(struct.tuples.size());
          for (java.util.List<java.lang.String> _iter13 : struct.tuples)
          {
            {
              oprot.writeI32(_iter13.size());
              for (java.lang.String _iter14 : _iter13)
              {
                oprot.writeString(_iter14);
              }
            }
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Table struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.name = iprot.readString();
        struct.setNameIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list15 = iprot.readListBegin(org.apache.thrift.protocol.TType.STRUCT);
          struct.attributes = new java.util.ArrayList<Attribute>(_list15.size);
          @org.apache.thrift.annotation.Nullable Attribute _elem16;
          for (int _i17 = 0; _i17 < _list15.size; ++_i17)
          {
            _elem16 = new Attribute();
            _elem16.read(iprot);
            struct.attributes.add(_elem16);
          }
        }
        struct.setAttributesIsSet(true);
      }
      if (incoming.get(2)) {
        struct.primaryKeyIndex = iprot.readI32();
        struct.setPrimaryKeyIndexIsSet(true);
      }
      if (incoming.get(3)) {
        {
          org.apache.thrift.protocol.TList _list18 = iprot.readListBegin(org.apache.thrift.protocol.TType.LIST);
          struct.tuples = new java.util.ArrayList<java.util.List<java.lang.String>>(_list18.size);
          @org.apache.thrift.annotation.Nullable java.util.List<java.lang.String> _elem19;
          for (int _i20 = 0; _i20 < _list18.size; ++_i20)
          {
            {
              org.apache.thrift.protocol.TList _list21 = iprot.readListBegin(org.apache.thrift.protocol.TType.STRING);
              _elem19 = new java.util.ArrayList<java.lang.String>(_list21.size);
              @org.apache.thrift.annotation.Nullable java.lang.String _elem22;
              for (int _i23 = 0; _i23 < _list21.size; ++_i23)
              {
                _elem22 = iprot.readString();
                _elem19.add(_elem22);
              }
            }
            struct.tuples.add(_elem19);
          }
        }
        struct.setTuplesIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}
