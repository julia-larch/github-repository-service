package com.larch.githubrepositoryservice.service;

import com.larch.githubrepositoryservice.api.GitHubApiClient;
import com.larch.githubrepositoryservice.bean.Branch;
import com.larch.githubrepositoryservice.bean.Repository;
import com.larch.githubrepositoryservice.bean.RepositoryResponse;
import com.larch.githubrepositoryservice.mapper.GitHubRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitHubRepositoryService {
    private final GitHubApiClient client;
    private final GitHubRepositoryMapper mapper;

    public List<RepositoryResponse> getRepositories(String username) {
        Repository[] repositories = client.getRepositories(username);
        return Arrays.stream(repositories)
                .filter(repository -> !repository.isForked())
                .map(repository -> fillRepositoryResponseWithBranches(username, repository))
                .collect(Collectors.toList());
    }

    private RepositoryResponse fillRepositoryResponseWithBranches(String username, Repository repository) {
        String repositoryName = repository.getName();
        Branch[] branches = client.getBranches(repositoryName, username);
        List<Branch> branchesWithLastCommit = Arrays.stream(branches).toList();
        return mapper.toRepositoryResponse(repository, branchesWithLastCommit);
    }
}
