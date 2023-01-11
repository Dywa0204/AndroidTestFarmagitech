package com.dywa.e_doc.data.response;

public class LoginResponse {
    private Response response;

    public Response getResponse () {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public class Response {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "token='" + token + '\'' +
                    '}';
        }
    }
}
