package ru.spb.awk.jv8unpack;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;


public class ContentDocument implements Document, Closeable {
	
	
	
	public RandomAccessFile dataInput;

	private final Cluster raw;
	private Set<AttributeDocument> attributeSet;

	private final Container _parent;
	
	public ContentDocument(Container container, Cluster cluster) {
		_parent = container;
		raw = cluster;
		dataInput = cluster.getDataInput();
	}

	public Set<AttributeDocument> getContent() throws DataFormatException, IOException {
		if(attributeSet == null) {
			attributeSet = new HashSet<AttributeDocument>();
			for(int i=0;i<raw.getTotal();i+=4) {
				long aOffset = raw.getLong(i);
				i+=4;
				long cOffset = raw.getLong(i);
				i+=4;
				if(raw.getLong(i) != Cluster.MAGIC_NUMBER) {
					throw new DataFormatException();
				}
				attributeSet.add(new AttributeDocument(this, aOffset, cOffset));
			}
		}
		return attributeSet;
	}

	public void close() throws IOException {
		try {
			for(AttributeDocument doc: getContent()) {
				if(doc.getContent() instanceof Container) {
					((Container)doc.getContent()).close();
				}
			}
		} catch (DataFormatException e) {
			e.printStackTrace();
		}
		raw.close();
	}


	public String getType() {
		return getClass().getSimpleName();
	}

	public Container getParent() {
		return _parent;
	}

	public AttributeDocument getDocumentByName(String name) throws DataFormatException, IOException {
		for(AttributeDocument doc : getContent()) {
			if(doc.getName().trim().equalsIgnoreCase(name.trim())) {
				return doc;
			}
		}
		return null;
	}

	public void write() throws DataFormatException, IOException {
		RandomAccessFile dataInput = raw.getDataInput();
		long pos = dataInput.getFilePointer();
		for(AttributeDocument d:getContent()) {
			d.write();
			writeLong(dataInput, d.getAttributeOffset());
			writeLong(dataInput, d.getContentOffset());
			writeLong(dataInput, Cluster.MAGIC_NUMBER);
		}
	}

	private void writeLong(RandomAccessFile dataInput2, long magicNumber) throws IOException {
		long l = Long.reverseBytes(magicNumber);
		dataInput2.writeLong(l);		
	}


}
