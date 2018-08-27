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

import com.arangodb.entity.ArangoDBVersion;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.BaseEdgeDocument;
import com.arangodb.entity.CollectionStatus;
import com.arangodb.entity.CollectionType;
import com.arangodb.entity.LogLevel;
import com.arangodb.entity.Permissions;
import com.arangodb.entity.QueryExecutionState;
import com.arangodb.entity.ReplicationFactor;
import com.arangodb.entity.ViewType;
import com.arangodb.entity.arangosearch.ArangoSearchProperties;
import com.arangodb.entity.arangosearch.ArangoSearchPropertiesEntity;
import com.arangodb.entity.arangosearch.ConsolidateType;
import com.arangodb.entity.arangosearch.ConsolidationPolicy;
import com.arangodb.internal.velocystream.internal.AuthenticationRequest;
import com.arangodb.model.TraversalOptions;
import com.arangodb.velocystream.Request;
import com.arangodb.velocystream.Response;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author Mark Vollmary
 *
 */
public class VPackDriverModule extends SimpleModule {

	private static final long serialVersionUID = 1L;

	public VPackDriverModule() {
		super();

		addSerializer(Request.class, VPackSerializers.REQUEST);
		addSerializer(AuthenticationRequest.class, VPackSerializers.AUTH_REQUEST);
		addSerializer(CollectionType.class, VPackSerializers.COLLECTION_TYPE);
		addSerializer(BaseDocument.class, VPackSerializers.BASE_DOCUMENT);
		addSerializer(BaseEdgeDocument.class, VPackSerializers.BASE_EDGE_DOCUMENT);
		addSerializer(TraversalOptions.Order.class, VPackSerializers.TRAVERSAL_ORDER);
		addSerializer(LogLevel.class, VPackSerializers.LOG_LEVEL);
		addSerializer(Permissions.class, VPackSerializers.PERMISSIONS);
		addSerializer(ReplicationFactor.class, VPackSerializers.REPLICATION_FACTOR);
		addSerializer(ViewType.class, VPackSerializers.VIEW_TYPE);
		addSerializer(ArangoSearchProperties.class, VPackSerializers.ARANGO_SEARCH_PROPERTIES);
		addSerializer(ConsolidateType.class, VPackSerializers.CONSOLIDATE_TYPE);

		addDeserializer(Response.class, VPackDeserializers.RESPONSE);
		addDeserializer(CollectionType.class, VPackDeserializers.COLLECTION_TYPE);
		addDeserializer(CollectionStatus.class, VPackDeserializers.COLLECTION_STATUS);
		addDeserializer(BaseDocument.class, VPackDeserializers.BASE_DOCUMENT);
		addDeserializer(BaseEdgeDocument.class, VPackDeserializers.BASE_EDGE_DOCUMENT);
		// addDeserializer(QueryEntity.PROPERTY_STARTED, Date.class, VPackDeserializers.DATE_STRING);
		addDeserializer(LogLevel.class, VPackDeserializers.LOG_LEVEL);
		addDeserializer(ArangoDBVersion.License.class, VPackDeserializers.LICENSE);
		addDeserializer(Permissions.class, VPackDeserializers.PERMISSIONS);
		addDeserializer(QueryExecutionState.class, VPackDeserializers.QUERY_EXECUTION_STATE);
		addDeserializer(ReplicationFactor.class, VPackDeserializers.REPLICATION_FACTOR);
		addDeserializer(ViewType.class, VPackDeserializers.VIEW_TYPE);
		addDeserializer(ArangoSearchPropertiesEntity.class, VPackDeserializers.ARANGO_SEARCH_PROPERTIES_ENTITY);
		addDeserializer(ConsolidationPolicy.class, VPackDeserializers.CONSOLIDATE);
	}

}
