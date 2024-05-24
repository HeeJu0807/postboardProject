package org.example.boardexam.controller;

import lombok.RequiredArgsConstructor;
import org.example.boardexam.domain.Board;
import org.example.boardexam.repository.BoardRepository;
import org.example.boardexam.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final BoardRepository boardRepository;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "5") int size){
        Pageable pageable = PageRequest.of(page -1, size);

        Page<Board> boards =  boardService.findAllBoards(pageable);
        model.addAttribute("boards", boards);
        //현재 페이지 값도 보내줌
        model.addAttribute("currentPage", page);

        return "boards/list";
    }

    //게시글 상세 조회
    @GetMapping("/view")
    public String detailBoard(@RequestParam Long id, Model model){
        Board board = boardService.findBoardById(id);
        model.addAttribute("board",board);
        model.addAttribute("updatedAt", board.getUpdatedAt());

        return "boards/view";
    }

    //게시글 작성
    @GetMapping("/writeform")
    public String writeForm(Model model){
        model.addAttribute("board", new Board());
        return "boards/writeform";
    }

    @PostMapping("/write")
    public String writeBoard(@ModelAttribute Board board){
        boardService.saveBoard(board);
        return "redirect:/list";
    }

    //게시글 삭제
    @GetMapping("/deleteform")
    public String deleteForm(@RequestParam(name = "id") Long id, Model model) {
        model.addAttribute("id", id);
        return "boards/deleteform";
    }

    @PostMapping("/delete")
    public String deleteBoard(@RequestParam Long id, @RequestParam String password, Model model) {
        try {
            model.addAttribute("id", id);
            boardService.deleteBoardById(id, password);
            return "redirect:/list";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());

            return "boards/deleteform";
        }
    }


    @GetMapping("/updateform")
    public String updateForm(@RequestParam Long id, Model model) {
        Board board = boardService.findBoardById(id);
        model.addAttribute("board", board);
        return "boards/updateform";
    }

    @PostMapping("/update")
    public String updateBoard(@ModelAttribute Board board, @RequestParam String password, Model model) {
        try {
            model.addAttribute("board", board);
            boardService.updateBoard(board, password);
            return "redirect:/view?id=" + board.getId();
        } catch(IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());

            return "boards/updateform";
        }
    }




}
