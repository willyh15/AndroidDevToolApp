package com.example.androiddevtoolapp;

import com.google.gson.annotations.SerializedName;

public class Workflow {

  @SerializedName("id")
  private long id;

  @SerializedName("name")
  private String name;

  // Getters and setters
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
