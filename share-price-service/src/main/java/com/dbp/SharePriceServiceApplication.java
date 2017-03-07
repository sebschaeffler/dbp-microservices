package com.dbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Stream;

import static springfox.documentation.builders.PathSelectors.regex;

interface SharePriceChannels {
    @Input
    MessageChannel input();
}

@EnableBinding(SharePriceChannels.class)
@EnableDiscoveryClient
@SpringBootApplication
@EnableSwagger2
public class SharePriceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SharePriceServiceApplication.class, args);
    }

    @Bean
    public Docket sharePriceApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("share-price")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/sharePrices.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring REST API for Share Prices with Swagger")
                .description("Spring REST API for Share Prices with Swagger")
                .termsOfServiceUrl("http://www.dbg-dbp.com")
                .contact("Sebastien")
                .license("Open Bar")
                .version("1.0")
                .build();
    }
}

@MessageEndpoint
class SharePriceProcessor {

    private final SharePriceRepository sharePriceRepository;

    @ServiceActivator(inputChannel = "input")
    public void onMessage(Message<String> msg) {
        this.sharePriceRepository.save(new SharePrice(msg.getPayload(), Double.MIN_VALUE));
    }

    @Autowired
    public SharePriceProcessor(SharePriceRepository sharePriceRepository) {
        this.sharePriceRepository = sharePriceRepository;
    }
}


@RefreshScope
@RestController
class MessageRestController {

    private final String value;

    @Autowired
    public MessageRestController(@Value("${message}") String value) {
        this.value = value;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/message")
    private String read() {
        return this.value;
    }
}

@Component
class SampleDataCLR implements CommandLineRunner {

    private final SharePriceRepository sharePriceRepository;

    @Autowired
    public SampleDataCLR(SharePriceRepository sharePriceRepository) {
        this.sharePriceRepository = sharePriceRepository;
    }

    @Override
    public void run(String... strings) throws Exception {
        Random r = new Random();
        Double rangeMin = new Double(10);
        Double rangeMax = new Double(100);
        Stream.of("Deutsche Boerse", "Barclays", "Fortis", "ING")
                .forEach(n -> sharePriceRepository.save(new SharePrice(n, rangeMin + (rangeMax - rangeMin) * r.nextDouble() )));
        sharePriceRepository.findAll().forEach(System.out::println);
    }
}

@RepositoryRestResource
interface SharePriceRepository extends JpaRepository<SharePrice, Long> {

    @RestResource(path = "by-id")
    SharePrice findById(@Param("id") Long id);

    @RestResource(path = "by-name")
    Collection<SharePrice> findBySharePriceName(@Param("share") String sharePriceName);
}

@Entity
class SharePrice {

    @Id
    @GeneratedValue
    private Long id;

    private String sharePriceName;

    private Double price;

    public SharePrice() {
    }

    @Override
    public String toString() {
        return "SharePrice{" +
                "id=" + id +
                ", sharePriceName='" + sharePriceName + '\'' +
                ", sharePrice='" + price + '\'' +
                '}';
    }

    public SharePrice(String sharePriceName, Double value) {
        this.sharePriceName = sharePriceName;
        this.price = value;
    }

    @JsonProperty(required = true)
    @ApiModelProperty(notes = "Id", required = true)
    public Long getId() {
        return id;
    }

    @JsonProperty(required = true)
    @ApiModelProperty(notes = "The name of the share", required = true)
    public String getSharePriceName() {
        return sharePriceName;
    }

    @JsonProperty(required = true)
    @ApiModelProperty(notes = "The share price", required = true)
    public Double getPrice() {
        return price;
    }

}