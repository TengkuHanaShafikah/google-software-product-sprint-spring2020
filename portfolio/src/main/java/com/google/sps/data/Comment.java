package com.google.sps.data;
import java.util.Date;

/** Comment element within the comments section. */
public final class Comment {

  private final long id;
  private final String text;
  private final Date date;

  public Comment(long id, String text, long time) {
    this.id = id;
    this.text = text;
    this.date = new Date(time);
  }
}