package edu.emory.cci.aiw.umls;

import java.util.logging.Logger;

public final class UMLSUtil {
	private static class LazyLoggerHolder {
		private static Logger instance = Logger.getLogger(UMLSUtil.class
		        .getPackage().getName());
	}

	private UMLSUtil() {

	}

	/**
	 * Gets the logger for this package
	 * 
	 * @return a <code>Logger</code> object.
	 */
	static Logger logger() {
		return LazyLoggerHolder.instance;
	}
}
