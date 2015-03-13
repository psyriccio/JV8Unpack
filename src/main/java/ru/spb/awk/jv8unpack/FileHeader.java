/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.spb.awk.jv8unpack;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Василий Казьмин
 */
class FileHeader {

    public FileHeader() {
    }

    void init(InputStream file) throws IOException {
        byte[] buf = new byte[16];
        file.read(buf, 0, 16);
    }
    
}
