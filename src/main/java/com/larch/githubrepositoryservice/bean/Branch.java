package com.larch.githubrepositoryservice.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Branch {
    private String name;
    @JsonProperty("commit")
    private LastCommit lastCommit;
}
