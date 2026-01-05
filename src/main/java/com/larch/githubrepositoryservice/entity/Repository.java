package com.larch.githubrepositoryservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Repository {
    private String name;
    private Owner owner;
    private List<Branch> branches;
    @JsonProperty("fork")
    private boolean isForked;
}
