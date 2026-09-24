package com.taskbridge.projects;

public record LoginResponse(String accessToken, String tokenType, long expiresInSeconds) {
}