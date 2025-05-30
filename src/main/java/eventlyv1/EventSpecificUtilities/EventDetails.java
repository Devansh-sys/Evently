package eventlyv1.EventSpecificUtilities;

public class EventDetails {

    private String description;
    private String summary;
    private String startDateTime ;
    private String endDateTime ;


    public String getDescription() {
        return description;
    }


    public String getSummary() {
        return summary;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartDateTime() {
        return startDateTime;
    }



    public String getEndDateTime() {
        return endDateTime;
    }



    public String getLocation() {
        return location;
    }



    public String location;
}
