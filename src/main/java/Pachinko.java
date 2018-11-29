import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;



public class Pachinko {

    public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException{

        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequenceRemoveStopwords(false));
        pipeList.add( new TokenSequence2FeatureSequence() );

        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

        Reader fileReader = new InputStreamReader(new FileInputStream("ohsumed.91"), "UTF-8");
        instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                3, 2, 1)); // data, label, name fields

        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is the parameter for a single dimension of the Dirichlet prior.
        int superTopics = 20;
        int subTopics = 60;
        PAM4L model = new PAM4L(superTopics, subTopics, 0.5/(80), 0.01);
        int numIterations = 1000;
        String output = new String("pamOutput.txt");
        Randoms rand = new Randoms(4);
        model.estimate(instances, numIterations, 10, 50, 50, output, rand);
        model.printWordCounts();
        model.printDocumentTopics(new File("pamDocTopics.txt"));

    }
}
