package com.application.nasa;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import one.jpro.platform.mdfx.MarkdownView;

import java.util.Objects;
import java.util.function.Consumer;

/** Renders the HTML explanation returned by NASA using MDFX's native JavaFX nodes. */
public class ExplanationView extends MarkdownView {
    private Consumer<String> linkOpener = _ -> {};

    public void setExplanation(String html) {
        setMdString(ExplanationMarkdown.fromHtml(html));
    }

    public void setLinkOpener(Consumer<String> linkOpener) {
        this.linkOpener = Objects.requireNonNull(linkOpener);
    }

    @Override
    public void setLink(Node node, String link, String description) {
        node.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                linkOpener.accept(link);
            }
        });
    }
}
