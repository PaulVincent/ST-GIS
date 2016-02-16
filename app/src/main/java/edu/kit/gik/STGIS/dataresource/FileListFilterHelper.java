package edu.kit.gik.STGIS.dataresource;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileListFilterHelper implements FilenameFilter {

		private String name;

		private List<String> extension = new ArrayList<String>();

		public FileListFilterHelper(String name, String[] ext) {
			this.name = name;
			this.extension = Arrays.asList(ext);
		}

		public boolean accept(File directory, String filename) {
			boolean fileOK = true;

			if (name != null) {
				fileOK &= filename.startsWith(name);
			}

			if (extension != null) {

				if (filename.contains(".")) {
					String[] splitname = filename.split("\\.");
					fileOK &= extension.contains(splitname[1]);
				} else {
					fileOK = false;
				}
			}
			return fileOK;
		}
	}