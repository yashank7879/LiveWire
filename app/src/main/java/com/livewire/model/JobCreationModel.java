package com.livewire.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mindiii on 10/1/18.
 */

public class JobCreationModel {
    public String skill = "";
    @SerializedName("job_start_date")
    public String job_start_date = "";
    @SerializedName("job_end_date")
    public String job_end_date = "";
    @SerializedName("job_budget")
    public String job_budget = "";
    @SerializedName("job_title")
    public String job_title = "";
    @SerializedName("job_type")
    public String job_type = "";
    @SerializedName("job_week_days")
    public String job_week_days = "";
    @SerializedName("job_time_duration")
    public String job_time_duration = "";
    @SerializedName("job_location")
    public String job_location = "";
    @SerializedName("job_latitude")
    public String job_latitude = "";
    @SerializedName("job_longitude")
    public String job_longitude = "";
    @SerializedName("job_description")
    public String job_description = "";
}
