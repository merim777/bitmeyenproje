/**
 * Created with IntelliJ IDEA.
 * User: seniz
 * Date: 03/06/19
 * Time: 8:46 AM
 * To change this template use File | Settings | File Templates.
 */
package TRPipeline;

import TRPipeline.Words.WordList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Pipeline {

  protected Path input_dir_path;     //the folder that contains input files
  protected Path output_dir_path;    //the folder that contains output files
  protected Path tools_dir_path;     //the folder that contains language tools
  protected static String out_format;       //the output format
  public static  WordList SentenceWords;

  public Pipeline(Path conf_file_path){


      BufferedReader br;
      String sCurrentLine;

      try {

          br = Files.newBufferedReader(Paths.get(conf_file_path+"/conf.txt"), StandardCharsets.UTF_8);

          while ((sCurrentLine = br.readLine()) != null) {
              if (sCurrentLine.indexOf("input_directory:") != -1)
                  input_dir_path = Paths.get(conf_file_path + "/" + sCurrentLine.substring(sCurrentLine.indexOf(":") + 2));
              else if (sCurrentLine.indexOf("output_directory:") != -1)
                  output_dir_path = Paths.get(conf_file_path + "/" + sCurrentLine.substring(sCurrentLine.indexOf(":") + 2));
              else if (sCurrentLine.indexOf("tools_directory:") != -1)
                  tools_dir_path = Paths.get(conf_file_path + "/" + sCurrentLine.substring(sCurrentLine.indexOf(":") + 2));
              else if (sCurrentLine.indexOf("out_format:") != -1)
                  out_format = sCurrentLine.substring(sCurrentLine.indexOf(":") + 2);
          }
          br.close();

        SentenceWords=new WordList(tools_dir_path);

      } catch (IOException e) {
          e.printStackTrace();
      }
  }


    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * reads all input files and processes them
     */
    public void DirectoryReader() {

        ArrayList<String> ext_list = new ArrayList<String>(Arrays.asList(".txt", ".TXT", ".rtf", ".RTF")); //olası girdi dosyası uzantıları
        String input_file_name;
        int index, i, j;


        //identifies all files in the folder
        File folder = new File(input_dir_path.toString());
        File[] list_of_files = folder.listFiles();

        for (i = 0; i < list_of_files.length; i++) {
            input_file_name = list_of_files[i].getName();
            index = -1;
            for (j = 0; j < ext_list.size(); j++) {
                index = input_file_name.indexOf(ext_list.get(j));
                if (index != -1) {
                    ProcessFile(input_file_name);
                    break;
                }
            }
        }
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //reads all lines from the input file
    public void ProcessFile(String input_file) {

        String input_sentence;
        BufferedReader br;
        int sent_num=1;

        try {

            br = Files.newBufferedReader(Paths.get(input_dir_path.toString()+"/"+input_file), StandardCharsets.UTF_8);
            BufferedWriter f= Files.newBufferedWriter(Paths.get(output_dir_path+"/tagged-"+input_file), StandardCharsets.UTF_8);

            BufferedWriter senout= Files.newBufferedWriter(Paths.get(output_dir_path+"/taggedout-"+input_file), StandardCharsets.UTF_8);

            f.write("WORD-" + ",POS" +",NER" +",DEPRel" + ",DEPHead" + ",Lemma" + ",Root"+",MorpFeats" +"\n\n");


            while ((input_sentence = br.readLine()) != null) {



                if(input_sentence.contains("&")) continue;

                System.out.println("Sentence num: "+sent_num+" - "+input_sentence);
                SentenceWords.CreateWordList(input_sentence,out_format,f,senout);
                sent_num++;
            }
            br.close();
            f.close();
            senout.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   //~ ----------------------------------------------------------------------------------------------------------------
   public static void main(String[] args) throws IOException {

       Pipeline TrPipeline = new Pipeline(Paths.get(args[0]));
       TrPipeline.DirectoryReader(); //processes input directory

   }
}

