package ru.practicum.ewm.client;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public List<ViewStatsDto> getStats(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime, List<String> uris, boolean unique) {
        String start = startLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = endLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String urisString = "";
        for (String uri : uris) {
            urisString = urisString + "," + uri;
        }
        urisString = urisString.substring(1);
        System.out.println(urisString);
        List<ViewStatsDto> list = webClient
                .get()
                .uri("/stats?start={start}&end={end}&uris={urisString}&unique={unique}", start, end, urisString, String.valueOf(unique))
                .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(ViewStatsDto.class))
                .collectList()
                .block();
        return list;
    }
}

