package com.divas.cemii.domain.service;

import com.divas.cemii.domain.exception.EntidadeEmUsoException;
import com.divas.cemii.domain.exception.EntidadeNaoEncontradaException;
import com.divas.cemii.domain.model.Idoso;
import com.divas.cemii.domain.repository.IdosoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class IdosoService {

    @Autowired
    private IdosoRepository idosoRepository;

    public Idoso salvar(Idoso idoso){
        return idosoRepository.save(idoso);
    }

    public Idoso verificar(LocalDate nascimento){

        LocalDate dataAtual = LocalDate.now();
        int idade = Period.between(nascimento, dataAtual).getYears();

        if (idade <= 60) {
            Idoso novoIdoso = new Idoso();
            novoIdoso.setNascimento(nascimento);
            return idosoRepository.save(novoIdoso);
        } else {throw new IllegalArgumentException("Cadastro não permitido: Não é idoso.");
        }
    }

    public void excluir(Long id){
        try{
            idosoRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e){
            throw new EntidadeEmUsoException(String.format("Idoso ou código %d não pode ser removida, pois está em uso.", id));
        }
        catch (EmptyResultDataAccessException e){
            throw new EntidadeNaoEncontradaException(String.format("Não existe cadastro de idoso %d", id));
        }
    }


}
