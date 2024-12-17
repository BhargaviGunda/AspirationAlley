package AspirationAlley.repository;

import AspirationAlley.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // Find all reports with a specific status
    List<Report> findByStatus(Report.ReportStatus status);

    // Find reports by user
    List<Report> findByUserId(Long userId);
    List<Report> findAll();

}
