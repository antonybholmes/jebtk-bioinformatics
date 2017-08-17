/**
 * Copyright 2017 Antony Holmes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jebtk.bioinformatics.annotation;

import org.jebtk.core.NameProperty;

// TODO: Auto-generated Javadoc
/**
 * The Class Type.
 */
public class Type extends Entity implements Comparable<Type>, NameProperty {
	
	/** The m name. */
	protected String mName;

	/**
	 * Instantiates a new type.
	 *
	 * @param id the id
	 * @param name the name
	 */
	public Type(int id, String name) {
		super(id);
		
		mName = name;
	}
	
	/* (non-Javadoc)
	 * @see org.abh.common.NameProperty#getName()
	 */
	@Override
	public String getName() {
		return mName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return mName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Type t) {
		return mName.compareTo(t.mName);
	}
	
	/* (non-Javadoc)
	 * @see org.jebtk.bioinformatics.annotation.Entity#hashCode()
	 */
	@Override
	public int hashCode() {
		return mName.hashCode();
	}
	
	
}
