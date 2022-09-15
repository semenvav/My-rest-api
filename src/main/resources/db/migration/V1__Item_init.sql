create table system_item
(
    id       varchar(255) not null
        primary key,
    date     varchar(255) not null,
    parentid varchar(255),
    size     integer      not null,
    type     varchar(255) not null,
    url      varchar(255)
);

create table system_item_system_item
(
    systemitem_id varchar(255) not null
        constraint fkrmmr7xodewqd6i4jm0j6gwg82
            references system_item,
    children_id   varchar(255) not null
        constraint uk_gfbkoyyox1ynv6jxi6gg2vnak
            unique
        constraint fk3tmgh9hp4j9bag424fv2qf1yx
            references system_item
);
