package smallTask;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogModel {

    public String message_type;
    public long timestamp;
    public long origin;
    public long destination;
    public long duration;
    public String status_code;
    public String status_description;
    public String message_content;



    }



