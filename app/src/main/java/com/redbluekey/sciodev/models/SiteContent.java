package com.redbluekey.sciodev.models;

import java.util.List;

import lombok.Data;

@Data
public class SiteContent {
    private int id;
    private String name;
    private String header;
    private String headerSub;
    private List<Page> pages;
}
