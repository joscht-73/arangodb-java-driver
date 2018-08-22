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

import com.arangodb.velocypack.VPackBuilder;
import com.arangodb.velocypack.VPackSerializationContext;
import com.arangodb.velocypack.VPackSerializer;
import com.arangodb.velocypack.exception.VPackBuilderException;
import com.arangodb.velocypack.exception.VPackParserException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @author Mark Vollmary
 *
 */
public class VelocyJackSerializerWrapper<T> extends StdSerializer<T> {

	private static final long serialVersionUID = 1L;
	private final VPackSerializer<T> serializer;
	private final ObjectMapper mapper;

	public VelocyJackSerializerWrapper(final ObjectMapper mapper, final VPackSerializer<T> serializer,
		final Class<T> t) {
		super(t);
		this.mapper = mapper;
		this.serializer = serializer;
	}

	@Override
	public void serialize(final T value, final JsonGenerator gen, final SerializerProvider provider)
			throws IOException {
		final VPackBuilder builder = new VPackBuilder();
		final VPackSerializationContext context = new VPackSerializationContext() {
			@Override
			public void serialize(final VPackBuilder builder, final String attribute, final Object entity)
					throws VPackParserException {
				try {
					builder.add(attribute, mapper.writeValueAsBytes(entity));
				} catch (VPackBuilderException | JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		};
		serializer.serialize(builder, null, value, context);
	}

}
