package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.DuplicateResourceException;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.*;
import com.ailadeekshith.schoolManagement.repository.*;
import com.ailadeekshith.schoolManagement.service.SchoolConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SchoolConfigServiceImpl implements SchoolConfigService {

    private final SchoolProfileRepository profileRepo;
    private final ReceiptTemplateRepository receiptTemplateRepo;
    private final DashboardSlideRepository dashboardSlideRepo;
    private final GradeRepository gradeRepo;
    private final SectionRepository sectionRepo;
    private final SubjectRepository subjectRepo;
    private final FeeStructureRepository feeStructureRepo;
    private final HolidayRepository holidayRepo;

    // ── Profile ──────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public SchoolProfile getProfile() {
        return profileRepo.findById(1L).orElse(new SchoolProfile());
    }

    @Override
    public SchoolProfile saveProfile(SchoolProfile incoming) {
        SchoolProfile profile = profileRepo.findById(1L).orElse(new SchoolProfile());
        profile.setId(1L);
        profile.setSchoolName(incoming.getSchoolName());
        profile.setAddress(incoming.getAddress());
        profile.setPhone(incoming.getPhone());
        profile.setEmail(incoming.getEmail());
        profile.setPrincipalName(incoming.getPrincipalName());
        profile.setAcademicYear(incoming.getAcademicYear());
        profile.setEstablishedYear(incoming.getEstablishedYear());
        profile.setWebsite(incoming.getWebsite());
        profile.setAffiliationBoard(incoming.getAffiliationBoard());
        profile.setSchoolType(incoming.getSchoolType());
        profile.setLogoBase64(incoming.getLogoBase64());
        return profileRepo.save(profile);
    }

    // ── Receipt Templates ────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<ReceiptTemplate> getAllReceiptTemplates() {
        return receiptTemplateRepo.findAllByOrderByIdAsc();
    }

    @Override
    public ReceiptTemplate createReceiptTemplate(ReceiptTemplate incoming) {
        incoming.setId(null);
        // First template becomes the default automatically.
        boolean makeDefault = Boolean.TRUE.equals(incoming.getIsDefault())
                || receiptTemplateRepo.count() == 0;
        if (makeDefault) clearDefaultFlag();
        incoming.setIsDefault(makeDefault);
        return receiptTemplateRepo.save(incoming);
    }

    @Override
    public ReceiptTemplate updateReceiptTemplate(Long id, ReceiptTemplate updated) {
        ReceiptTemplate existing = receiptTemplateRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt template not found: " + id));
        existing.setName(updated.getName());
        existing.setTitle(updated.getTitle());
        existing.setAccentColor(updated.getAccentColor());
        existing.setShowLogo(updated.getShowLogo());
        existing.setShowSchoolName(updated.getShowSchoolName());
        existing.setShowSchoolAddress(updated.getShowSchoolAddress());
        existing.setShowSchoolContact(updated.getShowSchoolContact());
        existing.setShowReceiptNo(updated.getShowReceiptNo());
        existing.setShowDate(updated.getShowDate());
        existing.setShowAcademicYear(updated.getShowAcademicYear());
        existing.setShowFeeType(updated.getShowFeeType());
        existing.setShowPaymentMethod(updated.getShowPaymentMethod());
        existing.setShowTransactionId(updated.getShowTransactionId());
        existing.setShowTotals(updated.getShowTotals());
        existing.setShowAmountInWords(updated.getShowAmountInWords());
        existing.setThankYouMessage(updated.getThankYouMessage());
        existing.setSignatureLabel(updated.getSignatureLabel());
        existing.setFooterNote(updated.getFooterNote());
        // Promoting to default here also demotes any other default.
        if (Boolean.TRUE.equals(updated.getIsDefault()) && !Boolean.TRUE.equals(existing.getIsDefault())) {
            clearDefaultFlag();
            existing.setIsDefault(true);
        }
        return receiptTemplateRepo.save(existing);
    }

    @Override
    public ReceiptTemplate setDefaultReceiptTemplate(Long id) {
        ReceiptTemplate target = receiptTemplateRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt template not found: " + id));
        clearDefaultFlag();
        target.setIsDefault(true);
        return receiptTemplateRepo.save(target);
    }

    @Override
    public void deleteReceiptTemplate(Long id) {
        ReceiptTemplate target = receiptTemplateRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt template not found: " + id));
        boolean wasDefault = Boolean.TRUE.equals(target.getIsDefault());
        receiptTemplateRepo.delete(target);
        // Keep a default if any templates remain.
        if (wasDefault) {
            receiptTemplateRepo.findAllByOrderByIdAsc().stream().findFirst().ifPresent(t -> {
                t.setIsDefault(true);
                receiptTemplateRepo.save(t);
            });
        }
    }

    private void clearDefaultFlag() {
        receiptTemplateRepo.findFirstByIsDefaultTrue().ifPresent(t -> {
            t.setIsDefault(false);
            receiptTemplateRepo.save(t);
        });
    }

    // ── Dashboard Slides ─────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<DashboardSlide> getAllDashboardSlides() {
        return dashboardSlideRepo.findAllByOrderBySortOrderAscIdAsc();
    }

    @Override
    public DashboardSlide createDashboardSlide(DashboardSlide incoming) {
        incoming.setId(null);
        if (incoming.getSortOrder() == null) {
            // Append to the end of the current list.
            incoming.setSortOrder(dashboardSlideRepo.findAll().size());
        }
        return dashboardSlideRepo.save(incoming);
    }

    @Override
    public DashboardSlide updateDashboardSlide(Long id, DashboardSlide updated) {
        DashboardSlide existing = dashboardSlideRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dashboard slide not found: " + id));
        existing.setImageBase64(updated.getImageBase64());
        existing.setTagline(updated.getTagline());
        if (updated.getSortOrder() != null) existing.setSortOrder(updated.getSortOrder());
        return dashboardSlideRepo.save(existing);
    }

    @Override
    public void deleteDashboardSlide(Long id) {
        DashboardSlide target = dashboardSlideRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dashboard slide not found: " + id));
        dashboardSlideRepo.delete(target);
    }

    // ── Grades ───────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<Grade> getAllGrades() {
        return gradeRepo.findAllByOrderByDisplayOrderAsc();
    }

    @Override
    public Grade createGrade(Grade grade) {
        if (gradeRepo.existsByName(grade.getName())) {
            throw new DuplicateResourceException("Grade already exists: " + grade.getName());
        }
        if (grade.getDisplayOrder() == null) {
            grade.setDisplayOrder(gradeRepo.count() == 0 ? 1 : (int) gradeRepo.count() + 1);
        }
        return gradeRepo.save(grade);
    }

    @Override
    public void deleteGrade(Long id) {
        Grade grade = gradeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found: " + id));
        gradeRepo.delete(grade);
    }

    // ── Sections ─────────────────────────────────────────────
    @Override
    public Section addSection(Long gradeId, String letter) {
        Grade grade = gradeRepo.findById(gradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found: " + gradeId));
        if (sectionRepo.existsByGradeIdAndLetter(gradeId, letter.toUpperCase())) {
            throw new DuplicateResourceException("Section " + letter + " already exists in this grade");
        }
        Section section = Section.builder()
                .letter(letter.toUpperCase())
                .grade(grade)
                .isActive(true)
                .build();
        return sectionRepo.save(section);
    }

    @Override
    public void deleteSection(Long sectionId) {
        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found: " + sectionId));
        sectionRepo.delete(section);
    }

    // ── Subjects ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<Subject> getAllSubjects() {
        return subjectRepo.findAll();
    }

    @Override
    public Subject createSubject(Subject subject) {
        if (subjectRepo.existsByName(subject.getName())) {
            throw new DuplicateResourceException("Subject already exists: " + subject.getName());
        }
        return subjectRepo.save(subject);
    }

    @Override
    public Subject updateSubject(Long id, Subject updated) {
        Subject existing = subjectRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + id));
        existing.setName(updated.getName());
        existing.setCode(updated.getCode());
        existing.setType(updated.getType());
        existing.setDescription(updated.getDescription());
        existing.setIsActive(updated.getIsActive());
        return subjectRepo.save(existing);
    }

    @Override
    public void deleteSubject(Long id) {
        subjectRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + id));
        subjectRepo.deleteById(id);
    }

    // ── Fee Structure ────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<FeeStructure> getAllFeeStructures() {
        return feeStructureRepo.findAll();
    }

    @Override
    public FeeStructure createFeeStructure(FeeStructure fs) {
        return feeStructureRepo.save(fs);
    }

    @Override
    public FeeStructure updateFeeStructure(Long id, FeeStructure updated) {
        FeeStructure existing = feeStructureRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee structure not found: " + id));
        existing.setGradeName(updated.getGradeName());
        existing.setFeeCategory(updated.getFeeCategory());
        existing.setAmount(updated.getAmount());
        existing.setAcademicYear(updated.getAcademicYear());
        existing.setFrequency(updated.getFrequency());
        existing.setDescription(updated.getDescription());
        existing.setIsActive(updated.getIsActive());
        return feeStructureRepo.save(existing);
    }

    @Override
    public void deleteFeeStructure(Long id) {
        feeStructureRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee structure not found: " + id));
        feeStructureRepo.deleteById(id);
    }

    // ── Holidays ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<Holiday> getAllHolidays() {
        return holidayRepo.findAllByOrderByDateAsc();
    }

    @Override
    public Holiday createHoliday(Holiday holiday) {
        log.info("holiday");
        return holidayRepo.save(holiday);
    }

    @Override
    public Holiday updateHoliday(Long id, Holiday updated) {
        log.info("updating holiday");
        Holiday existing = holidayRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found: " + id));
        existing.setName(updated.getName());
        existing.setDate(updated.getDate());
        existing.setEndDate(updated.getEndDate());
        existing.setType(updated.getType());
        existing.setDescription(updated.getDescription());
        existing.setAcademicYear(updated.getAcademicYear());
        return holidayRepo.save(existing);
    }

    @Override
    public void deleteHoliday(Long id) {
        holidayRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found: " + id));
        holidayRepo.deleteById(id);
    }
}
