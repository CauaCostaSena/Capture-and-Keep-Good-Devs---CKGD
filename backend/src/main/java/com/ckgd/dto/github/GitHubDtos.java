package com.ckgd.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GitHubDtos {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchUsersResponse {
        @JsonProperty("total_count")
        public long totalCount;
        public List<UserSummary> items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserSummary {
        public String login;
        public Long id;
        @JsonProperty("node_id")
        public String nodeId;
        @JsonProperty("avatar_url")
        public String avatarUrl;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserDetail {
        public String login;
        public Long id;
        public String name;
        public String bio;
        public String location;
        @JsonProperty("avatar_url")
        public String avatarUrl;
        @JsonProperty("public_repos")
        public Integer publicRepos;
        @JsonProperty("html_url")
        public String htmlUrl;
        public String email;
        public String blog;
        public String company;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RepoSummary {
        public String name;
        @JsonProperty("full_name")
        public String fullName;
        public String description;
        @JsonProperty("html_url")
        public String htmlUrl;
        public String language;
        @JsonProperty("stargazers_count")
        public Integer stargazersCount;
        @JsonProperty("forks_count")
        public Integer forksCount;
        @JsonProperty("open_issues_count")
        public Integer openIssuesCount;
        @JsonProperty("default_branch")
        public String defaultBranch;
        @JsonProperty("pushed_at")
        public String pushedAt;
        public Boolean fork;
    }
}
