/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
