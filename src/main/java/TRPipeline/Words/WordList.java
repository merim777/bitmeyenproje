package TRPipeline.Words;

import TRPipeline.LangTools.Tools;
import is2.data.SentenceData09;
import tr.gov.tubitak.bilgem.bte.akbis.cekirdek.structure.Word;

import java.io.BufferedWriter;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by seniz on 3.6.2019.
 */
public class WordList {

    ArrayList<WordDetails> wordList;
    Tools lang_tools;


    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * Creates an empty words list
     *
     */
    public WordList(Path tools_dir_path) {

       lang_tools=new Tools(tools_dir_path);
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //fills the features of all words in the words list
     public void CreateWordList (String input_sentence, String out_format, BufferedWriter tagged_file, BufferedWriter taggedsenik_file){

        input_sentence=lang_tools.CleanLines2(input_sentence);
        FillWordLists(input_sentence);
        POSTagging();
        ParserTagging(taggedsenik_file);
        PrintWordLists(out_format, tagged_file);
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //creates an empty words list
    protected void FillWordLists(String input_sentence) {

        WordDetails wordd;
        String[] snt_words;
        int counter=0;

        wordList=new ArrayList<>();

        snt_words = input_sentence.split(" "); //the sentence is splitted into words

            for (int i = 0; i < snt_words.length; i++) {
                wordd = new WordDetails();
                wordd.surface_form = snt_words[i];
                //System.out.println("gelenler "+snt_words[i]);
                wordd.nertag="none";
                wordd.postag="mone";
                wordd.morp_feats="none";
                wordd.lemma="none";
                wordd.root="none";
                wordd.deptag="none";
                wordd.dephead="none";
                wordd.depindex=-1;
                wordList.add(counter++, wordd);
            }
        }



    //~ ----------------------------------------------------------------------------------------------------------------
    //prints details of all words in the list
    private void PrintWordLists(String out_format,BufferedWriter f) {

        WordDetails wordd;

        try{
        if(out_format.compareTo("singlelinetagged")==0) {
            for (int i = 0; i < wordList.size(); i++) {
                wordd = wordList.get(i);
                f.write(wordd.surface_form + "-POS:" + wordd.postag + ",NER:" + wordd.nertag.toLowerCase() + ",DEPRel:" + wordd.deptag.toLowerCase() + ",DEPHead:" + wordd.dephead + ",Lemma:" + wordd.lemma + ",Root:" + wordd.root + ",MorpFeats:" + wordd.morp_feats+"  ");
            }
        }
        else if(out_format.compareTo("singleline")==0) {
            for (int i = 0; i < wordList.size(); i++) {
                wordd = wordList.get(i);
                f.write(wordd.surface_form + "-" + wordd.postag + "," + wordd.nertag.toLowerCase() + "," + wordd.deptag.toLowerCase() + "," + wordd.dephead + "," + wordd.lemma + "," + wordd.root + ","+ wordd.morp_feats+"  ");
            }
        }
        else if(out_format.compareTo("multilinetagged")==0) {
            for (int i = 0; i < wordList.size(); i++) {
                wordd = wordList.get(i);
                f.write(wordd.surface_form + "-POS:" + wordd.postag + ",NER:" + wordd.nertag.toLowerCase() + ",DEPRel:" + wordd.deptag.toLowerCase() + ",DEPHead:" + wordd.dephead + ",Lemma:" + wordd.lemma + ",Root:" + wordd.root+ ",MorpFeats:" + wordd.morp_feats+"\n");
            }
        }
        else if(out_format.compareTo("multiline")==0) {
            for (int i = 0; i < wordList.size(); i++) {
                wordd = wordList.get(i);
                f.write(wordd.surface_form + "-" + wordd.postag + "," + wordd.nertag.toLowerCase() + "," + wordd.deptag.toLowerCase() + "," + wordd.dephead + "," + wordd.lemma + "," + wordd.root + "," + wordd.morp_feats+"\n");
            }
        }
        else if(out_format.compareTo("justner")==0) {
            for (int i = 0; i < wordList.size(); i++) {
                wordd = wordList.get(i);
                f.write(wordd.surface_form + "-" + wordd.nertag.toLowerCase()+" ");
            }
        }
        else if(out_format.compareTo("justmorphmultiple")==0) {
            for (int i = 0; i < wordList.size(); i++) {
                wordd = wordList.get(i);
                f.write(wordd.surface_form + "-POS:" + wordd.postag + ",Lemma:" + wordd.lemma + ",Root:" + wordd.root+ ",MorpFeats:" + wordd.morp_feats+"\n");
            }
        }
        else if(out_format.compareTo("justmorphsingle")==0) {
            for (int i = 0; i < wordList.size(); i++) {
                wordd = wordList.get(i);
                f.write(wordd.surface_form + "-"+wordd.lemma + "-" + wordd.root+"  ");
            }

        }
        f.write("\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //fills POS information of all words in the list
    private void POSTagging(){

        List<String> sentence;
        List<Word> morph_analyses;

            sentence=new ArrayList<>();

            for(int m=0;m<wordList.size();m++) sentence.add(m,wordList.get(m).GetSurfaceForm());
            morph_analyses=lang_tools.disambiguator.disambiguate(sentence);

            for (int i = 0; i <wordList.size(); i++) {
                wordList.get(i).lemma=morph_analyses.get(i).getStem().getSurface();
                wordList.get(i).root=morph_analyses.get(i).getRoot().allomorphAt(0).getSurface();

                if(!morph_analyses.get(i).allomorphAt(0).toString().contains("/BILINMEYEN(noun)"))
                    wordList.get(i).postag=morph_analyses.get(i).getPos();
                else wordList.get(i).postag="noun-unk";
                wordList.get(i).morp_feats= lang_tools.MorphDetails(wordList.get(i).GetSurfaceForm(),wordList.get(i).GetPosTag(), morph_analyses.get(i).getRoot().getSurface());

        }

    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //uppercase the first letter of the input word
    protected String CapitalizeFirstLetter(String input_word){
        Locale lcl = new Locale("tr", "TR");
        StringBuffer capitalized_word;

        capitalized_word=new StringBuffer(input_word.toLowerCase(lcl));

        capitalized_word.setCharAt(0,Character.toString(input_word.charAt(0)).toUpperCase(lcl).charAt(0));
        return  capitalized_word.toString();
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //fills NER information of all words in the list
    /*private void NERTagging() {

        String ner_sentence="", ner_tag="none";
        int word_index = 0;

         for (int i = 0; i < wordList.size(); i++)
            ner_sentence = ner_sentence + " " + wordList.get(i).GetSurfaceForm();
         ner_sentence = ner_sentence.trim();

          //finds NER tags
          ner_sentence = lang_tools.NER_tool.simpleNerFunction(ner_sentence);
          ner_sentence = ner_sentence.substring(0, ner_sentence.length() - 1); //cümle sonuna bir sebepten eof ekliyor -- bu çıkartılıyor

          ner_sentence = ner_sentence.replaceAll("  ", " ");

          ner_sentence=lang_tools.CorrectLines(ner_sentence);
          String[] words = ner_sentence.trim().split(" ");

        for (int i = 0; i < words.length; i++) {
                if ((!words[i].contains("[")) && (!words[i].contains("]"))) {
                   wordList.get(word_index).nertag=ner_tag;
                   word_index++;
                }
                else if (words[i].contains("[")) ner_tag = words[i].substring(1);
                else if ((words[i].contains("]"))) ner_tag = "none";
            }

    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //fills NER information of all words in the list
    private void NERTagging2() {

        String ner_sentence="", ner_tag="none";
        int word_index = 0;

        for (int i = 0; i < wordList.size(); i++) {
            ner_sentence = ner_sentence + " " + wordList.get(i).GetSurfaceForm();
        }
        ner_sentence = ner_sentence.trim();

        //finds NER tags
        ner_sentence = lang_tools.NER_tool.simpleNerFunction(ner_sentence);
        ner_sentence = ner_sentence.substring(0, ner_sentence.length() - 1); //cümle sonuna bir sebepten eof ekliyor -- bu çıkartılıyor
        ner_sentence = ner_sentence.replaceAll("  ", " ");

        lang_tools.CorrectLines2(ner_sentence, wordList);



    }

*/
    //~ ----------------------------------------------------------------------------------------------------------------
    //fills parser related information of all words
    private void ParserTagging(BufferedWriter senik) {

        SentenceData09 dep_sentence;
        try {
            dep_sentence = lang_tools.dep_parser.ParseSentence(wordList);
            senik.write(dep_sentence + "\n");
            for (int j = 0; j < wordList.size(); j++) {
                wordList.get(j).deptag = dep_sentence.plabels[j];
                if (dep_sentence.pheads[j] != 0) {
                    wordList.get(j).dephead = wordList.get(dep_sentence.pheads[j] - 1).GetSurfaceForm();
                    wordList.get(j).depindex = dep_sentence.pheads[j] - 1;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
