# KollApp REST API Documentation

## Introduction

This document provides details on the REST endpoints available in the KollApp application, including request and response formats, HTTP status codes, and general usage.

## Running the Application

To start the Spring Boot application:

```sh
cd kollapp
mvn spring-boot:run -pl api
```

## REST Endpoints

### Expenses

- **Load Group Expenses**
  - **GET** `/api/v1/expenses/groups/{groupName}`
  - **Description:** Retrieves a list of expenses for the specified group.
  - **Response:** `200 OK` with a list of `Expense` objects, `500 INTERNAL_SERVER_ERROR` if an error occurs.

- **Update Group Expenses**
  - **PUT** `/api/v1/expenses/groups/{groupName}`
  - **Description:** Updates the list of expenses for the specified group.
  - **Request Body:** List of `Expense` objects.
  - **Response:** `200 OK` on success, `400 BAD_REQUEST` if there is a validation error.

---

### Group Chats

- **Create Group Chat**
  - **POST** `/api/v1/groupchats/{groupName}`
  - **Description:** Creates a new group chat.
  - **Response:** `200 OK` on success, `400 BAD_REQUEST` if there is an error.

- **Get Group Chat**
  - **GET** `/api/v1/groupchats/{groupName}`
  - **Description:** Retrieves the specified group chat.
  - **Response:** `200 OK` with a `GroupChat` object, `404 NOT_FOUND` if not found.

- **Send Message**
  - **POST** `/api/v1/groupchats/{groupName}/messages`
  - **Description:** Sends a new message to the specified group chat.
  - **Request Body:** `Message` object.
  - **Response:** `200 OK` on success, `400 BAD_REQUEST` if there is an error.

- **Get Messages**
  - **GET** `/api/v1/groupchats/{groupName}/messages`
  - **Description:** Retrieves messages from the specified group chat.
  - **Response:** `200 OK` with a list of `Message` objects, `404 NOT_FOUND` if not found.

---

### Groups

- **Get Group**
  - **GET** `/api/v1/groups/{groupName}`
  - **Description:** Retrieves the specified user group.
  - **Response:** `200 OK` with a `UserGroup` object, `404 NOT_FOUND` if not found.

- **Create Group**
  - **POST** `/api/v1/groups/{username}/{groupName}`
  - **Description:** Creates a new group with the specified name and assigns it to a user.
  - **Response:** `201 CREATED` on success, `400 BAD_REQUEST` if there is a validation error.

- **Assign User to Group**
  - **POST** `/api/v1/groups/{groupName}/assignUser`
  - **Description:** Assigns a user to a specified group.
  - **Request Params:** `username` (String)
  - **Response:** `200 OK` on success, `400 BAD_REQUEST` if there is an error.

- **Group Exists**
  - **GET** `/api/v1/groups/exists/{groupName}`
  - **Description:** Checks if a group exists.
  - **Response:** `200 OK` with `true` or `false`.

---

### To-Do Lists

- **Load User's To-Do List**
  - **GET** `/api/v1/todolists/{username}`
  - **Description:** Retrieves the to-do list for the specified user.
  - **Response:** `200 OK` with a `ToDoList` object, `404 NOT_FOUND` if not found.

- **Assign To-Do List to User**
  - **POST** `/api/v1/todolists/{username}`
  - **Description:** Assigns a new to-do list to the specified user.
  - **Response:** `201 CREATED` on success, `400 BAD_REQUEST` if there is an error.

- **Update User's To-Do List**
  - **PUT** `/api/v1/todolists/{username}`
  - **Description:** Updates the to-do list for the specified user.
  - **Request Body:** `ToDoList` object.
  - **Response:** `200 OK` on success, `400 BAD_REQUEST` if there is an error.

- **Load Group To-Do List**
  - **GET** `/api/v1/todolists/groups/{groupName}`
  - **Description:** Retrieves the to-do list for the specified group.
  - **Response:** `200 OK` with a `ToDoList` object, `404 NOT_FOUND` if not found.

- **Update Group To-Do List**
  - **PUT** `/api/v1/todolists/groups/{groupName}`
  - **Description:** Updates the to-do list for the specified group.
  - **Request Body:** `ToDoList` object.
  - **Response:** `200 OK` on success, `400 BAD_REQUEST` if there is an error.

---

### Users

- **Save User**
  - **POST** `/api/v1/users`
  - **Description:** Saves a new user.
  - **Request Body:** `User` object.
  - **Response:** `200 OK` on success, `400 BAD_REQUEST` or `500 INTERNAL_SERVER_ERROR` on error.

- **Remove User**
  - **DELETE** `/api/v1/users/{username}`
  - **Description:** Deletes a user by username.
  - **Response:** `200 OK` on success, `400 BAD_REQUEST` or `500 INTERNAL_SERVER_ERROR` on error.

- **User Exists**
  - **GET** `/api/v1/users/exists/{username}`
  - **Description:** Checks if a user exists.
  - **Response:** `200 OK` with `true` or `false`.

- **Login**
  - **POST** `/api/v1/users/login`
  - **Description:** Authenticates a user.
  - **Request Params:** `username` (String), `password` (String)
  - **Response:** `200 OK` with `User` object, `401 UNAUTHORIZED` if authentication fails.

- **Validate User**
  - **POST** `/api/v1/users/validate`
  - **Description:** Confirms that a new user is valid.
  - **Request Params:** `username` (String), `password` (String), `confirmPassword` (String)
  - **Response:** `true` or `false` for valid user confirmation.

- **User Validation Error Message**
  - **POST** `/api/v1/users/validate/message`
  - **Description:** Provides validation error messages for a user.
  - **Request Params:** `username` (String), `password` (String), `confirmPassword` (String)
  - **Response:** Error message as `String`.

- **Assign Group to User**
  - **POST** `/api/v1/users/{username}/assignGroup`
  - **Description:** Assigns a user to a specific group.
  - **Request Params:** `groupName` (String)
  - **Response:** `200 OK` on success, `400 BAD_REQUEST` or `500 INTERNAL_SERVER_ERROR` on error.
