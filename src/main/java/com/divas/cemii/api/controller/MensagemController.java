package com.divas.cemii.api.controller;

import com.divas.cemii.domain.model.Mensagem;
import com.divas.cemii.domain.repository.MensagemRepository;
import com.divas.cemii.domain.service.MensagemService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/mensagens")
@RestController
public class MensagemController {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private MensagemService mensagemService;

    @GetMapping
    public List<Mensagem> listar(){
        return mensagemRepository.findAll();
    }

    @GetMapping("/{mensagemId}")
    public ResponseEntity<Mensagem> buscar(@PathVariable Long mensagemId){
        Optional<Mensagem>mensagem = mensagemRepository.findById(mensagemId);

        if (mensagem.isPresent()){
            return ResponseEntity.ok(mensagem.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mensagem adicionar(@RequestBody Mensagem mensagem){
        return mensagemService.salvar(mensagem);
    }

    @PutMapping("/{mensagemId}")
    public ResponseEntity<Mensagem> atualizar(@PathVariable Long mensagemId, @RequestBody Mensagem mensagem){
        Optional<Mensagem> mensagemAtual = mensagemRepository.findById(mensagemId);

        if (mensagemAtual != null){
            BeanUtils.copyProperties(mensagem, mensagemAtual, "id");

            Mensagem mensagemSalva = mensagemService.salvar(mensagemAtual.get());
            return ResponseEntity.ok(mensagemSalva);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{mensagemId}")
    public ResponseEntity<Mensagem> remover(@PathVariable Long mensagemId){
        try{
            mensagemService.excluir(mensagemId);
            return ResponseEntity.notFound().build();
        } catch (EnumConstantNotPresentException e){
            return ResponseEntity.notFound().build();
        }
    }
}
