package com.github.megrad79.AccountSecurity.domain;

import java.util.Objects;

public class Item {
    private int account;
    private String password;
    private double time;

    public Item() {
    }

    public Item(int account, String password, double time) {
        this.account = account;
        this.password = password;
        this.time = time;
    }

    public int getAccount() {
        return account;
    }
    public void setAccount(int account){
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Item{" +
                "account=" + account +
                ", password=" + password + '\'' +
                ", time=" + time +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return account == item.account && Double.compare(item.time, time) == 0 && Objects.equals(password, item.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, password, time);
    }
}
