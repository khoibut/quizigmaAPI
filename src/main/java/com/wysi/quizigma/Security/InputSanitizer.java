package com.wysi.quizigma.Security;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class InputSanitizer {

    // Sanitize HTML input, only allowing basic safe tags like <b>, <i>, <a>, etc.
    public String sanitize(String html) {
        if(html == null) {
            return null;
        }
        return Jsoup.clean(html, Safelist.basic());  // You can customize the Safelist as per your need
    }

}

