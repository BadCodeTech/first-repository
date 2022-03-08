package smallTask;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Metrics {
    private long missingFields = 0;
    private long blankContent = 0;
    private long fieldError = 0;
    private Map<Long, Long> callOriginDestination = new HashMap<>();
    private Map<Long, Long> averageCallDuration = new HashMap<>();
    private Map<String, Long> wordOccurrences = new HashMap<>();

    public void incBlankContent(){
        blankContent++;
    }

    public void incWordOccurrence(String word) {
        if (!wordOccurrences.containsKey(word)){
            wordOccurrences.put(word, 1l);
        } else {
            wordOccurrences.put(word, wordOccurrences.get(word)+1);
        }
    }
}
