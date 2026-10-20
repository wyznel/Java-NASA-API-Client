package com.application.nasa;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExplanationMarkdownTest {
    @Test
    void convertsNasaHtmlFormattingAndLinksToMarkdown() {
        String markdown = ExplanationMarkdown.fromHtml("""
                <strong>Explanation:</strong> A <em>bright</em>
                <a href="https://science.nasa.gov/sun/">Sun</a>.<br><br>
                <strong>Tomorrow's picture:</strong> open space
                """);

        assertTrue(markdown.contains("**Explanation:**"), markdown);
        assertTrue(markdown.contains("*bright*"), markdown);
        assertTrue(markdown.contains("[Sun](https://science.nasa.gov/sun/)"), markdown);
        assertTrue(markdown.contains("\n\n"), markdown);
        assertFalse(markdown.contains("<strong>"), markdown);
        assertFalse(markdown.contains("<br>"), markdown);
    }

    @Test
    void decodesEntitiesAndEscapesLiteralMarkdownCharacters() {
        String markdown = ExplanationMarkdown.fromHtml("<p>Stars &amp; dust &#8212; 2 * 3 &lt; 10.</p>");

        assertEquals("Stars \\& dust — 2 \\* 3 \\< 10.", markdown.strip());
    }

    @Test
    void handlesEmptyAndPlainTextExplanations() {
        assertEquals("", ExplanationMarkdown.fromHtml(null));
        assertEquals("", ExplanationMarkdown.fromHtml(""));
        assertEquals("", ExplanationMarkdown.fromHtml(" \n "));
        assertEquals("A distant galaxy.", ExplanationMarkdown.fromHtml("A distant galaxy.").strip());
    }
}
