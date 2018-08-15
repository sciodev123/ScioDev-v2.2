package com.redbluekey.sciodev.models;

import java.util.List;

import lombok.Data;

@Data
public class Page {
    private String name;
    private List<Section> sections;
    private List<Object> tags;
}
