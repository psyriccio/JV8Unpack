package ru.spb.awk.jv8unpack;

import java.io.File;

public class Helper {

	public static String getRepresintation(File pFile) {
		StringBuilder sb = new StringBuilder();
		sb.append("name:");
		sb.append(pFile.getName());
		sb.append("\n");
		sb.append("absolute path:");
		sb.append(pFile.getAbsolutePath());
		sb.append("\n");
		return sb.toString();
	}

}
