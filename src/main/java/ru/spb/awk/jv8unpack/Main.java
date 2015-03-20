/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.awk.jv8unpack;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.DataFormatException;

/**
 *
 * @author Василий Казьмин
 */
public class Main {
    private static final String V8P_VERSION="1.0";
    private static final String V8P_RIGHT="awk";
    private static int SHOW_USAGE;

    public static void usage() {
        System.out.println();
        System.out.println("V8Upack Version " + V8P_VERSION
                + " Copyright (c) " + V8P_RIGHT);

        System.out.println();
        System.out.println("Unpack, pack, deflate and inflate 1C v8 file (*.cf)");
        System.out.println();
        System.out.println("V8UNPACK");
        System.out.println("  -U[NPACK]     in_filename.cf     out_dirname");
        System.out.println("  -PA[CK]       in_dirname         out_filename.cf");
        System.out.println("  -I[NFLATE]    in_filename.data   out_filename");
        System.out.println("  -D[EFLATE]    in_filename        filename.data");
        System.out.println("  -E[XAMPLE]");
        System.out.println("  -BAT");
        System.out.println("  -P[ARSE]      in_filename        out_dirname");
        System.out.println("  -B[UILD]      in_dirname         out_filename");
        System.out.println("  -V[ERSION]");
    }

    public static void version() {
        System.out.println(V8P_VERSION);
    }

    public static void main(String[] argv) throws DataFormatException, IOException {

	String cur_mode="";
        int argc = argv.length;
        if (argc > 0) {
            cur_mode = argv[0].toLowerCase();
        }


        if ("-version".equals(cur_mode) || "-v".equals(cur_mode)) {

            version();
            return;
        }

        if ("-inflate".equals(cur_mode) || "-i".equals(cur_mode) || "-und".equals(cur_mode) || "-undeflate".equals(cur_mode)) {

            
            try {
				JV8File.Inflate(argv[1], argv[2]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
            return;
        }

        if ("-deflate".equals(cur_mode) || "-d".equals(cur_mode)) {

            
            JV8File.Deflate(argv[1], argv[2]);
            return;
        }

        if ("-unpack".equals(cur_mode) || "-u".equals(cur_mode) || "-unp".equals(cur_mode)) {

            
            JV8File.UnpackToFolder(argv[1], argv[2]);
            return;
        }

        if ("-pack".equals(cur_mode) || "-pa".equals(cur_mode)) {

            
            JV8File.PackFromFolder(argv[1], argv[2]);
            return;
        }

        if ("-parse".equals(cur_mode) || "-p".equals(cur_mode)) {

            
            try {
				JV8File.Parse(argv[1], argv[2]);
			} catch (IOException e) {
				e.printStackTrace();
			}
            return;
        }

        if ("-build".equals(cur_mode) || "-b".equals(cur_mode)) {

            

            int ret = JV8File.BuildCfFile(argv[1], argv[2]);
            if (ret == SHOW_USAGE) {
                usage();
            }

            return;
        }

        if ("-bat".equals(cur_mode)) {

            System.out.println("if %1 == P GOTO PACK");
            System.out.println("if %1 == p GOTO PACK");
            System.out.println("");
            System.out.println("");
            System.out.println(":UNPACK");
            System.out.println("V8Unpack.exe -unpack      %2                              %2.unp");
            System.out.println("V8Unpack.exe -undeflate   %2.unp\\metadata.data            %2.unp\\metadata.data.und");
            System.out.println("V8Unpack.exe -unpack      %2.unp\\metadata.data.und        %2.unp\\metadata.unp");
            System.out.println("GOTO END");
            System.out.println("");
            System.out.println("");
            System.out.println(":PACK");
            System.out.println("V8Unpack.exe -pack        %2.unp\\metadata.unp            %2.unp\\metadata_new.data.und");
            System.out.println("V8Unpack.exe -deflate     %2.unp\\metadata_new.data.und   %2.unp\\metadata.data");
            System.out.println("V8Unpack.exe -pack        %2.unp                         %2.new.cf");
            System.out.println("");
            System.out.println("");
            System.out.println(":END");

            return;
        }

        if ("-example".equals(cur_mode) || "-e".equals(cur_mode)) {

            System.out.println("");
            System.out.println("");
            System.out.println("UNPACK");
            System.out.println("V8Unpack.exe -unpack      1Cv8.cf                         1Cv8.unp");
            System.out.println("V8Unpack.exe -undeflate   1Cv8.unp\\metadata.data          1Cv8.unp\\metadata.data.und");
            System.out.println("V8Unpack.exe -unpack      1Cv8.unp\\metadata.data.und      1Cv8.unp\\metadata.unp");
            System.out.println("");
            System.out.println("");
            System.out.println("PACK");
            System.out.println("V8Unpack.exe -pack        1Cv8.unp\\metadata.unp           1Cv8.unp\\metadata_new.data.und");
            System.out.println("V8Unpack.exe -deflate     1Cv8.unp\\metadata_new.data.und  1Cv8.unp\\metadata.data");
            System.out.println("V8Unpack.exe -pack        1Cv8.und                        1Cv8_new.cf");
            System.out.println("");
            System.out.println("");

            return;
        }

        usage();
    }
}
