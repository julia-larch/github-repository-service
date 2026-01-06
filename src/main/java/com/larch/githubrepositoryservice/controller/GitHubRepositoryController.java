package com.larch.githubrepositoryservice.controller;

import com.larch.githubrepositoryservice.bean.RepositoryResponse;
import com.larch.githubrepositoryservice.service.GitHubRepositoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/github")
public class GitHubRepositoryController {

    private GitHubRepositoryService gitHubRepositoryService;

    @GetMapping("/users/{username}/repos")
    public List<RepositoryResponse> getUserRepositories(@PathVariable String username) {
        return gitHubRepositoryService.getRepositories(username);
    }
}
