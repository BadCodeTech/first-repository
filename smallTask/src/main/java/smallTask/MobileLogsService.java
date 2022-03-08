package smallTask;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class MobileLogsService {

    private final JsonMapper mapper = new JsonMapper();
    private List<LogModel> logsList = new ArrayList<>();
    String[] paths = new String[]{"https://github.com/TomasStesti/simpleTraining/blob/master/logs/MCP_20180131.json",
            "https://github.com/TomasStesti/simpleTraining/blob/master/logs/MCP_20180201.json",
            "https://github.com/TomasStesti/simpleTraining/blob/master/logs/MCP_20180202.json"};
    AtomicLong numberOfLoadsJsons = new AtomicLong(0);

    @Value("${mobileLogs.jsonPath}")
    private String jsonPath;

    @Value("${mobileLogs.watchedWords}")
    private String[] watchedWords;

/*    The service MUST implement the following requirements:
            - Java programming language has to be used.
            - Sourcecode has to compile and run.
            - A public GIT repository has to be used so its usage can be evaluated (https://github.com/ or similar)
            - The input JSON may have some errors (missing fields, wrong order, invalid value...)
            - The service will have an HTTP endpoint that receives a date parameter (YYYYMMDD). This method will be requested to select the JSON file to process. The URL to get the file will be https://github.com/TomasStesti/simpleTraining/tree/master/logs/MCP_YYYYMMDD.json
            - The service will have an HTTP endpoint (/metrics) that returns a set of counters related with the processed JSON file:*/

    @GetMapping(value = "metrics/{json}")
    public ResponseEntity<Metrics> metricsJson(@PathVariable String json) {

        long t0 = System.nanoTime();
        String pathJson = String.format(jsonPath, json);
        numberOfLoadsJsons.incrementAndGet();


        try (MappingIterator<LogModel> it = mapper.readerFor(LogModel.class)
                .readValues(new File(pathJson))) {
            while (it.hasNextValue()) {
                logsList.add(it.nextValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Number of rows with missing fields
        long missingFields = 0;
        for (LogModel fields : logsList) {
            if (fields.getMessage_type().isEmpty() || fields.getTimestamp() == 0 || fields.getOrigin() == 0 || fields.getDestination() == 0 || fields.getDuration() == 0 || fields.getStatus_code().isEmpty() || fields.getStatus_description().isEmpty())
                missingFields++;
        }
        Metrics metrics = new Metrics();
        for (LogModel logs : logsList) {
            //Number of messages with blank content

            if (logs.getMessage_type().equals("MSG") && StringUtils.hasText(logs.getMessage_content())) {
                metrics.incBlankContent();
            }
            //Number of rows with fields errors

            //Number of calls origin / destination grouped by country code (https://en.wikipedia.org/wiki/MSISDN)

           // loglistGroupedByOrigin.computeIfAbsent(model.getOrigin(), key -> new ArrayList<>()).add(model);

            // loglistGroupedByDestination.computeIfAbsent(logModel.getDestination(), k -> new ArrayList<>()).add(logModel);

            // Relationship between OK / KO calls

            // Average call duration grouped by country code (https://en.wikipedia.org/wiki/MSISDN)

            // Word occurrence ranking for the given words in message_content field.
            if (logs.getMessage_type().equals("MSG") && StringUtils.hasText(logs.getMessage_content())){
                for(String word : watchedWords){
                    if (logs.getMessage_content().contains(word)){
                        metrics.incWordOccurrence(word);
                    }
                }
            }

        }

        long t1 = System.nanoTime() - t0;

        return ResponseEntity.ok().body(new Metrics());
    }

  /*  // The service will have an HTTP endpoint (/kpis) that returns a set of counters related with the service:
    @GetMapping(value = "kpis")
    public String kpisJson() {
        long t0 = System.nanoTime();
        for (int i = 0; i < paths.length; i++) {
            String pathJson = getPaths()[i];
            numberOfLoadsJsons++;

            MobileLogsService jsonParse = new MobileLogsService();

            try (MappingIterator<LogModel> it = mapper.readerFor(LogModel.class)
                    .readValues(new File(pathJson))) {
                while (it.hasNextValue()) {
                    LogModel logs = it.nextValue();
                    String message_type = logs.message_type;
                    long timestamp = logs.timestamp;
                    long origin = logs.origin;
                    long destination = logs.destination;
                    long duration = logs.duration;
                    String status_code = logs.status_code;
                    String status_description = logs.status_description;
                    logsList.add(logs);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        // Total number of processed JSON files
        //= numberOfLoadsJsons!


        // Total number of rows
        long numberOfRows = logsList.size();

        // Total number of calls
        Map<String, List<LogModel>> logListNumberOfCalls =
                new HashMap<>();
        for (LogModel model : logsList) {
            if (model.getMessage_type().contains("CALL")) {
                logListNumberOfCalls.computeIfAbsent(model.getMessage_type(), k -> new ArrayList<>()).add(model);
            }
        }
        long numberOfCalls = logListNumberOfCalls.size();

        // Total number of messages
        Map<String, List<LogModel>> logListNumberOfMessages =
                new HashMap<>();
        for (LogModel model : logsList) {
            logListNumberOfCalls.computeIfAbsent(model.getMessage_type(), k -> new ArrayList<>()).add(model);
        }
        long numberOfMessages = logListNumberOfMessages.size();

        // Total number of different origin country codes (https://en.wikipedia.org/wiki/MSISDN)
        HashSet<Long> set = new HashSet<>();
        for (LogModel model : logsList) {
            set.add(model.getOrigin());
        }
        long numberOfOrigin = set.size();


        // Total number of different destination country codes (https://en.wikipedia.org/wiki/MSISDN)
        HashSet<Long> setOfDestinations = new HashSet<>();
        for (LogModel model : logsList) {
            setOfDestinations.add(model.getOrigin());
        }
        long numberOfDestination = setOfDestinations.size();

        // Duration of each JSON process
        long t1 = System.nanoTime();

        return "Total number of processed JSON files: " + numberOfLoadsJsons
        "Total number of calls: " + numberOfCalls
        "Total number of messages: " + numberOfMessages
        "Total number of different origin country codes: " + numberOfOrigin
        "Total number of different destination country codes: " + numberOfDestination
        "Duration of each JSON process: " + t1 + " nanoseconds."

    }
*/
}

