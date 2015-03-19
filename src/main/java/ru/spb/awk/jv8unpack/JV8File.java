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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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

    static void UnpackToFolder(String in_filename, String string0, String string1, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static void PackFromFolder(String in_filename, String out_filename) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static void Parse(String in_filename, String out_filename) throws IOException, DataFormatException {   	
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	private static void parse(Container c, File out_dir)
			throws DataFormatException, IOException, FileNotFoundException,
			UnsupportedEncodingException {
		for(AttributeDocument child : c.list()) {
    		parse(out_dir, child);
    	}
	}

	private static void parse(File out_dir, AttributeDocument child)
			throws DataFormatException, IOException, FileNotFoundException,
			UnsupportedEncodingException {
		if(!out_dir.exists()) {
			out_dir.mkdir();
		}
		out_dir.setWritable(true);
		Document doc = child.getContent();
		if(doc instanceof ContentDocument) {
			ContentDocument contDoc = (ContentDocument) doc;
			File out = new File(out_dir, child.getName());
			for(AttributeDocument ch :contDoc.getContent()) {
				parse(out, child);
			}
		} else if (doc instanceof TextDocument ) {
			new File(out_dir + File.separator + child.getName()).createNewFile();
			OutputStream os = new FileOutputStream(out_dir + File.separator + child.getName());
			TextDocument contDoc = (TextDocument) doc;
			os.write(contDoc .getText().getBytes("utf-8"));
			os.close();
		} else if (doc instanceof PropertyDocument ) {
			TextDocument contDoc = (TextDocument) doc;
			OutputStream os = new FileOutputStream(new File(out_dir, child.getName()));
			os.write(contDoc.getText().getBytes("utf-8"));
			os.close();
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
