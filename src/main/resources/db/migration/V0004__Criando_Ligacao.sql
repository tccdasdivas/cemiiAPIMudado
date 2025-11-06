alter table tb_cidade add constraint fk_cidade_estado foreign key (estado_id) references tb_estado(id);
