create table  tb_idoso(
id bigint not null auto_increment,
nome varchar (100),
cpf varchar (11),
necessidades varchar (255),
responsavel_id bigint not null,
cidade_id bigint not null,
nascimento date not null,
foto varchar(255),
logradouro varchar(255),
bairro varchar(255),
numero varchar(20),

primary key (id)
) engine=InnoDB default charset=utf8;

