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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.entity.ArangoDBVersion;
import com.arangodb.entity.ArangoDBVersion.License;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.BaseEdgeDocument;
import com.arangodb.entity.CollectionStatus;
import com.arangodb.entity.CollectionType;
import com.arangodb.entity.LogLevel;
import com.arangodb.entity.Permissions;
import com.arangodb.entity.QueryExecutionState;
import com.arangodb.entity.ReplicationFactor;
import com.arangodb.entity.ViewEntity;
import com.arangodb.entity.ViewType;
import com.arangodb.entity.arangosearch.ArangoSearchProperties;
import com.arangodb.entity.arangosearch.ArangoSearchPropertiesEntity;
import com.arangodb.entity.arangosearch.CollectionLink;
import com.arangodb.entity.arangosearch.ConsolidateThreshold;
import com.arangodb.entity.arangosearch.ConsolidateType;
import com.arangodb.entity.arangosearch.FieldLink;
import com.arangodb.entity.arangosearch.StoreValuesType;
import com.arangodb.velocystream.Response;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Mark Vollmary
 *
 */
@SuppressWarnings("unchecked")
public class VPackDeserializers {

	private static final Logger LOGGER = LoggerFactory.getLogger(VPackDeserializers.class);
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	public static final JsonDeserializer<Response> RESPONSE = new JsonDeserializer<Response>() {
		@Override
		public Response deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			final Response response = new Response();
			final JsonNode node = p.getCodec().readTree(p);
			response.setVersion(node.get(0).asInt());
			response.setType(node.get(1).asInt());
			response.setResponseCode(node.get(2).asInt());
			if (node.size() > 3) {
				final JsonParser metaP = node.get(3).traverse(ctxt.getParser().getCodec());
				response.setMeta(metaP.readValueAs(Map.class));
			}
			return response;
		}
	};

	public static final JsonDeserializer<CollectionType> COLLECTION_TYPE = new JsonDeserializer<CollectionType>() {
		@Override
		public CollectionType deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return CollectionType.fromType(p.getValueAsInt());
		}
	};

	public static final JsonDeserializer<CollectionStatus> COLLECTION_STATUS = new JsonDeserializer<CollectionStatus>() {
		@Override
		public CollectionStatus deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return CollectionStatus.fromStatus(p.getValueAsInt());
		}
	};

	public static final JsonDeserializer<BaseDocument> BASE_DOCUMENT = new JsonDeserializer<BaseDocument>() {
		@Override
		public BaseDocument deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return new BaseDocument(p.readValueAs(Map.class));
		}
	};

	public static final JsonDeserializer<BaseEdgeDocument> BASE_EDGE_DOCUMENT = new JsonDeserializer<BaseEdgeDocument>() {
		@Override
		public BaseEdgeDocument deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return new BaseEdgeDocument(p.readValueAs(Map.class));
		}
	};

	public static final JsonDeserializer<Date> DATE_STRING = new JsonDeserializer<Date>() {
		@Override
		public Date deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			try {
				return new SimpleDateFormat(DATE_TIME_FORMAT).parse(p.getValueAsString());
			} catch (final ParseException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("got ParseException for date string: " + p.getValueAsString());
				}
			}
			return null;
		}
	};

	public static final JsonDeserializer<LogLevel> LOG_LEVEL = new JsonDeserializer<LogLevel>() {
		@Override
		public LogLevel deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return LogLevel.fromLevel(p.getValueAsInt());
		}
	};

	public static final JsonDeserializer<ArangoDBVersion.License> LICENSE = new JsonDeserializer<ArangoDBVersion.License>() {
		@Override
		public License deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return License.valueOf(p.getValueAsString().toUpperCase());
		}
	};

	public static final JsonDeserializer<Permissions> PERMISSIONS = new JsonDeserializer<Permissions>() {
		@Override
		public Permissions deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return Permissions.valueOf(p.getValueAsString().toUpperCase());
		}
	};

	public static final JsonDeserializer<QueryExecutionState> QUERY_EXECUTION_STATE = new JsonDeserializer<QueryExecutionState>() {
		@Override
		public QueryExecutionState deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return QueryExecutionState.valueOf(p.getValueAsString().toUpperCase().replaceAll(" ", "_"));
		}

	};

	public static final JsonDeserializer<ReplicationFactor> REPLICATION_FACTOR = new JsonDeserializer<ReplicationFactor>() {
		@Override
		public ReplicationFactor deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			final ReplicationFactor replicationFactor = new ReplicationFactor();
			final String asString = p.getValueAsString();
			if ("satellite".equals(asString)) {
				replicationFactor.setSatellite(true);
			} else {
				replicationFactor.setReplicationFactor(p.getValueAsInt());
			}
			return replicationFactor;
		}
	};

	public static final JsonDeserializer<ViewType> VIEW_TYPE = new JsonDeserializer<ViewType>() {
		@Override
		public ViewType deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			final String value = p.getValueAsString();
			return "arangosearch".equals(value) ? ViewType.ARANGO_SEARCH : ViewType.valueOf(value.toUpperCase());
		}
	};
	public static final JsonDeserializer<ArangoSearchPropertiesEntity> ARANGO_SEARCH_PROPERTIES_ENTITY = new JsonDeserializer<ArangoSearchPropertiesEntity>() {
		@Override
		public ArangoSearchPropertiesEntity deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {

			final JsonNode node = p.getCodec().readTree(p);
			final String id = node.get("id").asText();
			final String name = node.get("name").asText();
			final ViewType viewType = node.get("type").traverse(ctxt.getParser().getCodec())
					.readValueAs(ViewType.class);
			final ViewEntity entity = new ViewEntity(id, name, viewType);

			final ArangoSearchProperties properties = new ArangoSearchProperties();
			final ArangoSearchPropertiesEntity result = new ArangoSearchPropertiesEntity(entity.getId(),
					entity.getName(), entity.getType(), properties);

			final JsonNode locale = node.get("locale");
			if (locale != null && locale.isTextual()) {
				properties.setLocale(locale.asText());
			}
			final JsonNode commit = node.get("commit");
			if (commit != null && commit.isObject()) {
				final JsonNode commitIntervalMsec = commit.get("commitIntervalMsec");
				if (commitIntervalMsec.isLong()) {
					properties.setCommitIntervalMsec(commitIntervalMsec.asLong());
				}
				final JsonNode cleanupIntervalStep = commit.get("cleanupIntervalStep");
				if (cleanupIntervalStep != null && cleanupIntervalStep.isLong()) {
					properties.setCleanupIntervalStep(cleanupIntervalStep.asLong());
				}
				final JsonNode consolidate = commit.get("consolidate");
				if (consolidate != null && consolidate.isObject()) {
					for (final ConsolidateType type : ConsolidateType.values()) {
						final JsonNode consolidateThreshold = consolidate.get(type.name().toLowerCase());
						if (consolidateThreshold != null && consolidateThreshold.isObject()) {
							final ConsolidateThreshold t = ConsolidateThreshold.of(type);
							final JsonNode threshold = consolidateThreshold.get("threshold");
							if (threshold != null && threshold.isDouble()) {
								t.threshold(threshold.asDouble());
							}
							final JsonNode segmentThreshold = consolidateThreshold.get("segmentThreshold");
							if (segmentThreshold != null && segmentThreshold.isLong()) {
								t.segmentThreshold(segmentThreshold.asLong());
							}
							properties.addThreshold(t);
						}
					}
				}
			}

			final JsonNode links = node.get("links");
			if (links != null && links.isObject()) {
				final Iterator<Entry<String, JsonNode>> collectionIterator = links.fields();
				for (; collectionIterator.hasNext();) {
					final Entry<String, JsonNode> entry = collectionIterator.next();
					final JsonNode value = entry.getValue();
					final CollectionLink link = CollectionLink.on(entry.getKey());
					final JsonNode analyzers = value.get("analyzers");
					if (analyzers != null && analyzers.isArray()) {
						final Iterator<JsonNode> analyzerIterator = analyzers.iterator();
						for (; analyzerIterator.hasNext();) {
							link.analyzers(analyzerIterator.next().asText());
						}
					}
					final JsonNode includeAllFields = value.get("includeAllFields");
					if (includeAllFields != null && includeAllFields.isBoolean()) {
						link.includeAllFields(includeAllFields.asBoolean());
					}
					final JsonNode trackListPositions = value.get("trackListPositions");
					if (trackListPositions != null && trackListPositions.isBoolean()) {
						link.trackListPositions(trackListPositions.asBoolean());
					}
					final JsonNode storeValues = value.get("storeValues");
					if (storeValues != null && storeValues.isTextual()) {
						link.storeValues(StoreValuesType.valueOf(storeValues.asText().toUpperCase()));
					}
					final JsonNode fields = value.get("fields");
					if (fields != null && fields.isObject()) {
						final Iterator<Entry<String, JsonNode>> fieldsIterator = fields.fields();
						for (; fieldsIterator.hasNext();) {
							link.fields(deserializeField(fieldsIterator.next()));
						}
					}
					properties.addLink(link);
				}
			}
			return result;
		}
	};

	protected static FieldLink deserializeField(final Entry<String, JsonNode> entry) {
		final JsonNode value = entry.getValue();
		final FieldLink link = FieldLink.on(entry.getKey());
		final JsonNode analyzers = value.get("analyzers");
		if (analyzers != null && analyzers.isArray()) {
			final Iterator<JsonNode> analyzerIterator = analyzers.iterator();
			for (; analyzerIterator.hasNext();) {
				link.analyzers(analyzerIterator.next().asText());
			}
		}
		final JsonNode includeAllFields = value.get("includeAllFields");
		if (includeAllFields != null && includeAllFields.isBoolean()) {
			link.includeAllFields(includeAllFields.asBoolean());
		}
		final JsonNode trackListPositions = value.get("trackListPositions");
		if (trackListPositions != null && trackListPositions.isBoolean()) {
			link.trackListPositions(trackListPositions.asBoolean());
		}
		final JsonNode storeValues = value.get("storeValues");
		if (storeValues != null && storeValues.isTextual()) {
			link.storeValues(StoreValuesType.valueOf(storeValues.asText().toUpperCase()));
		}
		final JsonNode fields = value.get("fields");
		if (fields != null && fields.isObject()) {
			final Iterator<Entry<String, JsonNode>> fieldsIterator = fields.fields();
			for (; fieldsIterator.hasNext();) {
				link.fields(deserializeField(fieldsIterator.next()));
			}
		}
		return link;
	}

}
