package com.fastbee.iot.model.ThingsModelItem;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;

public class Datatype
{
    /** 数据类型 */
    private String type;

    private String falseText;

    private String trueText;

    private BigDecimal min;

    private BigDecimal max;

    private BigDecimal step;

    private String unit;

    private String arrayType;

    private Integer arrayCount;

    private LinkedList<Integer> arrayIndex;

    private String showWay;

    private int maxLength;

    private List<EnumItem> enumList;

    private List<ThingsModelValueItem> params;

    private List<ThingsModel>[] arrayParams;

    private String parentType;

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentIdentifier() {
        return parentIdentifier;
    }

    public void setParentIdentifier(String parentIdentifier) {
        this.parentIdentifier = parentIdentifier;
    }

    private String parentName;
    private String parentIdentifier;

    public String getParentIndexName() {
        return parentIndexName;
    }

    public void setParentIndexName(String parentIndexName) {
        this.parentIndexName = parentIndexName;
    }

    private String parentIndexName;


    public Datatype(){
        falseText="";
        trueText="";
        min= BigDecimal.valueOf(0);
        max= BigDecimal.valueOf(100);
        step= BigDecimal.valueOf(1);
        unit="";
        arrayType="";
        arrayCount=0;
        maxLength=1024;
    }

    public LinkedList<Integer> getArrayIndex() {
        return arrayIndex;
    }

    public void setArrayIndex(LinkedList<Integer> arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    public List<ThingsModel>[] getArrayParams() {
        return arrayParams;
    }

    public void setArrayParams(List<ThingsModel>[] arrayParams) {
        this.arrayParams = arrayParams;
    }

    public List<ThingsModelValueItem> getParams() {
        return params;
    }

    public void setParams(List<ThingsModelValueItem> params) {
        this.params = params;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFalseText() {
        return falseText;
    }

    public void setFalseText(String falseText) {
        this.falseText = falseText;
    }

    public String getTrueText() {
        return trueText;
    }

    public void setTrueText(String trueText) {
        this.trueText = trueText;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public BigDecimal getStep() {
        return step;
    }

    public void setStep(BigDecimal step) {
        this.step = step;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getArrayType() {
        return arrayType;
    }

    public void setArrayType(String arrayType) {
        this.arrayType = arrayType;
    }

    public String getShowWay() {
        return showWay;
    }

    public void setShowWay(String showWay) {
        this.showWay = showWay;
    }

    public Integer getArrayCount() {
        return arrayCount;
    }

    public void setArrayCount(Integer arrayCount) {
        this.arrayCount = arrayCount;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public List<EnumItem> getEnumList() {
        return enumList;
    }

    public void setEnumList(List<EnumItem> enumList) {
        this.enumList = enumList;
    }

}
