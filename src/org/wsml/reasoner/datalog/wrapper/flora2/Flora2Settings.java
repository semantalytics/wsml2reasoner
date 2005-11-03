/**
 * WSML Reasoner Implementation based on FLORA2.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package org.wsml.reasoner.datalog.wrapper.flora2;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * A helper class for loading the paths for Flora
 * 
 * @author Ioan Toma, DERI Innsbruck, Austria
 */

public class Flora2Settings {

	private String floraPath = null;

	private String xsbPath = null;

	private String FLORA_PROPERTY_FILE = "./conf/flora.properties";

	private Properties floraXSBProperties = null;

	public Flora2Settings() {
		setProperties();
		setPaths();
	}

	private void setProperties() {
		Properties prop = new Properties();
		try {
			InputStream is = new FileInputStream(FLORA_PROPERTY_FILE);
			prop.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		floraXSBProperties = new Properties(prop);
	}

	private void setPaths() {
		if (floraXSBProperties == null) {
			System.out.println("Flora Properties Not Found");
			return;
		}
		xsbPath = floraXSBProperties.getProperty("xsbDir");
		floraPath = floraXSBProperties.getProperty("floraDir");
	}

	public String getFloraPath() {
		return floraPath;
	}

	public void setFloraPath(String floraPath) {
		this.floraPath = floraPath;
	}

	public String getXsbPath() {
		return xsbPath;
	}

	public void setXsbPath(String xsbPath) {
		this.xsbPath = xsbPath;
	}
}
