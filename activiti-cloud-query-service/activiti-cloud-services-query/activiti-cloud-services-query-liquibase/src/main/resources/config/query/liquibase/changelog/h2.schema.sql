create sequence process_variable_sequence start with 1 increment by 50;
create sequence task_variable_sequence start with 1 increment by 50;

create table bpmn_activity
(
    id                         varchar(255) not null,
    app_name                   varchar(255),
    app_version                varchar(255),
    service_full_name          varchar(255),
    service_name               varchar(255),
    service_type               varchar(255),
    service_version            varchar(255),
    activity_name              varchar(255),
    activity_type              varchar(255),
    business_key               varchar(255),
    cancelled_date             timestamp,
    completed_date             timestamp,
    element_id                 varchar(255),
    process_definition_id      varchar(255),
    process_definition_key     varchar(255),
    process_definition_version integer,
    process_instance_id        varchar(255),
    execution_id	       	   varchar(255),
    started_date               timestamp,
    status                     varchar(255),

    primary key (id)
);
create table bpmn_sequence_flow
(
    id                         varchar(255) not null,
    app_name                   varchar(255),
    app_version                varchar(255),
    service_full_name          varchar(255),
    service_name               varchar(255),
    service_type               varchar(255),
    service_version            varchar(255),
    business_key               varchar(255),
    taken_date                 timestamp,
    element_id                 varchar(255),
    event_id                   varchar(255),
    process_definition_id      varchar(255),
    process_definition_key     varchar(255),
    process_definition_version integer,
    process_instance_id        varchar(255),
    source_activity_element_id varchar(255),
    source_activity_name       varchar(255),
    source_activity_type       varchar(255),
    target_activity_element_id varchar(255),
    target_activity_name       varchar(255),
    target_activity_type       varchar(255),
    primary key (id)
);
create table process_definition
(
    id                     varchar(255) not null,
    app_name               varchar(255),
    app_version            varchar(255),
    service_full_name      varchar(255),
    service_name           varchar(255),
    service_type           varchar(255),
    service_version        varchar(255),
    description            varchar(4000),
    form_key               varchar(255),
    process_definition_key varchar(255),
    name                   varchar(255),
    category               varchar(255),
    version                integer      not null,
    primary key (id)
);
create table process_instance
(
    id                         varchar(255) not null,
    app_name                   varchar(255),
    app_version                varchar(255),
    service_full_name          varchar(255),
    service_name               varchar(255),
    service_type               varchar(255),
    service_version            varchar(255),
    business_key               varchar(255),
    initiator                  varchar(255),
    last_modified              timestamp,
    last_modified_from         timestamp,
    last_modified_to           timestamp,
    name                       varchar(255),
    parent_id                  varchar(255),
    process_definition_id      varchar(255),
    process_definition_key     varchar(255),
    process_definition_version integer,
    start_date                 timestamp,
    start_from                 timestamp,
    start_to                   timestamp,
    completed_date             timestamp,
    suspended_date             timestamp,
    status                     varchar(255),
    process_definition_name    varchar(255),
    primary key (id)
);
create table process_model
(
    process_model_content text,
    process_definition_id varchar(255) not null,
    primary key (process_definition_id)
);
create table process_variable
(
    id                  bigint,
    app_name            varchar(255),
    app_version         varchar(255),
    service_full_name   varchar(255),
    service_name        varchar(255),
    service_type        varchar(255),
    service_version     varchar(255),
    create_time         timestamp,
    execution_id        varchar(255),
    last_updated_time   timestamp,
    marked_as_deleted   boolean,
    name                varchar(255),
    process_instance_id varchar(255),
    type                varchar(255),
    "value"             json,
    variable_definition_id varchar(64),
    process_definition_key varchar(255),
    primary key (id)
);
create table task
(
    id                         varchar(255) not null,
    app_name                   varchar(255),
    app_version                varchar(255),
    service_full_name          varchar(255),
    service_name               varchar(255),
    service_type               varchar(255),
    service_version            varchar(255),
    assignee                   varchar(255),
    business_key               varchar(255),
    claimed_date               timestamp,
    completed_date             timestamp,
    completed_from             timestamp,
    completed_to               timestamp,
    created_date               timestamp,
    created_from               timestamp,
    created_to                 timestamp,
    description                varchar(255),
    due_date                   timestamp,
    duration                   bigint,
    form_key                   varchar(255),
    last_claimed_from          timestamp,
    last_claimed_to            timestamp,
    last_modified              timestamp,
    last_modified_from         timestamp,
    last_modified_to           timestamp,
    name                       varchar(255),
    owner                      varchar(255),
    parent_task_id             varchar(255),
    priority                   integer      not null,
    process_definition_id      varchar(255),
    process_definition_version integer,
    process_instance_id        varchar(255),
    status                     varchar(255),
    task_definition_key        varchar(255),
    process_definition_name    varchar(255),
    completed_by               varchar(255),
    primary key (id)
);
create table task_candidate_group
(
    group_id varchar(255) not null,
    task_id  varchar(255) not null,
    primary key (group_id, task_id)
);
create table task_candidate_user
(
    task_id varchar(255) not null,
    user_id varchar(255) not null,
    primary key (task_id, user_id)
);
create table task_variable
(
    id                  bigint,
    app_name            varchar(255),
    app_version         varchar(255),
    service_full_name   varchar(255),
    service_name        varchar(255),
    service_type        varchar(255),
    service_version     varchar(255),
    create_time         timestamp,
    execution_id        varchar(255),
    last_updated_time   timestamp,
    marked_as_deleted   boolean,
    name                varchar(255),
    process_instance_id varchar(255),
    type                varchar(255),
    "value"             json,
    task_id             varchar(255),
    primary key (id)
);

create table integration_context
(
    id                  		varchar(255) not null,
    app_name            		varchar(255),
    app_version         		varchar(255),
    service_full_name   		varchar(255),
    service_name        		varchar(255),
    service_type        		varchar(255),
    service_version     		varchar(255),

    process_definition_id      	varchar(255),
    process_definition_key     	varchar(255),
    process_definition_version 	integer,
    root_process_instance_id   	varchar(255),
    process_instance_id        	varchar(255),
    execution_id	        	varchar(255),
    parent_process_instance_id  varchar(255),
    business_key		      	varchar(255),

    client_id                  	varchar(255),
    client_name                	varchar(255),
    client_type                	varchar(255),

    connector_type            	varchar(255),
    status                      varchar(255),

    request_date               	timestamp,
    result_date                	timestamp,
    error_date                 	timestamp,

    error_code				   	varchar(255),
    error_message			   	varchar(255),
    error_class_name    	   	varchar(255),
	stack_trace_elements 	   	text,

	inbound_variables	 	   	text,
	out_bound_variables 	   	text,

    primary key (id)
);

create table application
(
    id                         varchar(255) not null,
    name                       varchar(255) not null,
    version                    varchar(255),
    primary key (id)
);

create table task_process_variable
(
  task_id varchar(255) not null,
  process_variable_id  bigint not null,
  primary key (task_id, process_variable_id)
);

create table process_candidate_starter_group
(
  process_definition_id  varchar(255) not null,
  group_id varchar(255) not null,
  primary key (process_definition_id, group_id)
);
create table process_candidate_starter_user
(
  process_definition_id varchar(255) not null,
  user_id varchar(255) not null,
  primary key (process_definition_id, user_id)
);

create index bpmn_activity_status_idx on bpmn_activity (status);
create index bpmn_activity_processInstance_idx on bpmn_activity (process_instance_id);
alter table bpmn_activity
    add constraint bpmn_activity_processInstance_elementId_idx unique (process_instance_id, element_id, execution_id);
create index bpmn_sequence_flow_processInstance_idx on bpmn_sequence_flow (process_instance_id);
create index bpmn_sequence_flow_elementId_idx on bpmn_sequence_flow (element_id);
create index bpmn_sequence_flow_processInstance_elementId_idx on bpmn_sequence_flow (process_instance_id, element_id);
create index bpmn_sequence_flow_eventId_idx on bpmn_sequence_flow (event_id);
create index pd_name_idx on process_definition (name);
create index pd_key_idx on process_definition (process_definition_key);
create index pi_status_idx on process_instance (status);
create index pi_businessKey_idx on process_instance (business_key);
create index pi_name_idx on process_instance (name);
create index pi_processDefinitionId_idx on process_instance (process_definition_id);
create index pi_processDefinitionKey_idx on process_instance (process_definition_key);
create index proc_var_processInstanceId_idx on process_variable (process_instance_id);
create index proc_var_name_idx on process_variable (name);
create index proc_var_executionId_idx on process_variable (execution_id);
create index task_status_idx on task (status);
create index task_processInstance_idx on task (process_instance_id);
create index tcg_groupId_idx on task_candidate_group (group_id);
create index tcg_taskId_idx on task_candidate_group (task_id);
create index tcu_userId_idx on task_candidate_user (user_id);
create index tcu_taskId_idx on task_candidate_user (task_id);
create index task_var_processInstanceId_idx on task_variable (process_instance_id);
create index task_var_taskId_idx on task_variable (task_id);
create index task_var_name_idx on task_variable (name);
create index task_var_executionId_idx on task_variable (execution_id);
create index pi_processDefinitionName_idx on process_instance (process_definition_name);
create index task_processDefinitionName_idx on task (process_definition_name);
create index pcsg_groupId_idx on process_candidate_starter_group (group_id);
create index pcsg_processDefinition_idx on process_candidate_starter_group (process_definition_id);
create index pcsu_userId_idx on process_candidate_starter_user (user_id);
create index pcsu_processDefinition_idx on process_candidate_starter_user (process_definition_id);
alter table integration_context
    add constraint integration_context_bpmn_activity_idx unique (process_instance_id, client_id, execution_id);
alter table process_model
    add constraint FKmqdabtfsoy52f0585vkfj40b foreign key (process_definition_id) references process_definition;
alter table task_process_variable
  add constraint fk_task_id foreign key (task_id) references task;
alter table task_process_variable
  add constraint fk_process_variable_id foreign key (process_variable_id) references process_variable;
alter table task_process_variable
  add constraint uk_task_process_var unique (task_id, process_variable_id);
create index idx_task_assignee on task(assignee);
create index idx_task_owner on task(owner);
create index idx_process_instance_initiator on process_instance(initiator);
CREATE INDEX idx_task_id_name_status ON task(id, name, status);
CREATE INDEX idx_task_process_var_taskId_processVarId ON task_process_variable (task_id, process_variable_id);
CREATE INDEX idx_task_createdDate ON task (created_date);
