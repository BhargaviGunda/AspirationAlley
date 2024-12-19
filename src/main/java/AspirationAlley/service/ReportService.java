package AspirationAlley.service;

import AspirationAlley.model.Post;
import AspirationAlley.model.Report;
import AspirationAlley.model.User;
import AspirationAlley.repository.CommentRepository;
import AspirationAlley.repository.PostRepository;
import AspirationAlley.repository.ReportRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;
    private PostRepository postRepository;
    private CommentRepository commentRepository;
    @Autowired
    public ReportService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }
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
    public void deleteReport(Long reportId) {
        // Fetch the report from the repository
        Optional<Report> reportOptional = reportRepository.findById(reportId);

        if (reportOptional.isPresent()) {
            // Get the Report object
            Report report = reportOptional.get();
            // Get the associated Post object
            Post post = report.getPost();

            // Delete the report first to avoid foreign key violations
            reportRepository.delete(report);

            // Check if the report is associated with a Post
            if (post != null) {
                // Get the post ID
                Long postId = post.getId();

                // Delete all comments associated with the post
                commentRepository.deleteByPostId(postId);

                // Delete the post after comments are deleted
                postRepository.delete(post);
            }
        } else {
            // Throw an exception if the report is not found
            throw new EntityNotFoundException("Report not found with id: " + reportId);
        }
    }


     // Get all reports by user
    public List<Report> getReportsByUserId(Long userId) {
        return reportRepository.findByUserId(userId);
    }
    public List<Report> getAllReports() {
        // Logic to fetch all reports
        return reportRepository.findAll();
    }
    // Save a report
    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }
    // Method to get the total number of reports
    public long getReportCount() {
        return reportRepository.count();
    }

   
}
