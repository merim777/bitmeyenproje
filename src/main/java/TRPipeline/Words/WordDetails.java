package TRPipeline.Words;

public class WordDetails {

    String lemma;// word lemma
    String root;//word root
    String morp_feats; //morph features
    String surface_form; //surface form of the word
    String postag; //POS tag
    String nertag; //NER tag
    String deptag; //parse details
    String dephead;//the head word
    int depindex; //the position of the head word in the sentence

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * returns the lemma of the word
     *
     */
    public String GetLemma() {
        return lemma;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * sets the field
     *
     */
    public void SetLemma(String input) {
        lemma=input;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * returns the root of the word
     *
     */
    public String GetRoot() {
        return root;
    }


    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * sets the field
     *
     */
    public void SetRoot(String input) {
        root=input;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * returns morphological features of the word
     *
     */
    public String GetMorpFeats() {
        return morp_feats;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * sets the field
     *
     */
    public void SetMorpFeats(String input) {
        morp_feats=input;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * returns the word as it appears in the sentence
     *
     */
    public String GetSurfaceForm() {
        return surface_form;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * sets the field
     *
     */
    public void SetSurfaceForm(String input) {
        surface_form=input;
    }


    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * returns the POS tag of the word
     *
     */
    public String GetPosTag() {
        return postag;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * sets the field
     *
     */
    public void SetPosTag(String input) {
        postag=input;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * returns the NER tag of the word
     *
     */
    public String GetNerTag() {
        return nertag;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * sets the field
     *
     */
    public void SetNerTag(String input) {
        nertag=input;
    }


    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * returns the dependency relation
     *
     */
    public String GetDepTag() {
        return deptag;

    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * sets the field
     *
     */
    public void SetDepTag(String input) {

        deptag=input;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    /**
     * sets the field
     *
     */
    public void SetHeadIndex (int input){
        depindex=input;

    }
    //~ ----------------------------------------------------------------------------------------------------------------
    /**
    returns the position of the head word
     */
    public int GetHeadIndex (){
        return depindex;

    }


}


