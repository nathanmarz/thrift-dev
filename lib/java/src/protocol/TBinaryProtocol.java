package com.facebook.thrift.protocol;

import com.facebook.thrift.TException;
import com.facebook.thrift.transport.TTransport;

/**
 * Binary protocol implementation for thrift.
 *
 * @author Mark Slee <mcslee@facebook.com>
 */
public class TBinaryProtocol extends TProtocol {

  /**
   * Factory
   */
  public static class Factory implements TProtocolFactory {
    public TProtocol getProtocol(TTransport trans) {
      return new TBinaryProtocol(trans);
    }
  }

  /**
   * Constructor
   */
  public TBinaryProtocol(TTransport trans) {
    super(trans);
  }

  public void writeMessageBegin(TMessage message) throws TException {
    writeString(message.name);
    writeByte(message.type);
    writeI32(message.seqid);
  }

  public void writeMessageEnd() {}

  public void writeStructBegin(TStruct struct) {}

  public void writeStructEnd() {}

  public void writeFieldBegin(TField field) throws TException {
    writeByte(field.type);
    writeI16(field.id);
  }

  public void writeFieldEnd() {}

  public void writeFieldStop() throws TException {
    writeByte(TType.STOP);
  }

  public void writeMapBegin(TMap map) throws TException {
    writeByte(map.keyType);
    writeByte(map.valueType);
    writeI32(map.size);
  }

  public void writeMapEnd() {}

  public void writeListBegin(TList list) throws TException {
    writeByte(list.elemType);
    writeI32(list.size);
  }

  public void writeListEnd() {}

  public void writeSetBegin(TSet set) throws TException {
    writeByte(set.elemType);
    writeI32(set.size);
  }

  public void writeSetEnd() {}

  public void writeBool(boolean b) throws TException {
    writeByte(b ? (byte)1 : (byte)0);
  }

  private byte [] bout = new byte[1];
  public void writeByte(byte b) throws TException {
    bout[0] = b;
    trans_.write(bout, 0, 1);
  }

  private byte[] i16out = new byte[2];
  public void writeI16(short i16) throws TException {
    i16out[0] = (byte)(0xff & (i16 >> 8));
    i16out[1] = (byte)(0xff & (i16));
    trans_.write(i16out, 0, 2);
  }

  private byte[] i32out = new byte[4];
  public void writeI32(int i32) throws TException {
    i32out[0] = (byte)(0xff & (i32 >> 24));
    i32out[1] = (byte)(0xff & (i32 >> 16));
    i32out[2] = (byte)(0xff & (i32 >> 8));
    i32out[3] = (byte)(0xff & (i32));
    trans_.write(i32out, 0, 4);
  }

  private byte[] i64out = new byte[8];
  public void writeI64(long i64) throws TException {
    i64out[0] = (byte)(0xff & (i64 >> 56));
    i64out[1] = (byte)(0xff & (i64 >> 48));
    i64out[2] = (byte)(0xff & (i64 >> 40));
    i64out[3] = (byte)(0xff & (i64 >> 32));
    i64out[4] = (byte)(0xff & (i64 >> 24));
    i64out[5] = (byte)(0xff & (i64 >> 16));
    i64out[6] = (byte)(0xff & (i64 >> 8));
    i64out[7] = (byte)(0xff & (i64));
    trans_.write(i64out, 0, 8);
  }

  public void writeDouble(double dub) throws TException {
    writeI64(Double.doubleToLongBits(dub));
  }

  public void writeString(String str) throws TException {
    byte[] dat = str.getBytes();
    writeI32(dat.length);
    trans_.write(dat, 0, dat.length);
  }

  /**
   * Reading methods.
   */

  public TMessage readMessageBegin() throws TException {
    TMessage message = new TMessage();
    message.name = readString();
    message.type = readByte();
    message.seqid = readI32();
    return message;
  }

  public void readMessageEnd() {}

  public TStruct readStructBegin() {
    return new TStruct();
  }

  public void readStructEnd() {}

  public TField readFieldBegin() throws TException {
    TField field = new TField();
    field.type = readByte();
    if (field.type != TType.STOP) {
      field.id = readI16();
    }
    return field;
  }
  
  public void readFieldEnd() {}
 
  public TMap readMapBegin() throws TException {
    TMap map = new TMap();
    map.keyType = readByte();
    map.valueType = readByte();
    map.size = readI32();
    return map;
  }

  public void readMapEnd() {}

  public TList readListBegin() throws TException {
    TList list = new TList();
    list.elemType = readByte();
    list.size = readI32();
    return list;
  }

  public void readListEnd() {}

  public TSet readSetBegin() throws TException {
    TSet set = new TSet();
    set.elemType = readByte();
    set.size = readI32();
    return set;
  }

  public void readSetEnd() {}

  public boolean readBool() throws TException {
    return (readByte() == 1);
  }

  private byte[] bin = new byte[1];
  public byte readByte() throws TException {
    trans_.readAll(bin, 0, 1);
    return bin[0];
  }

  private byte[] i16rd = new byte[2];
  public short readI16() throws TException {
    trans_.readAll(i16rd, 0, 2);
    return
      (short)
      (((i16rd[0] & 0xff) << 8) |
       ((i16rd[1] & 0xff)));
  }

  private byte[] i32rd = new byte[4];
  public int readI32() throws TException {
    trans_.readAll(i32rd, 0, 4);
    return
      ((i32rd[0] & 0xff) << 24) |
      ((i32rd[1] & 0xff) << 16) |
      ((i32rd[2] & 0xff) <<  8) |
      ((i32rd[3] & 0xff));
  }
 
  private byte[] i64rd = new byte[8];
  public long readI64() throws TException {
    trans_.readAll(i64rd, 0, 8);
    return
      ((long)(i64rd[0] & 0xff) << 56) |
      ((long)(i64rd[1] & 0xff) << 48) |
      ((long)(i64rd[2] & 0xff) << 40) |
      ((long)(i64rd[3] & 0xff) << 32) |
      ((long)(i64rd[4] & 0xff) << 24) |
      ((long)(i64rd[5] & 0xff) << 16) |
      ((long)(i64rd[6] & 0xff) <<  8) |
      ((long)(i64rd[7] & 0xff));
  }

  public double readDouble() throws TException {
    return Double.longBitsToDouble(readI64());
  }

  public String readString() throws TException {
    int size = readI32();
    byte[] buf = new byte[size];
    trans_.readAll(buf, 0, size);
    return new String(buf);
  }
}
