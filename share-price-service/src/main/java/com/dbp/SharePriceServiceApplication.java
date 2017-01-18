package com.dbp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Stream;

interface SharePriceChannels {
    @Input
    MessageChannel input();
}

@EnableBinding(SharePriceChannels.class)
@EnableDiscoveryClient
@SpringBootApplication
public class SharePriceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SharePriceServiceApplication.class, args);
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
    String read() {
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

    @RestResource(path = "by-name")
    Collection<SharePrice> findBySharePriceName(@Param("share") String rn);
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

    public Long getId() {
        return id;
    }

    public String getSharePriceName() {
        return sharePriceName;
    }

    public Double getPrice() {
        return price;
    }

}