package com.larch.githubrepositoryservice.api;

import com.larch.githubrepositoryservice.bean.Branch;
import com.larch.githubrepositoryservice.bean.Repository;
import com.larch.githubrepositoryservice.exception.GitHubApiException;
import com.larch.githubrepositoryservice.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class GitHubApiClient {
    private final RestTemplate restTemplate;

    @Value("${github.api.repositories}")
    private String repositoriesApiUrl;

    @Value("${github.api.branches}")
    private String branchesApiUrl;

    public Repository[] getRepositories(String username) {
        try {
            return restTemplate.getForObject(repositoriesApiUrl, Repository[].class, username);
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException("GitHub user not found: " + username);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new GitHubApiException("Error calling GitHub API");
        }
    }

    public Branch[] getBranches(String username, String repositoryName) {
        return restTemplate.getForObject(branchesApiUrl, Branch[].class, username, repositoryName);
    }
}
