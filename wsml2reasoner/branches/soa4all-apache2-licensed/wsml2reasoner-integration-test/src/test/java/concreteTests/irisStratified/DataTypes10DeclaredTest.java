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
package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractDataTypes10Declared;

public class DataTypes10DeclaredTest extends AbstractDataTypes10Declared
{
	public LPReasoner getLPReasoner()
	{
		return Reasoner.get();
	}

}
