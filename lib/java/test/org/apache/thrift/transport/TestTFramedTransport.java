package org.apache.thrift.transport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

public class TestTFramedTransport extends TestCase {

  private static byte[] byteSequence(int start, int end) {
    byte[] result = new byte[end-start+1];
    for (int i = 0; i <= (end-start); i++) {
      result[i] = (byte)(start+i);
    }
    return result;
  }

  public void testRead() throws IOException, TTransportException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(50);
    dos.write(byteSequence(0, 49));

    TMemoryBuffer membuf = new TMemoryBuffer(0);
    membuf.write(baos.toByteArray());

    ReadCountingTransport countTrans = new ReadCountingTransport(membuf);
    TFramedTransport trans = new TFramedTransport(countTrans);

    byte[] readBuf = new byte[10];
    trans.read(readBuf, 0, 10);
    assertTrue(Arrays.equals(readBuf, byteSequence(0,9)));

    trans.read(readBuf, 0, 10);
    assertTrue(Arrays.equals(readBuf, byteSequence(10,19)));

    assertEquals(2, countTrans.readCount);
  }

  public void testWrite() throws TTransportException, IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    WriteCountingTransport countingTrans = new WriteCountingTransport(new TIOStreamTransport(baos));
    TTransport trans = new TFramedTransport(countingTrans);

    trans.write(byteSequence(0,100));
    assertEquals(0, countingTrans.writeCount);
    trans.write(byteSequence(101,200));
    trans.write(byteSequence(201,255));
    assertEquals(0, countingTrans.writeCount);

    trans.flush();
    assertEquals(2, countingTrans.writeCount);

    DataInputStream din = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(256, din.readInt());

    byte[] buf = new byte[256];
    din.read(buf, 0, 256);
    assertTrue(Arrays.equals(byteSequence(0,255), buf));
  }

  public void testDirectRead() throws IOException, TTransportException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(50);
    dos.write(byteSequence(0, 49));

    TMemoryBuffer membuf = new TMemoryBuffer(0);
    membuf.write(baos.toByteArray());

    ReadCountingTransport countTrans = new ReadCountingTransport(membuf);
    TFramedTransport trans = new TFramedTransport(countTrans);

    assertEquals(0, trans.getBytesRemainingInBuffer());

    byte[] readBuf = new byte[10];
    trans.read(readBuf, 0, 10);
    assertTrue(Arrays.equals(readBuf, byteSequence(0,9)));

    assertEquals(40, trans.getBytesRemainingInBuffer());
    assertEquals(10, trans.getBufferPosition());

    trans.consumeBuffer(5);
    assertEquals(35, trans.getBytesRemainingInBuffer());
    assertEquals(15, trans.getBufferPosition());

    assertEquals(2, countTrans.readCount);
  }
}
