package com.larch.githubrepositoryservice.controller;

import com.larch.githubrepositoryservice.entity.RepositoryResponse;
import com.larch.githubrepositoryservice.service.GitHubRepositoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/github")
public class GitHubRepositoryController {

    private GitHubRepositoryService gitHubRepositoryService;

    @GetMapping("/users/{username}/repos")
    public ResponseEntity<List<RepositoryResponse>> getUserRepositories(@PathVariable String username) {
        List<RepositoryResponse> repositoryResponses = gitHubRepositoryService.getRepositories(username);
        return new ResponseEntity<>(repositoryResponses, HttpStatus.OK);
    }
}
