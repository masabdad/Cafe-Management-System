package com.cafe.management.service;

import io.jsonwebtoken.impl.crypto.MacProvider;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface DashboardService {

    ResponseEntity <Map<String, Object>> getCount();
}
