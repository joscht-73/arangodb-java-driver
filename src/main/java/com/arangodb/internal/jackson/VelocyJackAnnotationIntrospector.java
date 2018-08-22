/*
 * DISCLAIMER
 *
 * Copyright 2018 ArangoDB GmbH, Cologne, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 */

package com.arangodb.internal.jackson;

import com.arangodb.entity.DocumentField;
import com.arangodb.velocypack.annotations.Expose;
import com.arangodb.velocypack.annotations.SerializedName;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * @author Mark Vollmary
 *
 */
@SuppressWarnings("deprecation")
public class VelocyJackAnnotationIntrospector extends JacksonAnnotationIntrospector {

	private static final long serialVersionUID = 1L;

	@Override
	public PropertyName findNameForSerialization(final Annotated a) {
		final DocumentField df = _findAnnotation(a, DocumentField.class);
		if (df != null) {
			return PropertyName.construct(df.value().getSerializeName());
		}
		final SerializedName sn = _findAnnotation(a, SerializedName.class);
		if (sn != null) {
			return PropertyName.construct(sn.value());
		}
		return super.findNameForSerialization(a);
	}

	@Override
	public PropertyName findNameForDeserialization(final Annotated a) {
		final DocumentField df = _findAnnotation(a, DocumentField.class);
		if (df != null) {
			return PropertyName.construct(df.value().getSerializeName());
		}
		final SerializedName sn = _findAnnotation(a, SerializedName.class);
		if (sn != null) {
			return PropertyName.construct(sn.value());
		}
		return super.findNameForDeserialization(a);
	}

	@Override
	protected boolean _isIgnorable(final Annotated a) {
		final boolean ignorable = super._isIgnorable(a);
		if (ignorable) {
			return ignorable;
		}
		// TODO differ between serialzie and deserialize
		final Expose expose = _findAnnotation(a, Expose.class);
		return expose != null && (!expose.serialize() || !expose.deserialize());
	}

}
