package com.redbluekey.sciodev.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

@Data
public class SectionFact {
    @JsonProperty(value = "f")
    private String fact;
    @JsonProperty(value = "ts")
    private List<String> tag;
}
