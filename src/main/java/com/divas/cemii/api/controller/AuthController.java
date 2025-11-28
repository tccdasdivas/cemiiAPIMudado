package com.divas.cemii.api.controller;

import com.divas.cemii.domain.model.Cidade;
import com.divas.cemii.domain.model.Estado;
import com.divas.cemii.domain.model.Usuario;
import com.divas.cemii.domain.repository.CidadeRepository;
import com.divas.cemii.domain.repository.EstadoRepository;
import com.divas.cemii.domain.repository.IdosoRepository;
import com.divas.cemii.domain.repository.UsuarioRepository;
import com.divas.cemii.dto.CidadeDTO;
import com.divas.cemii.dto.LoginRequestDTO;
import com.divas.cemii.dto.RegisterRequestDTO;
import com.divas.cemii.dto.ResponseDTO;
import com.divas.cemii.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Base64;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private IdosoRepository idosoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private CidadeRepository cidadeRepository;

    // 🔐 LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {
        Usuario usuario = usuarioRepository.findByEmail(body.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (passwordEncoder.matches(body.senha(), usuario.getSenha())) {
            String token = tokenService.generateToken(usuario);
            return ResponseEntity.ok(
                    new ResponseDTO(usuario.getId(), usuario.getNome(), token, usuario.getEmail(), usuario.getTipo())
            );
        }

        return ResponseEntity.badRequest().body("Senha incorreta");
    }

    // ♻️ REFRESH TOKEN
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(name = "Authorization") String bearerToken) {
        String subject = tokenService.validateToken(tokenService.recoverToken(bearerToken));

        Usuario usuario = usuarioRepository.findByEmail(subject)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = tokenService.generateToken(usuario);

        return ResponseEntity.ok(
                new ResponseDTO(usuario.getId(), usuario.getNome(), token, usuario.getEmail(), usuario.getTipo())
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO body) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(body.email());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.status(409).body("Email já cadastrado");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(body.name());
        novoUsuario.setEmail(body.email());
        novoUsuario.setSenha(passwordEncoder.encode(body.password()));
        novoUsuario.setTelefone(body.telefone());
        novoUsuario.setCpf(body.cpf());
        if (body.foto() != null && !body.foto().isBlank()) {
            try {
                byte[] fotoBytes = Base64.getDecoder().decode(body.foto());
                novoUsuario.setFoto(fotoBytes);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Base64 inválido para a foto.");
            }
        }
        novoUsuario.setNascimento(body.nascimento());
        novoUsuario.setParentesco(body.parentesco());
        novoUsuario.setProfissao(body.profissao());
        novoUsuario.setCoren(body.coren());
        novoUsuario.setDiasHorarios(body.diasHorarios());
        novoUsuario.setExperiencia(body.experiencia());
        if (body.cidade() != null) {
            Long cidadeId = body.cidade().id();
            if (cidadeId != null) {
                Cidade cidade = cidadeRepository.findById(cidadeId)
                        .orElseThrow(() -> new RuntimeException("Cidade não encontrada"));
                novoUsuario.setCidade(cidade);
            } else if (body.cidade().estado() != null && body.cidade().estado().id() != null) {
                // só para o caso improvável de receber somente ids do estado e precisar montar cidade:
                Estado estado = estadoRepository.findById(body.cidade().estado().id())
                        .orElseThrow(() -> new RuntimeException("Estado não encontrado"));
                Cidade cidade = new Cidade();
                cidade.setId(null); // ou algum valor — normalmente não se cria assim
                cidade.setNome(body.cidade().nome());
                cidade.setEstado(estado);
                novoUsuario.setCidade(cidade);
            }
        } // importante: verificar se a entidade Cidade está correta

        if (body.tipo() == null || body.tipo().isBlank()) {
            return ResponseEntity.badRequest().body("Campo 'tipo' é obrigatório (RESPONSAVEL ou CUIDADOR).");
        }
        novoUsuario.setTipo(body.tipo().toUpperCase());

        usuarioRepository.save(novoUsuario);

        String token = tokenService.generateToken(novoUsuario);
        return ResponseEntity.ok(
                new ResponseDTO(novoUsuario.getId(), novoUsuario.getNome(), token, novoUsuario.getEmail(), novoUsuario.getTipo())
        );
    }
}

