import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Fifteen {

    public static void main(String[] args) {
        WordFrequencyFramework wfApp = new WordFrequencyFramework();
        StopWordFilter stopWordFilter = new StopWordFilter(wfApp);
        DataStorage dataStorage = new DataStorage(wfApp, stopWordFilter);
        WordFrequencyCounter wordFreqCounter = new WordFrequencyCounter(wfApp, dataStorage);

        wfApp.run(args[0]);
    }

    // Framework
    static class WordFrequencyFramework {
        private final List<Consumer<String>> loadEventHandlers = new ArrayList<>();
        private final List<Runnable> doWorkEventHandlers = new ArrayList<>();
        private final List<Runnable> endEventHandlers = new ArrayList<>();

        public void registerForLoadEvent(Consumer<String> handler) {
            loadEventHandlers.add(handler);
        }

        public void registerForDoWorkEvent(Runnable handler) {
            doWorkEventHandlers.add(handler);
        }

        public void registerForEndEvent(Runnable handler) {
            endEventHandlers.add(handler);
        }

        public void run(String pathToFile) {
            for (Consumer<String> handler : loadEventHandlers) {
                handler.accept(pathToFile);
            }
            for (Runnable handler : doWorkEventHandlers) {
                handler.run();
            }
            for (Runnable handler : endEventHandlers) {
                handler.run();
            }
        }
    }

    // Stop Word Filter
    static class StopWordFilter {
        private final Set<String> stopWords = new HashSet<>();

        public StopWordFilter(WordFrequencyFramework app) {
            app.registerForLoadEvent(this::load);
        }

        private void load(String ignored) {
            try {
                String[] words = Files.readString(Paths.get("stopwords.txt")).split(",");
                stopWords.addAll(Arrays.asList(words));
                for (char ch = 'a'; ch <= 'z'; ch++) {
                    stopWords.add(String.valueOf(ch));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean isStopWord(String word) {
            return stopWords.contains(word);
        }
    }

    // Data Storage
    static class DataStorage {
        private String data = "";
        private final StopWordFilter stopWordFilter;
        private final List<Consumer<String>> wordEventHandlers = new ArrayList<>();

        public DataStorage(WordFrequencyFramework app, StopWordFilter stopWordFilter) {
            this.stopWordFilter = stopWordFilter;
            app.registerForLoadEvent(this::load);
            app.registerForDoWorkEvent(this::produceWords);
        }

        private void load(String pathToFile) {
            try {
                data = Files.readString(Paths.get(pathToFile));
                data = Pattern.compile("[\\W_]+").matcher(data).replaceAll(" ").toLowerCase();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void produceWords() {
            for (String word : data.split("\\s+")) {
                if (!stopWordFilter.isStopWord(word)) {
                    for (Consumer<String> handler : wordEventHandlers) {
                        handler.accept(word);
                    }
                }
            }
        }

        public void registerForWordEvent(Consumer<String> handler) {
            wordEventHandlers.add(handler);
        }
    }

    // Word Frequency Counter
    static class WordFrequencyCounter {
        private final Map<String, Integer> wordFreqs = new HashMap<>();

        public WordFrequencyCounter(WordFrequencyFramework app, DataStorage storage) {
            storage.registerForWordEvent(this::incrementCount);
            app.registerForEndEvent(this::printFreqs);
        }

        private void incrementCount(String word) {
            wordFreqs.put(word, wordFreqs.getOrDefault(word, 0) + 1);
        }

        private void printFreqs() {
            wordFreqs.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(25)
                    .forEach(entry ->
                            System.out.println(entry.getKey() + " - " + entry.getValue()));
        }
    }
}
