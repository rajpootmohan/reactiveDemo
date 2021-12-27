package com.example.reactiveDemo.configuration;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Primary
public class ElasticSearchConfiguration extends AbstractFactoryBean implements DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchConfiguration.class);

    @Value("${spring.data.elasticsearch.cluster.nodes}")
    private String clusterNodes;

    private int numberOfShards = 1;

    private int numberOfReplicas = 5;

    private RestHighLevelClient restHighLevelClient1;

    @Override
    public void destroy() {
        try {
            LOGGER.info("Closing elasticSearch client");
            if (restHighLevelClient1 != null) {
                restHighLevelClient1.close();
            }
        } catch (final Exception e) {
            LOGGER.error("Error closing ElasticSearch client: " + e);
        }
    }

    @Override
    @Primary
    public Class<RestHighLevelClient> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    @Primary
    protected RestHighLevelClient createInstance() throws Exception {
        return buildClient();
    }

    @Primary
    private RestHighLevelClient buildClient() throws UnknownHostException {
        try {
            String[] nodes = clusterNodes.split(",");

            List<HttpHost> hosts = new ArrayList<>();

            for (String node : nodes) {
                String[] inetSocket = node.split(":");
                String address = inetSocket[0];
                Integer port = Integer.valueOf(inetSocket[1]);
                hosts.add(new HttpHost(address, port));
            }
            System.out.println(hosts);


            HttpRequestInterceptor httpRequestInterceptor = (request, context) -> {
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                //StackWalker.getCallerClass();
                System.out.println("RequestInterceptor");
                System.out.println(request.toString());
                context.setAttribute("time1", System.currentTimeMillis());
            };

            HttpResponseInterceptor httpResponseInterceptor = (response, context) -> {
                System.out.println("ResponseInterceptor");
                System.out.println(context.getAttribute("time1"));
                System.out.println(System.currentTimeMillis());
                System.out.println(response.toString());
            };


            RestClientBuilder restClientBuilder = RestClient.builder(hosts.toArray(new HttpHost[0]));
            restClientBuilder.setHttpClientConfigCallback(httpClientBuilder ->
                    httpClientBuilder.addInterceptorLast(httpResponseInterceptor)
                            .addInterceptorFirst(httpRequestInterceptor)
            );

            return restHighLevelClient1 = new RestHighLevelClient(
                    restClientBuilder
            );

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }



    public int getNumberOfShards() {
        return numberOfShards;
    }

    public void setNumberOfShards(int numberOfShards) {
        this.numberOfShards = numberOfShards;
    }

    public int getNumberOfReplicas() {
        return numberOfReplicas;
    }

    public void setNumberOfReplicas(int numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }
}
