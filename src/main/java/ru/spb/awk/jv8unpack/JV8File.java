/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.awk.jv8unpack;

import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.ObjectInputStream.GetField;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

/**
 *
 * @author Василий Казьмин
 */
class JV8File {

    private static final int CHUNK = 16384;
    private static final int Z_NULL = 0;
    private static final int MAX_WBITS = 15;

    static void Inflate(String in_filename, String out_filename) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static void Deflate(String in_filename, String out_filename) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static void UnpackToFolder(String in_filename, String out_filename) throws IOException, DataFormatException {
    	Container c = new Container(in_filename);
    	File out_dir = new File(out_filename);
    	if(!out_dir.exists()) {
    		out_dir.mkdir();
    	} else {
    		if(!out_dir.isDirectory()) {
    			throw new IllegalArgumentException(out_filename);
    		}
    		out_dir.delete();
    		out_dir.mkdir();
    	}
    	parse(c, out_dir);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static void PackFromFolder(String in_filename, String out_filename) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static void Parse(String in_filename, String out_filename) throws IOException, DataFormatException {   	
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	private static void parse(Container c, File out_dir)
			throws DataFormatException, IOException, FileNotFoundException,
			UnsupportedEncodingException {
		for(AttributeDocument child : c.list()) {
    		UnpackToFolder(out_dir, child);
    	}
	}

	private static void UnpackToFolder(File out_dir, AttributeDocument child)
			throws DataFormatException, IOException, FileNotFoundException,
			UnsupportedEncodingException {
		Document doc = child.getContent();
		if(doc instanceof ContentDocument) {
			ContentDocument contDoc = (ContentDocument) doc;
			File out = new File(out_dir, child.getName());
			for(AttributeDocument ch :contDoc.getContent()) {
				UnpackToFolder(out, child);
			}
		} else if (doc instanceof TextDocument ) {
			TextDocument contDoc = (TextDocument) doc;
			Writer os = new FileWriter(new File(out_dir, child.getName().trim()));
			Reader reader = contDoc.getInputStream();
			char[] cbuf = new char[256];
			int s = 0;
			boolean st = true;
			boolean prop = false;
			char ch = (char) reader.read();
			if(ch == '{') { 
				prop = true;
				os.append('[');
			} else os.append(ch);
			
			while((s = reader.read(cbuf)) >0 ) {
				for(int i=0;prop && i<s;i++) {
					switch (cbuf[i]) {
					case '"':
						st = !st;
						break;
					case '{':
						if(st) cbuf[i] = '[';
						break;
					case '}':
						if(st) cbuf[i] = ']';
						break;
					case '\n':
					case '\r':
					case '\t':
						if(st) cbuf[i] = ' ';
						break;

					default:
						break;
					}
				}
				os.write(cbuf, 0, s);
			}
			os.close();
		/*} else if (doc instanceof TextDocument ) {
			TextDocument contDoc = (TextDocument) doc;
			OutputStream os = null;
			File file = new File(out_dir, child.getName().trim());
			try {
				os = new FileOutputStream(file);
			} catch (FileNotFoundException fnf) {
				throw new FileNotFoundException(Helper.getRepresintation(file));
			}
			os.write(contDoc.getText().getBytes("utf-8"));
			os.close();*/
		} else if (doc instanceof RawDocument) {
			RawDocument contDoc = (RawDocument) doc;
			OutputStream os = new FileOutputStream(new File(out_dir, child.getName()));
			// TODO:
			os.close();
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	}

    static int BuildCfFile(String in_filename, String out_filename) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



}
