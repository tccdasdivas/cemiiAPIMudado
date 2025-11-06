package com.divas.cemii.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;

@Service
public class IBGEService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String IBGE_ESTADOS_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/estados";
    private static final String IBGE_CIDADES_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/{UF}/municipios";

    public List<Map<String, Object>> listarEstados() {
        ResponseEntity<List> response = restTemplate.getForEntity(IBGE_ESTADOS_URL, List.class);
        return response.getBody();
    }

    public List<Map<String, Object>> listarCidadesPorUf(String uf) {
        ResponseEntity<List> response = restTemplate.getForEntity(IBGE_CIDADES_URL, List.class, uf);
        return response.getBody();
    }
}
