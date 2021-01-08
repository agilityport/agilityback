package org.smorgrav.agilityback.sources.sagik;

import org.jsoup.select.Elements;

/**
 * WIP for making extracting data from JSoup elements even easier and more robust.
 */
public class ElementsWrap {

    private final Elements elements;

    ElementsWrap(Elements elements) {
        this.elements = elements;
    }

    long getNumber(int index, int defaultValue) {
        Long number = getNumber(index);
        return number == null ? defaultValue : number;
    }

    Long getNumber(int index) {
        String content = elements.get(index).text().trim();
        try {
            return Long.parseLong(content);
        } catch (NumberFormatException e) {
            // TODO log
            return null;
        }
    }
}
