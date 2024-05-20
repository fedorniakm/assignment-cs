# Users REST API Documentation

Simple Spring Boot RESTful API application project with the <b>Users</b> resource.

## Overview

This project provides a REST API for managing the Users resource. It supports the following operations:
- Get all users
- Get user
- Create user
- Update user
- Partial update user
- Delete user

The API accepts <b>JSON</b> body. All the incoming data must be wrapped into the <i>data</i> field as show in below examples.

The API responses with <b>JSON</b> only. The response data are wrapped in the <i>data</i> field if successful. Otherwise, error messages are returned. The error messages are wrapped in <i>errors</i> field as shown below:


    {
        "errors": [
            {
                "status": 400,
                "message": "Field [data.email] is not valid.",
                "detail": "must not be blank"
            },
            {
                "status": 400,
                "message": "Field [data.firstName] is not valid.",
                "detail": "must not be blank"
            },
            {
                "status": 400,
                "message": "Field [data.lastName] is not valid.",
                "detail": "must not be blank"
            }
        ]
    }

The project implements simple in-memory data persistence layer.

## Endpoints

### Get All Resources

    GET /v1/users

- Description: Retrieve a list of all users.
- Response: 200 OK
- Example:

    
    {
        "data": [
            {
                "id": 1,
                "email": "1990user@123",
                "firstName": "1990user",
                "lastName": "1990user",
                "birthDate": "01-01-1990",
                "address": "1990user Address",
                "phoneNumber": "+228475628465"
            },
            {
                "id": 2,
                "email": "TheSecond@email.com",
                "firstName": "The Second",
                "lastName": "The Second",
                "birthDate": "01-01-1983",
                "address": "The Second Address",
                "phoneNumber": "+382756254757"
            }
        ]
    }

### Get a Single Resource

    GET /v1/users/:id

- Description: Retrieve a user by ID. 
- Response: 200 OK 
- Example:


    {
        "data": [
            {
                "id": 1,
                "email": "1990user@123",
                "firstName": "1990user",
                "lastName": "1990user",
                "birthDate": "01-01-1990",
                "address": "1990user Address",
                "phoneNumber": "+228475628465"
            }
        ]
    }

### Create a New Resource

    POST /v1/users

- Description: Create a new user.
- Request Body:


    {
        "data": {
            "email": "tempUser@temp.com",
            "firstName": "John",
            "lastName": "Doe",
            "birthDate": "08-05-1994",
            "address": "Temp Address, NY, Ukraine",
            "phoneNumber": "+380936482351"
        }
    }

Response: 

    201 Created
    Location: "/v1/users/1"

### Update a Resource

    PUT /v1/users/:id

- Description: Update an user by ID.
- Request Body:


    {
        "data": {
            "email": "email@email.com",
            "firstName": "John",
            "lastName": "Doe",
            "birthDate": "01-01-1991",
            "address": "Address str. Address DC, ADR",
            "phoneNumber": "+380927364527"
        }
    }

    Response: 200 OK

### Partial user update

    PATCH /v1/users/:id

- Description: Update some fields of the user by ID.
- Request Body:


    {
        "data": {
            "email": "email@email.com",
            "birthDate": "01-01-1991",
        }
    }

    Response: 200 OK

### Delete a Resource

    DELETE /v1/users/:id

- Description: Delete a specific resource by ID.
- Response: 200 OK if deleted OR 204 NO CONTENT if no user with the requested id.

