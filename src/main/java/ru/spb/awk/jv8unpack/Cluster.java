package ru.spb.awk.jv8unpack;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Date;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;


public class Cluster implements Closeable {
	
	private class Content {
		private long lenght;
		private long next;
	}
	static final int MAGIC_NUMBER = 0x7fffffff;
	private boolean deflate;


	private long total;
	

	private RandomAccessFile dataInput;


	private ByteBuffer byte_content;
	
	public Cluster(RandomAccessFile dataInput, boolean deflate) throws IOException, DataFormatException {
		this.dataInput = dataInput;
		this.deflate = deflate;
		readContent(dataInput);
		if(deflate) {
			this.dataInput = inflate();
		}
	}
	public void close() throws IOException {
		dataInput.close();
	} 
	public RandomAccessFile getDataInput() {
		return dataInput;
	}
	public Date getDate(int offset) {
		// TODO Auto-generated method stub
		return null;
	}
	public long getLong(int offset) throws DataFormatException {
		long result = 0,
				a1 = (long)byte_content.get(offset)   + (byte_content.get(offset)<0?256:0),
				a2 = (long)byte_content.get(offset+1) + (byte_content.get(offset+1)<0?256:0),
				a3 = (long)byte_content.get(offset+2) + (byte_content.get(offset+2)<0?256:0),
				a4 = (long)byte_content.get(offset+3) + (byte_content.get(offset+3)<0?256:0);
		result = (a4 << 24) + (a3 << 16) + (a2 << 8) + a1;
		return result;
	}
	
	public String getText() throws IOException {
		StringBuilder builder = new StringBuilder();
		if(deflate) {
			dataInput.seek(3);
			Reader reader = Channels.newReader(dataInput.getChannel(), "UTF-8");
			char[] line = new char[4096];
			int i = 0;
			while((i=reader.read(line,0,4096))>0) {
				builder.append(line,0,i);
			}
		} else {
			byte_content.position(3);
			byte[] a = new byte[(int) (total-3)];
			byte_content.get(a);
			return new String(a, "UTF-8");
		}
		return builder.toString();

	}
	
	public long getTotal() {
		return total;
	}
	
	public String getUtf16(int offset) throws DataFormatException, IOException {
		byte_content.position(offset);
		byte[] a = new byte[(int)total-offset];
		byte_content.get(a);
		for(int i = 0; i < a.length-1;i+=2) {
			byte t = a[i];
			a[i] = a[i+1];
			a[i+1] = t;
		}
		String result = new String(a, "UTF-16");
		return result;
	}
	
	private RandomAccessFile inflate() throws IOException {
		File f = File.createTempFile("tmp", "tmp");
		byte_content.position(0);
		InflaterInputStream decoder = new InflaterInputStream(new ByteArrayInputStream(byte_content.array()), new Inflater(true), 4096);
		byte[] buffer = new byte[4096];
		int read = -1;
		OutputStream out = new FileOutputStream(f);
		while((read = decoder.read(buffer))!=-1) {
			out.write(buffer, 0, read);
		}
		decoder.close();
		out.close();
		RandomAccessFile file = new RandomAccessFile(f, "rw");
		return file;
	}
	
	public boolean isContainer() throws IOException {
		if(!deflate)
			return byte_content.get(0) == -1
				&& byte_content.get(1) == -1
				&& byte_content.get(2) == -1
				&& byte_content.get(3) == 127;
		else {
			dataInput.seek(0);
			return dataInput.readByte() == -1
				&& dataInput.readByte() == -1
				&& dataInput.readByte() == -1
				&& dataInput.readByte() == 127;
		}
	}
	
	public boolean isDeflate() {
		return deflate;
	}
	
	
	public boolean isProperty() throws IOException {
		if(!deflate)
			return byte_content.get(0) == -17
				&& byte_content.get(1) == -69
				&& byte_content.get(2) == -65
				&& byte_content.get(3) == '{';
		else {
			dataInput.seek(0);
			return dataInput.readByte() == -17
				&& dataInput.readByte() == -69
				&& dataInput.readByte() == -65
				&& dataInput.readChar() == '{';
		}
	}
	public boolean isText() throws IOException {
		if(!deflate)
			return byte_content.get(0) == -17
				&& byte_content.get(1) == -69
				&& byte_content.get(2) == -65;
		else {
			dataInput.seek(0);
			return dataInput.readByte() == -17
				&& dataInput.readByte() == -69
				&& dataInput.readByte() == -65;
		}
	}


	private void readContent(RandomAccessFile dataInput) throws IOException,
			DataFormatException {
		String nl = dataInput.readLine(); 
		if(nl==null || !nl.isEmpty()) {
			throw new DataFormatException();
		}
		nl = dataInput.readLine();
		String[] buf = nl.split(" ");
		if(buf.length != 3) {
			throw new DataFormatException();
		}
		total = Long.parseLong(buf[0], 16);
		Content content = null;
		byte_content = ByteBuffer.allocate((int) total); 
		long total = this.total;
		do {
			content = new Content();
			content.lenght = Long.parseLong(buf[1], 16);
			content.next   = Long.parseLong(buf[2], 16);
			byte[] buffer = new byte[(int) Math.min(total, content.lenght)];
			total -= dataInput.read(buffer);
			if(content.next != MAGIC_NUMBER) {
				dataInput.seek(content.next);
			}
			byte_content.put(buffer);
		} while(content.next != MAGIC_NUMBER);
	}
	
	public void setText(String text) throws IOException {
		if(deflate) {
			dataInput.seek(3);
			dataInput.write(text.getBytes("UTF-8"));
		} else {
			byte_content = ByteBuffer.wrap(text.getBytes("UTF-8")); 
		}
	}

}
