create table tb_mensagem(
id bigint not null auto_increment,
mensagem varchar(255),

primary key (id)
) engine=InnoDB default charset=utf8;

create table tb_usuario(
id bigint not null auto_increment,
nome varchar(255),
email varchar (255),
telefone varchar (12),
cpf varchar(12),
parentesco varchar(50),
profissao varchar(70),
cidade_id bigint not null,
senha varchar(255),
nascimento date,
coren varchar(50),
tipo varchar(50),
foto longblob,
dias_horarios text,
experiencia varchar(255),

primary key (id)
) engine=InnoDB default charset=utf8;