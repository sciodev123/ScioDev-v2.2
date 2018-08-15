package com.redbluekey.sciodev.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Section implements Serializable, Parcelable {
    @JsonProperty(value = "t")
    private String type;
    @JsonProperty(value = "n")
    private String name;
    @JsonProperty(value = "ns")
    private String nameSub;
    @JsonProperty(value = "tx")
    private String textBlock;
    @JsonProperty(value = "sDPA")
    private List<SectionDataPropertyAvailable> propertyAvailable;
    @JsonProperty(value = "sIR")
    private List<SectionImageRibbon> imagesRibbon;
    @JsonProperty(value = "nl")
    private String nameLink;
    @JsonProperty(value = "nsl")
    private String nameSubLink;
    @JsonProperty(value = "sIS")
    private List<SectionImageScroll> imagesScroll;
    @JsonProperty(value = "sTL")
    private List<SectionTextList> textList;
    @JsonProperty(value = "sF")
    private List<SectionFact> facts;
    @JsonProperty(value = "sVR")
    private List<SectionVideosRibbon> videosRibbon;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}
}
