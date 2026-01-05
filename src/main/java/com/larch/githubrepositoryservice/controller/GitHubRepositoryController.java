package com.larch.githubrepositoryservice.controller;

import com.larch.githubrepositoryservice.entity.RepositoryResponse;
import com.larch.githubrepositoryservice.service.GitHubRepositoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/github")
public class GitHubRepositoryController {

    private GitHubRepositoryService gitHubRepositoryService;

    @GetMapping("/users/{username}/repos")
    public ResponseEntity<List<RepositoryResponse>> getUserRepositories(@PathVariable String username) {
        log.info("Request to get GitHub repositories for user: {}", username);
        List<RepositoryResponse> repositories = gitHubRepositoryService.getRepositories(username);

        log.info("Found {} repositories for user: {}", repositories.size(), username);
        return ResponseEntity.ok(repositories);
    }
}
