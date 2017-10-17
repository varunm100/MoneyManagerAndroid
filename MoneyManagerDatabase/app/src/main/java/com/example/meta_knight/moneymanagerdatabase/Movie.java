package com.example.meta_knight.moneymanagerdatabase;

public class Movie {
    private String title, genre, year, SecretString;

    public Movie() {
    }

    public Movie(String title, String genre, String year) {
        this.title = title;
        this.genre = genre;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setSecretString(String InSec) { this.SecretString = InSec; }

    public String GetSecretString () { return this.SecretString; }
}
