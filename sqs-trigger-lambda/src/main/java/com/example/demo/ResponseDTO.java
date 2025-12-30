package com.example.demo;

public record ResponseDTO<T>(String message, T content) {
}