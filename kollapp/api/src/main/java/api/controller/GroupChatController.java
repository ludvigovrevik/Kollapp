package api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import api.service.GroupChatService;
import core.Message;
import core.GroupChat;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groupchats")
public class GroupChatController {

    private final GroupChatService groupChatService;

    @Autowired
    public GroupChatController(GroupChatService groupChatService) {
        this.groupChatService = groupChatService;
    }

    @PostMapping("/{groupName}")
    public ResponseEntity<String> createGroupChat(@PathVariable String groupName) {
        try {
            groupChatService.createGroupChat(groupName);
            return ResponseEntity.ok("Group chat created successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{groupName}")
    public ResponseEntity<GroupChat> getGroupChat(@PathVariable String groupName) {
        try {
            GroupChat groupChat = groupChatService.getGroupChat(groupName);
            return ResponseEntity.ok(groupChat);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{groupName}/messages")
    public ResponseEntity<String> sendMessage(@PathVariable String groupName, @RequestBody Message message) {
        try {
            groupChatService.sendMessage(groupName, message);
            return ResponseEntity.ok("Message sent successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{groupName}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String groupName) {
        try {
            List<Message> messages = groupChatService.getMessages(groupName);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}