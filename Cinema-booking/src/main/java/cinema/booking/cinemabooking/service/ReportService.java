package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dao.SalesDao;
import cinema.booking.cinemabooking.dto.report.DailySalesDto;
import cinema.booking.cinemabooking.dto.report.SalesReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final SalesDao salesDao;

    public List<SalesReportDto> getSalesReport() {
        return salesDao.fetchSalesReport();
    }

    public List<DailySalesDto> getDailySalesReport() {
        return salesDao.fetchDailySales();
    }
}
