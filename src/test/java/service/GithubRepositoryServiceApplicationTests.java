package service;

import com.larch.githubrepositoryservice.api.GitHubApiClient;
import com.larch.githubrepositoryservice.bean.*;
import com.larch.githubrepositoryservice.exception.GitHubApiException;
import com.larch.githubrepositoryservice.exception.UserNotFoundException;
import com.larch.githubrepositoryservice.mapper.GitHubRepositoryMapper;
import com.larch.githubrepositoryservice.service.GitHubRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubRepositoryServiceApplicationTests {
    @Mock
    private GitHubApiClient client;

    @Mock
    private GitHubRepositoryMapper mapper;

    @InjectMocks
    private GitHubRepositoryService service;

    private Repository repository1;
    private Repository repository2;
    private Repository forkedRepository;
    private Branch branch1;
    private Branch branch2;
    private RepositoryResponse response1;
    private RepositoryResponse response2;

    @BeforeEach
    void setUp() {
        String username = "testUser";
        branch1 = new Branch("main", new LastCommit("sha1"));
        branch2 = new Branch("develop", new LastCommit("sha2"));
        List<Branch> branches = new ArrayList<>();
        branches.add(branch1);
        branches.add(branch2);

        Owner owner = new Owner(username);

        repository1 = new Repository("repo1", owner, branches, false);
        repository2 = new Repository("repo2", owner, branches, false);
        forkedRepository = new Repository("forked-repo", owner, branches, true);

        response1 = new RepositoryResponse("repo1", username, List.of(branch1));
        response2 = new RepositoryResponse("repo2", username, branches);
    }

    @Test
    void getRepositories_userExists_returnsRepositories() {
        // Given
        String username = "testUser";
        Repository[] repositories = {repository1, repository2, forkedRepository};

        when(client.getRepositories(username))
                .thenReturn(repositories);
        when(client.getBranches(username, "repo1"))
                .thenReturn(new Branch[]{branch1});
        when(client.getBranches(username, "repo2"))
                .thenReturn(new Branch[]{branch1, branch2});
        when(mapper.toRepositoryResponse(repository1, List.of(branch1)))
                .thenReturn(response1);
        when(mapper.toRepositoryResponse(repository2, List.of(branch1, branch2)))
                .thenReturn(response2);

        // When
        List<RepositoryResponse> result = service.getRepositories(username);

        // Then
        assertThat(result)
                .hasSize(2)
                .containsExactly(response1, response2);

        verify(client).getRepositories(username);
        verify(client).getBranches(username, "repo1");
        verify(client).getBranches(username, "repo2");
        verify(mapper).toRepositoryResponse(repository1, List.of(branch1));
        verify(mapper).toRepositoryResponse(repository2, List.of(branch1, branch2));
        verifyNoMoreInteractions(client, mapper);
    }

    @Test
    void getRepositories_userExists_shouldFilterOutForkedRepositories() {
        // Given
        String username = "testUser";
        Repository[] repositories = {forkedRepository};

        when(client.getRepositories(username))
                .thenReturn(repositories);

        // When
        List<RepositoryResponse> result = service.getRepositories(username);

        // Then
        assertThat(result).isEmpty();
        verify(client).getRepositories(username);
        verifyNoInteractions(mapper);
    }

    @Test
    void getRepositories_userExists_shouldReturnEmptyListWhenNoRepositories() {
        // Given
        String username = "testUser";
        when(client.getRepositories(username))
                .thenReturn(new Repository[0]);

        // When
        List<RepositoryResponse> result = service.getRepositories(username);

        // Then
        assertThat(result).isEmpty();
        verify(client).getRepositories(username);
        verifyNoMoreInteractions(client);
        verifyNoInteractions(mapper);
    }

    @Test
    void getRepositories_userNotExist_shouldPropagateUserNotFoundException() {
        // Given
        String username = "nonExistentUser";
        when(client.getRepositories(username))
                .thenThrow(new UserNotFoundException("GitHub user not found: " + username));

        // When & Then
        assertThatThrownBy(() -> service.getRepositories(username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("GitHub user not found: " + username);

        verify(client).getRepositories(username);
        verifyNoInteractions(mapper);
    }

    @Test
    void getRepositories_shouldPropagateGitHubApiException() {
        // Given
        String username = "testUser";
        when(client.getRepositories(username))
                .thenThrow(new GitHubApiException("Error calling GitHub API"));

        // When & Then
        assertThatThrownBy(() -> service.getRepositories(username))
                .isInstanceOf(GitHubApiException.class)
                .hasMessageContaining("Error calling GitHub API");

        verify(client).getRepositories(username);
        verifyNoInteractions(mapper);
    }
}
