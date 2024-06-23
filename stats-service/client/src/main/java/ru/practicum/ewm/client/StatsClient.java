package ru.practicum.ewm.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

@Component
@Slf4j
public class StatsClient implements BaseClient {
    private final WebClient webClient;
    Properties property = new Properties();
    FileInputStream fis;
    String url;

    public StatsClient() {
        try {
            fis = new FileInputStream("stats-service/client/src/main/resources/config.properties");
            property.load(fis);
            url = property.getProperty("statsServer.url");
            System.out.println(url);
        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
        }
        webClient = WebClient.create(url);
    }

    public EndpointHitDto addHit(EndpointHitDto endpointHitDto) {
        return webClient
                .post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(endpointHitDto))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is5xxServerError()) {
                        log.error("Server Error");
                        return Mono.error(new RuntimeException("Server Error"));
                    } else if (clientResponse.statusCode().is4xxClientError()) {
                        log.error("Client Error");
                        return Mono.error(new RuntimeException("Client Error"));
                    } else {
                        return clientResponse.bodyToMono(EndpointHitDto.class);
                    }
                })
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
                .exchangeToFlux(clientResponse -> {
                    if (clientResponse.statusCode().is5xxServerError()) {
                        log.error("Server Error");
                        return Flux.error(new RuntimeException("Server Error"));
                    } else if (clientResponse.statusCode().is4xxClientError()) {
                        log.error("Client Error");
                        return Flux.error(new RuntimeException("Client Error"));
                    } else {
                        return clientResponse.bodyToFlux(ViewStatsDto.class);
                    }
                })
                .collectList()
                .block();
        return list;
    }
}

