package com.application.nasa;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;

/** Converts NASA's HTML fragments into Markdown before they reach MDFX. */
final class ExplanationMarkdown {
    private static final FlexmarkHtmlConverter CONVERTER;

    static {
        var builder = FlexmarkHtmlConverter.builder();
        builder.set(FlexmarkHtmlConverter.TYPOGRAPHIC_SMARTS, false);
        builder.set(FlexmarkHtmlConverter.TYPOGRAPHIC_QUOTES, false);
        CONVERTER = builder.build();
    }

    private ExplanationMarkdown() {}

    static String fromHtml(String html) {
        return html == null || html.isBlank() ? "" : CONVERTER.convert(html);
    }
}
