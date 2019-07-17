package com.example.order_with.menuItem;

public class Index {
    char word;
    int count;
    String list;

    public Index(char word, int count, String list) {
        this.word = word;
        this.count = count;
        this.list = list;
    }

    public char getWord() {
        return word;
    }

    public void setWord(char word) {
        this.word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }
}
