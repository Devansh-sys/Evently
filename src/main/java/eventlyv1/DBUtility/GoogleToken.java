package eventlyv1.DBUtility;

public class GoogleToken {
    public String userId;
    public String accessToken;
    public String refreshToken;
    public String tokenType;
    public Long expiryTime;

    public GoogleToken(String userId, String accessToken, String refreshToken, String tokenType, Long expiryTime) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiryTime = expiryTime;
    }


}
