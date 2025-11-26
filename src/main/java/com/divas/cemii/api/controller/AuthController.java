package com.divas.cemii.api.controller;

import com.divas.cemii.domain.model.Cidade;
import com.divas.cemii.domain.model.Estado;
import com.divas.cemii.domain.model.Idoso;
import com.divas.cemii.domain.model.Usuario;
import com.divas.cemii.domain.repository.IdosoRepository;
import com.divas.cemii.domain.repository.UsuarioRepository;
import com.divas.cemii.dto.LoginRequestDTO;
import com.divas.cemii.dto.RegisterRequestDTO;
import com.divas.cemii.dto.ResponseDTO;
import com.divas.cemii.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
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

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<?> register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String telefone,
            @RequestParam String cpf,
            @RequestParam String nascimento,
            @RequestParam String profissao,
            @RequestParam(required = false) String coren,
            @RequestParam String diasHorarios,
            @RequestParam(required = false) String experiencia,
            @RequestParam String tipo,
            @RequestParam("cidade[id]") Long cidadeId,
            @RequestParam("cidade[nome]") String cidadeNome,
            @RequestParam("cidade[estado][id]") Long estadoId,
            @RequestParam("cidade[estado][nome]") String estadoNome,
            @RequestParam("cidade[estado][sigla]") String estadoSigla,
            @RequestPart(required = false) MultipartFile foto
    ) throws IOException {

        // Valida campos obrigatórios
        if (name.isBlank() || email.isBlank() || password.isBlank() || telefone.isBlank() ||
                cpf.isBlank() || nascimento.isBlank() || profissao.isBlank() ||
                diasHorarios.isBlank() || tipo.isBlank()) {
            return ResponseEntity.badRequest().body("Todos os campos obrigatórios devem ser preenchidos.");
        }

        // Verifica se email já existe
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.status(409).body("Email já cadastrado");
        }

        // Cria novo usuário
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(name);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(passwordEncoder.encode(password));
        novoUsuario.setTelefone(telefone);
        novoUsuario.setCpf(cpf);
        novoUsuario.setNascimento(LocalDate.parse(nascimento));
        novoUsuario.setProfissao(profissao);
        novoUsuario.setCoren(coren);
        novoUsuario.setDiasHorarios(diasHorarios);
        novoUsuario.setExperiencia(experiencia);
        novoUsuario.setTipo(tipo.toUpperCase());

        // Configura cidade e estado
        Estado estado = new Estado();
        estado.setId(estadoId);
        estado.setNome(estadoNome);
        estado.setSigla(estadoSigla);

        Cidade cidade = new Cidade();
        cidade.setId(cidadeId);
        cidade.setNome(cidadeNome);
        cidade.setEstado(estado);

        novoUsuario.setCidade(cidade);

        // Configura foto
        if (foto != null && !foto.isEmpty()) {
            novoUsuario.setFoto(foto.getBytes());
        }

        // Salva no banco
        usuarioRepository.save(novoUsuario);

        // Gera token
        String token = tokenService.generateToken(novoUsuario);

        return ResponseEntity.ok(
                new ResponseDTO(novoUsuario.getId(), novoUsuario.getNome(), token, novoUsuario.getEmail(), novoUsuario.getTipo())
        );
    }
}

