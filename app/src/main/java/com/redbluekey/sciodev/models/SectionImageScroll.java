package com.redbluekey.sciodev.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SectionImageScroll {
    @JsonProperty(value = "ti")
    private String title;
    @JsonProperty(value = "i")
    private String imageUri;
    @JsonProperty(value = "s")
    private String scroll;
    @JsonProperty(value = "t")
    private String tag;
}
