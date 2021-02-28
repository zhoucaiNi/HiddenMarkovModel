import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Hidden Markov Model
 */
public class HMM {

    private boolean debug = false;

    private String start;

    private Map<String, Map<String, Double>> transitionP;


    private Map<String, Map<String, Double>> observationP;

    public HMM(){
        transitionP = new HashMap<>();
        observationP = new HashMap<>();
        start = "#";
    };

    public void training(String trainFile, String tagFile) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(trainFile));
        BufferedReader br2 = new BufferedReader(new FileReader(tagFile));

        // create a set of all the values
        String fileWords;
        String fileTags;
        while((fileWords= br.readLine()) != null && (fileTags = br2.readLine()) != null){
            String[] words = fileWords.split(" ");
            String[] tags = fileTags.split(" ");

            if(debug) System.out.println(Arrays.toString(words));
            if(debug) System.out.println(Arrays.toString(tags));

            String prevTag = start;

            if(words.length != tags.length) System.out.println("Sentences and tag does not match");
            else{
                for(int i =0; i< tags.length; i++){
                    if(!tags[i].equals(".")){
                        String currTag = tags[i];
                        String currWord = words[i];
                        // if observation map contains the tag -> get the nested map and increment the value.
                        if(observationP.containsKey(currTag)){
                            Map<String, Double> emission = observationP.get(currTag);
                            if(emission.containsKey(currWord)){
                                Double emissionValue = emission.get(currWord);
                                emission.put(currWord, emissionValue+1);
                            }
                            else{
                                emission.put(currWord, 1.0);
                            }
                            // else put a new key
                        }
                        else{
                            Map<String, Double> emission = new HashMap<>();
                            emission.put(currWord, 1.0);
                            observationP.put(currTag, emission);

                        }

                        if(transitionP.containsKey(prevTag)){
                            Map<String, Double> transmission = transitionP.get(prevTag);
                            if(transmission.containsKey(currTag)){
                                Double transmissionValue = transmission.get(currTag);
                                transmission.put(currTag, transmissionValue+1);
                            }
                            else{
                                transmission.put(currTag, 1.0);
                            }
                        }
                        else{
                            Map<String, Double> transmission = new HashMap<>();
                            transmission.put(currTag, 1.0);
                            transitionP.put(prevTag,transmission);
                        }
                        prevTag = currTag;


                    }
                }
            }
        }

        // calculate observation probability
        normalize(observationP);

        // calculate transition probability
        normalize(transitionP);



        br.close();
        br2.close();

    }

    public static double total(String key, Map<String, Map<String, Double>> map){
        Double total = 0.0;
        Map<String, Double> nestedMap = map.get(key);
        for(String s: nestedMap.keySet()){
            total+= nestedMap.get(s);
        }

        return total;

    }

    public static void normalize(Map<String, Map<String, Double>> map){
        for(String key: map.keySet()){
            double normalizeValue = total(key, map);
            Map<String, Double> currNestedMap = map.get(key);
            for(String key2 : currNestedMap.keySet()){
                currNestedMap.put(key2, currNestedMap.get(key2)/normalizeValue);
            }
        }
    }

    /**
     * Viterbi Algo
     *
     * @param filePath
     * @param testFilePath
     */
    public void viterbi(String filePath, String testFilePath){

    };

    public void viterbi(String line){


        Set<String> currStates = new HashSet<>();
        Map<String, Double> currScores;


        String[] s = line.split(" ");

        if(debug) System.out.println(Arrays.toString(s));


        for(int i =0; i< s.length -1; i++){
            String[] nextStates;
            Map<String, Double> nextScores = new HashMap<>();

            for(String currState: currStates){

            }

        }


    }

    public static void main(String [] args){
        HMM h = new HMM();
        try {
            h.viterbi("This is a new sentence .");
            h.training("inputs/simple-train-sentences.txt", "inputs/simple-train-tags.txt");
            System.out.println(h.observationP);
            System.out.println(h.transitionP);
        }
        catch( Exception e){
            System.out.println(e);
            System.out.println("Pleaser insert a valid file name");
        }


    }



}
