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

package com.arangodb;

import com.arangodb.entity.ArangoDBVersion;
import org.junit.Ignore;
import org.junit.Test;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

/**
 * @author Mark Vollmary
 */
public class ArangoSslTest {

    /*-
     * a SSL trust store
     *
     * create the trust store for the self signed certificate:
     * keytool -import -alias "my arangodb server cert" -file UnitTests/server.pem -keystore example.truststore
     *
     * Documentation:
     * https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/conn/ssl/SSLSocketFactory.html
     */
    private static final String SSL_TRUSTSTORE = "/example.truststore";
    private static final String SSL_TRUSTSTORE_PASSWORD = "12345678";

    @Test
    @Ignore
    public void connect() throws Exception {
        final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(this.getClass().getResourceAsStream(SSL_TRUSTSTORE), SSL_TRUSTSTORE_PASSWORD.toCharArray());

        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, SSL_TRUSTSTORE_PASSWORD.toCharArray());

        final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        final SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        final ArangoDB arangoDB = new ArangoDB.Builder()
                .loadProperties(ArangoSslTest.class.getResourceAsStream("/arangodb-ssl.properties")).useSsl(true)
                .sslContext(sc).build();
        final ArangoDBVersion version = arangoDB.getVersion();
        assertThat(version, is(notNullValue()));
    }

    @Test
    @Ignore
    public void connectWithoutValidSslContext() {
        try {
            final ArangoDB arangoDB = new ArangoDB.Builder()
                    .loadProperties(ArangoSslTest.class.getResourceAsStream("/arangodb-ssl.properties")).useSsl(true)
                    .build();
            arangoDB.getVersion();
            fail("this should fail");
        } catch (final ArangoDBException ex) {
            assertThat(ex.getCause() instanceof SSLHandshakeException, is(true));
        }
    }

}
