package fr.pronofoot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

@Configuration
public class WebClientConfig {

    @Value("${football.api.base-url}")
    private String baseUrl;

    @Value("${football.api.token}")
    private String apiToken;

    // Configure ton proxy ici :
    @Value("${proxy.host}")
    private String proxyHost;

    @Value("${proxy.port}")
    private int proxyPort;

    @Bean
    public WebClient footballApiClient() {
        HttpClient httpClient = HttpClient.create()
                .proxy(proxy -> proxy
                        .type(ProxyProvider.Proxy.HTTP)
                        .host(proxyHost)
                        .port(proxyPort)
                );

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 1 Mo
                .build();

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Auth-Token", apiToken)
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
