/**
 * Created with IntelliJ IDEA.
 * User: seniz
 * Date: 11/11/16
 * Time: 9:04 AM
 * To change this template use File | Settings | File Templates.
 */

package TRPipeline.LangTools;

import TRPipeline.Words.WordDetails;
import is2.data.InstancesTagger;
import is2.data.SentenceData09;
import is2.io.CONLLReader09;
import is2.lemmatizer.Lemmatizer;
import is2.parser.MFO;
import is2.parser.Parser;
import is2.tag.Tagger;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DependencyParser {


   Lemmatizer lemmatizer;
   Tagger tagger;
   is2.mtag.Tagger mtagger;
   Parser parser;
   CONLLReader09 reader;
   Path pre_train_data_path,train_data_path,lemma_model_path,tagger_model_path,morph_tagger_model_path,parser_model_path,dependency_jar_path;


   public DependencyParser(Path dep_path, Path depen_jar_path){

      dependency_jar_path=depen_jar_path;
      pre_train_data_path=Paths.get(dep_path+"/turkishtreebank06.conll");
      train_data_path=Paths.get(dep_path+"/turkishtreebank09.conll");
      lemma_model_path=Paths.get(dep_path+"/lemma.mdl");
      tagger_model_path=Paths.get(dep_path+"/tagger.mdl");
      morph_tagger_model_path=Paths.get(dep_path+"/morph_tagger.mdl");
      parser_model_path=Paths.get(dep_path+"/parser.mdl");

      ReadModels(lemma_model_path,tagger_model_path,morph_tagger_model_path,parser_model_path);
   }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * reads required models
     *
     */
    protected void ReadModels(Path lemma_model,Path tagger_model,Path morphtagger_model, Path parser_model){

        //lemmatizer aracı
        lemmatizer = new Lemmatizer(lemma_model.toString());

        //tagger aracı
        tagger = new Tagger(tagger_model.toString());

        //morphology tagger aracı
        mtagger = new is2.mtag.Tagger(morphtagger_model.toString());

        //parser aracı
        parser = new Parser(parser_model.toString());

        //dosya okuyucu
        reader = new CONLLReader09(CONLLReader09.NO_NORMALIZE);
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * trains models from data
     *
     */
    protected void TrainModels(){

        //NOTE: If the training file is in Conll 2006 format, it should be converted into Conll2009 format first. Running it once is adequate
        //Turkish_parser.ConvertTreeBank09Conll(pre_train_data_path,train_data_path);

        System.out.println("Lemmatizer Model:");
        TrainLemmatizer();

        System.out.println("Tagger Model:");
        TrainTagger();

        System.out.println("Morph Tagger Model:");
        TrainMorphTagger();

        System.out.println("Parser Model:");
        TrainParser();

    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * trains the lemmatizer
     *
     */
    protected void TrainLemmatizer(){

        try {

            int returnValue=-1;
            String line=null;
            BufferedReader input;
            Process p;

            Runtime rt = Runtime.getRuntime();
            p = rt.exec("java -cp "+dependency_jar_path.toString()+"anna-3.61.jar is2.lemmatizer.Lemmatizer -train "+train_data_path+" -model "+ lemma_model_path);

            input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while((line=input.readLine()) != null) {
                System.out.println(line);
            }
            returnValue=p.waitFor();
            System.out.println("return value "+returnValue);

        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * trains the tagger
     *
     */
    protected void TrainTagger(){

        try {

            int returnValue=-1;
            String line;
            BufferedReader input;
            Process p;

            Runtime rt = Runtime.getRuntime();
            p = rt.exec("java -cp "+dependency_jar_path.toString()+"anna-3.61.jar is2.tag.Tagger -train "+train_data_path+" -model "+ tagger_model_path);

            input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while((line=input.readLine()) != null) {
                System.out.println(line);
            }
            returnValue=p.waitFor();
            System.out.println("return value "+returnValue);

        }catch (IOException| InterruptedException e) {
            e.printStackTrace();
        }

    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * trains the morph tagger
     *
     */
    protected void TrainMorphTagger(){

        try {

            int returnValue=-1;
            String line;
            BufferedReader input;
            Process p;

            Runtime rt = Runtime.getRuntime();
            p = rt.exec("java -cp  "+dependency_jar_path.toString()+"anna-3.61.jar is2.mtag.Tagger -train "+train_data_path+" -model "+ morph_tagger_model_path);

            input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while((line=input.readLine()) != null) {
                System.out.println(line);
            }
            returnValue=p.waitFor();
            System.out.println("return value "+returnValue);

        }catch (IOException| InterruptedException e) {
            e.printStackTrace();
        }

    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * trains the parser
     *
     */
    protected void TrainParser(){

        try {

            int returnValue=-1;
            String line=null;
            BufferedReader input;
            Process p;

            Runtime rt = Runtime.getRuntime();
            p = rt.exec("java -cp  "+dependency_jar_path.toString()+"anna-3.61.jar is2.parser.Parser -train "+train_data_path+" -model "+ parser_model_path);

            input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while((line=input.readLine()) != null) {
                System.out.println(line);
            }
            returnValue=p.waitFor();
            System.out.println("return value "+returnValue);

        }catch (IOException| InterruptedException e) {
            e.printStackTrace();
        }

    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * converts training data in Conll2006 format into Conll2009 format
     *
     */
    protected void ConvertTreeBank09Conll(String input_file_name, String output_file_name) {

        BufferedReader input_file;
        FileWriter output_file;
        String word_desc;
        String[] word_desc_parts;
        int i;

        try {

            input_file = Files.newBufferedReader(Paths.get(input_file_name), UTF_8);
            output_file = new FileWriter(output_file_name);

            while ((word_desc = input_file.readLine()) != null) {

                //cümle sonunu belirten boş satır yazdırılıp sonraki cümleye atlanıyor
                if (word_desc.compareTo("") == 0) {
                    output_file.write("\n");
                    continue;
                }

                word_desc_parts = word_desc.split("\t");
                for (i = 0; i < 10; i++) {
                    output_file.write(word_desc_parts[i] + "\t");
                    if ((i == 2) || (i == 5) || (i == 6) || (i == 7))
                        output_file.write(word_desc_parts[i] + "\t");
                }
                output_file.write("\n");
            }

            input_file.close();
            output_file.flush();
            output_file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //parses the input sentence
    public SentenceData09 ParseSentence(List<WordDetails> sentence_words) {

        int index;
        SentenceData09 result;
        String [] features;

        //Prepare the sentence
        InstancesTagger instanceTagger = new InstancesTagger();
        instanceTagger.init(1, new MFO());
        String[] split = new String[sentence_words.size()];
        for(int i=0;i<sentence_words.size();i++)
            split[i]=sentence_words.get(i).GetSurfaceForm();

        String[] splitRoot = new String[split.length + 1];
        System.arraycopy(split, 0, splitRoot, 1, split.length);
        splitRoot[0] = CONLLReader09.ROOT;
        SentenceData09 instance = new SentenceData09();
        instance.init(splitRoot);

        reader.insert(instanceTagger, instance);

        result = lemmatizer.apply(instance);
        index=0;
        for (String f : result.plemmas) {
            if((f.compareTo("_")==0) && (index<sentence_words.size()) && (sentence_words.get(index).GetLemma().compareTo("")!=0))
                result.plemmas[index]=sentence_words.get(index).GetLemma();
            index++;
        }


        result=mtagger.apply(result);
        index=0;
        for (String f : result.pfeats) {
            if ((f.compareTo("_")==0) && (sentence_words.get(index).GetMorpFeats()!=null))
                result.pfeats[index]=sentence_words.get(index).GetMorpFeats();

            else if ((f.compareTo("_")!=0) && (sentence_words.get(index).GetMorpFeats()!=null)){
                features=sentence_words.get(index).GetMorpFeats().split("\\|");
                for(String feat:features)
                    if(!f.contains(feat)) f=f+"|"+feat;
                result.pfeats[index]=f;
            }
            index++;
        }
        result= tagger.apply(result);

        index=0;
        for (String f : result.ppos) {
            if((sentence_words.get(index).GetNerTag().compareTo("none")!=0) && (f.compareTo("Noun")==0))
                result.ppos[index]="Prop";
            index++;
        }

        result=parser.apply(result);

        return result;
    }
}
