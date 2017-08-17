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

import org.jebtk.core.IdProperty;

// TODO: Auto-generated Javadoc
/**
 * The Class Entity.
 */
public class Entity implements IdProperty {
	
	/** The m id. */
	protected int mId;

	/**
	 * Instantiates a new entity.
	 *
	 * @param id the id
	 */
	public Entity(int id) {
		mId = id;
	}

	/* (non-Javadoc)
	 * @see org.abh.common.IdProperty#getId()
	 */
	@Override
	public int getId() {
		return mId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Entity) {
			return mId == ((Entity)o).mId;
		} else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return mId;
	}
}
