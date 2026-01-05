package com.larch.githubrepositoryservice.service;

import com.larch.githubrepositoryservice.entity.Branch;
import com.larch.githubrepositoryservice.entity.LastCommit;
import com.larch.githubrepositoryservice.entity.Repository;
import com.larch.githubrepositoryservice.entity.RepositoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitHubRepositoryService {
    private final RestTemplate restTemplate;

    @Value("${github.api.url}")
    private String githubApiUrl;

    public List<RepositoryResponse> getRepositories(String username) {

        String path = githubApiUrl + "/users/" + username + "/repos";

        try {
            Repository[] repositories = restTemplate.getForObject(path, Repository[].class);
            assert repositories != null;
            return Arrays.stream(repositories)
                    .filter(repository -> !repository.isForked())
                    .map(repository -> createRepositoryResponseWithBranches(username, repository))
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException(e);
        }
    }

    private RepositoryResponse createRepositoryResponseWithBranches(String username, Repository repository){
        String repositoryName = repository.getName();
        String path = githubApiUrl + "/repos/" + username + "/" + repositoryName + "/branches";
        Branch[] branches = restTemplate.getForObject(path, Branch[].class);

        List<Branch> branchesWithLastCommit = Arrays.stream(branches)
                .map(branch -> {
                    LastCommit lastCommit = getBranchLastCommit(branch, username, repositoryName);
                     Branch b = new Branch();
                     b.setName(branch.getName());
                     b.setLastCommit(lastCommit);
                     return b;
                }).toList();

        RepositoryResponse response = new RepositoryResponse();
        response.setName(repositoryName);
        response.setOwnerLogin(repository.getOwner().getLogin());
        response.setBranches(branchesWithLastCommit);
        return response;
    }

    private LastCommit getBranchLastCommit(Branch branch, String username, String repositoryName){
        String commitPath = githubApiUrl + "/repos/" + username + "/" + repositoryName + "/commits/" + branch.getName();
        return restTemplate.getForObject(commitPath, LastCommit.class);
    }

}
