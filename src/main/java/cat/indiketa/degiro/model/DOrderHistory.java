package cat.indiketa.degiro.model;

import java.util.List;

import lombok.Data;

@Data
public class DOrderHistory {
    private List<DOrderHistoryRecord> data;
}
