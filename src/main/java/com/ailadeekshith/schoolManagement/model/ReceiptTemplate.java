package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Admin-configurable template for the fee-payment receipt that is printed
 * for the parent after a payment is collected. Multiple templates can be
 * defined; the one flagged {@code isDefault} is used at collection time.
 */
@Entity
@Table(name = "receipt_template")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @Builder.Default
    private String name = "Untitled Template";

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "title")
    @Builder.Default
    private String title = "Fee Payment Receipt";

    @Column(name = "accent_color")
    @Builder.Default
    private String accentColor = "#6C63FF";

    // ── Header / branding ─────────────────────────────────────
    @Column(name = "show_logo")
    @Builder.Default
    private Boolean showLogo = true;

    @Column(name = "show_school_name")
    @Builder.Default
    private Boolean showSchoolName = true;

    @Column(name = "show_school_address")
    @Builder.Default
    private Boolean showSchoolAddress = true;

    @Column(name = "show_school_contact")
    @Builder.Default
    private Boolean showSchoolContact = true;

    // ── Body fields ───────────────────────────────────────────
    @Column(name = "show_receipt_no")
    @Builder.Default
    private Boolean showReceiptNo = true;

    @Column(name = "show_date")
    @Builder.Default
    private Boolean showDate = true;

    @Column(name = "show_academic_year")
    @Builder.Default
    private Boolean showAcademicYear = true;

    @Column(name = "show_fee_type")
    @Builder.Default
    private Boolean showFeeType = true;

    @Column(name = "show_payment_method")
    @Builder.Default
    private Boolean showPaymentMethod = true;

    @Column(name = "show_transaction_id")
    @Builder.Default
    private Boolean showTransactionId = true;

    @Column(name = "show_totals")
    @Builder.Default
    private Boolean showTotals = true; // total / paid-to-date / balance due summary

    @Column(name = "show_amount_in_words")
    @Builder.Default
    private Boolean showAmountInWords = true;

    // ── Footer ────────────────────────────────────────────────
    @Column(name = "thank_you_message")
    @Builder.Default
    private String thankYouMessage = "Thank you for your payment!";

    @Column(name = "signature_label")
    @Builder.Default
    private String signatureLabel = "Authorised Signatory";

    @Column(name = "footer_note", columnDefinition = "TEXT")
    @Builder.Default
    private String footerNote =
            "This is a computer-generated receipt and does not require a physical signature.";
}
