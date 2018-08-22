/*
 * DISCLAIMER
 *
 * Copyright 2016 ArangoDB GmbH, Cologne, Germany
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

package com.arangodb.internal.velocypack;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.BaseEdgeDocument;
import com.arangodb.entity.CollectionType;
import com.arangodb.entity.LogLevel;
import com.arangodb.entity.Permissions;
import com.arangodb.entity.ReplicationFactor;
import com.arangodb.entity.ViewType;
import com.arangodb.entity.arangosearch.ArangoSearchProperties;
import com.arangodb.entity.arangosearch.CollectionLink;
import com.arangodb.entity.arangosearch.ConsolidateThreshold;
import com.arangodb.entity.arangosearch.FieldLink;
import com.arangodb.entity.arangosearch.StoreValuesType;
import com.arangodb.internal.velocystream.internal.AuthenticationRequest;
import com.arangodb.model.TraversalOptions;
import com.arangodb.model.TraversalOptions.Order;
import com.arangodb.velocystream.Request;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author Mark Vollmary
 *
 */
public class VPackSerializers {

	public static final JsonSerializer<Request> REQUEST = new JsonSerializer<Request>() {
		@Override
		public void serialize(final Request value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException {
			gen.writeStartArray();
			gen.writeNumber(value.getVersion());
			gen.writeNumber(value.getType());
			gen.writeString(value.getDatabase());
			gen.writeNumber(value.getRequestType().getType());
			gen.writeString(value.getRequest());
			gen.writeStartObject();
			for (final Entry<String, String> entry : value.getQueryParam().entrySet()) {
				gen.writeFieldName(entry.getKey());
				gen.writeString(entry.getValue());
			}
			gen.writeEndObject();
			gen.writeStartObject();
			for (final Entry<String, String> entry : value.getHeaderParam().entrySet()) {
				gen.writeFieldName(entry.getKey());
				gen.writeString(entry.getValue());
			}
			gen.writeEndObject();
			gen.writeEndArray();
		}
	};

	public static final JsonSerializer<AuthenticationRequest> AUTH_REQUEST = new JsonSerializer<AuthenticationRequest>() {
		@Override
		public void serialize(
			final AuthenticationRequest value,
			final JsonGenerator gen,
			final SerializerProvider serializers) throws IOException {
			gen.writeStartArray();
			gen.writeNumber(value.getVersion());
			gen.writeNumber(value.getType());
			gen.writeString(value.getEncryption());
			gen.writeString(value.getUser());
			gen.writeString(value.getPassword());
			gen.writeEndArray();
		}
	};

	public static final JsonSerializer<CollectionType> COLLECTION_TYPE = new JsonSerializer<CollectionType>() {
		@Override
		public void serialize(final CollectionType value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException {
			gen.writeNumber(value.getType());
		}
	};

	public static final JsonSerializer<BaseDocument> BASE_DOCUMENT = new JsonSerializer<BaseDocument>() {
		@Override
		public void serialize(final BaseDocument value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException {
			final Map<String, Object> doc = new HashMap<>();
			doc.putAll(value.getProperties());
			doc.put("_id", value.getId());
			doc.put("_key", value.getKey());
			doc.put("_rev", value.getRevision());
			gen.writeObject(doc);
		}
	};

	public static final JsonSerializer<BaseEdgeDocument> BASE_EDGE_DOCUMENT = new JsonSerializer<BaseEdgeDocument>() {
		@Override
		public void serialize(
			final BaseEdgeDocument value,
			final JsonGenerator gen,
			final SerializerProvider serializers) throws IOException {
			final Map<String, Object> doc = new HashMap<>();
			doc.putAll(value.getProperties());
			doc.put("_id", value.getId());
			doc.put("_key", value.getKey());
			doc.put("_rev", value.getRevision());
			doc.put("_from", value.getFrom());
			doc.put("_to", value.getTo());
			gen.writeObject(doc);
		}
	};

	public static final JsonSerializer<TraversalOptions.Order> TRAVERSAL_ORDER = new JsonSerializer<TraversalOptions.Order>() {
		@Override
		public void serialize(final Order value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException {
			if (TraversalOptions.Order.preorder_expander == value) {
				gen.writeString("preorder-expander");
			} else {
				gen.writeString(value.name());
			}
		}
	};

	public static final JsonSerializer<LogLevel> LOG_LEVEL = new JsonSerializer<LogLevel>() {
		@Override
		public void serialize(final LogLevel value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException {
			gen.writeNumber(value.getLevel());
		}
	};

	public static final JsonSerializer<Permissions> PERMISSIONS = new JsonSerializer<Permissions>() {
		@Override
		public void serialize(final Permissions value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException {
			gen.writeString(value.toString().toLowerCase());
		}
	};

	public static final JsonSerializer<ReplicationFactor> REPLICATION_FACTOR = new JsonSerializer<ReplicationFactor>() {
		@Override
		public void serialize(
			final ReplicationFactor value,
			final JsonGenerator gen,
			final SerializerProvider serializers) throws IOException {
			final Boolean satellite = value.getSatellite();
			if (Boolean.TRUE == satellite) {
				gen.writeString("satellite");
			} else if (value.getReplicationFactor() != null) {
				gen.writeNumber(value.getReplicationFactor());
			}
		}
	};

	public static final JsonSerializer<ViewType> VIEW_TYPE = new JsonSerializer<ViewType>() {
		@Override
		public void serialize(final ViewType value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException {
			final String type = value == ViewType.ARANGO_SEARCH ? "arangosearch" : value.name().toLowerCase();
			gen.writeString(type);
		}
	};

	public static final JsonSerializer<ArangoSearchProperties> ARANGO_SEARCH_PROPERTIES = new JsonSerializer<ArangoSearchProperties>() {
		@Override
		public void serialize(
			final ArangoSearchProperties value,
			final JsonGenerator gen,
			final SerializerProvider serializers) throws IOException {
			final String locale = value.getLocale();
			if (locale != null) {
				gen.writeFieldName("locale");
				gen.writeString(locale);
			}
			final Long commitIntervalMsec = value.getCommitIntervalMsec();
			final Long cleanupIntervalStep = value.getCleanupIntervalStep();
			final Collection<ConsolidateThreshold> thresholds = value.getThresholds();

			if (commitIntervalMsec != null || cleanupIntervalStep != null || !thresholds.isEmpty()) {
				gen.writeFieldName("commit");
				gen.writeStartObject();
				if (commitIntervalMsec != null) {
					gen.writeFieldName("commitIntervalMsec");
					gen.writeNumber(commitIntervalMsec);
				}
				if (cleanupIntervalStep != null) {
					gen.writeFieldName("cleanupIntervalStep");
					gen.writeNumber(cleanupIntervalStep);
				}
				if (!thresholds.isEmpty()) {
					gen.writeFieldName("consolidate");
					gen.writeStartObject();
					for (final ConsolidateThreshold consolidateThreshold : thresholds) {
						gen.writeFieldName(consolidateThreshold.getType().name().toLowerCase());
						gen.writeStartObject();
						final Double threshold = consolidateThreshold.getThreshold();
						if (threshold != null) {
							gen.writeFieldName("threshold");
							gen.writeNumber(threshold);
						}
						final Long segmentThreshold = consolidateThreshold.getSegmentThreshold();
						if (segmentThreshold != null) {
							gen.writeFieldName("segmentThreshold");
							gen.writeNumber(segmentThreshold);
						}
						gen.writeEndObject();
					}
					gen.writeEndObject();
				}
				gen.writeEndObject();
			}

			final Collection<CollectionLink> links = value.getLinks();
			if (!links.isEmpty()) {
				gen.writeFieldName("links");
				gen.writeStartObject();
				for (final CollectionLink collectionLink : links) {
					gen.writeFieldName(collectionLink.getName());
					gen.writeStartObject();
					final Collection<String> analyzers = collectionLink.getAnalyzers();
					if (!analyzers.isEmpty()) {
						gen.writeFieldName("analyzers");
						gen.writeStartArray();
						for (final String analyzer : analyzers) {
							gen.writeString(analyzer);
						}
						gen.writeEndArray();
					}
					final Boolean includeAllFields = collectionLink.getIncludeAllFields();
					if (includeAllFields != null) {
						gen.writeFieldName("includeAllFields");
						gen.writeBoolean(includeAllFields);
					}
					final Boolean trackListPositions = collectionLink.getTrackListPositions();
					if (trackListPositions != null) {
						gen.writeFieldName("trackListPositions");
						gen.writeBoolean(trackListPositions);
					}
					final StoreValuesType storeValues = collectionLink.getStoreValues();
					if (storeValues != null) {
						gen.writeFieldName("storeValues");
						gen.writeString(storeValues.name().toLowerCase());
					}
					serializeFieldLinks(gen, collectionLink.getFields());
					gen.writeEndObject();
				}
				gen.writeEndObject();
			}
		}
	};

	private static void serializeFieldLinks(final JsonGenerator gen, final Collection<FieldLink> links)
			throws IOException {
		if (!links.isEmpty()) {
			gen.writeFieldName("fields");
			gen.writeStartObject();
			for (final FieldLink fieldLink : links) {
				gen.writeFieldName(fieldLink.getName());
				gen.writeStartObject();
				final Collection<String> analyzers = fieldLink.getAnalyzers();
				if (!analyzers.isEmpty()) {
					gen.writeFieldName("analyzers");
					gen.writeStartArray();
					for (final String analyzer : analyzers) {
						gen.writeString(analyzer);
					}
					gen.writeEndArray();
				}
				final Boolean includeAllFields = fieldLink.getIncludeAllFields();
				if (includeAllFields != null) {
					gen.writeFieldName("includeAllFields");
					gen.writeBoolean(includeAllFields);
				}
				final Boolean trackListPositions = fieldLink.getTrackListPositions();
				if (trackListPositions != null) {
					gen.writeFieldName("trackListPositions");
					gen.writeBoolean(trackListPositions);
				}
				final StoreValuesType storeValues = fieldLink.getStoreValues();
				if (storeValues != null) {
					gen.writeFieldName("storeValues");
					gen.writeString(storeValues.name().toLowerCase());
				}
				serializeFieldLinks(gen, fieldLink.getFields());
				gen.writeEndObject();
			}
			gen.writeEndObject();
		}
	}

}
