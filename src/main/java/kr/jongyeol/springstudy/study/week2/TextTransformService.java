package kr.jongyeol.springstudy.study.week2;

import org.springframework.stereotype.Service;

@Service
public class TextTransformService {
    public String upper(String text) {
        return text.toUpperCase();
    }

    public String lower(String text) {
        return text.toLowerCase();
    }

    public String reverse(String text) {
        return new StringBuilder(text).reverse().toString();
    }

    public int length(String text) {
        return text.length();
    }

    public String replace(ReplaceRequest request) {
        return request.text().replace(request.from(), request.to());
    }

    public int wordCount(String text) {
        int count = 0;
        String[] t = text.split(" ");
        for(String s : t){
            if(!s.isBlank()) count++;
        }
        return count;
    }

    public String trim(String text) {
        return text.trim();
    }

    public String mask(String text) {
        if(text.length() <= 4) return "*".repeat(text.length());
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 2; i++) sb.append(text.charAt(i));
        sb.repeat("*", text.length() - 4);
        for(int i = text.length() - 2; i < text.length(); i++) sb.append(text.charAt(i));
        return sb.toString();
    }

    public String repeat(RepeatRequest request) {
        return request.text().repeat(request.times());
    }

    public boolean palindrome(String text) {
        text = text.trim().toLowerCase();
        if(text.charAt(0) != text.charAt(text.length() - 1)) return false;
        String text2 = new StringBuilder(text).reverse().toString();
        return text.equals(text2);
    }
}