package org.example.boardexam.service;

import lombok.RequiredArgsConstructor;
import org.example.boardexam.domain.Board;
import org.example.boardexam.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    @Transactional(readOnly=true)
    public Iterable<Board> findAllBoards(){
        return boardRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Board findBoardById(Long id){
        return boardRepository.findById(id).orElse(null);
    }

    //게시글 등록
    @Transactional
    public Board saveBoard(Board board){
        board.setCreatedAt(LocalDateTime.now());
        return boardRepository.save(board);
    }

    //비밀번호 검증
    public boolean verifyPassword(Long id, String password){
        Board board = findBoardById(id);
        boolean isPasswordCorrect = board.getPassword().equals(password);
        return isPasswordCorrect;
    }

    //게시글 삭제
    @Transactional
    public void deleteBoardById(Long id, String password) {
        if (verifyPassword(id, password)) {
            boardRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Transactional
    public void updateBoard(Board board, String password) {
        if (!verifyPassword(board.getId(), password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // 해당 게시글의 id를 가져와서 그 id를 가진 게시글의 이름,제목,내용을 업데이트
        Board existingBoard = findBoardById(board.getId());
        existingBoard.setName(board.getName());
        existingBoard.setTitle(board.getTitle());
        existingBoard.setContent(board.getContent());
        existingBoard.setUpdatedAt(LocalDateTime.now()); // updatedAt 업데이트
        boardRepository.save(existingBoard);
    }

    public Page<Board> findAllBoards(Pageable pageable){
        Pageable sortedByDescId = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id"));
        return boardRepository.findAll(sortedByDescId);
    }
}
