package com.larch.githubrepositoryservice.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Branch {
    private String name;
    @JsonProperty("commit")
    private LastCommit lastCommit;
}
