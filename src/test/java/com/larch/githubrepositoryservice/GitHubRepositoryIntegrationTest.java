package com.larch.githubrepositoryservice;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@SpringBootTest
@ActiveProfiles("test")
@WireMockTest(httpPort = 8082)
@AutoConfigureRestTestClient
public class GitHubRepositoryIntegrationTest {

    @Autowired
    private RestTestClient restTestClient;

    @Test
    void givenWireMockStub_whenGetRepositories_thenReturnsGitHubApiException() {
        stubFor(get("/users/testUser/repos").willReturn(serverError()));

        restTestClient.get()
                .uri("http://localhost:8080/api/github/users/testUser/repos")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("{\"status\":502,\"message\":\"Error calling GitHub API\"}");
    }

    @Test
    void givenWireMockStub_whenGetRepositories_thenReturnsUserNotFoundException() {
        stubFor(get("/users/testUser/repos").willReturn(notFound()));

        restTestClient.get()
                .uri("http://localhost:8080/api/github/users/testUser/repos")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("{\"status\":404,\"message\":\"GitHub user not found: testUser\"}");
    }

    @Test
    void givenWireMockStub_whenGetRepositories_thenReturnsRepositories() {
        stubFor(get("/users/testUser/repos").willReturn(okJson("""
                    [
                      {
                        "name": "repo1",
                        "fork": false,
                        "owner": {
                         "login": "testUser"
                          }
                      },
                      {
                        "name": "repo2",
                        "fork": true,
                        "owner": {
                         "login": "testUser"
                          }
                      }
                    ]
                """)));

        stubFor(get("/repos/testUser/repo1/branches")
                .willReturn(okJson("""
                            [
                              {
                                "name": "main",
                                "commit": {
                                 "sha": "abc123"
                                  }
                              }
                            ]
                        """)));

        restTestClient.get()
                .uri("http://localhost:8080/api/github/users/testUser/repos")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody()
                .json("""
                            [
                              {
                                "repo_name": "repo1",
                                "owner_login": "testUser",
                                "branches": [
                                  {
                                    "name": "main",
                                    "commit": {
                                      "sha": "abc123"
                                    }
                                  }
                                ]
                              }
                            ]
                        """);
    }
}
