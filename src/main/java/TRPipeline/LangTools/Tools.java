/**
 * Created with IntelliJ IDEA.
 * User: seniz
 * Date: 11/11/16
 * Time: 9:04 AM
 * To change this template use File | Settings | File Templates.
 */

package TRPipeline.LangTools;

import Main.NERCoreMainClass;
import TRPipeline.Words.WordDetails;
import tr.gov.tubitak.bilgem.bte.akbis.cekirdek.data.Language;
import tr.gov.tubitak.bilgem.bte.akbis.cekirdek.disambiguation.Disambiguator;
import tr.gov.tubitak.bilgem.bte.akbis.cekirdek.disambiguation.SimpleDisambiguator;
import tr.gov.tubitak.bilgem.bte.akbis.cekirdek.process.CekirdekAnalyzer;
import tr.gov.tubitak.bilgem.bte.akbis.cekirdek.process.WordAnalyzer;
import tr.gov.tubitak.bilgem.bte.akbis.cekirdek.reader.LanguageReader;
import tr.gov.tubitak.bilgem.bte.akbis.cekirdek.structure.Allomorph;
import tr.gov.tubitak.bilgem.bte.akbis.cekirdek.structure.Word;
import tr.gov.tubitak.bilgem.bte.akbis.langidapi.LanguageIdentifier;
import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.tokenizer.SentenceBoundaryDetector;
import zemberek.tokenizer.SimpleSentenceBoundaryDetector;


import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

    Locale lcl = new Locale("tr", "TR");

    //Nuve morphological parser and disambiguator
    Language l;
    public WordAnalyzer nuve_parser;
    public Disambiguator disambiguator;
    List<Word> sentence_words;
    List<WordDetails> sentence_word_details;


    //Zemberek morphological parser
    public TurkishMorphology zemberek_parser;


    //Dependency parser
    public DependencyParser dep_parser;

    //Sentence splitter
    public SentenceBoundaryDetector splitter;


    public Tools(Path tools_path) {

        try {

            l = LanguageReader.read("tr");
            nuve_parser = new CekirdekAnalyzer(l);
            disambiguator = new SimpleDisambiguator(nuve_parser);
            sentence_words = new ArrayList<>();
            sentence_word_details = new ArrayList<>();
            splitter = new SimpleSentenceBoundaryDetector();


            zemberek_parser = TurkishMorphology.createWithDefaults();

            dep_parser = new DependencyParser(Paths.get(tools_path + "/dependency"), Paths.get(tools_path.getParent() + "/lib/runtime/"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * for the given word with root and POS tag, returns all identified morphological features
     */
    public String MorphDetails(String input_word, String POS, String Root) {


        if (CheckOovNuve(input_word)==0) return "none"; //for some non-Turkish words Zemberek crashes

        List<zemberek.morphology.analysis.WordAnalysis> result = zemberek_parser.analyze(input_word);
        String morph_details = "none";
        String[] parts;

        for (zemberek.morphology.analysis.WordAnalysis morphParse : result) {

            if ((morphParse.getPos().toString().compareTo("Unknown") != 0) && (morphParse.getPos().toString().toLowerCase().contains(POS.toLowerCase())) && (CaseInsensitiveMatch(morphParse.getStems(), Root) == 1)) {
                if (morphParse.getLastIg().formatNoSurface().contains(";")) {
                    parts = morphParse.getLastIg().formatNoSurface().split(";");
                    morph_details = parts[1].replaceAll("\\+", "\\|").substring(0, parts[1].length() - 1);
                }
                break;
            }
        }

        return morph_details;
    }


    //~ ----------------------------------------------------------------------------------------------------------------


//~ ----------------------------------------------------------------------------------------------------------------

    /**
     * checks whether the input word is a valid word or not via NUVE parser
     * returns 1 for Turkish words and 0 for out of vocabulary words
     */

    protected int CheckOovNuve(String input_word) {

        //checks whether the word has a morphological parse
        List<Word> words = nuve_parser.analyze(input_word.toLowerCase(lcl));

        if (words.size() > 1) return 1; //at least one parse exists
        else if (words.size() == 1) {
            if (words.get(0).allomorphAt(0).toString().contains("/BILINMEYEN")) return 0;
            else return 1;
        } else return 0; //no parse at all

    }


    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * finds whether the given string is in the list
     */
    private int CaseInsensitiveMatch(List<String> input_list, String input_str) {
        Locale lcl = new Locale("tr", "TR");
        for (String str : input_list)
            if (str.toLowerCase(lcl).compareTo(input_str.toLowerCase(lcl)) == 0)
                return 1;

        return 0;
    }

    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * performs necessary cleaning
     */
    public String CleanLines2(String input_str) {

        Pattern reg_pattern;
        Matcher reg_matcher;
        String[] reg_expp = new String[3];
        reg_expp[0] = "([\\(\\)\\[\\],;:?])"; //separate punctuations and parenthesis
        reg_expp[1] = "\\s+"; //remove multiple spaces
        reg_expp[2] = "([\\p{IsLatin}ıİşŞğĞüÜçÇöÖä])(\\s*)\\."; //separate dot at the end of the sentence

        for (int i = 0; i < 3; i++) {
            reg_pattern = Pattern.compile(reg_expp[i], Pattern.CASE_INSENSITIVE);
            reg_matcher = reg_pattern.matcher(input_str);
            if (i == 0) input_str = reg_matcher.replaceAll(" $1 ");
            else if (i == 1) input_str = reg_matcher.replaceAll(" ");
            else if (i == 2) input_str = reg_matcher.replaceAll("$1 \\.");
        }

        return input_str.trim();
    }
    //~ ----------------------------------------------------------------------------------------------------------------



}
