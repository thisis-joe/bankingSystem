create table customer (
    customer_id bigserial primary key,
    customer_no varchar(30) not null,
    customer_type varchar(20) not null,
    name varchar(100) not null,
    status varchar(20) not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint uk_customer_customer_no unique (customer_no)
);

create table product (
    product_id bigserial primary key,
    product_code varchar(30) not null,
    product_name varchar(100) not null,
    product_type varchar(30) not null,
    status varchar(20) not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint uk_product_product_code unique (product_code)
);

create table channel (
    channel_id bigserial primary key,
    channel_code varchar(30) not null,
    channel_name varchar(100) not null,
    channel_type varchar(30) not null,
    status varchar(20) not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint uk_channel_channel_code unique (channel_code)
);

create table account (
    account_id bigserial primary key,
    account_no varchar(30) not null,
    product_id bigint not null,
    currency_code varchar(3) not null,
    status varchar(20) not null,
    opened_at timestamptz not null,
    closed_at timestamptz,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint uk_account_account_no unique (account_no),
    constraint fk_account_product
        foreign key (product_id) references product (product_id)
);

create table account_holder (
    account_holder_id bigserial primary key,
    account_id bigint not null,
    customer_id bigint not null,
    holder_role varchar(30) not null,
    is_primary boolean not null default false,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint fk_account_holder_account
        foreign key (account_id) references account (account_id),
    constraint fk_account_holder_customer
        foreign key (customer_id) references customer (customer_id)
);

create table account_balance (
    account_id bigint primary key,
    ledger_balance numeric(18, 2) not null default 0,
    available_balance numeric(18, 2) not null default 0,
    hold_amount numeric(18, 2) not null default 0,
    last_transaction_id bigint,
    version bigint not null default 0,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint fk_account_balance_account
        foreign key (account_id) references account (account_id),
    constraint ck_account_balance_amounts
        check (ledger_balance >= 0 and available_balance >= 0 and hold_amount >= 0)
);

create table account_status_history (
    account_status_history_id bigserial primary key,
    account_id bigint not null,
    previous_status varchar(20),
    new_status varchar(20) not null,
    changed_reason varchar(200),
    changed_by varchar(100),
    changed_at timestamptz not null default current_timestamp,
    created_at timestamptz not null default current_timestamp,
    constraint fk_account_status_history_account
        foreign key (account_id) references account (account_id)
);

create table bank_transaction (
    transaction_id bigserial primary key,
    transaction_type varchar(30) not null,
    transaction_status varchar(20) not null,
    request_id varchar(100),
    idempotency_key varchar(100),
    channel_type varchar(30),
    business_date date not null,
    occurred_at timestamptz not null,
    posted_at timestamptz,
    description varchar(255),
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint uk_bank_transaction_request_id unique (request_id),
    constraint uk_bank_transaction_idempotency_key unique (idempotency_key)
);

create table transaction_entry (
    entry_id bigserial primary key,
    transaction_id bigint not null,
    account_id bigint not null,
    entry_type varchar(30) not null,
    amount numeric(18, 2) not null,
    currency_code varchar(3) not null,
    resulting_balance numeric(18, 2) not null,
    sequence_no integer not null,
    created_at timestamptz not null default current_timestamp,
    constraint fk_transaction_entry_transaction
        foreign key (transaction_id) references bank_transaction (transaction_id),
    constraint fk_transaction_entry_account
        foreign key (account_id) references account (account_id),
    constraint uk_transaction_entry_tx_seq unique (transaction_id, sequence_no),
    constraint ck_transaction_entry_amount check (amount > 0),
    constraint ck_transaction_entry_balance check (resulting_balance >= 0)
);

create table account_ledger (
    ledger_id bigserial primary key,
    account_id bigint not null,
    transaction_id bigint not null,
    entry_id bigint not null,
    ledger_type varchar(30) not null,
    amount_delta numeric(18, 2) not null,
    balance_after numeric(18, 2) not null,
    occurred_at timestamptz not null,
    created_at timestamptz not null default current_timestamp,
    constraint fk_account_ledger_account
        foreign key (account_id) references account (account_id),
    constraint fk_account_ledger_transaction
        foreign key (transaction_id) references bank_transaction (transaction_id),
    constraint fk_account_ledger_entry
        foreign key (entry_id) references transaction_entry (entry_id),
    constraint ck_account_ledger_balance check (balance_after >= 0)
);

create table auto_transfer_rule (
    rule_id bigserial primary key,
    source_account_id bigint not null,
    destination_account_id bigint not null,
    amount_type varchar(20) not null,
    fixed_amount numeric(18, 2),
    schedule_type varchar(20) not null,
    day_of_month integer,
    day_of_week integer,
    start_date date not null,
    end_date date,
    status varchar(20) not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint fk_auto_transfer_rule_source_account
        foreign key (source_account_id) references account (account_id),
    constraint fk_auto_transfer_rule_destination_account
        foreign key (destination_account_id) references account (account_id),
    constraint ck_auto_transfer_rule_fixed_amount
        check (fixed_amount is null or fixed_amount > 0),
    constraint ck_auto_transfer_rule_day_of_month
        check (day_of_month is null or day_of_month between 1 and 31),
    constraint ck_auto_transfer_rule_day_of_week
        check (day_of_week is null or day_of_week between 1 and 7)
);

create table auto_transfer_schedule (
    schedule_id bigserial primary key,
    rule_id bigint not null,
    next_execution_at timestamptz not null,
    last_execution_at timestamptz,
    status varchar(20) not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint fk_auto_transfer_schedule_rule
        foreign key (rule_id) references auto_transfer_rule (rule_id),
    constraint uk_auto_transfer_schedule_rule_id unique (rule_id)
);

create table auto_transfer_execution (
    execution_id bigserial primary key,
    rule_id bigint not null,
    planned_at timestamptz not null,
    executed_at timestamptz,
    result_status varchar(20) not null,
    failure_code varchar(50),
    transaction_id bigint,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint fk_auto_transfer_execution_rule
        foreign key (rule_id) references auto_transfer_rule (rule_id),
    constraint fk_auto_transfer_execution_transaction
        foreign key (transaction_id) references bank_transaction (transaction_id)
);

create table interest_policy (
    interest_policy_id bigserial primary key,
    product_id bigint not null,
    version_no integer not null,
    interest_type varchar(30) not null,
    base_rate numeric(9, 6) not null,
    day_count_basis varchar(20) not null,
    accrual_cycle varchar(20) not null,
    posting_cycle varchar(20) not null,
    effective_from date not null,
    effective_to date,
    status varchar(20) not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint fk_interest_policy_product
        foreign key (product_id) references product (product_id),
    constraint uk_interest_policy_product_version unique (product_id, version_no)
);

create table interest_calculation_batch (
    batch_id bigserial primary key,
    business_date date not null,
    product_id bigint,
    run_sequence integer not null,
    status varchar(20) not null,
    started_at timestamptz,
    ended_at timestamptz,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint fk_interest_calculation_batch_product
        foreign key (product_id) references product (product_id),
    constraint uk_interest_calculation_batch_business_date_run
        unique (business_date, product_id, run_sequence)
);

create table interest_accrual_history (
    accrual_id bigserial primary key,
    batch_id bigint not null,
    account_id bigint not null,
    interest_policy_id bigint not null,
    accrual_start_date date not null,
    accrual_end_date date not null,
    base_amount numeric(18, 2) not null,
    applied_rate numeric(9, 6) not null,
    calculated_interest numeric(18, 2) not null,
    created_at timestamptz not null default current_timestamp,
    constraint fk_interest_accrual_history_batch
        foreign key (batch_id) references interest_calculation_batch (batch_id),
    constraint fk_interest_accrual_history_account
        foreign key (account_id) references account (account_id),
    constraint fk_interest_accrual_history_policy
        foreign key (interest_policy_id) references interest_policy (interest_policy_id),
    constraint ck_interest_accrual_history_amounts
        check (base_amount >= 0 and calculated_interest >= 0)
);

create table interest_posting_history (
    posting_id bigserial primary key,
    accrual_id bigint not null,
    transaction_id bigint not null,
    posted_amount numeric(18, 2) not null,
    posted_at timestamptz not null,
    created_at timestamptz not null default current_timestamp,
    constraint fk_interest_posting_history_accrual
        foreign key (accrual_id) references interest_accrual_history (accrual_id),
    constraint fk_interest_posting_history_transaction
        foreign key (transaction_id) references bank_transaction (transaction_id),
    constraint ck_interest_posting_history_amount check (posted_amount >= 0)
);

create table api_client (
    api_client_id bigserial primary key,
    channel_id bigint not null,
    client_key varchar(100) not null,
    client_name varchar(100) not null,
    status varchar(20) not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint fk_api_client_channel
        foreign key (channel_id) references channel (channel_id),
    constraint uk_api_client_client_key unique (client_key)
);

create table api_request_log (
    api_request_log_id bigserial primary key,
    api_client_id bigint,
    channel_id bigint not null,
    transaction_id bigint,
    request_id varchar(100) not null,
    idempotency_key varchar(100),
    trace_id varchar(100) not null,
    api_path varchar(255) not null,
    http_method varchar(10) not null,
    response_status integer,
    request_at timestamptz not null,
    response_at timestamptz,
    created_at timestamptz not null default current_timestamp,
    constraint fk_api_request_log_api_client
        foreign key (api_client_id) references api_client (api_client_id),
    constraint fk_api_request_log_channel
        foreign key (channel_id) references channel (channel_id),
    constraint fk_api_request_log_transaction
        foreign key (transaction_id) references bank_transaction (transaction_id)
);

create table authentication_audit_log (
    authentication_audit_log_id bigserial primary key,
    channel_id bigint not null,
    principal_id varchar(100) not null,
    auth_result varchar(20) not null,
    failure_reason varchar(200),
    ip_address varchar(50),
    occurred_at timestamptz not null,
    created_at timestamptz not null default current_timestamp,
    constraint fk_authentication_audit_log_channel
        foreign key (channel_id) references channel (channel_id)
);

create table outbox_event (
    event_id bigserial primary key,
    aggregate_type varchar(50) not null,
    aggregate_id varchar(100) not null,
    event_type varchar(50) not null,
    payload jsonb not null,
    publish_status varchar(20) not null,
    occurred_at timestamptz not null,
    published_at timestamptz,
    created_at timestamptz not null default current_timestamp
);

create index ix_account_product_id on account (product_id);
create index ix_account_holder_account_id on account_holder (account_id);
create index ix_account_holder_customer_id on account_holder (customer_id);
create index ix_account_status_history_account_changed_at on account_status_history (account_id, changed_at desc);
create index ix_bank_transaction_occurred_at on bank_transaction (occurred_at desc);
create index ix_bank_transaction_business_date on bank_transaction (business_date);
create index ix_transaction_entry_account_created_at on transaction_entry (account_id, created_at desc);
create index ix_account_ledger_account_occurred_at on account_ledger (account_id, occurred_at desc, ledger_id desc);
create index ix_auto_transfer_schedule_next_execution_at on auto_transfer_schedule (next_execution_at, status);
create index ix_auto_transfer_execution_rule_planned_at on auto_transfer_execution (rule_id, planned_at desc);
create index ix_interest_accrual_history_account_end_date on interest_accrual_history (account_id, accrual_end_date desc);
create index ix_api_request_log_request_id on api_request_log (request_id);
create index ix_api_request_log_trace_id on api_request_log (trace_id);
create index ix_api_request_log_transaction_id on api_request_log (transaction_id);
create index ix_authentication_audit_log_principal_occurred_at on authentication_audit_log (principal_id, occurred_at desc);
create index ix_outbox_event_publish_status_occurred_at on outbox_event (publish_status, occurred_at);
