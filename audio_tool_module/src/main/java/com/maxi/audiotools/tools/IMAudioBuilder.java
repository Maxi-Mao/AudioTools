package com.maxi.audiotools.tools;

import com.maxi.audiotools.IMAudioManager;

/**
 * Created by maxi on 2016/12/24.
 */

public class IMAudioBuilder {
    private String name;
    private int age;
    private double height;
    private double weight;

    public IMAudioBuilder name(String name) {
        this.name = name;
        return this;
    }

    public IMAudioBuilder age(int age) {
        this.age = age;
        return this;
    }

    public IMAudioBuilder height(double height) {
        this.height = height;
        return this;
    }

    public IMAudioBuilder weight(double weight) {
        this.weight = weight;
        return this;
    }

    public IMAudioManager build() {
        return IMAudioManager.instance();
    }


}
