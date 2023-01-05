package org.fileserver.resthelper;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.fileserver.resthelper.annotations.Path;
import org.fileserver.resthelper.annotations.RequestType;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ApiHandler {
    public static final String API_PATH = "/api";
    private static RestEndpoint endpoint = new RestEndpoint();

    public static FullHttpResponse invokeMethod(ParsedRequest request) {
        if (!request.getUri().startsWith(API_PATH)) {
            FullHttpResponse response = createNotFoundHttpResponse();
            return response;
        }

        Method[] methods = endpoint.getClass().getDeclaredMethods();

        String subPath = request.getUri().replaceFirst(API_PATH, "");
        for (Method method: methods) {
            Annotation annotation = method.getDeclaredAnnotation(Path.class);
            if (annotation == null) {
                continue;
            } else {
                Path pathAnnotation = (Path) annotation;
                if (pathAnnotation.value().equals(subPath)) {
                    Annotation requestTypeAnnotation =
                            method.getDeclaredAnnotation(RequestType.class);
                    if (requestTypeAnnotation != null
                            && ((RequestType)requestTypeAnnotation).type().equals(
                                    request.getHttpMethod())) {
                        try {
                            Object result = method.invoke(endpoint);
                            Class<?> returnType = method.getReturnType();
                            if (returnType.equals(Void.TYPE)) {
                                return new DefaultFullHttpResponse(
                                        HttpVersion.HTTP_1_1,
                                        HttpResponseStatus.OK);
                            }
                            if (returnType.isPrimitive() || returnType.equals(String.class)) {
                                if (result == null) {
                                    String message = "Error. Primitive type expected, but got null";
                                    System.out.println(message);
                                    return new DefaultFullHttpResponse(
                                            HttpVersion.HTTP_1_1,
                                            HttpResponseStatus.INTERNAL_SERVER_ERROR,
                                            Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
                                } else {
                                    return createPrimitiveResponse(String.valueOf(result));
                                }
                            } else {
                                //non-primitive type
                                if (result == null) {
                                    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                            HttpResponseStatus.OK);
                                } else {
                                    //non-primitive non-null result
                                    //jsonify and send
                                    Gson gson = new Gson();
                                    String jsonArray = gson.toJson(result);
                                    return sendJsonResponse(jsonArray);
                                }
                            }
                        } catch (IllegalAccessException e) {
                            return createNotFoundHttpResponse();
                        } catch (InvocationTargetException e) {
                            return createNotFoundHttpResponse();
                        }
                    }
                }
            }
        }

        FullHttpResponse response = createNotFoundHttpResponse();
        return response;
    }

    private static FullHttpResponse createPrimitiveResponse(String content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        return response;
    }

    private static FullHttpResponse sendJsonResponse(String content) {
        // Build the HTTP response.
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");

        return response;
    }

    private static FullHttpResponse createNotFoundHttpResponse() {
        HttpResponseStatus status = HttpResponseStatus.NOT_FOUND;
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status +
                        "\r\n",
                CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        return response;
    }

    private static FullHttpResponse createSuccessResponse() {
        HttpResponseStatus status = HttpResponseStatus.OK;
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer("Success: " + status +
                        "\r\n",
                CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        return response;
    }
}
