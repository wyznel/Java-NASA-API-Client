package com.application.nasa;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class apodModel {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Apod {
        private final String date;
        private final String title;
        private final String mediaType;
        private final String explanation;
        private final String credit;
        private final String copyright;
        private final String alt;
        private final String url;
        private String hdurl;
        private final String permalink;
        private final String basic_html;
        @JsonCreator
        public Apod(
                @JsonProperty("date") String date,
                @JsonProperty("title") String title,
                @JsonProperty("media_type") String mediaType,
                @JsonProperty("explanation") String explanation,
                @JsonProperty("credit") String credit,
                @JsonProperty("copyright") String copyright,
                @JsonProperty("alt") String alt,
                @JsonProperty("url") String url,
                @JsonProperty("hdurl") String hdurl,
                @JsonProperty("permalink") String permalink,
                @JsonProperty("basic_html") String basic_html
        ) {
            this.date = date;
            this.title = title;
            this.mediaType = mediaType;
            this.explanation = explanation;
            this.credit = credit;
            this.copyright = copyright;
            this.alt = alt;
            this.url = url;
            this.hdurl = hdurl;
            this.permalink = permalink;
            this.basic_html = basic_html;
        }

        @JsonProperty("date")
        public String date() {
            return date;
        }

        @JsonProperty("title")
        public String title() {
            return title;
        }

        @JsonProperty("media_type")
        public String mediaType() {
            return mediaType;
        }

        @JsonProperty("explanation")
        public String explanation() {
            return explanation;
        }

        @JsonProperty("credit")
        public String credit() {
            return credit;
        }

        @JsonProperty("copyright")
        public String copyright() {
            return copyright;
        }

        @JsonProperty("alt")
        public String alt() {
            return alt;
        }

        @JsonProperty("url")
        public String url() {
            return url;
        }

        @JsonProperty("hdurl")
        public String hdurl() {
            return hdurl;
        }

        public void setHdurl(String newHdurl) {
            this.hdurl = newHdurl;
        }

        @JsonProperty("permalink")
        public String permalink() {
            return permalink;
        }

        @JsonProperty("basic_html")
        public String basic_html() {
            return basic_html;
        }
    }
}
