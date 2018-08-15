package com.redbluekey.sciodev.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SearchItem {
    @JsonProperty(value = "ti")
    private String tag;
    @JsonProperty(value = "t")
    private String title;
    @JsonProperty(value = "i")
    private String image;
}
