package gov.ihd.apiservice.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Custom JsonFileItemReader to read JSON files line by line for batch processing
 */
public class JsonFileItemReader implements ItemReader<FeedbackBatchItem> {

    private final File file;
    private final ObjectMapper objectMapper;
    private BufferedReader reader;
    private Iterator<FeedbackBatchItem> feedbackIterator;
    
    public JsonFileItemReader(File file, ObjectMapper objectMapper) {
        this.file = file;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public FeedbackBatchItem read() throws Exception {
        if (reader == null) {
            initializeReader();
        }
        
        if (feedbackIterator != null && feedbackIterator.hasNext()) {
            return feedbackIterator.next();
        }
        
        String line = reader.readLine();
        if (line == null) {
            // End of file
            closeReader();
            return null;
        }
        
        try {
            // For single item per line
            return objectMapper.readValue(line, FeedbackBatchItem.class);
        } catch (Exception e) {
            // Try parsing as array if single item fails
            try {
                List<FeedbackBatchItem> items = objectMapper.readValue(
                    line, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, FeedbackBatchItem.class)
                );
                
                if (items.isEmpty()) {
                    // Empty array, read next line
                    return read();
                }
                
                feedbackIterator = items.iterator();
                return feedbackIterator.next();
            } catch (Exception arrayEx) {
                throw new ParseException("Failed to parse JSON content", arrayEx);
            }
        }
    }
    
    private void initializeReader() throws IOException {
        reader = new BufferedReader(new FileReader(file));
    }
    
    private void closeReader() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // Log but don't throw
                System.err.println("Error closing reader: " + e.getMessage());
            }
        }
    }
}
