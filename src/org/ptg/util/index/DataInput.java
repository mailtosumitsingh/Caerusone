package org.ptg.util.index;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataInput 	{
	  private boolean preUTF8Strings;

	  public void setModifiedUTF8StringsMode()
	  {
	    this.preUTF8Strings = true;
	  }

	  public byte readByte()
	    throws IOException{
		  return 0;
	  }

	  public void readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
	    throws IOException{
		  
	  }

	  public void readBytes(byte[] b, int offset, int len, boolean useBuffer)
	    throws IOException
	  {
	    readBytes(b, offset, len);
	  }

	  public short readShort()
	    throws IOException
	  {
	    return (short)((readByte() & 0xFF) << 8 | readByte() & 0xFF);
	  }

	  public int readInt()
	    throws IOException
	  {
	    return (readByte() & 0xFF) << 24 | (readByte() & 0xFF) << 16 | (readByte() & 0xFF) << 8 | readByte() & 0xFF;
	  }

	  public int readVInt()
	    throws IOException
	  {
	    byte b = readByte();
	    int i = b & 0x7F;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7F) << 7;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7F) << 14;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7F) << 21;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    assert ((b & 0x80) == 0);
	    return i | (b & 0x7F) << 28;
	  }

	  public long readLong()
	    throws IOException
	  {
	    return readInt() << 32 | readInt() & 0xFFFFFFFF;
	  }

	  public long readVLong()
	    throws IOException
	  {
	    byte b = readByte();
	    long i = b & 0x7F;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7F) << 7;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7F) << 14;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7F) << 21;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7F) << 28;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7F) << 35;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7F) << 42;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7F) << 49;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    assert ((b & 0x80) == 0);
	    return i | (b & 0x7F) << 56;
	  }

	  public String readString()
	    throws IOException
	  {
	    if (this.preUTF8Strings)
	      return readModifiedUTF8String();
	    int length = readVInt();
	    byte[] bytes = new byte[length];
	    readBytes(bytes, 0, length);
	    return new String(bytes, 0, length, "UTF-8");
	  }

	  private String readModifiedUTF8String() throws IOException {
	    int length = readVInt();
	    char[] chars = new char[length];
	    readChars(chars, 0, length);
	    return new String(chars, 0, length);
	  }

	  @Deprecated
	  public void readChars(char[] buffer, int start, int length)
	    throws IOException
	  {
	    int end = start + length;
	    for (int i = start; i < end; i++) {
	      byte b = readByte();
	      if ((b & 0x80) == 0)
	        buffer[i] = (char)(b & 0x7F);
	      else if ((b & 0xE0) != 224) {
	        buffer[i] = (char)((b & 0x1F) << 6 | readByte() & 0x3F);
	      }
	      else
	        buffer[i] = (char)((b & 0xF) << 12 | (readByte() & 0x3F) << 6 | readByte() & 0x3F);
	    }
	  }

	  public Object clone()
	  {
	    DataInput clone = null;
	    try {
	      clone = (DataInput)super.clone();
	    } catch (CloneNotSupportedException e) {
	    }
	    return clone;
	  }

	  public Map<String, String> readStringStringMap() throws IOException {
	    Map map = new HashMap();
	    int count = readInt();
	    for (int i = 0; i < count; i++) {
	      String key = readString();
	      String val = readString();
	      map.put(key, val);
	    }

	    return map;
	  }
	}

