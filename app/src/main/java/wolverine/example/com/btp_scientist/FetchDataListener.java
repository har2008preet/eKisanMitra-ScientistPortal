package wolverine.example.com.btp_scientist;

import java.util.List;

public interface FetchDataListener {
    public void onFetchComplete(List<Questions> data);
    public void onFetchFailure(String msg);
}
