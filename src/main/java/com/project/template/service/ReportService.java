package com.project.template.service;

import javax.servlet.http.HttpServletResponse;

public interface ReportService {
    /**
     * 导出运营数据报表
     *
     */
    default void exportBusinessData(HttpServletResponse response) {

    }
}
