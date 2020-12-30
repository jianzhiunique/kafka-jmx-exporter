package io.github.jianzhiunique.controller;

import io.github.jianzhiunique.lag.LagMetrics;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LagController {
    @RequestMapping(value = "/lag", produces = "text/plain; version=0.0.4; charset=utf-8")
    String lag(){
        StringBuilder stringBuilder = new StringBuilder();
        LagMetrics.lagMap.forEach((key, value)-> {
            stringBuilder.append(String.format("kafka_lag{topic=\"%s\",partition=\"%s\",group=\"%s\"} %d", key.topicPartition().topic(),
                    key.topicPartition().partition(), key.group(), value));
            stringBuilder.append("\n");
        });

        return stringBuilder.toString();
    }
}
