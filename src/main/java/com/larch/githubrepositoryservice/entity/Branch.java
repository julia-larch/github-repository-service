package com.larch.githubrepositoryservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Branch {
    private String name;
    @JsonProperty("last_commit")
    private LastCommit lastCommit;
}
