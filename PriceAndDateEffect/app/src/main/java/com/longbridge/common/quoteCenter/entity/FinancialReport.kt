package com.longbridge.common.quoteCenter.entity

data class FinancialReport(
    val list: List<ReportItem>,
    val total: String
)

data class ReportItem(
    val act_desc: String,
    val act_title: String,
    val action: String,
    val chart_uid: String,
    val currency: String,
    val data_kv: List<DataKV>,
    val date: String,
    val date_str: String,
    val date_type: String,
    val date_zone: String,
    val icon: String,
    val id: String,
    val region: String,
    val star: Int,
    val timestamp: String,
    val url: String
)

data class DataKV(
    val key: String,
    val type: String,
    val value: String
)