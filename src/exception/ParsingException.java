package exception;

/**
 * Created by ospre on 8/15/2018.
 */
public class ParsingException extends Exception {

    private String reason;
    private String detail;

    public ParsingException(String reason, String detail) {
        this.reason = reason;
        this.detail = detail;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
