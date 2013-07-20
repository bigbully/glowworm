package userJavabean;

/**
 * User: biandi
 * Date: 13-7-10
 * Time: 下午3:22
 */
public class CommonResponse {

    public static int SUCCESS_TYPE = 0;
    public static int EXCEPTION_TYPE = 1;

    public static CommonResponse successResponse(){
        return new CommonResponse(SUCCESS_TYPE);
    }

    public static CommonResponse successResponse(Object content){
        return new CommonResponse(SUCCESS_TYPE, content);
    }

    public static CommonResponse exceptionResponse(String exception){
        return new CommonResponse(EXCEPTION_TYPE, exception);
    }

    private int type;
    private Object content;

    public CommonResponse(){}

    private CommonResponse(int type) {
        this.type = type;
    }

    private CommonResponse(int type, String exception){
        this.type = type;
        this.content = exception;
    }

    private CommonResponse(int type, Object content){
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
