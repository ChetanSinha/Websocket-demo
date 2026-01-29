package com.example.web.socket_demo.controller;

import com.example.web.socket_demo.model.Document;
import com.example.web.socket_demo.model.DocumentHistory;
import com.example.web.socket_demo.model.User;
import com.example.web.socket_demo.service.DocumentService;
import com.example.web.socket_demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @Autowired
    private DocumentService documentService;

    @GetMapping("/")
    public String home() {
        return "redirect:/onboard";
    }

    @GetMapping("/onboard")
    public String onboard(Model model) {
        model.addAttribute("user", new User());
        return "onboard";
    }

    @PostMapping("/onboard")
    public String onboardUser(@ModelAttribute User user, HttpSession session) {
        User savedUser = userService.save(user);
        session.setAttribute("userId", savedUser.getId());
        return "redirect:/dashboard/" + savedUser.getId();
    }

    @GetMapping("/dashboard/{userId}")
    public String dashboard(@PathVariable Long userId, Model model, HttpSession session) {
        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/onboard";
        }
        session.setAttribute("userId", userId); // Set session to this userId
        model.addAttribute("documents", documentService.findAll());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userId", userId);
        return "dashboard";
    }

    @GetMapping("/document/new")
    public String newDocument(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/onboard";
        }
        model.addAttribute("document", new Document());
        model.addAttribute("userId", userId);
        return "new-document";
    }

    @PostMapping("/document")
    public String createDocument(@ModelAttribute Document document, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/onboard";
        }
        documentService.save(document, userId);
        return "redirect:/dashboard/" + userId;
    }

    @GetMapping("/document/{id}")
    public String viewDocument(@PathVariable Long id, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/onboard";
        }
        Document document = documentService.findById(id).orElse(null);
        if (document == null) {
            return "redirect:/dashboard/" + userId;
        }
        model.addAttribute("document", document);
        model.addAttribute("userId", userId);
        return "document";
    }

    @GetMapping("/document/{id}/edit")
    public String editDocument(@PathVariable Long id, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/onboard";
        }
        Document document = documentService.findById(id).orElse(null);
        if (document == null) {
            return "redirect:/dashboard/" + userId;
        }
        model.addAttribute("document", document);
        model.addAttribute("userId", userId);
        return "edit-document";
    }

    @PostMapping("/document/{id}")
    public String updateDocument(@PathVariable Long id, @ModelAttribute Document document, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/onboard";
        }
        document.setId(id);
        documentService.save(document, userId);
        return "redirect:/dashboard/" + userId;
    }

    @PostMapping("/document/{id}/delete")
    public String deleteDocument(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/onboard";
        }
        documentService.deleteById(id);
        return "redirect:/dashboard/" + userId;
    }

    @GetMapping("/document/{id}/history")
    public String viewHistory(@PathVariable Long id, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/onboard";
        }
        List<DocumentHistory> history = documentService.findHistoryByDocumentId(id);
        List<Map<String, Object>> historyWithNames = new ArrayList<>();
        for (DocumentHistory h : history) {
            Map<String, Object> map = new HashMap<>();
            map.put("version", h.getVersion());
            map.put("updatedAt", h.getUpdatedAt());
            map.put("content", h.getContent());
            User user = userService.findById(h.getUserId()).orElse(null);
            map.put("username", user != null ? user.getUsername() : "Unknown");
            historyWithNames.add(map);
        }
        model.addAttribute("history", historyWithNames);
        model.addAttribute("documentId", id);
        model.addAttribute("userId", userId);
        return "history";
    }

    @GetMapping("/document/{id}/version/{version}")
    public String viewVersion(@PathVariable Long id, @PathVariable int version, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/onboard";
        }
        DocumentHistory hist = documentService.findHistoryByDocumentId(id).stream()
                .filter(h -> h.getVersion() == version).findFirst().orElse(null);
        if (hist == null) {
            return "redirect:/dashboard/" + userId;
        }
        model.addAttribute("history", hist);
        model.addAttribute("userId", userId);
        return "document-version";
    }
}