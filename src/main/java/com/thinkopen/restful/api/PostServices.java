package com.thinkopen.restful.api;

import com.thinkopen.restful.api.dtos.Post;
import com.thinkopen.restful.api.dtos.User;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.sql.SQLException;
import java.util.List;

@Path("posts")
public class PostServices {
    private static MySQLAccess dao = MySQLAccess.getInstance();

    public PostServices() {

    }

    @GET
    public String getPosts() {
        final User loggedUser = UserServices.getLoggedUser();

        if (loggedUser == null)
            return "Attualmente non sei connesso.";

        try {
            final List<Post> posts = dao.selectAllPosts(null, null, null, -1);

            if(posts.isEmpty())
                return "Non ci sono post da visualizzare.";

            final StringBuilder sb = new StringBuilder();
            posts.forEach(post -> sb.append(post.toString() + "\n"));
            return sb.toString();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return "Si è verificato un errore grave durante la procedura di recupero dei post.";
        }
    }

    @GET
    @Path("me")
    public String getMyPosts() {
        final User loggedUser = UserServices.getLoggedUser();

        if (loggedUser == null)
            return "Attualmente non sei connesso.";

        try {
            final List<Post> posts = dao.selectAllPosts(loggedUser.getId(), null, null, -1);

            if(posts.isEmpty())
                return "Non ci sono post da visualizzare.";

            final StringBuilder sb = new StringBuilder();
            posts.forEach(post -> sb.append(post.toString() + "\n"));
            return sb.toString();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return "Si è verificato un errore grave durante la procedura di recupero dei post.";
        }
    }
}
