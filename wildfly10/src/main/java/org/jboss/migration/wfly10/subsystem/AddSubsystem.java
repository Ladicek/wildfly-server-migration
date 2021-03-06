/*
 * Copyright 2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.migration.wfly10.subsystem;

import org.jboss.as.controller.operations.common.Util;
import org.jboss.dmr.ModelNode;
import org.jboss.migration.core.ServerMigrationTask;
import org.jboss.migration.core.ServerMigrationTaskContext;
import org.jboss.migration.core.ServerMigrationTaskName;
import org.jboss.migration.core.ServerMigrationTaskResult;
import org.jboss.migration.core.env.TaskEnvironment;
import org.jboss.migration.wfly10.standalone.WildFly10StandaloneServer;

import static org.jboss.as.controller.PathAddress.pathAddress;
import static org.jboss.as.controller.PathElement.pathElement;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

/**
 * A task which creates a subsystem if its missing from the server's config.
 * @author emmartins
 */
public class AddSubsystem implements WildFly10SubsystemMigrationTaskFactory {

    public static final AddSubsystem INSTANCE = new AddSubsystem();

    protected AddSubsystem() {
    }

    @Override
    public ServerMigrationTask getServerMigrationTask(ModelNode config, final WildFly10Subsystem subsystem, WildFly10StandaloneServer server) {
        return new WildFly10SubsystemMigrationTask(config, subsystem, server) {
            @Override
            public ServerMigrationTaskName getName() {
                return new ServerMigrationTaskName.Builder().setName("add-subsystem-config").addAttribute("name", subsystem.getName()).build();
            }
            @Override
            protected ServerMigrationTaskResult run(ModelNode config, WildFly10Subsystem subsystem, WildFly10StandaloneServer server, ServerMigrationTaskContext context, TaskEnvironment taskEnvironment) throws Exception {
                if (config != null) {
                    context.getLogger().infof("Skipped adding subsystem %s, already exists in config.", subsystem.getName());
                    return ServerMigrationTaskResult.SKIPPED;
                }
                context.getLogger().debugf("Adding subsystem %s config...", subsystem.getName());
                addSubsystem(subsystem, server, context);
                context.getLogger().infof("Subsystem %s config added.", subsystem.getName());
                return ServerMigrationTaskResult.SUCCESS;
            }
        };
    }

    protected void addSubsystem(WildFly10Subsystem subsystem, WildFly10StandaloneServer server, ServerMigrationTaskContext context) throws Exception {
        final ModelNode op = Util.createAddOperation(pathAddress(pathElement(SUBSYSTEM, subsystem.getName())));
        server.executeManagementOperation(op);
    }
}
