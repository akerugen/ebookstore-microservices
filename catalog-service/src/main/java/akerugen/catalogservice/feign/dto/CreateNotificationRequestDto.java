package akerugen.catalogservice.feign.dto;

public class CreateNotificationRequestDto {

    private Long userId;
    private String type; // BOOK_CREATED, BOOK_UPDATED, BOOK_DELETED, PRICE_CHANGED
    private String title;
    private String message;
    private Long bookId;

    public CreateNotificationRequestDto() {
    }

    public CreateNotificationRequestDto(Long userId, String type, String title, String message, Long bookId) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.bookId = bookId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}

