package com.redbluekey.sciodev.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SectionTextList {
    @JsonProperty(value = "ti")
    private String title;
    @JsonProperty(value = "i")
    private String image;
    @JsonProperty(value = "t")
    private String tag;
    @JsonProperty(value = "tx")
    private String text;
}
