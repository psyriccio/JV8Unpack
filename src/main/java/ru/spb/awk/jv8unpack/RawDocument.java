package ru.spb.awk.jv8unpack;

import java.io.IOException;
import java.util.zip.DataFormatException;

public class RawDocument implements Document {

	private Cluster raw;
	private Container _parent;

	public RawDocument(Container container, Cluster content) {
		raw = content;
		_parent = container;
	}

	public String getType() {
		return getClass().getSimpleName();
	}

	public Container getParent() {
		return _parent;
	}

	public void write() throws DataFormatException, IOException {
		if(_parent.isRoot()) {
			
		}
	}


}
