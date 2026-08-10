package com.ailadeekshith.schoolManagement.service;

/**
 * Builds the HTML/text bodies for notification emails. Kept separate so the
 * services stay focused on business logic and the markup lives in one place.
 */
public final class EmailTemplates {

    private EmailTemplates() {}

    /** Simple carrier for a rendered email (subject + both bodies). */
    public record Email(String subject, String html, String text) {}

    /**
     * Welcome email delivered when a login is auto-created for a new student or teacher.
     *
     * @param name        the person's display name
     * @param role        human-readable role, e.g. "Teacher" or "Student"
     * @param username    the generated login username
     * @param password    the default password
     * @param loginUrl    portal URL (may be blank — the block is then omitted)
     */
    public static Email welcomeCredentials(String name, String role, String username,
                                           String password, String loginUrl) {
        String subject = "Your " + role + " account is ready";

        String loginButton = (loginUrl != null && !loginUrl.isBlank())
                ? "<p style=\"margin:24px 0;\"><a href=\"" + esc(loginUrl) + "\" "
                    + "style=\"background:#6C63FF;color:#fff;text-decoration:none;padding:11px 26px;"
                    + "border-radius:8px;font-weight:600;display:inline-block;\">Log in</a></p>"
                : "";

        String html = """
                <div style="font-family:Segoe UI,Arial,sans-serif;max-width:520px;margin:0 auto;color:#1E1B4B;">
                  <div style="background:#6C63FF;padding:20px 24px;border-radius:14px 14px 0 0;">
                    <h2 style="margin:0;color:#fff;font-size:19px;">Welcome, %s 👋</h2>
                  </div>
                  <div style="border:1px solid #DDE3F5;border-top:none;border-radius:0 0 14px 14px;padding:22px 24px;">
                    <p style="margin:0 0 14px;font-size:14px;">Your %s account has been created. Use the credentials below to sign in:</p>
                    <table style="font-size:14px;border-collapse:collapse;">
                      <tr><td style="padding:6px 14px 6px 0;color:#6B7280;">Username</td><td style="font-weight:700;">%s</td></tr>
                      <tr><td style="padding:6px 14px 6px 0;color:#6B7280;">Password</td><td style="font-weight:700;">%s</td></tr>
                    </table>
                    %s
                    <p style="margin:18px 0 0;font-size:12px;color:#6B7280;">For your security, please change this password after your first login.</p>
                  </div>
                </div>
                """.formatted(esc(name), esc(role.toLowerCase()), esc(username), esc(password), loginButton);

        StringBuilder text = new StringBuilder()
                .append("Welcome, ").append(name).append("!\n\n")
                .append("Your ").append(role.toLowerCase()).append(" account has been created.\n\n")
                .append("Username: ").append(username).append("\n")
                .append("Password: ").append(password).append("\n");
        if (loginUrl != null && !loginUrl.isBlank()) {
            text.append("Login: ").append(loginUrl).append("\n");
        }
        text.append("\nPlease change your password after your first login.");

        return new Email(subject, html, text.toString());
    }

    /**
     * Admission status-change notification sent to the guardian's email.
     *
     * @param applicantName the applicant's name
     * @param applyClass    the class applied for
     * @param statusLabel   human-readable status, e.g. "Approved"
     * @param message       status-specific message body
     */
    public static Email admissionStatus(String applicantName, String applyClass,
                                        String statusLabel, String message) {
        String subject = "Admission update for " + applicantName + " — " + statusLabel;

        String html = """
                <div style="font-family:Segoe UI,Arial,sans-serif;max-width:520px;margin:0 auto;color:#1E1B4B;">
                  <div style="background:#6C63FF;padding:20px 24px;border-radius:14px 14px 0 0;">
                    <h2 style="margin:0;color:#fff;font-size:19px;">Admission Update</h2>
                  </div>
                  <div style="border:1px solid #DDE3F5;border-top:none;border-radius:0 0 14px 14px;padding:22px 24px;">
                    <p style="margin:0 0 14px;font-size:14px;">%s</p>
                    <table style="font-size:14px;border-collapse:collapse;">
                      <tr><td style="padding:6px 14px 6px 0;color:#6B7280;">Applicant</td><td style="font-weight:700;">%s</td></tr>
                      <tr><td style="padding:6px 14px 6px 0;color:#6B7280;">Class applied</td><td style="font-weight:700;">%s</td></tr>
                      <tr><td style="padding:6px 14px 6px 0;color:#6B7280;">Status</td><td style="font-weight:700;">%s</td></tr>
                    </table>
                  </div>
                </div>
                """.formatted(esc(message), esc(applicantName), esc(applyClass), esc(statusLabel));

        String text = message + "\n\n"
                + "Applicant: " + applicantName + "\n"
                + "Class applied: " + applyClass + "\n"
                + "Status: " + statusLabel;

        return new Email(subject, html, text);
    }

    /**
     * Fee payment reminder sent to a student's email.
     *
     * @param studentName  the student's name
     * @param className    the student's class
     * @param academicYear academic year of the fee
     * @param dueAmount    outstanding amount (already formatted, e.g. "12,500.00")
     * @param dueDate      formatted due date, or null/blank if none
     */
    public static Email feeReminder(String studentName, String className, String academicYear,
                                    String dueAmount, String dueDate) {
        String subject = "Fee payment reminder — " + academicYear;

        String dueDateRow = (dueDate != null && !dueDate.isBlank())
                ? "<tr><td style=\"padding:6px 14px 6px 0;color:#6B7280;\">Due date</td>"
                    + "<td style=\"font-weight:700;\">" + esc(dueDate) + "</td></tr>"
                : "";

        String html = """
                <div style="font-family:Segoe UI,Arial,sans-serif;max-width:520px;margin:0 auto;color:#1E1B4B;">
                  <div style="background:#6C63FF;padding:20px 24px;border-radius:14px 14px 0 0;">
                    <h2 style="margin:0;color:#fff;font-size:19px;">Fee Payment Reminder</h2>
                  </div>
                  <div style="border:1px solid #DDE3F5;border-top:none;border-radius:0 0 14px 14px;padding:22px 24px;">
                    <p style="margin:0 0 14px;font-size:14px;">Dear %s, this is a gentle reminder that a fee payment is pending.</p>
                    <table style="font-size:14px;border-collapse:collapse;">
                      <tr><td style="padding:6px 14px 6px 0;color:#6B7280;">Class</td><td style="font-weight:700;">%s</td></tr>
                      <tr><td style="padding:6px 14px 6px 0;color:#6B7280;">Academic year</td><td style="font-weight:700;">%s</td></tr>
                      <tr><td style="padding:6px 14px 6px 0;color:#6B7280;">Amount due</td><td style="font-weight:700;color:#EF4444;">₹%s</td></tr>
                      %s
                    </table>
                    <p style="margin:18px 0 0;font-size:12px;color:#6B7280;">Please clear the outstanding amount at your earliest convenience. Ignore this message if payment was made recently.</p>
                  </div>
                </div>
                """.formatted(esc(studentName), esc(className), esc(academicYear), esc(dueAmount), dueDateRow);

        StringBuilder text = new StringBuilder()
                .append("Dear ").append(studentName).append(",\n\n")
                .append("This is a reminder that a fee payment is pending.\n\n")
                .append("Class: ").append(className).append("\n")
                .append("Academic year: ").append(academicYear).append("\n")
                .append("Amount due: INR ").append(dueAmount).append("\n");
        if (dueDate != null && !dueDate.isBlank()) {
            text.append("Due date: ").append(dueDate).append("\n");
        }
        text.append("\nPlease clear the outstanding amount at your earliest convenience.");

        return new Email(subject, html, text.toString());
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
