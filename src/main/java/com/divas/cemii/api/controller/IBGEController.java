package com.divas.cemii.api.controller;

import com.divas.cemii.domain.model.Cidade;
import com.divas.cemii.domain.model.Estado;
import com.divas.cemii.domain.repository.CidadeRepository;
import com.divas.cemii.domain.repository.EstadoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ibge")
@CrossOrigin(origins = "*")
public class IBGEController {

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private CidadeRepository cidadeRepository;

    /**
     * 🔹 Importa estados e cidades do IBGE e salva no banco
     */
    @GetMapping("/importar")
    public ResponseEntity<String> importarEstadosECidades() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        // 🔹 Importa estados
        String estadosJson = restTemplate.getForObject(
                "https://servicodados.ibge.gov.br/api/v1/localidades/estados",
                String.class
        );
        JsonNode estadosNode = mapper.readTree(estadosJson);

        for (JsonNode estadoNode : estadosNode) {
            Long estadoId = estadoNode.get("id").asLong();
            String nomeEstado = estadoNode.get("nome").asText();
            String sigla = estadoNode.get("sigla").asText();

            Estado estado = estadoRepository.findById(estadoId).orElseGet(() -> {
                Estado novo = new Estado();
                novo.setId(estadoId);
                novo.setNome(nomeEstado);
                novo.setSigla(sigla);
                return estadoRepository.save(novo);
            });

            // 🔹 Importa cidades do estado
            String cidadesJson = restTemplate.getForObject(
                    "https://servicodados.ibge.gov.br/api/v1/localidades/estados/" + estadoId + "/municipios",
                    String.class
            );

            JsonNode cidadesNode = mapper.readTree(cidadesJson);

            for (JsonNode cidadeNode : cidadesNode) {
                Long cidadeId = cidadeNode.get("id").asLong();
                String nomeCidade = cidadeNode.get("nome").asText();

                // Evita duplicados
                if (cidadeRepository.existsById(cidadeId)) continue;

                Cidade cidade = new Cidade();
                cidade.setId(cidadeId);
                cidade.setNome(nomeCidade);
                cidade.setEstado(estado);
                cidadeRepository.save(cidade);
            }
        }

        return ResponseEntity.ok("Importação de estados e cidades concluída com sucesso!");
    }

    /**
     * 🔹 Lista todos os estados cadastrados
     */
    @GetMapping("/estados")
    public ResponseEntity<List<Estado>> listarEstados() {
        List<Estado> estados = estadoRepository.findAll(Sort.by("nome"));
        return ResponseEntity.ok(estados);
    }

    /**
     * 🔹 Lista cidades de um estado
     */
    @GetMapping("/cidades/{estadoId}")
    public ResponseEntity<List<Cidade>> listarCidades(@PathVariable Long estadoId) {
        List<Cidade> cidades = cidadeRepository.findByEstadoIdOrderByNome(estadoId);
        return ResponseEntity.ok(cidades);
    }
}
