
package com.saicmotor.sc.myapplication.ui.home;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class TripData implements Parcelable, Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("totalMileage")
    @Expose
    private long totalMileage;
    @SerializedName("totalOil")
    @Expose
    private double totalOil;
    @SerializedName("vin")
    @Expose
    private String vin;
    @SerializedName("startTime")
    @Expose
    private Date startTime;
    @SerializedName("endTime")
    @Expose
    private Date endTime;
    @SerializedName("startLat")
    @Expose
    private double startLat;
    @SerializedName("startLong")
    @Expose
    private double startLong;
    @SerializedName("startPosition")
    @Expose
    private BasePosition startPosition;
    @SerializedName("endPosition")
    @Expose
    private BasePosition endPosition;
    @SerializedName("endLat")
    @Expose
    private double endLat;
    @SerializedName("endLong")
    @Expose
    private double endLong;
    @SerializedName("averageOil")
    @Expose
    private double averageOil;
    @SerializedName("averageSpeed")
    @Expose
    private double averageSpeed;
    @SerializedName("vehicleStatus")
    @Expose
    private int vehicleStatus;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("score")
    @Expose
    private int score;
    @SerializedName("speed0")
    @Expose
    private double speed0;
    @SerializedName("speed1_30")
    @Expose
    private double speed130;
    @SerializedName("speed31_60")
    @Expose
    private double speed3160;
    @SerializedName("speed61_80")
    @Expose
    private double speed6180;
    @SerializedName("speed81_100")
    @Expose
    private double speed81100;
    @SerializedName("speed100")
    @Expose
    private double speed100;
    @SerializedName("idlOil")
    @Expose
    private double idlOil;
    @SerializedName("idlStartTime")
    @Expose
    private long idlStartTime;
    @SerializedName("idlEndTime")
    @Expose
    private long idlEndTime;
    @SerializedName("fuelScore")
    @Expose
    private int fuelScore;
    @SerializedName("sameModelAverageFuel")
    @Expose
    private double sameModelAverageFuel;
    @SerializedName("historyAverageFuel")
    @Expose
    private double historyAverageFuel;
    @SerializedName("fuelSuggestion")
    @Expose
    private String fuelSuggestion;
    @SerializedName("analysisDrivingScore")
    @Expose
    private int analysisDrivingScore;
    @SerializedName("analysisDrivingSuggestion")
    @Expose
    private String analysisDrivingSuggestion;
    public final static Creator<TripData> CREATOR = new Creator<TripData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TripData createFromParcel(android.os.Parcel in) {
            return new TripData(in);
        }

        public TripData[] newArray(int size) {
            return (new TripData[size]);
        }

    };

    protected TripData(android.os.Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.totalMileage = ((long) in.readValue((long.class.getClassLoader())));
        this.totalOil = ((double) in.readValue((double.class.getClassLoader())));
        this.vin = ((String) in.readValue((String.class.getClassLoader())));
        this.startTime = new Date((long) in.readValue((long.class.getClassLoader())));
        this.endTime = new Date((long) in.readValue((long.class.getClassLoader())));
        this.startLat = ((double) in.readValue((double.class.getClassLoader())));
        this.startLong = ((double) in.readValue((double.class.getClassLoader())));
        this.startPosition = ((BasePosition) in.readValue((BasePosition.class.getClassLoader())));
        this.endPosition = ((BasePosition) in.readValue((BasePosition.class.getClassLoader())));
        this.endLat = ((double) in.readValue((double.class.getClassLoader())));
        this.endLong = ((double) in.readValue((double.class.getClassLoader())));
        this.averageOil = ((double) in.readValue((double.class.getClassLoader())));
        this.averageSpeed = ((double) in.readValue((double.class.getClassLoader())));
        this.vehicleStatus = ((int) in.readValue((int.class.getClassLoader())));
        this.status = ((int) in.readValue((int.class.getClassLoader())));
        this.score = ((int) in.readValue((int.class.getClassLoader())));
        this.speed0 = ((double) in.readValue((double.class.getClassLoader())));
        this.speed130 = ((double) in.readValue((double.class.getClassLoader())));
        this.speed3160 = ((double) in.readValue((double.class.getClassLoader())));
        this.speed6180 = ((double) in.readValue((double.class.getClassLoader())));
        this.speed81100 = ((double) in.readValue((double.class.getClassLoader())));
        this.speed100 = ((double) in.readValue((double.class.getClassLoader())));
        this.idlOil = ((double) in.readValue((double.class.getClassLoader())));
        this.idlStartTime = ((long) in.readValue((long.class.getClassLoader())));
        this.idlEndTime = ((long) in.readValue((long.class.getClassLoader())));
        this.fuelScore = ((int) in.readValue((int.class.getClassLoader())));
        this.sameModelAverageFuel = ((double) in.readValue((double.class.getClassLoader())));
        this.historyAverageFuel = ((double) in.readValue((double.class.getClassLoader())));
        this.fuelSuggestion = ((String) in.readValue((String.class.getClassLoader())));
        this.analysisDrivingScore = ((int) in.readValue((int.class.getClassLoader())));
        this.analysisDrivingSuggestion = ((String) in.readValue((String.class.getClassLoader())));
    }

    public TripData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(long totalMileage) {
        this.totalMileage = totalMileage;
    }

    public double getTotalOil() {
        return totalOil;
    }

    public void setTotalOil(double totalOil) {
        this.totalOil = totalOil;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLong() {
        return startLong;
    }

    public void setStartLong(double startLong) {
        this.startLong = startLong;
    }

    public BasePosition getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(BasePosition startPosition) {
        this.startPosition = startPosition;
    }

    public BasePosition getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(BasePosition endPosition) {
        this.endPosition = endPosition;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLong() {
        return endLong;
    }

    public void setEndLong(double endLong) {
        this.endLong = endLong;
    }

    public double getAverageOil() {
        return averageOil;
    }

    public void setAverageOil(double averageOil) {
        this.averageOil = averageOil;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public int getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(int vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public double getSpeed0() {
        return speed0;
    }

    public void setSpeed0(double speed0) {
        this.speed0 = speed0;
    }

    public double getSpeed130() {
        return speed130;
    }

    public void setSpeed130(double speed130) {
        this.speed130 = speed130;
    }

    public double getSpeed3160() {
        return speed3160;
    }

    public void setSpeed3160(double speed3160) {
        this.speed3160 = speed3160;
    }

    public double getSpeed6180() {
        return speed6180;
    }

    public void setSpeed6180(double speed6180) {
        this.speed6180 = speed6180;
    }

    public double getSpeed81100() {
        return speed81100;
    }

    public void setSpeed81100(double speed81100) {
        this.speed81100 = speed81100;
    }

    public double getSpeed100() {
        return speed100;
    }

    public void setSpeed100(double speed100) {
        this.speed100 = speed100;
    }

    public double getIdlOil() {
        return idlOil;
    }

    public void setIdlOil(double idlOil) {
        this.idlOil = idlOil;
    }

    public long getIdlStartTime() {
        return idlStartTime;
    }

    public void setIdlStartTime(long idlStartTime) {
        this.idlStartTime = idlStartTime;
    }

    public long getIdlEndTime() {
        return idlEndTime;
    }

    public void setIdlEndTime(long idlEndTime) {
        this.idlEndTime = idlEndTime;
    }

    public int getFuelScore() {
        return fuelScore;
    }

    public void setFuelScore(int fuelScore) {
        this.fuelScore = fuelScore;
    }

    public double getSameModelAverageFuel() {
        return sameModelAverageFuel;
    }

    public void setSameModelAverageFuel(double sameModelAverageFuel) {
        this.sameModelAverageFuel = sameModelAverageFuel;
    }

    public double getHistoryAverageFuel() {
        return historyAverageFuel;
    }

    public void setHistoryAverageFuel(double historyAverageFuel) {
        this.historyAverageFuel = historyAverageFuel;
    }

    public String getFuelSuggestion() {
        return fuelSuggestion;
    }

    public void setFuelSuggestion(String fuelSuggestion) {
        this.fuelSuggestion = fuelSuggestion;
    }

    public int getAnalysisDrivingScore() {
        return analysisDrivingScore;
    }

    public void setAnalysisDrivingScore(int analysisDrivingScore) {
        this.analysisDrivingScore = analysisDrivingScore;
    }

    public String getAnalysisDrivingSuggestion() {
        return analysisDrivingSuggestion;
    }

    public void setAnalysisDrivingSuggestion(String analysisDrivingSuggestion) {
        this.analysisDrivingSuggestion = analysisDrivingSuggestion;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(totalMileage);
        dest.writeValue(totalOil);
        dest.writeValue(vin);
        dest.writeValue(startTime == null ? 0L : startTime.getTime());
        dest.writeValue(endTime == null ? 0L : endTime.getTime());
        dest.writeValue(startLat);
        dest.writeValue(startLong);
        dest.writeValue(startPosition);
        dest.writeValue(endPosition);
        dest.writeValue(endLat);
        dest.writeValue(endLong);
        dest.writeValue(averageOil);
        dest.writeValue(averageSpeed);
        dest.writeValue(vehicleStatus);
        dest.writeValue(status);
        dest.writeValue(score);
        dest.writeValue(speed0);
        dest.writeValue(speed130);
        dest.writeValue(speed3160);
        dest.writeValue(speed6180);
        dest.writeValue(speed81100);
        dest.writeValue(speed100);
        dest.writeValue(idlOil);
        dest.writeValue(idlStartTime);
        dest.writeValue(idlEndTime);
        dest.writeValue(fuelScore);
        dest.writeValue(sameModelAverageFuel);
        dest.writeValue(historyAverageFuel);
        dest.writeValue(fuelSuggestion);
        dest.writeValue(analysisDrivingScore);
        dest.writeValue(analysisDrivingSuggestion);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "TripData{" +
                "id='" + id + '\'' +
                ", totalMileage=" + totalMileage +
                ", totalOil=" + totalOil +
                ", vin='" + vin + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", startLat=" + startLat +
                ", startLong=" + startLong +
                ", startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                ", endLat=" + endLat +
                ", endLong=" + endLong +
                ", averageOil=" + averageOil +
                ", averageSpeed=" + averageSpeed +
                ", vehicleStatus=" + vehicleStatus +
                ", status=" + status +
                ", score=" + score +
                ", speed0=" + speed0 +
                ", speed130=" + speed130 +
                ", speed3160=" + speed3160 +
                ", speed6180=" + speed6180 +
                ", speed81100=" + speed81100 +
                ", speed100=" + speed100 +
                ", idlOil=" + idlOil +
                ", idlStartTime=" + idlStartTime +
                ", idlEndTime=" + idlEndTime +
                ", fuelScore=" + fuelScore +
                ", sameModelAverageFuel=" + sameModelAverageFuel +
                ", historyAverageFuel=" + historyAverageFuel +
                ", fuelSuggestion='" + fuelSuggestion + '\'' +
                ", analysisDrivingScore=" + analysisDrivingScore +
                ", analysisDrivingSuggestion='" + analysisDrivingSuggestion + '\'' +
                '}';
    }

    public static class IdComparator implements java.util.Comparator<TripData> {
        @Override
        public int compare(TripData o1, TripData o2) {
            return o1.getVin().equals(o2.getVin()) == true ? 0 : 1;
        }
    }
}
