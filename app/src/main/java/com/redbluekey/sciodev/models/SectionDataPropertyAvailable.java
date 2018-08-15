package com.redbluekey.sciodev.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionDataPropertyAvailable {

    private int id;
    @JsonProperty(value = "t")
    private String title;
    @JsonProperty(value = "l")
    private String link;
}
