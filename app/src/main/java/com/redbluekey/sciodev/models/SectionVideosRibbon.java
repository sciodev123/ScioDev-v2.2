package com.redbluekey.sciodev.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SectionVideosRibbon {
    @JsonProperty(value = "c")
    private String channel;
    @JsonProperty(value = "d")
    private String duration;
    @JsonProperty(value = "t")
    private String thumbnail;
    @JsonProperty(value = "ti")
    private String title;
    @JsonProperty(value = "v")
    private String videoUrl;
    @JsonProperty(value = "vs")
    private String views;
}
