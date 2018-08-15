package com.redbluekey.sciodev.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionImageRibbon {

    @JsonProperty(value = "i")
    private String image;
    @JsonProperty(value = "t")
    private String thumbnail;
}
