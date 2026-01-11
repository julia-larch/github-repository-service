# GitHub Repository API

 Simple API that returns non-forked GitHub repositories for a given user with repository names, owner information, branches, and their latest commit SHAs.

## Technologies Used
- Java 25
- Spring Boot 4.0.1
- Spring Web
- Lombok
- WireMock
- JUnit 5
- Gradle


## Installation

1. Clone repository to your pc:
```
git clone https://github.com/julia-larch/github-repository-service.git
cd github-repository-service
```
2. Open in IntelliJ IDEA
3. Set SDK and language level to Java 25
4. Build the project

## Usage
### Get User Repositories

 **Request:**
```
GET http://localhost:8080/api/github/users/{username}/repos 
Accept: application/json
```
Where {*username*} is the GitHub username whose repositories you want to retrieve.

 **Response:**
```
[
  {
    "repo_name": "github-repository-service",
    "owner_login": "julia-larch",
    "branches": [
      {
        "name": "main",
        "commit": {
          "sha": "c446fa2c2a32b27198552a6533e0caaa919f8469"
        }
      }
    ]
  }
]
```