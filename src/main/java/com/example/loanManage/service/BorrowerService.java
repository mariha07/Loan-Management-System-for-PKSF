package com.example.loanManage.service;

import com.example.loanManage.dto.BorrowerDto;
import java.util.List;
import org.springframework.data.domain.Page;
/**
 * The BorrowerService interface defines the 'rules' or 'menu' of actions
 * that can be performed on a Borrower.
 */
public interface BorrowerService {

    // Defines a method to get a specific 'slice' or page of borrowers (e.g., page 1 with 10 items).
    Page<BorrowerDto> getPage(int page, int size);

    // Defines the action to save a new borrower into the system.
    BorrowerDto create(BorrowerDto dto);

    // Defines the action to fetch every single borrower as a list.
    List<BorrowerDto> getAll();

    // Defines the action to find one specific borrower using their unique Database ID.
    BorrowerDto getById(Long id);

    // Defines the action to modify the information of an existing borrower.
    BorrowerDto update(Long id, BorrowerDto dto);

    // Defines the action to permanently remove a borrower from the system.
    void delete(Long id);
}
