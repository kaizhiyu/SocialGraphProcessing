package api.nlp.stanfordCoreNLP;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import java.util.Properties;

public class StanfordCoreNLPAPI {

    static StanfordCoreNLP pipeline;

    public StanfordCoreNLPAPI() {
        Properties properties = new Properties();
        properties.put("annotators", "tokenize, ssplit, parse, sentiment");
        pipeline = new StanfordCoreNLP(properties);
    }

    public int findSentiment(String text) {
        if(text.length()>=120){
            text = text.substring(0, 120);
        }        
        
        int mainSentiment = 0;
        int numSentences = 0;

        if (text != null && text.length() > 0) {
            Annotation annotation = pipeline.process(text);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentAnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                mainSentiment += sentiment;
                numSentences++;
            }
        }
        if (numSentences != 0) {
            mainSentiment = mainSentiment / numSentences;
        }

        return mainSentiment;
    }
}
