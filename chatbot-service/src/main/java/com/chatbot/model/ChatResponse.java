package com.chatbot.model;



public class ChatResponse {
    private String response;
    private String threadId;

    public ChatResponse() {
    }

    public ChatResponse(String response, String threadId) {
        this.response = response;
        this.threadId = threadId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "response='" + response + '\'' +
                ", threadId='" + threadId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatResponse that = (ChatResponse) o;

        if (response != null ? !response.equals(that.response) : that.response != null) return false;
        return threadId != null ? threadId.equals(that.threadId) : that.threadId == null;
    }

    @Override
    public int hashCode() {
        int result = response != null ? response.hashCode() : 0;
        result = 31 * result + (threadId != null ? threadId.hashCode() : 0);
        return result;
    }
}
