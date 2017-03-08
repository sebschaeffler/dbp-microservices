package com.dbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by sebastienschaeffler on 08/03/2017.
 */
@Entity
public class SharePrice {

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
