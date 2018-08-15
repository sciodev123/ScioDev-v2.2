package com.redbluekey.sciodev.helpers;

public class UriBuilder {

    private static final String BASE_URL = "http://api.redbluekey.com/api/item/GetItemPaging";

    public static String getRequestUrl(final String name, final int pageIndex, final int currentPage, final int lastPage) {
        return new StringBuilder(BASE_URL)
                .append("?name=")
                .append(name)
                .append("&page=")
                .append(pageIndex)
                .append("&pageNoFrom=")
                .append(currentPage)
                .append("&pageNoTo=")
                .append(lastPage)
                .toString();
    }

    public static String getRequestUrl(final String name, final int pageIndex, final int currentPage) {
        return new StringBuilder(BASE_URL)
                .append("?name=")
                .append(name)
                .append("&page=")
                .append(pageIndex)
                .append("&pageNo=")
                .append(currentPage)
                .toString();
    }
}
