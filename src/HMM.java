import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Hidden Markov Model
 */
public class HMM {

    private boolean debug = false;

    private double unseenWord = -1000;

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
                currNestedMap.put(key2, Math.log10(currNestedMap.get(key2)/normalizeValue));
            }
        }
    }

    /**
     * Viterbi Algo
     *
     * @param testFile
     * @param testTagFile
     */
    public void viterbi(String testFile, String testTagFile) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(testFile));
        BufferedReader br2 = new BufferedReader(new FileReader(testTagFile));

        String sentence;

        while((sentence = br.readLine())!= null){
            viterbi(sentence);
        }


    };

    public void viterbi(String line){

        // current states and current scores
        Set<String> currStates = new HashSet<>();
        Map<String, Double> currScores = new HashMap<>();

        // keeps track of back pointers.  Each word should have a map
        List<Map<String, String>> backTrace = new ArrayList<Map<String, String>>();

        // add start and it's value
        currStates.add(start);
        currScores.put(start, 0.0);

        // split the line into array
        String[] s = line.split(" ");


        // for every single word in line
        for(int i =0; i< s.length; i++){
            // for word that's not ". "
            if(!s[i].equals(".")){

                Set<String> nextStates = new HashSet<>();
                Map<String, Double> nextScores = new HashMap<>();

                // for all the states in current states
                for(String state : currStates){

                    // if the HMM contains the state
                    if(transitionP.containsKey(state)){
                        // for all the possible next states in the HMM
                        for(String nextState: transitionP.get(state).keySet()){

                            // add the state to next state
                            nextStates.add(nextState);

                            double nextScore;

                            // if observation contains current word add the observation p value
                            if(observationP.get(nextState).containsKey(s[i]) && observationP.get(nextState) != null){
                                nextScore = ( currScores.get(state) + transitionP.get(state).get(nextState) + observationP.get(nextState).get(s[i]));
                            }
                            // else add the unseen word penalty value
                            else {

                                nextScore = ( currScores.get(state) + transitionP.get(state).get(nextState) + unseenWord);
                            }

                            // if the nextScores does not contain nextState or that nextScore is greater than current score
                            if(!nextScores.containsKey(nextState) || nextScore > nextScores.get(nextState)){
                                nextScores.put(nextState, nextScore);

                                // back trace
                                if(backTrace.size() == i){
                                    Map<String, String> currMap = new HashMap<String, String>();
                                    currMap.put(nextState, state);
                                    backTrace.add(currMap);
                                }
                                else{
                                    backTrace.get(i).put(nextState, state);
                                }
                            }

                        }
                    }

                }
                // update currStates and currScores
                currStates = nextStates;
                currScores = nextScores;
            }

        }
        // print back pointer

        // get the highest value
        double max = -Double.MAX_VALUE;
        String backPointer = "";
        for(String key: currScores.keySet()){
            if(currScores.get(key) > max){
                max = currScores.get(key);
                backPointer = key;
            }
        }


        // use the backTrace to return the tags in order
        List<String> path = new LinkedList<>();
        for(int i = backTrace.size()-1; i>-1; i--){
//            System.out.println(backPointer);
            path.add(0,backPointer);
            backPointer = backTrace.get(i).get(backPointer);
        }

        System.out.println(path);




    }

    public static void main(String [] args) throws Exception{
        HMM h = new HMM();
        try {

            h.training("inputs/simple-train-sentences.txt", "inputs/simple-train-tags.txt");
//            h.training("inputs/cs10-train-sentences.txt", "inputs/cs10-train-tags.txt");
            h.viterbi("inputs/simple-test-sentences.txt", "inputs/simple-test-tags.txt");
//            h.viterbi("will eats the fish .");

        }
        catch( FileNotFoundException e){
            System.out.println("Pleaser insert a valid file name");
        }



    }



}
