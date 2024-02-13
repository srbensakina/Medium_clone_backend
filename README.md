# Spring Boot Blog API

This Spring Boot application serves as a backend for a simple blogging platform. It provides a set of RESTful API endpoints for user authentication, article management, and social interactions. Below is a brief overview of the key features:

## User Authentication
- **Login**: POST request to `/api/users/login` with email and password.
- **Registration**: POST request to `/api/users` with username, email, and password.
- **Get Current User**: GET request to `/api/user` (Authentication required).
- **Update User**: PUT request to `/api/user` (Authentication required).

## User Profiles
- **Get Profile**: GET request to `/api/profiles/:username` (Authentication optional).
- **Follow User**: POST request to `/api/profiles/:username/follow` (Authentication required).
- **Unfollow User**: DELETE request to `/api/profiles/:username/follow` (Authentication required).

## Articles
- **List Articles**: GET request to `/api/articles` (Authentication optional).
- **Feed Articles**: GET request to `/api/articles/feed` (Authentication required).
- **Get Article**: GET request to `/api/articles/:slug` (No authentication required).
- **Create Article**: POST request to `/api/articles` with title, description, body, and optional tagList (Authentication required).
- **Update Article**: PUT request to `/api/articles/:slug` with optional title, description, and body (Authentication required).
- **Delete Article**: DELETE request to `/api/articles/:slug` (Authentication required).

Got it. Here's the updated README with instructions for running the application using Docker Compose:

---
# How to use

## Cloning the Repository

To clone the repository, run the following command:

```bash
git clone <repository_url>
cd <repository_directory>
```

## Running the Application with Docker Compose

### Prerequisites

Make sure you have Docker Compose installed on your system.

### Running Docker Compose

To run the application using Docker Compose, execute the following command:

```bash
docker-compose up -d
```

This command will start the application and its dependencies defined in the `docker-compose.yml` file.

### Stopping Docker Compose

To stop the running containers managed by Docker Compose, run:

```bash
docker-compose down
```

## Test the Endpoints

### User Authentication

#### Login

```bash
curl -X POST -H "Content-Type: application/json" -d '{"user":{"email":"jake@jake.jake","password":"jakejake"}}' http://localhost:8080/api/users/login
```

This should return a token that you can use for authentication in subsequent requests.

#### Registration

```bash
curl -X POST -H "Content-Type: application/json" -d '{"user":{"username":"Jacob","email":"jake@jake.jake","password":"jakejake"}}' http://localhost:8080/api/users
```

### Get Current User

```bash
curl -H "Authorization: Token <token>" http://localhost:8080/api/user
```

Replace `<token>` with the token obtained during login.

### Update User

```bash
curl -X PUT -H "Content-Type: application/json" -H "Authorization: Token <token>" -d '{"user":{"email":"jake@jake.jake","bio":"I like to skateboard","image":"https://i.stack.imgur.com/xHWG8.jpg"}}' http://localhost:8080/api/user
```

### Get Profile

```bash
curl http://localhost:8080/api/profiles/<username>
```

Replace `<username>` with the desired username.

### Follow User

```bash
curl -X POST -H "Authorization: Token <token>" http://localhost:8080/api/profiles/<username>/follow
```

### Unfollow User

```bash
curl -X DELETE -H "Authorization: Token <token>" http://localhost:8080/api/profiles/<username>/follow
```

### List Articles

```bash
curl http://localhost:8080/api/articles
```

This will return a list of most recent articles.

### Feed Articles

```bash
curl -H "Authorization: Token <token>" http://localhost:8080/api/articles/feed
```

### Get Article

```bash
curl http://localhost:8080/api/articles/<slug>
```

Replace `<slug>` with the desired article's slug.

### Create Article

```bash
curl -X POST -H "Content-Type: application/json" -H "Authorization: Token <token>" -d '{"article":{"title":"How to train your dragon","description":"Ever wonder how?","body":"You have to believe","tagList":["reactjs","angularjs","dragons"]}}' http://localhost:8080/api/articles
```

### Update Article

```bash
curl -X PUT -H "Content-Type: application/json" -H "Authorization: Token <token>" -d '{"article":{"title":"Did you train your dragon?"}}' http://localhost:8080/api/articles/<slug>
```

Replace `<slug>` with the desired article's slug.

### Delete Article

```bash
curl -X DELETE -H "Authorization: Token <token>" http://localhost:8080/api/articles/<slug>
```

Replace `<slug>` with the desired article's slug.

# API Endpoints 


## User Authentication

### POST /api/users/login

Example request body:

```json
{
  "user": {
    "email": "jake@jake.jake",
    "password": "jakejake"
  }
}
```

No authentication required, returns a User.

Required fields: email, password.

## User Registration

### POST /api/users

Example request body:

```json
{
  "user": {
    "username": "Jacob",
    "email": "jake@jake.jake",
    "password": "jakejake"
  }
}
```

No authentication required, returns a User.

Required fields: email, username, password.

## Get Current User

### GET /api/user

Authentication required, returns the User that's the current user.

## Update User

### PUT /api/user

Example request body:

```json
{
  "user": {
    "email": "jake@jake.jake",
    "bio": "I like to skateboard",
    "image": "https://i.stack.imgur.com/xHWG8.jpg"
  }
}
```

Authentication required, returns the User.

Accepted fields: email, username, password, image, bio.

## Get Profile

### GET /api/profiles/:username

Authentication optional, returns a Profile.

## Follow User

### POST /api/profiles/:username/follow

Authentication required, returns a Profile.

No additional parameters required.

## Unfollow User

### DELETE /api/profiles/:username/follow

Authentication required, returns a Profile.

No additional parameters required.

## List Articles

### GET /api/articles

Returns most recent articles globally by default. Provide tag, author, or favorited query parameter to filter results.

#### Query Parameters:

- Filter by tag: `?tag=AngularJS`
- Filter by author: `?author=jake`
- Favorited by user: `?favorited=jake`
- Limit number of articles (default is 20): `?limit=20`
- Offset/skip number of articles (default is 0): `?offset=0`

Authentication optional, will return multiple articles, ordered by most recent first.

## Feed Articles

### GET /api/articles/feed

Can also take limit and offset query parameters like List Articles.

Authentication required, will return multiple articles created by followed users, ordered by most recent first.

## Get Article

### GET /api/articles/:slug

No authentication required, will return a single article.

## Create Article

### POST /api/articles

Example request body:

```json
{
  "article": {
    "title": "How to train your dragon",
    "description": "Ever wonder how?",
    "body": "You have to believe",
    "tagList": ["reactjs", "angularjs", "dragons"]
  }
}
```

Authentication required, will return an Article.

Required fields: title, description, body.

Optional fields: tagList as an array of Strings.

## Update Article

### PUT /api/articles/:slug

Example request body:

```json
{
  "article": {
    "title": "Did you train your dragon?"
  }
}
```

Authentication required, returns the updated Article.

Optional fields: title, description, body. The slug also gets updated when the title is changed.

## Delete Article

### DELETE /api/articles/:slug

Authentication required.
```
