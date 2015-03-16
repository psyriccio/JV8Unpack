package ru.spb.awk.jv8unpack;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;


public class PropertyDocument extends TextDocument implements Document {
		
	private List<Object> list;

	/**
	 * item = item (, item)*
	 * item = "{" (digit | string | item)* "}"
	 * digit = [0-9]+
	 * string = "\""[^"]"\""("\""string)*
	 * @param content
	 * @throws DataFormatException 
	 * @throws IOException 
	 */

	public PropertyDocument(Container container, Cluster content) throws IOException, DataFormatException {
		super(container, content);
		parse();
	}

	private void parse() throws IOException, DataFormatException {

	}



	public String getType() {
		return getClass().getSimpleName();
	}

}
