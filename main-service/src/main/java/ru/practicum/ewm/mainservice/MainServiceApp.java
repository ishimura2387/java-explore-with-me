package ru.practicum.ewm.mainservice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = "ru.practicum.ewm.client")
public class MainServiceApp {

    public static StatsClient statsClient = new StatsClient("http://localhost:9090");

    public static void main(String[] args) {
        SpringApplication.run(MainServiceApp.class, args);
        List<String> uris = new ArrayList<>();
        uris.add("/events/1");
        //1 DTO
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("ewm-main-service");
        endpointHitDto.setUri("/events/1");
        endpointHitDto.setIp("192.163.0.1");
        endpointHitDto.setCreateTime(LocalDateTime.now());
        statsClient.addHit(endpointHitDto);
        List<ViewStatsDto> loadEndpointHitDto = statsClient.getStats(LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                uris,false);
        System.out.println("size load = " + loadEndpointHitDto.size());
        System.out.println("app = " +  loadEndpointHitDto.get(0).getApp());
        System.out.println("uri = " +  loadEndpointHitDto.get(0).getUri());
        System.out.println("hits = " +  loadEndpointHitDto.get(0).getHits());
        //2 DTO
        EndpointHitDto endpointHitDto2 = new EndpointHitDto();
        endpointHitDto2.setApp("ewm-main-service");
        endpointHitDto2.setUri("/events/2");
        endpointHitDto2.setIp("192.163.0.4");
        endpointHitDto2.setCreateTime(LocalDateTime.now());
        statsClient.addHit(endpointHitDto2);
        //3 DTO
        EndpointHitDto endpointHitDto3 = new EndpointHitDto();
        endpointHitDto3.setApp("ewm-main-service");
        endpointHitDto3.setUri("/events/2");
        endpointHitDto3.setIp("192.163.0.5");
        endpointHitDto3.setCreateTime(LocalDateTime.now());
        statsClient.addHit(endpointHitDto3);
        uris.add("/events/2");
        List<ViewStatsDto> loadEndpointHitDto2 = statsClient.getStats(LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                uris,false);
        System.out.println("size load = " + loadEndpointHitDto2.size());
        System.out.println("app = " +  loadEndpointHitDto2.get(0).getApp());
        System.out.println("uri = " +  loadEndpointHitDto2.get(0).getUri());
        System.out.println("hits = " +  loadEndpointHitDto2.get(0).getHits());
        System.out.println("app = " +  loadEndpointHitDto2.get(1).getApp());
        System.out.println("uri = " +  loadEndpointHitDto2.get(1).getUri());
        System.out.println("hits = " +  loadEndpointHitDto2.get(1).getHits());
    }
}
