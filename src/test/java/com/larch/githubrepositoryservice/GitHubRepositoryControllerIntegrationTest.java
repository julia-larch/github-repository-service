package com.larch.githubrepositoryservice;

import com.larch.githubrepositoryservice.bean.Branch;
import com.larch.githubrepositoryservice.bean.LastCommit;
import com.larch.githubrepositoryservice.bean.RepositoryResponse;
import com.larch.githubrepositoryservice.controller.GitHubRepositoryController;
import com.larch.githubrepositoryservice.exception.GlobalExceptionHandler;
import com.larch.githubrepositoryservice.exception.UserNotFoundException;
import com.larch.githubrepositoryservice.service.GitHubRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GitHubRepositoryControllerIntegrationTest {
    private MockMvc mockMvc;

    @Mock
    private GitHubRepositoryService service;

    @InjectMocks
    private GitHubRepositoryController controller;

    @BeforeEach
    public void setup() {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    void getUserRepositories_withBranches_returnsCompleteData() throws Exception {
        Branch branch1 = new Branch();
        branch1.setName("main");
        branch1.setLastCommit(new LastCommit("sha1"));

        Branch branch2 = new Branch();
        branch2.setName("develop");
        branch2.setLastCommit(new LastCommit("sha2"));

        RepositoryResponse repo = new RepositoryResponse("repo1", "testUser", List.of(branch1, branch2));
        when(service.getRepositories(anyString())).thenReturn(List.of(repo));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/github/users/{username}/repos", "testUser")
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].repo_name").value("repo1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].owner_login").value("testUser"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].branches.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].branches[0].name").value("main"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].branches[0].commit.sha").value("sha1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].branches[1].name").value("develop"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].branches[1].commit.sha").value("sha2"));

        verify(service, times(1)).getRepositories("testUser");
    }

    @Test
    void getUserRepositories_userNotFound_returns404() throws Exception {
        String username = "nonExistentUser";
        String errorMessage = "GitHub user not found: " + username;

        when(service.getRepositories(anyString()))
                .thenThrow(new UserNotFoundException(errorMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/github/users/{username}/repos", username)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));

        verify(service, times(1)).getRepositories(username);
    }
}
