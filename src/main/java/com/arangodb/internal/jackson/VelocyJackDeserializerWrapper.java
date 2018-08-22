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

import java.io.IOException;

import com.arangodb.velocypack.VPackDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @author Mark Vollmary
 *
 */
public class VelocyJackDeserializerWrapper<T> extends StdDeserializer<T> {

	private static final long serialVersionUID = 1L;
	private final VPackDeserializer<T> deserializer;

	public VelocyJackDeserializerWrapper(final VPackDeserializer<T> deserializer, final Class<?> vc) {
		super(vc);
		this.deserializer = deserializer;
	}

	@Override
	public T deserialize(final JsonParser p, final DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		// TODO
		// deserializer.deserialize(parent, vpack, context);
		return null;
	}

}
