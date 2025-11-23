# Google SSO Authentication Setup Guide

This guide explains how to set up Google Single Sign-On (SSO) authentication for the UPI Dashboard backend.

## Prerequisites

1. A Google Cloud Platform (GCP) account
2. Access to Google Cloud Console

## Step 1: Create OAuth 2.0 Credentials in Google Cloud Console

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Navigate to **APIs & Services** > **Credentials**
4. Click **Create Credentials** > **OAuth client ID**
5. If prompted, configure the OAuth consent screen:
   - Choose **External** (unless you have a Google Workspace)
   - Fill in the required information (App name, User support email, Developer contact)
   - Add scopes: `openid`, `profile`, `email`
   - Add test users if your app is in testing mode
6. Create OAuth client ID:
   - Application type: **Web application**
   - Name: `UPI Dashboard Backend` (or any name you prefer)
   - Authorized redirect URIs:
     - For local development: `http://localhost:8080/login/oauth2/code/google`
     - For production: `https://yourdomain.com/login/oauth2/code/google`
   - Click **Create**
7. Copy the **Client ID** and **Client Secret**

## Step 2: Configure Environment Variables

Set the following environment variables or update `application.properties`:

```bash
# Windows PowerShell
$env:GOOGLE_CLIENT_ID="your-client-id-here"
$env:GOOGLE_CLIENT_SECRET="your-client-secret-here"

# Linux/Mac
export GOOGLE_CLIENT_ID="your-client-id-here"
export GOOGLE_CLIENT_SECRET="your-client-secret-here"
```

Or update `src/main/resources/application.properties`:

```properties
spring.security.oauth2.client.registration.google.client-id=your-client-id-here
spring.security.oauth2.client.registration.google.client-secret=your-client-secret-here
```

**Note:** For production, always use environment variables or a secure configuration management system. Never commit secrets to version control.

## Step 3: Update Redirect URI (if needed)

The default redirect URI format used by Spring Security OAuth2 is:
- `/login/oauth2/code/{registrationId}`

For Google, this becomes: `/login/oauth2/code/google`

Make sure this exact URI is added to your Google OAuth2 client's authorized redirect URIs.

## Step 4: Test the Integration

1. Start the application:
   ```bash
   mvn spring-boot:run
   ```

2. Initiate Google OAuth2 login by navigating to:
   ```
   http://localhost:8080/oauth2/authorization/google
   ```

3. Or use the helper endpoint:
   ```
   GET http://localhost:8080/api/auth/google
   ```

4. After successful authentication, you'll receive a JSON response with:
   - `token`: JWT token for API authentication
   - `expiresAt`: Token expiration timestamp
   - `user`: User profile information

## API Endpoints

### Get OAuth2 Authorization URL
```
GET /api/auth/google
```
Returns information about the Google OAuth2 authorization endpoint.

### Initiate Google OAuth2 Login
```
GET /oauth2/authorization/google
```
Redirects to Google's OAuth2 consent screen. After user approval, redirects back to:
```
GET /login/oauth2/code/google
```
This endpoint automatically processes the OAuth2 callback and returns a JWT token in JSON format.

## How It Works

1. User clicks "Login with Google" or navigates to `/oauth2/authorization/google`
2. User is redirected to Google's OAuth2 consent screen
3. After user approval, Google redirects back to `/login/oauth2/code/google` with an authorization code
4. Spring Security exchanges the code for user information
5. `GoogleOAuth2Service` processes the user:
   - If user exists (by email), updates provider information
   - If user doesn't exist, creates a new user with auto-generated UPI ID
6. A JWT token is generated and returned to the client
7. Client uses the JWT token for subsequent API requests

## User Account Creation

When a user logs in with Google for the first time:
- A new user account is automatically created
- UPI ID is auto-generated from the user's name or email (e.g., `john@upi`)
- User is assigned the `USER` role
- Initial balance is set to 0.0
- Provider is set to "google"
- Provider ID (Google's unique user ID) is stored

## Troubleshooting

### Error: "redirect_uri_mismatch"
- Ensure the redirect URI in Google Cloud Console exactly matches: `http://localhost:8080/login/oauth2/code/google`
- Check for trailing slashes or protocol mismatches (http vs https)

### Error: "invalid_client"
- Verify your Client ID and Client Secret are correct
- Ensure environment variables are set properly

### Error: "access_denied"
- Check OAuth consent screen configuration
- Ensure test users are added if app is in testing mode
- Verify required scopes are added

### CORS Issues
- If calling from a frontend, ensure CORS is properly configured in `SecurityConfig`
- Add your frontend origin to allowed origins

## Security Notes

1. **Never commit secrets**: Always use environment variables or secure vaults
2. **Use HTTPS in production**: OAuth2 requires secure connections
3. **Token expiration**: JWT tokens expire after 60 minutes (configurable)
4. **Provider validation**: The system validates that OAuth2 responses come from Google

## Integration with Frontend

For frontend integration, you can:

1. **Option 1: Direct Redirect**
   ```javascript
   window.location.href = 'http://localhost:8080/oauth2/authorization/google';
   ```

2. **Option 2: Popup Window**
   ```javascript
   const popup = window.open(
     'http://localhost:8080/oauth2/authorization/google',
     'Google Login',
     'width=500,height=600'
   );
   // Listen for message from popup with token
   ```

3. **Option 3: Backend Proxy**
   - Frontend calls your backend endpoint
   - Backend initiates OAuth2 flow
   - Backend handles callback and returns token

After receiving the token, store it and include it in subsequent API requests:
```
Authorization: Bearer <token>
```

