package com.thinkopen.restful.api;

import com.thinkopen.restful.api.dtos.Comment;
import com.thinkopen.restful.api.dtos.Post;
import com.thinkopen.restful.api.dtos.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

public class OldApplication {
    private static final int DROPDOWN_LIST_LIMIT = 5;

    private static final MySQLAccess dao;
    private static final Scanner scanner;
    private static User user;
    private static Post post;

    static {
        dao = MySQLAccess.getInstance();
        scanner = new Scanner(System.in);
    }

	public String login(String username, String password) throws ClassNotFoundException, SQLException {
        return String.format("USERNAME: %s, PASSWORD: %s", "Prova", "Prova");
//        final String msg = "1 - Login; \n2 - Sign up ;\n3 - Esci ;\n\nRisposta";
//
//        System.out.println("Benvenuto\n");
//
//        exit:
//        while(true) {
//            switch (read(msg, Integer::parseInt, r -> r >= 1 && r <= 3, "Risposta non valida")) {
//                case 1:
//                    String email = read("Email", t -> t.length() > 0 && t.length() <= 30, "Email non valida.");
//                    String password = read("Password", t -> t.length() > 0 && t.length() <= 20, "Password non valida.");
//
//                    if((user = dao.login(email, password)) != null) {
//                        showSuccess("Utente loggato con successo.");
//                        mainMenu();
//                    } else {
//                        showError("Credenziali errate.");
//                    }
//
//                    break;
//                case 2:
//                    createUser();
//                    break;
//                case 3:
//                    System.out.println("\nArrivederci\n");
//                    break exit;
//            }
//        }
	}

	private static void mainMenu() throws SQLException, ClassNotFoundException {
        final String msg = "1 - Visualizza gli ultimi post ;\n2 - Cambia password ;\n3 - Logout ;\n\nRisposta";

        logout:
        while (true) {
            int risp = read(msg, Integer::parseInt, r -> r >= 1 && r <= 3, "Risposta non valida.");

            switch (risp) {
                case 1:
                    showLastPosts();
                    break;
                case 2:
                    changePassword();
                    break;
                case 3:
                    user = null;
                    System.out.println("\nSei stato disconnesso.\n");
                    break logout;
            }
        }
    }

    private static void showLastPosts() throws SQLException, ClassNotFoundException {
        int postCount = dao.countPosts(null);
        List<Post> posts = null;
        int offset = 0;

        while(true) {
            if(postCount == 0) {
                showError("Non ci sono post.");
            } else {
                posts = dao.selectAllPosts(null, DROPDOWN_LIST_LIMIT, offset, -1);
                posts.forEach(System.out::println);
                System.out.println();
            }

            switch (postsMenu()) {
                case 1:
                    createPost();
                    postCount = dao.countPosts(null);
                    offset = 0;
                    break;
                case 2:
                    editPost();
                    break;
                case 3:
                    deletePost();
                    postCount = dao.countPosts(null);
                    break;
                case 4:
                    post = dao.selectPostById(read("ID Post", Integer::parseInt, OldApplication::postExists, "ID Post non valido."));
                    showLastComments();
                    post = null;
                    offset = 0;
                    break;
                case 5:
                    if(postCount > 0) {
                        if(offset + DROPDOWN_LIST_LIMIT >= postCount)
                            showError("Non ci sono ulteriori post da visualizzare.");
                        else
                            offset += posts.size();
                    }
                    continue;
                case 6:
                    return;
            }
        }
    }

    private static void showLastComments() throws SQLException, ClassNotFoundException {
        int commentCount = dao.countComments(post.getId());
        List<Comment> comments = null;
        int offset = 0;

        while(true) {

            if(commentCount == 0) {
                showError("Non ci sono commenti.");
            } else {
                comments = dao.selectAllComments(post.getId(), DROPDOWN_LIST_LIMIT, offset, -1);
                comments.forEach(System.out::println);
                System.out.println();
            }

            switch (commentsMenu()) {
                case 1:
                    createComment();
                    commentCount = dao.countComments(post.getId());
                    offset = 0;
                    break;
                case 2:
                    editComment();
                    break;
                case 3:
                    deleteComment();
                    commentCount = dao.countComments(post.getId());
                    break;
                case 4:
                    if(commentCount > 0) {
                        if(offset + DROPDOWN_LIST_LIMIT >= commentCount)
                            showError("Non ci sono ulteriori commenti da visualizzare.");
                        else
                            offset += comments.size();
                    }
                    continue;
                case 5:
                    return;
            }
        }
    }

    private static int postsMenu() {
        final String msg = "1 - Aggiungi post;\n2 - Modifica post;\n3 - Rimuovi post;\n4 - Visualizza commenti;" +
                "\n5 - Visualizza altri post;\n6 - Torna indietro;\n\nRisposta: ";

        return read(msg, Integer::parseInt, r -> r >= 1 && r <= 6, "Risposta non valida.");
    }

    private static int commentsMenu() {
        final String msg = "1 - Aggiungi commento;\n2 - Modifica commento;\n3 - Rimuovi commento;" +
                "\n4 - Visualizza altri commenti;\n5 - Torna indietro;\n\nRisposta: ";

        return read(msg, Integer::parseInt, r -> r >= 1 && r <= 5, "Risposta non valida.");
    }

	private static <T> T read(String msg, Function<String, T> f, Predicate<T> test, String errMsg) {
        while(true) {
            try {
                System.out.print(msg + ": ");
                String input = scanner.nextLine();
                T t = f.apply(input);

                if(!test.test(t))
                    throw new Exception();

                System.out.println();

                return t;
            } catch (Exception e) {
                showError(errMsg);
            }

        }
    }

    private static void showSuccess(String msg) {
        System.out.println("\n\t[ SUCCESS ]: " + msg + "\n");
        scanner.nextLine();
    }

    private static void showError(String errMsg) {
        System.out.println("\n\t[ ERRORE ]: " + errMsg + "\n");
        scanner.nextLine();
    }

    private static String read(String msg, Predicate<String> test, String errMsg) {
        return read(msg, str -> str, test, errMsg);
    }

    private static void createUser() throws SQLException, ClassNotFoundException {
        User user = new User();
        user.setName(read("Nome", n -> n.length() > 0 && n.length() <= 50, "Lunghezza nome non valida."));
        user.setEmail(read("Email", e -> e.length() > 0 && e.length() <= 30, "Lunghezza mail non valida."));
        String password = read("Password", p -> p.length() > 0 && p.length() <= 20, "Lunghezza password non valida");
        user.setAge(read("Età", Integer::parseInt, e -> e > 0 && e <= 100, "Eta non valida."));

        if(dao.insert(user, password) > 0) {
            showSuccess("Account creato con successo.");
        } else {
            showError("Creazione account fallita.");
        }
    }

    private static void createPost() throws SQLException, ClassNotFoundException {
        Post post = new Post();

        post.setTitle(read("Titolo", t -> t.length() > 0 && t.length() <= 255, "Lunghezza titolo non valida."));
        post.setContent(read("Contenuto", t -> t.length() > 0 && t.length() <= 1024, "Lunghezza contenuto non valida."));
        post.setClosed(read("Chiuso ai commenti?", Boolean::parseBoolean, t -> true, "Risposta non valida."));
        post.setDate(System.currentTimeMillis());
        post.setUserId(user.getId());

        if(dao.insert(post) > 0) {
            showSuccess("Post creato con successo.");
        } else {
            showError("Creazione post fallita.");
        }
    }

    private static void editPost() throws SQLException, ClassNotFoundException {
        int postId = read("ID Post", Integer::parseInt, OldApplication::postExists, "ID Post non valido.");

        if(!isPostOwner(postId)) {
            showError("Non puoi modificare post altrui.");
            return;
        }

        Post post = dao.selectPostById(postId);

        post.setTitle(read("Titolo", t -> t.length() > 0 && t.length() <= 255, "Lunghezza titolo non valida."));
        post.setContent(read("Contenuto", t -> t.length() > 0 && t.length() <= 1024, "Lunghezza contenuto non valida."));
        post.setClosed(read("Chiuso ai commenti?", Boolean::parseBoolean, t -> true, "Risposta non valida."));
        post.setDate(System.currentTimeMillis());

        if(dao.update(post) > 0) {
            showSuccess("Post aggiornato con successo.");
        } else {
            showError("Aggiornamento post fallito.");
        }
    }

    private static void deletePost() throws SQLException, ClassNotFoundException {
        int postId = read("ID Post", Integer::parseInt, OldApplication::postExists, "ID Post non valido.");

        if(!isPostOwner(postId)) {
            showError("Non puoi eliminare post altrui.");
            return;
        }

        Post post = dao.selectPostById(postId);

        if(dao.delete(post) > 0) {
            showSuccess("Post eliminato con successo.");
        } else {
            showError("Eliminazione post fallita.");
        }
    }
	
	private static void createComment() throws ClassNotFoundException, SQLException {
        if(!postIsOpen(post.getId())) {
            showError("Il post è chiuso ai commenti.");
            return;
        }

        String content = read("Commento", c -> c.length() > 0 && c.length() <= 140, "Lunghezza commento non valida.");
		
		Comment dto = new Comment();
		dto.setContent(content);
		dto.setPostId(post.getId());
		dto.setUserId(user.getId());
		dto.setDate(System.currentTimeMillis());

        if(dao.insert(dto) > 0) {
            showSuccess("Commento creato con successo.");
        } else {
            showError("Creazione commento fallita.");
        }
	}

    private static void editComment() throws ClassNotFoundException, SQLException {
        int commentId = read("ID Commento", Integer::parseInt, OldApplication::commentExists, "ID commento non valido.");

        if(!isCommentOwner(commentId)) {
            showError("Non puoi modificare commenti altrui.");
            return;
        }

        Comment dto = dao.selectCommentById(commentId);
        dto.setContent(read("Commento", c -> c.length() > 0 && c.length() <= 140, "Lunghezza commento non valida."));
        dto.setDate(System.currentTimeMillis());

        if(dao.update(dto) > 0) {
            showSuccess("Commento aggiornato con successo.");
        } else {
            showError("Aggiornamento commento fallito.");
        }
    }

    private static void deleteComment() throws ClassNotFoundException, SQLException {
        int commentId = read("ID Commento", Integer::parseInt, OldApplication::commentExists, "ID commento non valido.");

        if(!isCommentOwner(commentId)) {
            showError("Non puoi eliminare commenti altrui.");
            return;
        }

        Comment dto = dao.selectCommentById(commentId);

        if(dao.delete(dto) > 0) {
            showSuccess("Commento eliminato con successo.");
        } else {
            showError("Eliminazione commento fallita.");
        }
    }

    private static void changePassword() throws SQLException, ClassNotFoundException {
        final String currentPswd = read("Password corrente", str -> true, "Password non valida.");

        if(dao.login(user.getEmail(), currentPswd) == null) {
            showError("Password errata.");
            return;
        }

        String newPassword = read("Nuova password", t -> t.length() > 0 && t.length() <= 20 && !t.equals(currentPswd), "Password non valida.");
        String repeatPassowrd = read("Conferma password", t -> t.length() > 0 && t.length() <= 20, "Password non valida.");

        if(!newPassword.equals(repeatPassowrd)) {
            showError("Le password non corrispondono.");
        } else {
            if(dao.update(user.getId(), newPassword) > 0) {
                showSuccess("Password aggiornata con successo.");
            } else {
                showError("Aggiornamento password fallito.");
            }
        }
    }

    private static boolean isPostOwner(int id) throws SQLException, ClassNotFoundException {
        final Post post = dao.selectPostById(id);
        return post.getUserId() == user.getId();
    }

    private static boolean isCommentOwner(int id) throws SQLException, ClassNotFoundException {
        final Comment comment = dao.selectCommentById(id);
        return comment.getUserId() == user.getId();
    }

    private static boolean postExists(int id) {
        try {
            return dao.selectPostById(id) != null;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static boolean postIsOpen(int id) throws SQLException, ClassNotFoundException {
        Post post = dao.selectPostById(id);

        if(post == null)
            throw new NullPointerException();

        return !post.isClosed();
    }

    private static boolean commentExists(int id) {
        try {
            return dao.selectCommentById(id) != null;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
