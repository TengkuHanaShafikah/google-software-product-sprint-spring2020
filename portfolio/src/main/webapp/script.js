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

/**
 * Toggles between showing and hiding a surprise image.
 */
function showImage() {
  var x = document.getElementById("surprisePic");
  if (x.style.display === "block") {
    x.style.display = "none";
  } else {
    x.style.display = "block";
  }
}

/**
 * Fetches the comments and builds the UI.
 */
function getComments() {
  fetch("/data")
    .then(response => response.json())
    .then(comments => {
      const commentsListElement = document.getElementById("comments-list");
      commentsListElement.innerHTML = "";
      comments.map(createListElement).forEach(line => commentsListElement.appendChild(line));
    });
}

/** Creates an <li> element containing comment data.*/
function createListElement(comment) {
  const commentElement = document.createElement('li');
  commentElement.className = 'comment';

  const nameElement = document.createElement('b');
  nameElement.innerText = comment.name + ": ";

  const textElement = document.createElement('span');
  textElement.innerText = comment.text;

  const timeElement = document.createElement('span');
  timeElement.innerText = "   (" + comment.date + ")";
  
  commentElement.appendChild(nameElement);
  commentElement.appendChild(textElement);
  commentElement.appendChild(timeElement);

  return commentElement;
}
