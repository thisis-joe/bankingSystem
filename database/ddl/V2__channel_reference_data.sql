insert into channel (
    channel_code,
    channel_name,
    channel_type,
    status
)
select
    'OPEN_API',
    '오픈 API',
    'EXTERNAL_API',
    'ACTIVE'
where not exists (
    select 1
    from channel
    where channel_code = 'OPEN_API'
);

insert into api_client (
    channel_id,
    client_key,
    client_name,
    status
)
select
    channel.channel_id,
    'banking-open-client',
    '기본 오픈 API 클라이언트',
    'ACTIVE'
from channel
where channel.channel_code = 'OPEN_API'
  and not exists (
      select 1
      from api_client
      where client_key = 'banking-open-client'
  );
