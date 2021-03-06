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
package org.jboss.migration.core;

import org.jboss.logging.Logger;

import java.util.List;

/**
 * The server migration task context.
 * @author emmartins
 */
public class ServerMigrationTaskContext {

    private final ServerMigrationTaskExecution taskExecution;

    ServerMigrationTaskContext(ServerMigrationTaskExecution taskExecution) {
        this.taskExecution = taskExecution;
    }

    /**
     * Retrieves the children task executions.
     * @return the children task executions
     */
    public List<? extends ServerMigrationTaskExecution> getSubtasks() {
        return taskExecution.getSubtasks();
    }

    /**
     * Indicates if there is any subtask that had success status
     * @return true if there is at least one subtask that had success status; false otherwise
     */
    public boolean hasSucessfulSubtasks() {
        for (ServerMigrationTaskExecution subtask : getSubtasks()) {
            final ServerMigrationTaskResult result = subtask.getResult();
            if (result != null && result.getStatus() == ServerMigrationTaskResult.Status.SUCCESS) {
                return true;
            }
        }
        return false;
    }

    /**
     * Executes a subtask
     * @param subtask the subtask to execute
     * @return the subtask execution
     * @throws IllegalStateException if the task result is already set
     * @throws ServerMigrationFailedException if the subtask execution failed
     */
    public ServerMigrationTaskExecution execute(ServerMigrationTask subtask) throws IllegalStateException, ServerMigrationFailedException {
        return taskExecution.execute(subtask);
    }

    /**
     * Retrieves the server migration context.
     * @return the server migration context
     */
    public ServerMigrationContext getServerMigrationContext() {
        return taskExecution.getServerMigrationContext();
    }

    /**
     * Retrieves the task logger.
     * @return the task logger
     */
    public Logger getLogger() {
        return taskExecution.getLogger();
    }
}
