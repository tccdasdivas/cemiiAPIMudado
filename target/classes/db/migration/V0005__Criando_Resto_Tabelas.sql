create table tb_mensagem(
id bigint not null auto_increment,
mensagem varchar(255),

primary key (id)
) engine=InnoDB default charset=utf8;