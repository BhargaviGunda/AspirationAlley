package AspirationAlley.service;

import AspirationAlley.model.Report;
import AspirationAlley.model.User;
import AspirationAlley.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    // Create a new report
    public Report createReport(User user, String content) {
        Report report = new Report(user, content);
        return reportRepository.save(report);
    }

    // Get all reports by status
    public List<Report> getReportsByStatus(Report.ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    // Get a specific report by its ID
    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    // Approve a report (change status to APPROVED)
    public Report approveReport(Long id) {
        Optional<Report> report = reportRepository.findById(id);
        if (report.isPresent()) {
            Report existingReport = report.get();
            existingReport.setStatus(Report.ReportStatus.APPROVED);
            return reportRepository.save(existingReport);
        }
        return null;
    }

    // Delete a report (change status to DELETED)
    public Report deleteReport(Long id) {
        Optional<Report> report = reportRepository.findById(id);
        if (report.isPresent()) {
            Report existingReport = report.get();
            existingReport.setStatus(Report.ReportStatus.DELETED);
            return reportRepository.save(existingReport);
        }
        return null;
    }

    // Get all reports by user
    public List<Report> getReportsByUserId(Long userId) {
        return reportRepository.findByUserId(userId);
    }
    public List<Report> getAllReports() {
        // Logic to fetch all reports
        return reportRepository.findAll();
    }
}
