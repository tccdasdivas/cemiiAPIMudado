package com.divas.cemii.api.controller;

import com.divas.cemii.domain.exception.EntidadeEmUsoException;
import com.divas.cemii.domain.model.Estado;
import com.divas.cemii.domain.repository.EstadoRepository;
import com.divas.cemii.domain.service.EstadoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/estados")
@RestController
public class EstadoController {
    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private EstadoService estadoService;

    @GetMapping
    public List<Estado> listar(){
        return estadoRepository.findAll();
    }

    @GetMapping("/{estadoId}")
    public ResponseEntity<Estado> buscar(@PathVariable Long estadoId){
        Optional <Estado> estado = estadoRepository.findById(estadoId);

        if(estado.isPresent()){
            return ResponseEntity.ok(estado.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Estado> adicionar(@RequestBody Estado estado){
        estado = estadoService.salvar(estado);
        return ResponseEntity.status(HttpStatus.CREATED).body(estado);
    }

    @PutMapping("/{estadoId}")
    public ResponseEntity<Estado> atualizar(@PathVariable Long estadoId, @RequestBody Estado estado){
        Optional <Estado> estadoAtual = estadoRepository.findById(estadoId);

        if (estadoAtual != null){
            BeanUtils.copyProperties(estado, estadoAtual, "id");

            Estado estadoSalva = estadoService.salvar(estadoAtual.get());
            return ResponseEntity.ok(estadoSalva);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{estadoId}")
    public ResponseEntity<Estado> remover(@PathVariable Long estadoId){
        try{
            estadoService.excluir(estadoId);
            return ResponseEntity.notFound().build();
        } catch (EnumConstantNotPresentException e){
            return ResponseEntity.notFound().build();
        } catch (EntidadeEmUsoException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}

