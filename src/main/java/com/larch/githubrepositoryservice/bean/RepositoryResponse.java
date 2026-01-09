package com.larch.githubrepositoryservice.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryResponse {
    @JsonProperty("repo_name")
    private String name;
    @JsonProperty("owner_login")
    private String ownerLogin;
    @JsonProperty("branches")
    private List<Branch> branches;
}
