package com.larch.githubrepositoryservice.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Repository {
    private String name;
    private Owner owner;
    private List<Branch> branches;
    @JsonProperty("fork")
    private boolean isForked;
}
