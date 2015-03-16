package ru.spb.awk.jv8unpack;

import java.io.IOException;
import java.util.zip.DataFormatException;



public interface Document {
	String getType();
	Document getParent();
	void write() throws DataFormatException, IOException;
}
