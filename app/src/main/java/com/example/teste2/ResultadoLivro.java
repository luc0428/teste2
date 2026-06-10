package com.example.teste2;

import java.util.List;

public class ResultadoLivro {
    // A Open Library retorna uma lista chamada "docs"
    private List<BookDoc> docs;

    public List<BookDoc> getDocs() { return docs; }
    public void setDocs(List<BookDoc> docs) { this.docs = docs; }

    public static class BookDoc {
        private String title;
        private List<String> author_name;
        private List<String> first_sentence;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public List<String> getAuthorName() { return author_name; }
        public void setAuthorName(List<String> author_name) { this.author_name = author_name; }

        public List<String> getFirstSentence() { return first_sentence; }
        public void setFirstSentence(List<String> first_sentence) { this.first_sentence = first_sentence; }
    }
}