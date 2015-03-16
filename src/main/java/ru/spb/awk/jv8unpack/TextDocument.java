package ru.spb.awk.jv8unpack;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.channels.Channels;
import java.util.zip.DataFormatException;

public class TextDocument implements Document {

	private final Cluster raw;
	private final Container _parent;

	public TextDocument(Container container, Cluster content) {
		raw = content;
		_parent = container;
	}
	
	public String getType() {
		return getClass().getSimpleName();
	}

	public String getText() throws IOException {
		return raw.getText();
	}

	public Reader getInputStream() throws IOException {
		RandomAccessFile data = raw.getDataInput();
		data.seek(3);
		InputStream is = Channels.newInputStream(data.getChannel());
		return new InputStreamReader(is,"UTF-8");
		//return is;
	}

	public Container getParent() {
		return _parent;
	}

	public void write(String text) throws IOException, DataFormatException {
		raw.setText(text);
		getParent().write();
	}

	public void write() throws DataFormatException, IOException {
		
	}

}
