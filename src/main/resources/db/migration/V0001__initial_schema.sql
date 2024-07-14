CREATE FUNCTION generate_searchable_text(apelido varchar, nome varchar, nascimento date,
                                         stack character varying[]) RETURNS text AS
$$
select (apelido || ' ' || nome || ' ' || TO_CHAR(nascimento, 'YYYY-MM-DD') || ' ' || coalesce(array_to_string(stack, ' '), ''));
$$
    LANGUAGE sql immutable;

create
    extension if not exists pgcrypto;
create
    extension if not exists pg_trgm;

create table pessoa
(
    id              uuid primary key,
    apelido         varchar(32) unique not null,
    nome            varchar(100)       not null,
    nascimento      date               not null,
    stack           varchar(32)[],
    searchable_text text generated always as ( generate_searchable_text(apelido, nome, nascimento, stack)) stored
);

--create index pessoa_idx1 on pessoa using gin (searchable_text gin_trgm_ops);
create index pessoa_idx2 on pessoa using gist (searchable_text gist_trgm_ops);
