package com.example.loanManage.service.imp;

import com.example.loanManage.dto.LoanAccountDto;
import com.example.loanManage.entity.Borrower;
import com.example.loanManage.entity.LoanAccount;
import com.example.loanManage.entity.LoanProduct;
import com.example.loanManage.repository.BorrowerRepository;
import com.example.loanManage.repository.LoanAccountRepository;
import com.example.loanManage.repository.LoanProductRepository;
import com.example.loanManage.service.LoanAccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

@Service // Marks this as a Service class so Spring can use it to handle business logic.
@Transactional // Ensures that database operations either all succeed or all fail together (maintains data integrity)
public class LoanAccountServiceImpl implements LoanAccountService {

    private final LoanAccountRepository loanAccountRepository;
    private final BorrowerRepository borrowerRepository;
    private final LoanProductRepository loanProductRepository;

    public LoanAccountServiceImpl(
            LoanAccountRepository loanAccountRepository,
            BorrowerRepository borrowerRepository,
            LoanProductRepository loanProductRepository
    ) {
        this.loanAccountRepository = loanAccountRepository;
        this.borrowerRepository = borrowerRepository;
        this.loanProductRepository = loanProductRepository;
    }

    // ================= CREATE =================
    @Override
    public LoanAccountDto create(LoanAccountDto dto) {

        if (dto.getBorrowerId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Borrower is required");

        if (dto.getLoanProductId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loan product is required");

        if (dto.getApprovedAmount() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Approved amount is required");

        Borrower borrower = borrowerRepository.findById(dto.getBorrowerId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrower not found"));

        LoanProduct product = loanProductRepository.findById(dto.getLoanProductId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan product not found"));

        // ===== MIN–MAX VALIDATION =====
        BigDecimal approved = dto.getApprovedAmount();

        if (product.getMinLoan() != null &&
                approved.compareTo(BigDecimal.valueOf(product.getMinLoan())) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Approved amount must be ≥ " + product.getMinLoan()
            );
        }

        if (product.getMaxLoan() != null &&
                approved.compareTo(BigDecimal.valueOf(product.getMaxLoan())) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Approved amount must be ≤ " + product.getMaxLoan()
            );
        }

        // ===== ENTITY BUILD =====
        LoanAccount acc = new LoanAccount();
        acc.setBorrower(borrower);
        acc.setLoanProduct(product);
        acc.setApprovedAmount(approved);
        acc.setOpeningDate(
                dto.getOpeningDate() != null
                        ? LocalDate.parse(dto.getOpeningDate())
                        : LocalDate.now()
        );
        acc.setStatus(dto.getStatus() != null ? dto.getStatus() : "OPEN");

        // ===== AUTO LOAN NUMBER =====
        acc.setLoanNumber(getNextLoanNumber());

        LoanAccount saved = loanAccountRepository.save(acc);
        return toDto(saved);
    }

    // ================= PAGINATION =================
    @Override //method is implementing a rule defined in the LoanAccountService interface.
    @Transactional(readOnly = true) //Tells the database this is a "read-only" task. This makes the database faster because it doesn't have to prepare for changes or locks.
    public Page<LoanAccountDto> getPaged(int page, int size) { // Takes 'page' (which page number) and 'size' (how many loans per page) as inputs.
        return loanAccountRepository // Accesses the database tool.
                .findAll(PageRequest.of(page, size, Sort.by("id").descending()))
                .map(this::toDto);
        // After getting the 'Page' of Entities from the database, this converts each
    }
    // ================= GET BY ID =================
    @Override
    @Transactional(readOnly = true)
    public LoanAccountDto getById(Long id) {

        LoanAccount acc = loanAccountRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan account not found"));

        return toDto(acc);
    }

    // ================= SEARCH BORROWER =================
    @Override
    @Transactional(readOnly = true)
    public List<Borrower> searchBorrower(String q) {

        if (q == null || q.trim().isEmpty()) {
            return List.of();
        }

        return borrowerRepository
                .findTop10ByIdNumberContainingIgnoreCaseOrMobileContainingIgnoreCase(q, q);
    }

    // ================= PRODUCTS =================
    @Override
    @Transactional(readOnly = true)
    public List<LoanProduct> getProducts() {
        return loanProductRepository.findAll();
    }

    // ================= NEXT LOAN NUMBER =================
    @Override
    @Transactional(readOnly = true)
    public String getNextLoanNumber() {

        long nextId = loanAccountRepository.findTopByOrderByIdDesc()
                .map(a -> a.getId() + 1)
                .orElse(1L);

        return String.format("LN-%06d", nextId);
    }

    // ================= FIND BY LOAN NUMBER =================
    @Override
    @Transactional(readOnly = true)
    public LoanAccountDto getByLoanNumber(String loanNumber) {

        LoanAccount acc = loanAccountRepository.findByLoanNumber(loanNumber)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan account not found"));

        return toDto(acc);
    }

    // ================= ENTITY → DTO =================
    private LoanAccountDto toDto(LoanAccount acc) {

        LoanAccountDto dto = new LoanAccountDto();

        dto.setId(acc.getId());
        dto.setLoanNumber(acc.getLoanNumber());

        // Borrower
        if (acc.getBorrower() != null) {
            dto.setBorrowerId(acc.getBorrower().getId());
            dto.setBorrowerName(acc.getBorrower().getName());
            dto.setBorrowerMobile(acc.getBorrower().getMobile());
            dto.setBorrowerIdNumber(acc.getBorrower().getIdNumber());
            if (acc.getBorrower().getIdType() != null) {
                dto.setBorrowerIdType(acc.getBorrower().getIdType().name());
            }
        }

        // Product
        if (acc.getLoanProduct() != null) {
            LoanProduct lp = acc.getLoanProduct();
            dto.setLoanProductId(lp.getId());
            dto.setLoanProductName(lp.getName());

            if (lp.getInterestRate() != null) {
                dto.setInterestRate(BigDecimal.valueOf(lp.getInterestRate()));
            }
            if (lp.getInstallmentType() != null) {
                dto.setInstallmentType(lp.getInstallmentType().name());
            }
            dto.setNumberOfInstallments(lp.getNumberOfInstallments());
        }

        dto.setApprovedAmount(acc.getApprovedAmount());
        dto.setOpeningDate(
                acc.getOpeningDate() != null ? acc.getOpeningDate().toString() : null
        );
        dto.setStatus(acc.getStatus());
        dto.setLoanType(acc.getLoanProduct().getLoanType().name());
        return dto;
    }
    @Override
    public ByteArrayInputStream generateLoanAccountReport() {
        List<LoanAccount> accounts = loanAccountRepository.findAll();
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(18);
            Paragraph para = new Paragraph("Loan Account List Report", font);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{3, 4, 4, 3, 3});

            Stream.of("Loan No", "Borrower", "Product", "Amount", "Status").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                header.setBorderWidth(2);
                header.setPhrase(new Phrase(headerTitle));
                table.addCell(header);
            });

            for (LoanAccount acc : accounts) {
                table.addCell(acc.getLoanNumber());
                table.addCell(acc.getBorrower() != null ? acc.getBorrower().getName() : "");
                table.addCell(acc.getLoanProduct() != null ? acc.getLoanProduct().getName() : "");
                table.addCell(String.valueOf(acc.getApprovedAmount()));
                table.addCell(acc.getStatus());
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
