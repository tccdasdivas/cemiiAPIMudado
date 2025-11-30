package com.divas.cemii.domain.service;

import com.divas.cemii.domain.exception.EntidadeNaoEncontradaException;
import com.divas.cemii.domain.model.Mensagem;
import com.divas.cemii.domain.model.Usuario;
import com.divas.cemii.domain.repository.MensagemRepository;
import com.divas.cemii.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Mensagem salvar(Mensagem mensagem){
        return mensagemRepository.save(mensagem);
    }

    public void excluir(Long id){
        try{
            mensagemRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e){
            throw new EntidadeNaoEncontradaException(String.format("Mensagem não encontrada %d", id));
        }
    }


}
