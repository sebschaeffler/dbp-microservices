package com.dbp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collection;
import java.util.Random;
import java.util.stream.Stream;

import static springfox.documentation.builders.PathSelectors.regex;

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
