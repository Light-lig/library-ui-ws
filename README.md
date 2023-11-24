# library-ui-ws
# Instructions to Run library-ui-ws with Docker

Follow these steps to run the application in a Docker environment:

1. Clone the repository: `git clone https://github.com/Light-lig/library-ui-ws.git`
2. Navigate to the application directory: `cd library-ui-ws`
3. Build the Docker image: `docker-compose build`
4. Start the containers: `docker-compose up`
5. Access the application at http://localhost:8080

That's it! The application should now be running in your Docker environment.

Certainly! Here's an explanation in English for how to use your OAuth2-based authentication system:

## How to Use the OAuth2-based Authentication System

### Overview:

The authentication system is OAuth2-based and supports two roles: `LIBRARIAN` and `STUDENT`. Users need to follow a two-step process: registration and authentication. After registration, users can utilize the OAuth endpoint to obtain an access token, which is then used to make requests to different APIs. Each role (LIBRARIAN or STUDENT) comes with specific permissions and limitations.

### Steps:

1. **Registration:**

   - **Resource Endpoint:** `/register`

   - To begin, users must register by sending a request to the resource endpoint for registration.

   Example Request:
   ```http
   POST /register
   {
     "username": "your_username",
     "password": "your_password",
     "role": "LIBRARIAN"  // or "STUDENT"
   }
   ```

   - Upon successful registration, users are now ready to authenticate.

2. **Authentication and Token Retrieval:**

   - **OAuth Endpoint:** `/oauth/token`

   - After registration, users need to authenticate by obtaining an access token from the OAuth endpoint.

   Example Request:
   ```http
   POST /oauth/token
   {
     "grant_type": "password",
     "username": "your_username",
     "password": "your_password",
     "client_id": "your_client_id",
     "client_secret": "your_client_secret"
   }
   ```

   - The response will include an access token, which should be included in the headers of subsequent API requests.

   Example Response:
   ```json
   {
     "access_token": "your_access_token",
     "token_type": "Bearer",
     "expires_in": 3600,
     "refresh_token": "your_refresh_token",
     "scope": "read write"
   }
   ```

### Making API Requests:

   - With the obtained access token, users can make requests to the various APIs based on their assigned role.

   Example API Request:
   ```http
   GET /api/some-endpoint
   Authorization: Bearer your_access_token
   ```

### Roles and Permissions:

   - **LIBRARIAN Role:**
     - Limited access to all APIs.
   
   - **STUDENT Role:**
     - Limited access to specific APIs.
  
### Additional Notes:

   - Ensure to include the access token in the `Authorization` header of each API request.

   - Access tokens have a limited validity period (e.g., 3600 seconds). Use the refresh token to obtain a new access token when needed.

By following these steps, users can register, authenticate, and use the access token to interact with the application's APIs based on their assigned role.
