/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.rest.action.admin.cluster.node.tasks;

import org.elasticsearch.action.admin.cluster.node.tasks.cancel.CancelTasksRequest;
import org.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.support.RestToXContentListener;

import static org.elasticsearch.rest.RestRequest.Method.POST;


public class RestCancelTasksAction extends BaseRestHandler {

    @Inject
    public RestCancelTasksAction(Settings settings, RestController controller, Client client) {
        super(settings, client);
        controller.registerHandler(POST, "/_tasks/_cancel", this);
        controller.registerHandler(POST, "/_tasks/{nodeId}/_cancel", this);
        controller.registerHandler(POST, "/_tasks/{nodeId}/{taskId}/_cancel", this);
    }

    @Override
    public void handleRequest(final RestRequest request, final RestChannel channel, final Client client) {
        String[] nodesIds = Strings.splitStringByCommaToArray(request.param("nodeId"));
        long taskId = request.paramAsLong("taskId", ListTasksRequest.ALL_TASKS);
        String[] actions = Strings.splitStringByCommaToArray(request.param("actions"));
        String parentNode = request.param("parent_node");
        long parentTaskId = request.paramAsLong("parent_task", ListTasksRequest.ALL_TASKS);

        CancelTasksRequest cancelTasksRequest = new CancelTasksRequest(nodesIds);
        cancelTasksRequest.taskId(taskId);
        cancelTasksRequest.actions(actions);
        cancelTasksRequest.parentNode(parentNode);
        cancelTasksRequest.parentTaskId(parentTaskId);
        client.admin().cluster().cancelTasks(cancelTasksRequest, new RestToXContentListener<>(channel));
    }
}