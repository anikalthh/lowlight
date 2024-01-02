package sg.nus.lowlight.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.nus.lowlight.model.Post;
import sg.nus.lowlight.model.PostComment;
import sg.nus.lowlight.service.ChannelsService;
import sg.nus.lowlight.service.UserService;
import sg.nus.lowlight.utility.Utils;

@Controller
@RequestMapping("/channels/{channelName}")
public class PostController {

    @Autowired
    private ChannelsService chSvc;

    @Autowired
    private UserService userSvc;

    @GetMapping("/newpost")
    public String createNewPost(@PathVariable("channelName") String channelName, Model m, HttpSession sess) {

        // If user is not logged in, redirect to error page
        if (sess.getAttribute("currUser") == null) {
            return "notloggedinerror";
        }
        
        Post newPost = new Post();
        newPost.setAuthor(sess.getAttribute("currUser").toString());
        newPost.setChannelName(channelName);
        m.addAttribute("newpost", newPost);
        
        return "addnewpost";
    }

    @PostMapping("/success")
    public String postAdded(@Valid @ModelAttribute("newpost") Post newpost, BindingResult bindings, @PathVariable("channelName") String channelName, Model m, HttpSession sess) throws JsonProcessingException {
        if (bindings.hasErrors()) {
            m.addAttribute("channelName", channelName);
            return "addnewpost";
        }

        // String channelName = sess.getAttribute("channelName").toString();
        String username = sess.getAttribute("currUser").toString();
        chSvc.savePost(channelName, newpost);
        m.addAttribute("isnewpostadded", true);
        m.addAttribute("channelName", channelName);

        List<Post> listOfPosts = chSvc.getPostsFromChannel(channelName);
        List<Post> sortedPosts = Utils.sortByDatePosts(listOfPosts);

        m.addAttribute("posts", sortedPosts);
        m.addAttribute("username", username);

        // Update user's published posts
        userSvc.addPostToUserDetails(username, newpost);

        // Update user's moodtracker
        userSvc.addMoodToUserDetails(username, newpost.getMood());
        
        m.addAttribute("username", username);
        return "browseposts";
    }

    @GetMapping("/{postId}")
    public String getIndividualPostAndComments(@PathVariable("postId") String postId, @PathVariable("channelName") String channelName, Model m,  HttpSession sess) {
        // If user is not logged in, redirect to error page
        if (sess.getAttribute("currUser") == null) {
            return "notloggedinerror";
        }

        // Retrieve post from redis
        Post post = chSvc.getPost(channelName, postId);

        // Retrieve comments from Redis
        List<PostComment> comments = chSvc.getAllComments(postId);
        List<PostComment> sortedComments = Utils.sortByDateComments(comments);
        m.addAttribute("comments", sortedComments);

        PostComment newComment = new PostComment();
        newComment.setAuthor(sess.getAttribute("currUser").toString());
        m.addAttribute("newcomment", newComment);
        m.addAttribute("post", post);
        m.addAttribute("postId", postId);
        m.addAttribute("channelName", channelName);

        m.addAttribute("username", sess.getAttribute("currUser").toString());
        return "postcomments";
    }

    // Comments: Save comment
    @PostMapping("/{postId}/newcomment")
    public String processNewComment(@PathVariable("postId") String postId, @PathVariable("channelName") String channelName, @Valid @ModelAttribute("newcomment") PostComment comment, BindingResult bindings, Model m, HttpSession sess) {

        if (bindings.hasErrors()) {
            return "postcomments";
        }

        // Save comment to redis
        chSvc.saveComment(postId, comment);

        // Retrieve post from redis
        Post post = chSvc.getPost(channelName, postId);
        m.addAttribute("post", post);

        // Retrieve comments from Redis
        List<PostComment> comments = chSvc.getAllComments(postId);
        List<PostComment> sortedComments = Utils.sortByDateComments(comments);
        m.addAttribute("isnewcommentadded", true);
        m.addAttribute("comments", sortedComments);

        PostComment newComment = new PostComment();
        newComment.setAuthor(sess.getAttribute("currUser").toString());
        m.addAttribute("newcomment", newComment);

        m.addAttribute("username", sess.getAttribute("currUser").toString());
        return "postcomments";
    }

    // Comments: Delete comment
    @PostMapping("/{postId}/deletecomment")
    public String deleteComment(@PathVariable("postId") String postId, @PathVariable("channelName") String channelName, @RequestBody MultiValueMap mvm, Model m, HttpSession sess) {
        
        // Delete comment from redis
        String commentId = mvm.getFirst("commentId").toString();
        chSvc.deleteComment(postId, commentId);

        // Retrieve post from redis
        Post post = chSvc.getPost(channelName, postId);
        m.addAttribute("post", post);

        // Retrieve comments from Redis
        List<PostComment> comments = chSvc.getAllComments(postId);
        List<PostComment> sortedComments = Utils.sortByDateComments(comments);
        m.addAttribute("iscommentdeleted", true);
        m.addAttribute("comments", sortedComments);

        PostComment newComment = new PostComment();
        newComment.setAuthor(sess.getAttribute("currUser").toString());
        m.addAttribute("newcomment", newComment);

        m.addAttribute("username", sess.getAttribute("currUser").toString());
        return "postcomments";
    }
}
