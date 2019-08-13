/*
 *  Copyright (c) 2017 Otávio Santana and others
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
 */
package org.jnosql.diana.riak.kv;


import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import jakarta.nosql.Configurations;
import jakarta.nosql.Settings;
import jakarta.nosql.Settings.SettingsBuilder;
import jakarta.nosql.keyvalue.KeyValueConfiguration;
import org.jnosql.diana.driver.ConfigurationReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * The riak implementation to {@link KeyValueConfiguration} that returns {@link RiakBucketManagerFactory}.
 * It tries to read diana-riak.properties file.
 * <p>riak.host-: The prefix to host. eg: riak.server.host.1= host1</p>
 */
public class RiakKeyValueConfiguration implements KeyValueConfiguration {


    @Deprecated
    private static final String OLD_SERVER_PREFIX = "riak-server-host-";
    private static final String SERVER_PREFIX = "riak.host";

    private static final String FILE_CONFIGURATION = "diana-riak.properties";

    private static final RiakNode DEFAULT_NODE = new RiakNode.Builder()
            .withRemoteAddress("127.0.0.1").build();

    private final List<RiakNode> nodes = new ArrayList<>();


    public RiakKeyValueConfiguration() {
        Map<String, String> properties = ConfigurationReader.from(FILE_CONFIGURATION);
        SettingsBuilder builder = Settings.builder();

        properties.entrySet().forEach(e -> builder.put(e.getKey(), e.getValue()));

        Settings settings = builder.build();

        settings.prefix(asList(SERVER_PREFIX, OLD_SERVER_PREFIX, Configurations.HOST.get()))
                .stream().map(Object::toString)
                .forEach(this::add);
    }


    /**
     * Adds new Riak node
     *
     * @param node the node
     * @throws NullPointerException when node is null
     */
    public void add(RiakNode node) throws NullPointerException {
        requireNonNull(node, "Node is required");
        this.nodes.add(node);
    }

    /**
     * Adds a new address on riak
     *
     * @param address the address to be added
     * @throws NullPointerException when address is null
     */
    public void add(String address) throws NullPointerException {
        requireNonNull(address, "Address is required");
        this.nodes.add(new RiakNode.Builder().withRemoteAddress(address).build());
    }

    @Override
    public RiakBucketManagerFactory get() {

        if (nodes.isEmpty()) {
            nodes.add(DEFAULT_NODE);
        }
        RiakCluster cluster = new RiakCluster.Builder(nodes)
                .build();

        return new RiakBucketManagerFactory(cluster);
    }

    @Override
    public RiakBucketManagerFactory get(Settings settings) {
        requireNonNull(settings, "settings is required");
        List<RiakNode> nodes = new ArrayList<>();

        settings.prefix(asList(SERVER_PREFIX, OLD_SERVER_PREFIX, Configurations.HOST.get()))
                .stream()
                .map(Object::toString)
                .map(toNode())
                .forEach(nodes::add);

        if (nodes.isEmpty()) {
            nodes.add(DEFAULT_NODE);
        }
        RiakCluster cluster = new RiakCluster.Builder(nodes)
                .build();

        return new RiakBucketManagerFactory(cluster);
    }

    private Function<String, RiakNode> toNode() {
        return h -> {
            String[] values = h.split(":");
            if (values.length == 1) {
                return new RiakNode.Builder()
                        .withRemoteAddress(values[0]).build();
            } else {
                return new RiakNode.Builder()
                        .withRemoteAddress(values[0])
                        .withRemotePort(Integer.valueOf(values[1]))
                        .build();
            }
        };
    }
}
