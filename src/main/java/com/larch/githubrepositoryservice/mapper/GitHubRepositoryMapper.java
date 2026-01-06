package com.larch.githubrepositoryservice.mapper;

import com.larch.githubrepositoryservice.bean.Branch;
import com.larch.githubrepositoryservice.bean.Repository;
import com.larch.githubrepositoryservice.bean.RepositoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GitHubRepositoryMapper {
    public RepositoryResponse toRepositoryResponse(Repository repository, List<Branch> branchesWithLastCommit){
        RepositoryResponse response = new RepositoryResponse();
        response.setName(repository.getName());
        response.setOwnerLogin(repository.getOwner().getLogin());
        response.setBranches(branchesWithLastCommit);
        return response;
    }
}
