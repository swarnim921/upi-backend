# Validation & Exception Handling

## ✅ Validation
We use **Hibernate Validator** (JSR 380) to ensure data integrity before processing requests.

### Common Annotations
*   `@NotNull`: Field must not be null.
*   `@NotEmpty`: String must not be empty.
*   `@Email`: Must be a valid email format.
*   `@Size(min=6)`: Password/String must meet length requirements.
*   `@Min(1)`: Amount must be positive.

### Example
```java
public class RegisterRequest {
    @NotEmpty(message = "Username is required")
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 chars")
    private String password;
}
```

## ⚠️ Exception Handling
A global exception handler (`GlobalExceptionHandler`) catches errors across the application and returns consistent JSON responses.

### Error Response Format
```json
{
  "timestamp": "2023-10-27T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Specific error message here",
  "path": "/api/endpoint"
}
```

### Handled Exceptions
1.  **`MethodArgumentNotValidException`**: Triggered when DTO validation fails. Returns a map of field errors.
2.  **`UsernameNotFoundException`**: When a user is not found during auth.
3.  **`BadCredentialsException`**: Invalid login attempt.
4.  **`InsufficientBalanceException`**: Custom exception for payment failures.
5.  **`ResourceNotFoundException`**: Generic "Not Found" errors.
6.  **`Exception`**: Catch-all for unexpected server errors (HTTP 500).
