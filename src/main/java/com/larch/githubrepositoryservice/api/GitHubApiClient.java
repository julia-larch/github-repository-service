package com.larch.githubrepositoryservice.api;

import com.larch.githubrepositoryservice.bean.Branch;
import com.larch.githubrepositoryservice.bean.Repository;
import com.larch.githubrepositoryservice.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class GitHubApiClient {
    private final RestTemplate restTemplate;

    @Value("${github.api.url}")
    private String githubApiUrl;

    public Repository[] getRepositories(String username) {
        String path = githubApiUrl + "/users/" + username + "/repos";
        try {
            return restTemplate.getForObject(path, Repository[].class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException("GitHub user not found: " + username);
        }
    }

    public Branch[] getBranches(String repositoryName, String username) {
        String path = githubApiUrl + "/repos/" + username + "/" + repositoryName + "/branches";
        return restTemplate.getForObject(path, Branch[].class);
    }
}
