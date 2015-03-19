package ru.spb.awk.jv8unpack;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.zip.DataFormatException;


public class Container implements Document, Closeable {

	private final byte[] head;
	private final ContentDocument content;
	private Container _parent;
	private Cluster cluster;
	
	
	
	protected Container(Container parent, Cluster cluster) throws IOException, DataFormatException {
		_parent = parent;
		this.cluster = cluster;
		RandomAccessFile dataInput = cluster.getDataInput();
		dataInput.seek(0);
		head = new byte[16];
		dataInput.read(head);
		content = new ContentDocument(this, new Cluster(dataInput, false));
	}
	
	public Container(String file_name) throws IOException, DataFormatException {
		File file = new File(file_name);
		RandomAccessFile dataInput = new RandomAccessFile(file, "rw");
		head = new byte[16];
		if(dataInput.read(head)!=16) {
			dataInput.close();
			throw new EOFException(file.getAbsolutePath());
		}
		this.cluster = new Cluster(dataInput, false);
		content = new ContentDocument(this, cluster);
	}
	
	public void close() throws IOException {
		content.close();
	}
	
	public AttributeDocument getDocumentByName(String name) throws DataFormatException, IOException {
		return content.getDocumentByName(name);
	}
	
	public Container getParent() {
		return _parent;
	}
	public String getType() {
		return getClass().getSimpleName();
	}

	public boolean isRoot() {
		return _parent == null;
	}

	public Set<AttributeDocument> list() throws DataFormatException, IOException {
		return content.getContent();
	}

	public void write() throws DataFormatException, IOException {
		if(_parent!=null) {
			getParent().write();
		} else {
			RandomAccessFile dataInput = cluster.getDataInput();
			dataInput.seek(0);
			dataInput.write(head);
			content.write();
		}
	}
	
}
