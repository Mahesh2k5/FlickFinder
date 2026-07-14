package com.movielist.dto;

import jakarta.validation.constraints.NotBlank;

public record SimilarByTitleRequest(@NotBlank String title) {}
