package com.toygoon.safeshare.data;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RiskReportDTO {
    public String title;
    public String summary;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:MM:ssXXX", timezone = "Asia/Seoul")
    public Date reportedAt;
    public Boolean isSolved;
    public LatLngDTO latlng;
    public RiskFactorDTO riskFactor;
    public String user;

    public RiskReportDTO(String title, String summary, Date reportedAt, Boolean isSolved, LatLngDTO latlng, RiskFactorDTO riskFactor, String user) {
        this.title = title;
        this.summary = summary;
        this.reportedAt = reportedAt;
        this.isSolved = isSolved;
        this.latlng = latlng;
        this.riskFactor = riskFactor;
        this.user = user;
    }

    public RiskReportDTO(JSONObject json) {
        String title, summary, reportedAt, riskName, user;
        Boolean isSolved;
        Integer riskLevel, riskImpact;
        Double lat, lng;

        try {
            title = json.getString("title");
            summary = json.getString("summary");
            reportedAt = json.getString("reported_at");
            isSolved = json.getBoolean("is_solved");

            JSONObject riskFactorObject = new JSONObject(json.getString("risk_factor"));
            riskName = riskFactorObject.getString("name");
            riskLevel = riskFactorObject.getInt("risk_level");
            riskImpact = riskFactorObject.getInt("risk_impact");

            JSONObject latLngObject = new JSONObject(json.getString("latlng"));
            lat = latLngObject.getDouble("lat");
            lng = latLngObject.getDouble("lng");

            user = json.getString("user");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:ssXXX");
        Date reportedAtDate;

        try {
            reportedAtDate = sdf.parse(reportedAt);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        this.title = title;
        this.summary = summary;
        this.reportedAt = reportedAtDate;
        this.isSolved = isSolved;
        this.latlng = new LatLngDTO(lat, lng);
        this.riskFactor = new RiskFactorDTO(riskName, riskLevel, riskImpact);
        this.user = user;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        sb.append(this.title);
        sb.append(", ");
        sb.append(this.summary);
        sb.append(", ");
        sb.append(reportedAt);
        sb.append(", ");
        sb.append(latlng);
        sb.append(", ");
        sb.append(riskFactor);
        sb.append(", ");
        sb.append(user);
        sb.append(") ");

        return sb.toString();
    }
}
