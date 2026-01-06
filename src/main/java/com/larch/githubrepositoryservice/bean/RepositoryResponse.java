package com.larch.githubrepositoryservice.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RepositoryResponse {
    @JsonProperty("repo_name")
    private String name;
    @JsonProperty("owner_login")
    private String ownerLogin;
    @JsonProperty("branches")
    private List<Branch> branches;
}
