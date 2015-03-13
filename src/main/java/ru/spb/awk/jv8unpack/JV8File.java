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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Василий Казьмин
 */
class JV8File {

    private static final int CHUNK = 16384;
    private static final int Z_NULL = 0;
    private static final int MAX_WBITS = 15;

    static void Inflate(String in_filename, String out_filename) throws FileNotFoundException {
        InputStream in_file = new FileInputStream(in_filename);
        OutputStream out_file = new FileOutputStream(out_filename);

        Inflate(in_file, out_file);
    }

    static void Deflate(String string, String string0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static void UnpackToFolder(String string, String string0, String string1, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static void PackFromFolder(String string, String string0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static void Parse(String in_filename, String out_filename) throws IOException {
        InputStream file_in = new FileInputStream(in_filename);

        long FileDataSize = new File(in_filename).length();

        UnpackToDirectoryNoLoad(out_filename, file_in, FileDataSize);
        System.out.println("LoadFile: ok");
    }

    static int BuildCfFile(String string, String string0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static int Inflate(InputStream source, OutputStream dest) {
        int have = 0;
        byte[] in = new byte[CHUNK];
        byte[] out = new byte[CHUNK];

        // allocate inflate state
        Inflater strm = new Inflater();

        int ret = strm.init(-JZlib.MAX_WBITS);
        if (ret != JZlib.Z_OK) {
            return ret;
        }
        do {
            try {
                strm.avail_in = source.read(in, 0, CHUNK);
            } catch (IOException ex) {
                Logger.getLogger(JV8File.class.getName()).log(Level.SEVERE, null, ex);
                strm.inflateEnd();
                return JZlib.Z_ERRNO;
            }
            if (strm.avail_in == 0) {
                break;
            }

            strm.next_in = in;

            // run inflate() on input until output buffer not full
            do {
                strm.avail_out = CHUNK;
                strm.next_out = out;
                ret = strm.inflate(JZlib.Z_NO_FLUSH);
                assert (ret != JZlib.Z_STREAM_ERROR);  // state not clobbered
                switch (ret) {
                    case JZlib.Z_NEED_DICT:
                        ret = JZlib.Z_DATA_ERROR;     // and fall through
                    case JZlib.Z_DATA_ERROR:
                    case JZlib.Z_MEM_ERROR:
                        strm.inflateEnd();
                        return ret;
                }
                have = CHUNK - strm.avail_out;
                try {
                    dest.write(out, 0, have);
                } catch (IOException ex) {
                    Logger.getLogger(JV8File.class.getName()).log(Level.SEVERE, null, ex);
                    strm.inflateEnd();
                    return JZlib.Z_ERRNO;
                }
            } while (strm.avail_out == 0);

            // done when inflate() says it's done
        } while (ret != JZlib.Z_STREAM_END);
        // clean up and return
        strm.inflateEnd();
        return JZlib.Z_OK;
    }

    private static int UnpackToDirectoryNoLoad(String directory, InputStream file, long FileDataSize) throws IOException {
        int ret = 0;
        OutputStream p_dir = new FileOutputStream(directory);
        FileHeader fileHeader = new FileHeader();
        fileHeader.init(file);
        

        BlockHeader blockHeader = new BlockHeader();
        blockHeader.init(file);

        long ElemsAddrsSize;
        ElemAddr elemsAddrs = null;
        ReadBlockData(file, blockHeader, elemsAddrs, ElemsAddrsSize);

        long ElemsNum = ElemsAddrsSize / ElemAddr.size();

        Elems.clear();

        for (long i = 0; i < ElemsNum; i++) {

            if (pElemsAddrs[i].fffffff != V8_FF_SIGNATURE) {
                ElemsNum = i;
                break;
            }

            file.seekg(pElemsAddrs[i].elem_header_addr, std::ios_base::beg);
            file.read((char*)&BlockHeader, sizeof(BlockHeader));

            if (pBlockHeader->EOL_0D != 0x0d ||
                    pBlockHeader->EOL_0A != 0x0a ||
                    pBlockHeader->space1 != 0x20 ||
                    pBlockHeader->space2 != 0x20 ||
                    pBlockHeader->space3 != 0x20 ||
                    pBlockHeader->EOL2_0D != 0x0d ||
                    pBlockHeader->EOL2_0A != 0x0a) {

                ret = V8UNPACK_HEADER_ELEM_NOT_CORRECT;
                break;
            }

            CV8Elem elem;
            ReadBlockData(file, pBlockHeader, elem.pHeader, &elem.HeaderSize);

            char ElemName[512];
            UINT ElemNameLen;

            GetElemName(elem, ElemName, &ElemNameLen);

            boost::filesystem::path elem_path(p_dir / ElemName);

            boost::filesystem::ofstream o_tmp(p_dir / ".v8unpack.tmp", std::ios_base::binary);

            //080228 Блока данных может не быть, тогда адрес блока данных равен 0x7fffffff
            if (pElemsAddrs[i].elem_data_addr != V8_FF_SIGNATURE) {

                file.seekg(pElemsAddrs[i].elem_data_addr, std::ios_base::beg);
                file.read((char*)&BlockHeader, sizeof(BlockHeader));

                ReadBlockData(file, pBlockHeader, o_tmp, &elem.DataSize);
            } else {
                // TODO: Зачем это нужно??
                ReadBlockData(file, NULL, o_tmp, &elem.DataSize);
            }

            o_tmp.close();

            boost::filesystem::ifstream i_tmp(p_dir / ".v8unpack.tmp", std::ios_base::binary);

            elem.UnpackedData.IsDataPacked = false;

            if (boolInflate && IsDataPacked) {

                boost::filesystem::ofstream o_inf(p_dir / ".v8unpack.inf", std::ios_base::binary);
                ret = Inflate(i_tmp, o_inf);
                o_inf.close();

                boost::filesystem::ifstream i_inf(p_dir / ".v8unpack.inf", std::ios_base::binary);

                if (ret)
                    IsDataPacked = false;
                else {

                    elem.NeedUnpack = false; // отложенная распаковка не нужна
                    if (IsV8File(i_inf)) {

                        ret = elem.UnpackedData.UnpackToDirectoryNoLoad(elem_path.string(), i_inf, 0, boolInflate);
                        if (ret)
                            break;

                    } else {
                        boost::filesystem::ofstream out(elem_path, std::ios_base::binary);

                        i_inf.seekg(0, std::ios_base::beg);
                        i_inf.clear();

                        while (i_inf) {

                            const int buf_size = 1024;
                            char buf[buf_size];
                            int rd = i_inf.readsome(buf, buf_size);

                            if (rd)
                                out.write(buf, rd);
                            else
                                break;
                        }
                    }
                    ret = 0;
                }

            } else {

                i_tmp.seekg(0, std::ios_base::beg);
                i_tmp.clear();

                boost::filesystem::ofstream out(elem_path, std::ios_base::binary);
                while (!i_tmp.eof()) {

                    const int buf_size = 1024;
                    char buf[buf_size];
                    int rd = i_tmp.readsome(buf, buf_size);

                    if (rd)
                        out.write(buf, rd);
                    else
                        break;
                }
                ret = 0;
            }

        } // for i = ..ElemsNum

        if (boost::filesystem::exists(p_dir / ".v8unpack.inf"))
            boost::filesystem::remove(p_dir / ".v8unpack.inf");

        if (boost::filesystem::exists(p_dir / ".v8unpack.tmp"))
            boost::filesystem::remove(p_dir / ".v8unpack.tmp");

        return ret;
    }

}
