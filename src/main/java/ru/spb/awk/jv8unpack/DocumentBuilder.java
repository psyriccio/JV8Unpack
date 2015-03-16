package ru.spb.awk.jv8unpack;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.DataFormatException;

public class DocumentBuilder {

	public static Document build(Container container, RandomAccessFile dataInput, long cOffset, boolean deflate) throws IOException, DataFormatException {
		dataInput.seek(cOffset);
		Cluster content = new Cluster(dataInput, deflate);
		if(content.isContainer()) {
			return new Container(container, content);
		} else if(content.isText()) {
			return new TextDocument(container, content);
		} 
		return new RawDocument(container, content);
	}
	
}