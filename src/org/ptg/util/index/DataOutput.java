package org.ptg.util.index;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.UnicodeUtil;

public class DataOutput {
	  private static int COPY_BUFFER_SIZE;
	  private byte[] copyBuffer;

	  public void writeByte(byte paramByte)    throws IOException{
		  
	  }

	  public void writeBytes(byte[] b, int length)
	    throws IOException
	  {
	    writeBytes(b, 0, length);
	  }

	  public void writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
	    throws IOException{
		  
	  }

	  public void writeInt(int i)
	    throws IOException
	  {
	    writeByte((byte)(i >> 24));
	    writeByte((byte)(i >> 16));
	    writeByte((byte)(i >> 8));
	    writeByte((byte)i);
	  }

	  public final void writeVInt(int i)
	    throws IOException
	  {
	    while ((i & 0xFFFFFF80) != 0) {
	      writeByte((byte)(i & 0x7F | 0x80));
	      i >>>= 7;
	    }
	    writeByte((byte)i);
	  }

	  public void writeLong(long i)
	    throws IOException
	  {
	    writeInt((int)(i >> 32));
	    writeInt((int)i);
	  }

	  public final void writeVLong(long i)
	    throws IOException
	  {
	    while ((i & 0xFFFFFF80) != 0L) {
	      writeByte((byte)(int)(i & 0x7F | 0x80));
	      i >>>= 7;
	    }
	    writeByte((byte)(int)i);
	  }

	  public void writeString(String s)
	    throws IOException
	  {
	    BytesRef utf8Result = new BytesRef(10);
	    UnicodeUtil.UTF16toUTF8(s, 0, s.length(), utf8Result);
	    writeVInt(utf8Result.length);
	    writeBytes(utf8Result.bytes, 0, utf8Result.length);
	  }

	  public void copyBytes(DataInput input, long numBytes)
	    throws IOException
	  {
	    assert (numBytes >= 0L) : ("numBytes=" + numBytes);
	    long left = numBytes;
	    if (this.copyBuffer == null)
	      this.copyBuffer = new byte[COPY_BUFFER_SIZE];
	    while (left > 0L)
	    {
	      int toCopy;
	      if (left > COPY_BUFFER_SIZE)
	        toCopy = COPY_BUFFER_SIZE;
	      else
	        toCopy = (int)left;
	      input.readBytes(this.copyBuffer, 0, toCopy);
	      writeBytes(this.copyBuffer, 0, toCopy);
	      left -= toCopy;
	    }
	  }

	  @Deprecated
	  public void writeChars(String s, int start, int length)
	    throws IOException
	  {
	    int end = start + length;
	    for (int i = start; i < end; i++) {
	      int code = s.charAt(i);
	      if ((code >= 1) && (code <= 127)) {
	        writeByte((byte)code);
	      } else if (((code >= 128) && (code <= 2047)) || (code == 0)) {
	        writeByte((byte)(0xC0 | code >> 6));
	        writeByte((byte)(0x80 | code & 0x3F));
	      } else {
	        writeByte((byte)(0xE0 | code >>> 12));
	        writeByte((byte)(0x80 | code >> 6 & 0x3F));
	        writeByte((byte)(0x80 | code & 0x3F));
	      }
	    }
	  }

	  @Deprecated
	  public void writeChars(char[] s, int start, int length)
	    throws IOException
	  {
	    int end = start + length;
	    for (int i = start; i < end; i++) {
	      int code = s[i];
	      if ((code >= 1) && (code <= 127)) {
	        writeByte((byte)code);
	      } else if (((code >= 128) && (code <= 2047)) || (code == 0)) {
	        writeByte((byte)(0xC0 | code >> 6));
	        writeByte((byte)(0x80 | code & 0x3F));
	      } else {
	        writeByte((byte)(0xE0 | code >>> 12));
	        writeByte((byte)(0x80 | code >> 6 & 0x3F));
	        writeByte((byte)(0x80 | code & 0x3F));
	      }
	    }
	  }

	  public void writeStringStringMap(Map<String, String> map) throws IOException {
	    if (map == null) {
	      writeInt(0);
	    } else {
	      writeInt(map.size());
	      for (Map.Entry entry : map.entrySet()) {
	        writeString((String)entry.getKey());
	        writeString((String)entry.getValue());
	      }
	    }
	  }

	  static
	  {
	    COPY_BUFFER_SIZE = 16384;
	  }
	}