package com.example.loanManage.service.imp;

import com.example.loanManage.dto.LoanProductDto;
import com.example.loanManage.entity.LoanProduct;
import com.example.loanManage.repository.LoanProductRepository;
import com.example.loanManage.service.LoanProductService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanProductServiceImpl implements LoanProductService {

    @Autowired
    private LoanProductRepository loanProductRepository;

    @Override
    public LoanProductDto create(LoanProductDto request) {

        validate(request); //validate incoming data

        LoanProduct lp = new LoanProduct();
        copyDtoToEntity(request, lp);
        lp.setActive(true);

        LoanProduct saved = loanProductRepository.save(lp);
        return toDto(saved);
    }

    @Override
    public List<LoanProductDto> getAll() {
        return loanProductRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanProductDto> getActive() {
        return loanProductRepository.findByActiveTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LoanProductDto getById(Long id) {
        LoanProduct lp = loanProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan product not found with id: " + id));
        return toDto(lp);
    }

    @Override
    public LoanProductDto update(Long id, LoanProductDto request) {

        validate(request); //prevent null update crash

        LoanProduct lp = loanProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan product not found"));

        copyDtoToEntity(request, lp);

        LoanProduct saved = loanProductRepository.save(lp);
        return toDto(saved);
    }

    @Override
    public void deactivate(Long id) {
        LoanProduct lp = loanProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan product not found"));
        lp.setActive(false);
        loanProductRepository.save(lp);
    }

    // ======================== Helper Methods ========================

    private LoanProductDto toDto(LoanProduct lp) {
        LoanProductDto dto = new LoanProductDto();

        dto.setId(lp.getId());
        dto.setName(lp.getName());
        dto.setLoanType(lp.getLoanType());
        dto.setInterestRate(lp.getInterestRate());
        dto.setNumberOfInstallments(lp.getNumberOfInstallments());
        dto.setInstallmentType(lp.getInstallmentType());
        dto.setActive(lp.isActive());

        dto.setCode(lp.getCode());
        dto.setMaxLoan(lp.getMaxLoan());
        dto.setMinLoan(lp.getMinLoan());

        return dto;
    }

    private void copyDtoToEntity(LoanProductDto dto, LoanProduct lp) {
        lp.setName(dto.getName());
        lp.setLoanType(dto.getLoanType());
        lp.setInterestRate(dto.getInterestRate());
        lp.setNumberOfInstallments(dto.getNumberOfInstallments());
        lp.setInstallmentType(dto.getInstallmentType());

        lp.setCode(dto.getCode());
        lp.setMaxLoan(dto.getMaxLoan());
        lp.setMinLoan(dto.getMinLoan());
    }

    //Validation to avoid null crash from frontend
    private void validate(LoanProductDto dto) {
        if (dto.getName() == null || dto.getName().isBlank())
            throw new RuntimeException("Loan Product Name is required");

        if (dto.getCode() == null || dto.getCode().isBlank())
            throw new RuntimeException("Loan Product Code is required");

        if (dto.getMaxLoan() == null)
            throw new RuntimeException("Max Loan amount is required");

        if (dto.getMinLoan() == null)
            throw new RuntimeException("Min Loan amount is required");

        if (dto.getInterestRate() == null)
            throw new RuntimeException("Interest rate is required");

        if (dto.getNumberOfInstallments() == null)
            throw new RuntimeException("Number of installments is required");

        if (dto.getInstallmentType() == null)
            throw new RuntimeException("Installment type is required");
    }

    @Override
    public ByteArrayInputStream generateLoanProductReport() {
        List<LoanProduct> products = loanProductRepository.findAll();
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(18);
            Paragraph para = new Paragraph("Loan Product List Report", font);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 3, 2, 2, 2, 2});

            String[] headers = {"#", "Name", "Code", "Type", "Rate (%)", "Installments"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            int count = 1;
            for (LoanProduct p : products) {
                table.addCell(String.valueOf(count++));
                table.addCell(p.getName());
                table.addCell(p.getCode());
                table.addCell(p.getLoanType().toString());
                table.addCell(String.valueOf(p.getInterestRate()));
                table.addCell(String.valueOf(p.getNumberOfInstallments()));
            }

            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
