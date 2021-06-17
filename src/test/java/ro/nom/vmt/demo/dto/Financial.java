package ro.nom.vmt.demo.dto;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import ro.nom.vmt.spreadsheet_importer.annotation.Import;
import ro.nom.vmt.spreadsheet_importer.annotation.Importable;
import ro.nom.vmt.spreadsheet_importer.annotation.Ordinal;
import ro.nom.vmt.spreadsheet_importer.implementations.DefaultEnumProcessor;

import java.time.LocalDate;
import java.time.Month;

@Importable
public class Financial {

    @Ordinal(0)
    @Import(preProcess = DefaultEnumProcessor.class, required = true)
    private Segment segment;

    @Ordinal(1)
    @Import
    private String country;

    @Ordinal(2)
    @Import(matches = "[a-zA-Z]+")
    private String Product;

    @Ordinal(3)
    @Import(preProcess = DefaultEnumProcessor.class)
    private DiscountBand discountBand;

    @Ordinal(4)
    @Import
    private Integer unitsSold;

    @Ordinal(5)
    @Import(matches = "[0-9]+")
    private Float manufacturingPrice;

    @Ordinal(6)
    @Import
    private Float salePrice;

    @Ordinal(7)
    @Import
    private Float grossSales;

    @Ordinal(8)
    @Import
    private Float discounts;

    @Ordinal(9)
    @Import
    private Float sales;

    @Ordinal(10)
    @Import
    private Float cogs;

    @Ordinal(11)
    @Import
    private Float profit;

    @Ordinal(12)
    @Import
    private LocalDate localDate;

    @Ordinal(13)
    @Import
    private Byte monthNumber;

    @Ordinal(14)
    @Import(preProcess = DefaultEnumProcessor.class)
    private Month monthName; //it's not even our enum

    @Ordinal(15)
    @Import
    private Short year;

    public Segment getSegment() {
        return segment;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }

    public DiscountBand getDiscountBand() {
        return discountBand;
    }

    public void setDiscountBand(DiscountBand discountBand) {
        this.discountBand = discountBand;
    }

    public Integer getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(Integer unitsSold) {
        this.unitsSold = unitsSold;
    }

    public Float getManufacturingPrice() {
        return manufacturingPrice;
    }

    public void setManufacturingPrice(Float manufacturingPrice) {
        this.manufacturingPrice = manufacturingPrice;
    }

    public Float getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Float salePrice) {
        this.salePrice = salePrice;
    }

    public Float getGrossSales() {
        return grossSales;
    }

    public void setGrossSales(Float grossSales) {
        this.grossSales = grossSales;
    }

    public Float getDiscounts() {
        return discounts;
    }

    public void setDiscounts(Float discounts) {
        this.discounts = discounts;
    }

    public Float getSales() {
        return sales;
    }

    public void setSales(Float sales) {
        this.sales = sales;
    }

    public Float getCogs() {
        return cogs;
    }

    public void setCogs(Float cogs) {
        this.cogs = cogs;
    }

    public Float getProfit() {
        return profit;
    }

    public void setProfit(Float profit) {
        this.profit = profit;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public Byte getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(Byte monthNumber) {
        this.monthNumber = monthNumber;
    }

    public Month getMonthName() {
        return monthName;
    }

    public void setMonthName(Month monthName) {
        this.monthName = monthName;
    }

    public Short getYear() {
        return year;
    }

    public void setYear(Short year) {
        this.year = year;
    }


    public enum Segment {
        CHANNEL_PARTNERS,
        ENTERPRISE,
        GOVERNMENT,
        MIDMARKET,
        SMALL_BUSINESS
    }

    public enum DiscountBand {
        HIGH,
        LOW,
        MEDIUM,
        NONE
    }
}
