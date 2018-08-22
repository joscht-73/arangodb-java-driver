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

package com.arangodb.example.graph;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author a-brandt
 *
 */
public class CircleEdge {

	@JsonProperty("_id")
	private String id;

	@JsonProperty("_key")
	private String key;

	@JsonProperty("_rev")
	private String revision;

	@JsonProperty("_from")
	private String from;

	@JsonProperty("_to")
	private String to;

	private Boolean theFalse;
	private Boolean theTruth;
	private String label;

	public CircleEdge(final String from, final String to, final Boolean theFalse, final Boolean theTruth,
		final String label) {
		this.from = from;
		this.to = to;
		this.theFalse = theFalse;
		this.theTruth = theTruth;
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(final String revision) {
		this.revision = revision;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(final String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(final String to) {
		this.to = to;
	}

	public Boolean getTheFalse() {
		return theFalse;
	}

	public void setTheFalse(final Boolean theFalse) {
		this.theFalse = theFalse;
	}

	public Boolean getTheTruth() {
		return theTruth;
	}

	public void setTheTruth(final Boolean theTruth) {
		this.theTruth = theTruth;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

}
