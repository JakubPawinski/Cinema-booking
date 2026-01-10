package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dao.SalesDao;
import cinema.booking.cinemabooking.dto.report.DailySalesDto;
import cinema.booking.cinemabooking.dto.report.SalesReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Service for generating sales reports.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final SalesDao salesDao;

    /**
     * Retrieves the overall sales report.
     *
     * @return List of SalesReportDto containing sales data per movie.
     */
    @Transactional(readOnly = true)
    public List<SalesReportDto> getSalesReport() {
        log.info("Generating overall sales report");
        List<SalesReportDto> report = salesDao.fetchSalesReport();

        log.debug("Sales report generated with {} records", report.size());
        return report;
    }

    /**
     * Retrieves the daily sales report.
     *
     * @return List of DailySalesDto containing daily sales data.
     */
    @Transactional(readOnly = true)
    public List<DailySalesDto> getDailySalesReport() {
        log.info("Generating daily sales report");

        List<DailySalesDto> report = salesDao.fetchDailySales();

        log.debug("Daily sales report generated with {} records", report.size());
        return report;
    }
}
