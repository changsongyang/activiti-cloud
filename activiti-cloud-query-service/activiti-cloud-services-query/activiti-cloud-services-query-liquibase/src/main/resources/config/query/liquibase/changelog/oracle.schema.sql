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
    taken_date                     timestamp,
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
    description            varchar(255),
    form_key               varchar(255),
    process_definition_key varchar(255),
    name                   varchar(255),
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
    status                     varchar(255),
    primary key (id)
);
create table process_model
(
    process_model_content CLOB,
    process_definition_id varchar(255) not null,
    primary key (process_definition_id)
);
create table process_variable
(
    id                  NUMBER(19,0) GENERATED ALWAYS AS IDENTITY MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  NOT NULL ENABLE,
    app_name            varchar(255),
    app_version         varchar(255),
    service_full_name   varchar(255),
    service_name        varchar(255),
    service_type        varchar(255),
    service_version     varchar(255),
    create_time         timestamp,
    execution_id        varchar(255),
    last_updated_time   timestamp,
    marked_as_deleted   NUMBER(1,0),
    name                varchar(255),
    process_instance_id varchar(255),
    type                varchar(255),
    value               json,
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
    duration                   integer,
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
    id                  NUMBER(19,0) GENERATED ALWAYS AS IDENTITY MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  NOT NULL ENABLE,
    app_name            varchar(255),
    app_version         varchar(255),
    service_full_name   varchar(255),
    service_name        varchar(255),
    service_type        varchar(255),
    service_version     varchar(255),
    create_time         timestamp,
    execution_id        varchar(255),
    last_updated_time   timestamp,
    marked_as_deleted   NUMBER(1,0),
    name                varchar(255),
    process_instance_id varchar(255),
    type                varchar(255),
    value               json,
    task_id             varchar(255),
    primary key (id)
);
create index bpmn_activity_status_idx on bpmn_activity (status);
create index bpmn_activity_processInstance_idx on bpmn_activity (process_instance_id);
alter table bpmn_activity
    add constraint bpmn_activity_processInstance_elementId_idx unique (process_instance_id, element_id);
create index bpmn_sequence_flow_processInstance_idx on bpmn_sequence_flow (process_instance_id);
create index bpmn_sequence_flow_elementId_idx on bpmn_sequence_flow (element_id);
create index bpmn_sequence_flow_processInstance_elementId_idx on bpmn_sequence_flow (process_instance_id, element_id);
alter table bpmn_sequence_flow
    add constraint bpmn_sequence_flow_eventId_idx unique (event_id);
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
alter table process_model
    add constraint FKmqdabtfsoy52f0585vkfj40b foreign key (process_definition_id) references process_definition;
