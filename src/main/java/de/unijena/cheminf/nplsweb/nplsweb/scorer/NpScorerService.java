package de.unijena.cheminf.nplsweb.nplsweb.scorer;


import com.google.common.collect.Lists;
import de.unijena.cheminf.nplsweb.nplsweb.model.MoleculeRepository;
import de.unijena.cheminf.nplsweb.nplsweb.model.UserUploadedMolecule;
import de.unijena.cheminf.nplsweb.nplsweb.model.UserUploadedMoleculeRepository;
import org.javatuples.Quintet;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class NpScorerService {



    @Autowired
    UserUploadedMoleculeRepository uumr;

    @Autowired
    MoleculeRepository mr;

    private ArrayList<Quintet< IAtomContainer, Double, Double, Double, Double>> moleculesWithScores;


    private ArrayList<String> moleculeIdWithScores;

    private Hashtable<String, IAtomContainer> molecules;

    private String sesstionId;


    private Integer numberOfThreads = 5 ;

    List<Future<?>> futures = new ArrayList<Future<?>>();



    public void doWork(){

        try{
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);


            List<List<String>>  moleculeListBatch =  Lists.partition(new ArrayList<String>(molecules.keySet()), 5);

            int taskcount = 0;

            List<Callable<Object>> todo = new ArrayList<Callable<Object>>(moleculeListBatch.size());

            System.out.println("Total number of tasks:" + moleculeListBatch.size());


            for(List<String> stringMolBatch : moleculeListBatch){
                NplsTask task = new NplsTask();


                Hashtable<String, IAtomContainer> molBatch = new Hashtable<String, IAtomContainer>();
                for(String s : stringMolBatch){
                    molBatch.put(s, molecules.get(s));
                }

                task.setSessionid(sesstionId);

                task.setMoleculesToCompute(molBatch);

                taskcount++;

                System.out.println("Task "+taskcount+" created");
                task.taskid=taskcount;

                // tod o.add(Executors.callable(task));

                Future<?> f = executor.submit(task);

                futures.add(f);

                //executor.execute(task);

                System.out.println("Task "+taskcount+" executing");

            }






        } catch (Exception e) {
            e.printStackTrace();
        }



        return;
    }




    public ArrayList<String> returnResultsAsStrings(){
        this.moleculeIdWithScores = new ArrayList<String>();


        List<UserUploadedMolecule> results = uumr.findAllBySessionid(this.sesstionId);


        for(UserUploadedMolecule uum: results){

            String s = uum.getUu_id() + " : NPL score (sugar removal): " + uum.getNpl_score() + "; NPL score (full molecule): "+uum.getNpl_sugar_score()+"; SML score (sugar removal): "+uum.getSml_score()
                    +"; SML score (full molecule): " + uum.getSml_sugar_score();

            System.out.println(s);

            this.moleculeIdWithScores.add(s);


        }

        return this.moleculeIdWithScores;

    }

    public List<UserUploadedMolecule> returnResultsAsUserUploadedMolecules(){



        List<UserUploadedMolecule> results = uumr.findAllBySessionid(this.sesstionId);


        return results;
    }


    public List<Double> returnxAxis(String plotType){

        Double minscore = 0.0;
        Double maxscore = 0.0;

        List<Double> axis = new ArrayList<>();

        if(plotType.equals("np")){
            Object obj1 = mr.getMinNPLScore().get(0);
            minscore = Double.parseDouble((obj1.toString()));

            Object obj2 = mr.getMaxNPLScore().get(0);
            maxscore = Double.parseDouble(obj2.toString());

        }
        else if(plotType.equals("np_sugar")){
            Object obj1 = mr.getMinNPLSugarScore().get(0);
            minscore = Double.parseDouble((obj1.toString()));

            Object obj2 = mr.getMaxNPLSugarScore().get(0);
            maxscore = Double.parseDouble(obj2.toString());

        }
        else if(plotType.equals("sm")){
            Object obj1 = mr.getMinSMLScore().get(0);
            minscore = Double.parseDouble((obj1.toString()));

            Object obj2 = mr.getMaxSMLScore().get(0);
            maxscore = Double.parseDouble(obj2.toString());

        }
        else if(plotType.equals("sm_sugar")){

            Object obj1 = mr.getMinSMLSugarScore().get(0);
            minscore = Double.parseDouble((obj1.toString()));

            Object obj2 = mr.getMaxSMLSugarScore().get(0);
            maxscore = Double.parseDouble(obj2.toString());

        }


        Integer xmin = (int)Math.floor(minscore) ;
        Integer xmax  = (int)Math.ceil(maxscore);


        for(double i=xmin;i<=xmax;i=i+0.1){
            i = (double)Math.round(i *10) /10 ;
            axis.add(i);
        }


        return axis;
    }


    public Hashtable<Double, Double> computeBins(List<Double> scores, List<Double> xaxis){


        Hashtable<Double, Integer> counts = new Hashtable<Double, Integer>();
        Hashtable<Double, Double> probs = new Hashtable<Double, Double>();


        //initialization of bin counts
        for(double i:xaxis){
            counts.put(i, 0);
        }

        for(Double score : scores){
            double roundedScore = (double) Math.round(score *10) /10 ;
            counts.put( roundedScore, counts.get(roundedScore) + 1);
        }


        for(Double bin : counts.keySet()){
            probs.put(bin, (double)counts.get(bin)/(double)scores.size());
        }
        System.out.println(probs);
        return probs;
    }


    public Hashtable<Double, Double> returnAllNPLScoresNP(){

        List<Double> scores = (List<Double>)(Object) mr.getNPLSinNP();

        return( computeBins(scores, returnxAxis("np")) );
    }


    public Hashtable<Double, Double>  returnAllNPLScoresSM(){
        List<Double> scores = (List<Double>)(Object) mr.getNPLSinSM();
        return( computeBins(scores, returnxAxis("np")) );
    }





    public Hashtable<Double, Double> returnAllNPLSugarScoresNP(){
        List<Double> scores = (List<Double>)(Object) mr.getNPLSsugarSinNP();

        return(computeBins(scores, returnxAxis("np_sugar")));
    }

    public Hashtable<Double, Double> returnAllNPLSugarScoresSM(){

        List<Double> scores = (List<Double>)(Object) mr.getNPLSsugarSinSM();

        return(computeBins(scores, returnxAxis("np_sugar")));
    }

    public Hashtable<Double, Double> returnAllSMLScoresNP(){
        List<Double> scores = (List<Double>)(Object) mr.getSMLSinNP();

        return(computeBins(scores, returnxAxis("sm")));
    }

    public Hashtable<Double, Double> returnAllSMLScoresSM(){
        List<Double> scores = (List<Double>)(Object) mr.getSMLSinSM();

        return(computeBins(scores, returnxAxis("sm")));
    }


    public Hashtable<Double, Double> returnAllSMLSugarScoresNP(){
        List<Double> scores = (List<Double>)(Object) mr.getSMLSsugarSinNP();

        return(computeBins(scores, returnxAxis("sm_sugar")));
    }

    public Hashtable<Double, Double> returnAllSMLSugarScoresSM(){
        List<Double> scores = (List<Double>)(Object) mr.getSMLSsugarSinSM();

        return(computeBins(scores, returnxAxis("sm_sugar")));
    }



    public boolean processFinished(){

        boolean allFuturesDone = true;

        for(Future<?> future : this.futures){

            allFuturesDone &= future.isDone();

        }



        return allFuturesDone;
    }



    public ArrayList getMoleculesWithScores() {
        return moleculesWithScores;
    }

    public void setMoleculesWithScores(ArrayList moleculesWithScores) {
        this.moleculesWithScores = moleculesWithScores;
    }

    public Hashtable<String, IAtomContainer>  getMolecules() {
        return molecules;
    }

    public void setMolecules(Hashtable<String, IAtomContainer>  molecules) {
        this.molecules = molecules;
    }

    public void setNumberOfThreads(Integer numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public ArrayList<String> getMoleculeIdWithScores() {
        return moleculeIdWithScores;
    }

    public void setMoleculeIdWithScores(ArrayList<String> moleculeIdWithScores) {
        this.moleculeIdWithScores = moleculeIdWithScores;
    }

    public String getSesstionId() {
        return sesstionId;
    }

    public void setSesstionId(String sesstionId) {
        this.sesstionId = sesstionId;
    }
}