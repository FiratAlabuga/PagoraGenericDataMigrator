package com.pagora.migrator.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class TableMetadata {
    private String tableName;
    private List<ColumnMetadata> columns = new ArrayList<>();
}