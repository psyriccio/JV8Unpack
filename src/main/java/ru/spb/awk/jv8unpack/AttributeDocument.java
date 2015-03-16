package ru.spb.awk.jv8unpack;

import java.io.IOException;
import java.util.Date;
import java.util.zip.DataFormatException;

public class AttributeDocument {
	private static final int NAME_OFFSET = 20;
	private static final int MODIFY_OFFSET = 8;
	private static final int CREATE_OFFSET = 0;
	/**
	 * 
	 */
	private final ContentDocument _parent;
	private Cluster raw;
	private Date createdOn;
	private Date modifyOn;
	private final long reserved = 0;
	private final String name;
	private final Document content;
	private long attr_offset;
	private long content_offset;
	
	
	public AttributeDocument(ContentDocument document, long attr_offset, long content_offset) throws IOException, DataFormatException {
		_parent = document;
		this.attr_offset = attr_offset;
		this.content_offset = content_offset;
		_parent.dataInput.seek(attr_offset);
		raw = new Cluster(_parent.dataInput, false);
		createdOn = raw.getDate(CREATE_OFFSET);
		modifyOn =  raw.getDate(MODIFY_OFFSET);
		name = raw.getUtf16(NAME_OFFSET);
		content = DocumentBuilder.build((Container) _parent.getParent(), _parent.dataInput, content_offset, _parent.getParent().isRoot());
	}
	
	public Document getContent() {
		return content;
	}
	public String getName() {
		return name;
	}

	public void write() throws IOException, DataFormatException {
		_parent.dataInput.seek(attr_offset);
		_parent.dataInput.write(toBytes(createdOn));
		_parent.dataInput.write(toBytes(new Date()));
		_parent.dataInput.write(name.trim().getBytes("UTF-16"));
		content.write();
	}

	private byte[] toBytes(Date date) {
		return new byte[8];
	}

	public long getAttributeOffset() {
		return attr_offset;
	}

	public long getContentOffset() {
		return content_offset;
	}
}