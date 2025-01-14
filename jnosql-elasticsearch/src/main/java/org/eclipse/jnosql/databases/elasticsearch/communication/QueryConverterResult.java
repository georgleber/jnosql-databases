/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 *   Maximillian Arruda
 */
package org.eclipse.jnosql.databases.elasticsearch.communication;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;

class QueryConverterResult {

    private final Query.Builder statement;

    QueryConverterResult(Query.Builder statement) {
        this.statement = statement;
    }

    Query.Builder getStatement() {
        return statement;
    }

    public boolean hasStatement() {
        return statement != null;
    }

    public boolean hasQuery() {
        return statement != null;
    }
}
