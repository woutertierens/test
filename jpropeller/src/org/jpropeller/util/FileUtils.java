package org.jpropeller.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jpropeller.transformer.Transformer;

/**
 * Utility methods for working with FileChannels, ByteBuffers, etc.
 */
public class FileUtils {
	
	private static final Charset USASCII = Charset.forName("US-ASCII"); 

	/**
	 * Read an entire input stream to a string, assuming UTF-8 encoding
	 * @param is	The input stream
	 * @return		The string
	 */
	public static String convertStreamToString(InputStream is) { 
	    return new Scanner(is, "UTF-8").useDelimiter("\\A").next();
	}
	
	/**
	 * Read length bytes as an ASCII string
	 * @param buf		The buffer, must have capacity at least length
	 * 					When method returns, will contain the byte data read.
	 * @param in		The input channel
	 * @param length	The length of the string
	 * @return			The string
	 * @throws IOException	If data cannot be read
	 */
	public final static String readString(ByteBuffer buf, ReadableByteChannel in, int length) throws IOException {
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
	 * Read bytes from a {@link ByteBuffer} as an ASCII string, stopping at the first zero byte.
	 * @param buf		The buffer, must have a 0 byte available to read (eventually)
	 * @return			The string
	 * @throws IOException	If data cannot be read
	 */
	public final static String readStringToNull(ByteBuffer buf) throws IOException {
		StringBuilder builder = new StringBuilder();
		int b = 0;
		while ((b = buf.get()) != 0) {
			builder.append((char)b);				
		}
		return builder.toString();
	}

	
	/**
	 * Read all data from an input stream into a temporary file, which is marked
	 * for deletion on exit, then returned.
	 * @param in	The input stream
	 * @return		The temp file
	 * @throws IOException	If stream cannot be read, or file cannot be created or written
	 */
	public final static File tempFileFromInputStream(InputStream in) throws IOException {
		File temp = File.createTempFile("FromInputStream", ".tmp");
		temp.deleteOnExit();
		
		FileOutputStream out = new FileOutputStream(temp);
		
		byte[] buf = new byte[1024];
		int r = -1;
		while((r = in.read(buf, 0, buf.length)) != -1) {
			out.write(buf, 0, r);
		}
		out.close();
		
		return temp;
	}
	
	/**
	 * Read a long value from file channel
	 * @param buf	The buffer, must have capacity at least 8
	 * 				When method returns, will contain the byte data read.
	 * @param in	The input channel
	 * @return		The long value
	 * @throws IOException	If data cannot be read
	 */
	public final static long readLong(ByteBuffer buf, ReadableByteChannel in) throws IOException {
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
	public final static int readInt(ByteBuffer buf, ReadableByteChannel in) throws IOException {
		readBytesAndFlip(buf, in, 4);
		int i = buf.getInt();
		buf.rewind();
		return i;
	}
	
	/**
	 * Read an UNSIGNED byte value (as int) from file channel
	 * @param buf	The buffer, must have capacity at least 1
	 * 				When method returns, will contain the byte data read.
	 * @param in	The input channel
	 * @return		The byte value, as an int
	 * @throws IOException	If data cannot be read
	 */
	public final static int readByte(ByteBuffer buf, ReadableByteChannel in) throws IOException {
		readBytesAndFlip(buf, in, 1);
		int i = buf.get();	
		i = i & 0xFF;
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
	public final static long readUnsignedIntAsLong(ByteBuffer buf, ReadableByteChannel in) throws IOException {
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
	public final static void readBytesAndFlip(ByteBuffer buf, ReadableByteChannel in, int bytes) throws IOException {
		buf.clear();
		buf.limit(bytes);
		//System.out.println("Reading " + bytes + " bytes");
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
	public final static void readFully(ByteBuffer buf, ReadableByteChannel in) throws IOException {
		while (buf.hasRemaining()) {
			int r = in.read(buf);
			if (r < 0) {
				throw new IOException("Reached end of file");
			}
		}
	}
	
	/**
	 * Copy the whole of one file to another
	 * @param inFile		Input file
	 * @param outFile		Output file
	 * @throws IOException	If files cannot be read and written appropriately
	 */
	public final static void copyFile(File inFile, File outFile) throws IOException {
		FileChannel in = new FileInputStream(inFile).getChannel();
		FileChannel out = new FileOutputStream(outFile).getChannel();
		int fileLength = (int)in.size();
		transferFile(in, fileLength, out);
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
	public final static void writeString(ByteBuffer buf, WritableByteChannel out, String val) throws IOException {
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
	public final static void writeInt(ByteBuffer buf, WritableByteChannel out, int val) throws IOException {
		buf.clear();
		buf.putInt(val);
		buf.flip();
		writeBufferFully(buf, out);
		buf.clear();
	}
	
	/**
	 * Write a byte to output channel. Note that int is expected to be non-negative, and
	 * is written as an UNSIGNED byte.
	 * @param buf	The buffer, must have capacity at least 1
	 * @param out	The output channel
	 * @param val	The byte value
	 * @throws IOException	If data cannot be written
	 */
	public final static void writeByte(ByteBuffer buf, WritableByteChannel out, int val) throws IOException {
		buf.clear();
		buf.put((byte)(val & 0xFF));
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
	public final static void writeLong(ByteBuffer buf, WritableByteChannel out, long val) throws IOException {
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
	public final static void writeBufferFully(ByteBuffer buf, WritableByteChannel out) throws IOException {
		int toWrite = buf.remaining();
		int total = 0;
		while (total < toWrite) {
			total += out.write(buf);
		}
	}

	/**
	 * Create a new temp file, marked for deletion on exit, and copy the entire contents
	 * of specified {@link InputStream} to the file, then return the file.
	 * @param inputStream	The data to write to the file.
	 * @return				A new temporary file, marked for deletion on exit, containing all data from stream.
	 * @throws IOException	If stream cannot be read, file cannot be created, or file cannot be written.
	 */
	public static File streamToTempFile(BufferedInputStream inputStream) throws IOException {
		File file = File.createTempFile("inputStreamContents", ".bin");
		file.deleteOnExit();
		OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
		int b = -1;
		while ((b = inputStream.read()) != -1) {
			outputStream.write(b);
		}
		outputStream.flush();
		outputStream.close();
		return file;
	}

	/**
	 * Make a new list, with the same elements as the provided list,
	 * but with any null elements omitted.
	 * @param list	The input list
	 * @return		A new list, same elements as input list with nulls removed
	 */
	public static <A> List<A> filterNull(List<A> list) {
		List<A> mList = new ArrayList<A>(list.size());
		for (A a : list) {
			if (a != null)mList.add(a);
		}
		return mList;		
	}
	
	/**
	 * Map a list using a transformed
	 * @param list	The input list
	 * @param t		The transformer to apply to each element of the input list,
	 * 				to yield the corresponding element of the output list
	 * @return		A new list, where each element is the result of transforming
	 * 				the corresponding element of the input list
	 */
	public static <A, B> List<B> map(List<A> list, Transformer<A, B> t) {
		List<B> mList = new ArrayList<B>(list.size());
		for (A a : list) {
			mList.add(t.transform(a));
		}
		return mList;
	}
	
	/**
	 * Load a file's contents as a string, using the specified encoding,
	 * and convert it to a list of lines as provided by {@link BufferedReader#readLine()}
	 * @param file			The file to read
	 * @param encoding		The string encoding to use, or null to use system default
	 * @return				The lines of text in the file
	 * @throws IOException	If file cannot be read, or parsed as string, or encoding is unsupported.
	 */
	public static List<String> fileToLines(File file, String encoding) throws IOException {
		FileInputStream stream = new FileInputStream(file);
        if (encoding == null) {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            return readLines(reader);
        } else {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(stream, encoding));
            return readLines(reader);
        }

	}
	
	/**
	 * Get the {@link File} in the user's home directory with the specified name.
	 * @param name	File name
	 * @return		File
	 */
	public static File userFile(String name) {
		return new File(System.getProperty("user.home"), name);
	}

	private static List<String> readLines(BufferedReader reader) throws IOException {
        List<String> list = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
	}

}
