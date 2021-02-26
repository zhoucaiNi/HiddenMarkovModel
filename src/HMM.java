import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Hidden Markov Model
 */
public class HMM {

    private boolean debug = true;
    private Map<String, Map<String, Double>> transitionP;
    private Map<String, Map<String, Double>> observationP;

    public HMM(){
        transitionP = new HashMap<>();
        observationP = new HashMap<>();
    };

    public static void training(String trainFile, String tagFile) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(trainFile));
        BufferedReader br2 = new BufferedReader(new FileReader(tagFile));

        // transition probability



        // observation probability

        br.close();
        br2.close();

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
        h.viterbi("This is a new sentence .");


    }



}
