package wolverine.example.com.btp_scientist;

/**
 * Created by Wolverine on 28-06-2015.
 */

public class Questions {

    private String ques;
    private long number;
    private String date;

    /*public Questions(String ques,long number, String date) {
        super();
        this.ques = ques;
        this.number = number;
        this.date = date;
    }*/

    public void setQues(String ques) { this.ques = ques; }

    public void setNumber(long number) {
        this.number = number;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQues() {
        return ques;
    }

    public long getNumber() {
        return number;
    }

    public String getDate() {
        return date;
    }

}
