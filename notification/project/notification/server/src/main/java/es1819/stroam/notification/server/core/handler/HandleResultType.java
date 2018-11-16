package es1819.stroam.notification.server.core.handler;

public enum HandleResultType {
    //email codes
    EMAIL_SENDING_SUCCESS(100),
    EMAIL_SENDING_ERROR(101),
    EMAIL_ADDRESS_NULL_OR_EMPTY(102),
    EMAIL_SUBJECT_DECODE_ERROR(103),
    EMAIL_BODY_NULL_OR_EMPTY(104),
    EMAIL_BODY_DECODE_ERROR(105),
    EMAIL_DECODED_BODY_NULL_OR_EMPTY(106),
    EMAIL_UNKNOWN_ERROR(199),

    //phone error codes
    PHONE_SENDING_SUCCESS(200),
    PHONE_SENDING_ERROR(201),
    PHONE_NUMBER_NULL_OR_EMPTY(202),
    PHONE_BODY_NULL_OR_EMPTY(203),
    PHONE_BODY_DECODE_ERROR(204),
    PHONE_DECODED_BODY_NULL_OR_EMPTY(205),
    PHONE_BODY_TOO_LONG(206),
    PHONE_UNKNOWN_ERROR(299),

    //other error codes
    UNKNOWN_ERROR(400);


    private int resultCode;

    HandleResultType(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

}
