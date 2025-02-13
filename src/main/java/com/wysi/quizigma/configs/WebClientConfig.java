package com.wysi.quizigma.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider.Proxy;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        // Set your proxy IP and port
        String proxyHost = "178.128.113.118";
        int proxyPort = 23128;  // Change to correct port

        HttpClient httpClient = HttpClient.create()
                .proxy(proxy -> proxy
                    .type(Proxy.HTTP) // Use Proxy.HTTP for SSL tunneling
                    .host(proxyHost)
                    .port(proxyPort));

        // Create WebClient with proxy settings
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}
