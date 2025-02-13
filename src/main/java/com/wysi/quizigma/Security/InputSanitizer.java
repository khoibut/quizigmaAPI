package com.wysi.quizigma.security;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class InputSanitizer {

    // Sanitize HTML input, only allowing basic safe tags like <b>, <i>, <a>, etc.
    public String sanitize(String html) {
        if(html == null) {
            return null;
        }
        Safelist basicSafeList = Safelist.basic();
        basicSafeList.addTags("StaticMathField");
        return Jsoup.clean(html, basicSafeList);  // You can customize the Safelist as per your need
    }

}

