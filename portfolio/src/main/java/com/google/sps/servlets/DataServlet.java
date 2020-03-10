// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  // Pass user's comment text to LanguageServiceClient and receive sentiment score.
  public float getSentimentScore(String text) throws IOException {
    Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    float score = sentiment.getScore();
    languageService.close();
    return score;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("time", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      // Get comment's properties.
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty("name"); 
      String text = (String) entity.getProperty("text");
      double score = (double) entity.getProperty("score");
      long time = (long) entity.getProperty("time");

      // Create Comment object.
      Comment comment = new Comment(id, text, name, score, time);
      comments.add(comment);
    }

    // Return JSON
    Gson gson = new Gson();
    String json = gson.toJson(comments);

    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String userName = request.getParameter("name");
    String userInput = request.getParameter("text");
    long time = System.currentTimeMillis();

    // Pass user's comment text to LanguageServiceClient and receive sentiment score.
    float score = getSentimentScore(userInput);

    // Store comment in the comment entity.
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", userName);
    commentEntity.setProperty("text", userInput);
    commentEntity.setProperty("score", score); 
    commentEntity.setProperty("time", time); 

    // Store comment into Datastore.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }
}
