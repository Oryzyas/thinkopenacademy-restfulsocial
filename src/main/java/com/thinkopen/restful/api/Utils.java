package com.thinkopen.restful.api;

import com.thinkopen.restful.api.dtos.*;
import com.thinkopen.restful.api.factories.ResponseFactory;
import com.thinkopen.restful.api.factories.responses.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;

public final class Utils {

    public static final Predicate<String> NAME_VALIDATOR = n -> n.length() > 0 && n.length() <= 50;
    public static final Predicate<String> EMAIL_VALIDATOR = e -> e.length() > 0 && e.length() <= 30;
    public static final Predicate<String> PASSWORD_VALIDATOR = p -> p.length() > 0 && p.length() <= 20;
    public static final Predicate<Integer> AGE_VALIDATOR = e -> e > 0 && e <= 100;

    public static final Predicate<String> MSG_VALIDATOR = n -> n.length() > 0 && n.length() <= 250;

    public static <T> boolean validate(T obj, Predicate<T> p) {
        return p.test(obj);
    }

    public static Response writeResponse(User user) {
        Response response = ResponseFactory.createResponse(UserServices.getResponseType());
        response.setRootTag("User");
        response.addAttribute("id", String.valueOf(user.getId()));
        response.addAttribute("name" , user.getName());
        response.addAttribute("age", String.valueOf(user.getAge()));
        response.addAttribute("email", user.getEmail());
        return response;
    }

    public static Response writeResponse(Post post) {
        Response response = ResponseFactory.createResponse(UserServices.getResponseType());
        response.setRootTag("Post");
        response.addAttribute("id", String.valueOf(post.getId()));
        response.addAttribute("userId", String.valueOf(post.getUserId()));
        response.addAttribute("title", post.getTitle());
        response.addAttribute("content", post.getContent());
        response.addAttribute("date", new SimpleDateFormat().format(new Date(post.getDate())));
        return response;
    }

    public static Response writeResponse(Comment comment) {
        Response response = ResponseFactory.createResponse(UserServices.getResponseType());
        response.setRootTag("Comment");
        response.addAttribute("id", String.valueOf(comment.getId()));
        response.addAttribute("postId", String.valueOf(comment.getPostId()));
        response.addAttribute("userId", String.valueOf(comment.getUserId()));
        response.addAttribute("content", comment.getContent());
        response.addAttribute("date", new SimpleDateFormat().format(new Date(comment.getDate())));
        return response;
    }

    public static Response writeResponse(Message message) {
        Response response = ResponseFactory.createResponse(UserServices.getResponseType());
        response.setRootTag("Message");
        response.addAttribute("id", String.valueOf(message.getId()));
        response.addAttribute("senderId", String.valueOf(message.getSenderId()));
        response.addAttribute("content", message.getContent());
        response.addAttribute("date", new SimpleDateFormat().format(new Date(message.getDate())));
        return response;
    }

    public static Response writeResponse(MessageIO msgio) {
        Response response = ResponseFactory.createResponse(UserServices.getResponseType());
        response.setRootTag("MessageIO");
        response.addAttribute("msgId", String.valueOf(msgio.getMsgId()));
        response.addAttribute("userId", String.valueOf(msgio.getUserId()));
        response.addAttribute("msgread", String.valueOf(msgio.isRead()));
        response.addAttribute("msgdel", String.valueOf(msgio.isDeleted()));
        return response;
    }

    public static Response successResponse(String msg) {
        Response response = ResponseFactory.createResponse(UserServices.getResponseType());
        response.setRootTag("success");
        response.addAttribute("message", msg);
        return response;
    }

    public static Response errorResponse(String msg) {
        Response response = ResponseFactory.createResponse(UserServices.getResponseType());
        response.setRootTag("error");
        response.addAttribute("message", msg);
        return response;
    }
}
