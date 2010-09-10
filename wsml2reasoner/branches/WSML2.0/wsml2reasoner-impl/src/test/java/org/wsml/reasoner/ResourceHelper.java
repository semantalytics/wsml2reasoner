package org.wsml.reasoner;

import java.io.InputStream;
import java.util.Scanner;

public class ResourceHelper {

	private static final String NEW_LINE = System.getProperty("line.separator");

	public static String loadResource(String resourceName) {
		InputStream input = loadResourceAsStream(resourceName);
		Scanner scanner = new Scanner(input);

		StringBuilder buffer = new StringBuilder();

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			buffer.append(line);
			buffer.append(NEW_LINE);
		}

		return buffer.toString();
	}
	
	public static InputStream loadResourceAsStream(String resourceName) {
		ClassLoader loader = ResourceHelper.class.getClassLoader();

		InputStream input = loader.getResourceAsStream(resourceName);
		
		return input;
	}

}
