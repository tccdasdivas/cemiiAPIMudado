alter table tb_cidade add constraint fk_cidade_estado foreign key (estado_id) references tb_estado(id);

alter table tb_usuario add constraint fk_usuario_cidade foreign key (cidade_id) references tb_cidade(id);