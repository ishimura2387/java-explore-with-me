package ru.practicum.ewm.client;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public class StatsClient {
    private final WebClient webClient;

    public StatsClient(String url) {
        webClient = WebClient.create(url);
    }

    public EndpointHitDto addHit(EndpointHitDto endpointHitDto) {
        return webClient
                .post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(endpointHitDto))
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(EndpointHitDto.class))
                .block();
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return webClient
                .get()
                .uri("/stats?start={start}&end={end}&uris={uris}&unique={unique}", start, end, uris, unique)
                .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(ViewStatsDto.class))
                .collectList()
                .block();
    }
}
