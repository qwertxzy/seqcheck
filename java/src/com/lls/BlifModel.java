package com.lls;

public class BlifModel {
  String name;
  String model;

  BlifModel(String n, String m) {
    this.name = n;
    this.model = m;
  }

  @Override
  public String toString() {
    return "Name: " + name + "\nModel: " + model;
  }
}
