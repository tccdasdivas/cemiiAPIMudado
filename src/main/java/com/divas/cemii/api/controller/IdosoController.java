package com.divas.cemii.api.controller;

import com.divas.cemii.domain.exception.EntidadeEmUsoException;
import com.divas.cemii.domain.model.Idoso;
import com.divas.cemii.domain.model.Usuario;
import com.divas.cemii.domain.repository.IdosoRepository;
import com.divas.cemii.domain.repository.UsuarioRepository;
import com.divas.cemii.domain.service.IdosoService;
import com.divas.cemii.infra.security.TokenService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RequestMapping("/idosos")
@RestController
public class IdosoController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private IdosoRepository idosoRepository;

    @Autowired
    private IdosoService idosoService;

    @GetMapping
    public List<Idoso> listar(){
        return idosoRepository.findAll();
    }

    @GetMapping("/{idosoId}")
    public ResponseEntity<Idoso> buscar(@PathVariable Long idosoId){
        Optional <Idoso> idoso = idosoRepository.findById(idosoId);

        if(idoso.isPresent()){
            return ResponseEntity.ok(idoso.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Idoso adicionar(@RequestBody Idoso idoso){
        if (idoso.getFotoBase64() != null && !idoso.getFotoBase64().isBlank()) {
            try {
                byte[] fotoBytes = Base64.getDecoder().decode(idoso.getFotoBase64());
                idoso.setFotoBase64(null);
                idoso.setFoto(fotoBytes);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Base64 inválido para a foto.");
            }
        }

        return idosoService.salvar(idoso);
    }

    public ResponseEntity<Idoso> verificar(@RequestBody Idoso idoso) {
        Idoso salvo = idosoService.verificar(idoso.getNascimento());
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{idosoId}")
    public ResponseEntity<Idoso> atualizar(@PathVariable Long idosoId, @RequestBody Idoso idoso){
        Optional <Idoso> idosoAtual = idosoRepository.findById(idosoId);

        if (idosoAtual.isPresent()){
            BeanUtils.copyProperties(idoso, idosoAtual.get(), "id");

            Idoso idosoSalva = idosoService.salvar(idosoAtual.get());
            return ResponseEntity.ok(idosoSalva);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{idosoId}")
    public ResponseEntity<Idoso> remover(@PathVariable Long idosoId){
        try{
            idosoService.excluir(idosoId);
            return ResponseEntity.notFound().build();
        } catch (EnumConstantNotPresentException e){
            return ResponseEntity.notFound().build();
        } catch (EntidadeEmUsoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


}
