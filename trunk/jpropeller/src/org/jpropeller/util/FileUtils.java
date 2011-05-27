package org.jpropeller.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * Utility methods for working with FileChannels, ByteBuffers, etc.
 */
public class FileUtils {
	
	private static final Charset USASCII = Charset.forName("US-ASCII"); 

	/**
	 * Read length bytes as an ASCII string
	 * @param buf		The buffer, must have capacity at least length
	 * 					When method returns, will contain the byte data read.
	 * @param in		The input channel
	 * @param length	The length of the string
	 * @return			The string
	 * @throws IOException	If data cannot be read
	 */
	public final static String readString(ByteBuffer buf, FileChannel in, int length) throws IOException {
		readBytesAndFlip(buf, in, length);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			//TODO use the Charset instead
			builder.append((char)buf.get());				
		}
		buf.rewind();
		return builder.toString();
	}

	/**
	 * Read a long value from file channel
	 * @param buf	The buffer, must have capacity at least 8
	 * 				When method returns, will contain the byte data read.
	 * @param in	The input channel
	 * @return		The long value
	 * @throws IOException	If data cannot be read
	 */
	public final static long readLong(ByteBuffer buf, FileChannel in) throws IOException {
		readBytesAndFlip(buf, in, 8);
		long l = buf.getLong();
		buf.rewind();
		return l;
	}

	/**
	 * Read an int value from file channel
	 * @param buf	The buffer, must have capacity at least 4
	 * 				When method returns, will contain the byte data read.
	 * @param in	The input channel
	 * @return		The int value
	 * @throws IOException	If data cannot be read
	 */
	public final static int readInt(ByteBuffer buf, FileChannel in) throws IOException {
		readBytesAndFlip(buf, in, 4);
		int i = buf.getInt();
		buf.rewind();
		return i;
	}
	
	/**
	 * Read an int value from file channel
	 * @param buf	The buffer, must have capacity at least 4
	 * 				When method returns, will contain the byte data read.
	 * @param in	The input channel
	 * @return		The int value
	 * @throws IOException	If data cannot be read
	 */
	public final static long readUnsignedIntAsLong(ByteBuffer buf, FileChannel in) throws IOException {
		return ((long)readInt(buf, in)) & 0xFFFFFFFFL;
	}

	/**
	 * Cast an int that is meant to be unsigned to a long with the
	 * correct unsigned value. So for example if an int is read from
	 * a buffer that was written in C as an unsigned int, this method
	 * will cast it back to the intended unsigned value. This must be
	 * stored in a long since it will not fit in a Java signed int.
	 * @param i		The int
	 * @return		The usnigned int value as long
	 */
	public final static long unsignedIntAsLong(int i) {
		return ((long)i) & 0xFFFFFFFFL;
	}

	/**
	 * Read a given number of bytes from channel to the start of the buffer,
	 * and then flip the buffer to give access to the bytes
	 * Performs repeated reads as necessary to get requested byte count
	 * @param buf		The buffer, must have capacity at least bytes
	 * @param in		The input channel	
	 * @param bytes		The number of bytes to read
	 * @throws IOException	If data cannot be read
	 */
	public final static void readBytesAndFlip(ByteBuffer buf, FileChannel in, int bytes) throws IOException {
		buf.clear();
		buf.limit(bytes);
		int total = 0;
		while (total < bytes) {
			int r = in.read(buf);
			if (r < 0) {
				throw new IOException("Reached end of file");
			}
			total += r;
		}
		buf.flip();
	}
	
	/**
	 * Read from channel to buffer until the buffer has no remaining space
	 * Performs repeated reads as necessary to get requested byte count
	 * @param buf		The buffer
	 * @param in		The input channel	
	 * @throws IOException	If data cannot be read
	 */
	public final static void readFully(ByteBuffer buf, FileChannel in) throws IOException {
		while (buf.hasRemaining()) {
			int r = in.read(buf);
			if (r < 0) {
				throw new IOException("Reached end of file");
			}
		}
	}
	
	/**
	 * Transfer fileLength bytes from the in file channel to the out file channel.
	 * @param in			The input file channel, must contain at least fileLength more bytes to read
	 * @param fileLength	The number of bytes to read from in. If fileLength is -1, all remaining data
	 * 						in input filechannel will be read.
	 * @param out			The output file channel
	 * @throws IOException	If data cannot be read or written
	 */
	public final static void transferFile(FileChannel in, int fileLength, FileChannel out) throws IOException {
		
		//As a special case, if the fileLength is -1, we read all remaining data from the FileChannel
		if (fileLength == -1) {
			fileLength = (int)(in.size() - in.position());
		}

		ByteBuffer buffer = ByteBuffer.allocate(4096);
		int transferred = 0;
		while (transferred < fileLength) {
			int limit = fileLength - transferred;
			if (limit > buffer.capacity()) {
				limit = buffer.capacity();
			}
			buffer.clear();
			buffer.limit(limit);
			int r = in.read(buffer);
			if (r < 0) {
				throw new IOException("Insufficient data in input FileChannel");
			}
			
			buffer.flip();
			
			while (buffer.hasRemaining()) {
				out.write(buffer);
			}
			
			transferred += r;
		}
	}

	/**
	 * Write the length of a string as an int, then the string
	 * as ASCII.
	 * @param buf		The buffer, must have enough capacity to store string as ASCII
	 * @param out		The output channel
	 * @param val		The string
	 * @throws IOException	If data cannot be written
	 */
	public final static void writeStringAndLength(ByteBuffer buf, FileChannel out, String val) throws IOException {		
		writeInt(buf, out, val.length());
		writeString(buf, out, val);
	}

	/**
	 * Write a string as ASCII.
	 * @param buf		The buffer, must have enough capacity to store string as ASCII
	 * @param out		The output channel
	 * @param val		The string
	 * @throws IOException	If data cannot be written
	 */
	public final static void writeString(ByteBuffer buf, FileChannel out, String val) throws IOException {
		buf.clear();
		
		CharsetEncoder encoder = USASCII.newEncoder();
		encoder.encode(CharBuffer.wrap(val), buf, true);

		buf.flip();
		writeBufferFully(buf, out);
		buf.clear();
	}

	/**
	 * Write an int to output channel
	 * @param buf	The buffer, must have capacity at least 4
	 * @param out	The output channel
	 * @param val	The int value
	 * @throws IOException	If data cannot be written
	 */
	public final static void writeInt(ByteBuffer buf, FileChannel out, int val) throws IOException {
		buf.clear();
		buf.putInt(val);
		buf.flip();
		writeBufferFully(buf, out);
		buf.clear();
	}

	/**
	 * Write a long to output channel
	 * @param buf	The buffer, must have capacity at least 8
	 * @param out	The output channel
	 * @param val	The long value
	 * @throws IOException	If data cannot be written
	 */
	public final static void writeLong(ByteBuffer buf, FileChannel out, long val) throws IOException {
		buf.clear();
		buf.putLong(val);
		buf.flip();
		writeBufferFully(buf, out);
		buf.clear();
	}
	
	/**
	 * Write a given number of bytes from a buffer to a channel
	 * Performs repeated writes as necessary to write whole buffer
	 * @param buf		The buffer
	 * @param out		The output channel	
	 * @throws IOException	If data cannot be written
	 */
	public final static void writeBufferFully(ByteBuffer buf, FileChannel out) throws IOException {
		int toWrite = buf.remaining();
		int total = 0;
		while (total < toWrite) {
			total += out.write(buf);
		}
	}
}
