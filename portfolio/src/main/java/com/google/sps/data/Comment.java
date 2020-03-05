package com.google.sps.data;
import java.util.Date;

/** Comment element within the comments section. */
public final class Comment {

  private final long id;
  private final String text;
  private final String name;
  private final float score;
  private final Date date;

  public Comment(long id, String text, String name, float score, long time) {
    this.id = id;
    this.name = name;
    this.text = text;
    this.score = score;
    this.date = new Date(time);
  }
}