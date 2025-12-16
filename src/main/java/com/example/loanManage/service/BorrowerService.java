package com.example.loanManage.service;

import com.example.loanManage.dto.BorrowerDto;
import java.util.List;
import org.springframework.data.domain.Page;
public interface BorrowerService {
    Page<BorrowerDto> getPage(int page, int size);
    BorrowerDto create(BorrowerDto dto);

    List<BorrowerDto> getAll();

    BorrowerDto getById(Long id);

    BorrowerDto update(Long id, BorrowerDto dto);

    void delete(Long id);


}
