package hacker;

enum ResponseType {
    BAD_LOGIN("Wrong login!"),
    BAD_PASSWORD("Wrong password!"),
    BAD_REQUEST("Bad request!"),
    PREFIX_MATCH("Exception happened during login"),
    SUCCESS("Connection success!"),
    EXCEPTION(null);

    final String MESSAGE;

    ResponseType(String s) {
        this.MESSAGE = s;
    }

    public static ResponseType getByMsg(String msg) {
        for (ResponseType type : values()) {
            if ((type.MESSAGE != null && type.MESSAGE.equals(msg)) || (type.MESSAGE == null && msg == null)) {
                return type;
            }
        }
        return EXCEPTION;
    }
}
