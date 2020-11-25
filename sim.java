import java.io.*;
import java.util.ArrayList;

public class sim{

    public static final int SMITH_PREDICTORTYPE = 0;
    public static final int BIMODAL_PREDICTORTYPE = 1;
    public static final int GSHARE_PREDICTORTYPE = 2;
    public static final int HYBRID_PREDICTORTYPE = 3;
    
    public static void main(String[] args)
    {
        // Parker Scott - CDA 5106 - Fall 2020 - Machine Problem 2
        
        // In this project, you will construct a branch predictor simulator and use it to design branch
        // predictors well suited to the SPECint95 benchmarks.

        // initial configuration output
        System.out.println("COMMAND");
        System.out.print("./sim ");
        for(String arg : args)
        {
            System.out.print(arg + " ");
        }
        System.out.println("");
        System.out.println("OUTPUT");

        // import all lines from trace file
        
		ArrayList<String> lines = new ArrayList<String>();
		// begin processing input from trace file
		// Boilerplate code from: https://caveofprogramming.com/java/java-file-reading-and-writing-files-in-java.html
		String inputLine = null;
        try {
			FileReader fileReader = 
					new FileReader(args[args.length - 1]);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = 
				new BufferedReader(fileReader);
			while((inputLine = bufferedReader.readLine()) != null) {
				if(inputLine.length() > 0){
					lines.add(inputLine);
				}
			}   
			// Always close files.
			bufferedReader.close(); 
		}
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + args[args.length - 1] + "'");
        }


        // differentiate type of simulator based on argument
        String predictorTypeArg = args[0];
        int predictorType = 0;
        switch(predictorTypeArg)
        {
            case "smith": predictorType = SMITH_PREDICTORTYPE; break;
            case "bimodal": predictorType = BIMODAL_PREDICTORTYPE; break;
            case "gshare": predictorType = GSHARE_PREDICTORTYPE; break;
            case "hybrid": predictorType = HYBRID_PREDICTORTYPE; break;
        }

        // instantiate predictor and runs input against the simulator and outputs
        if(predictorType == SMITH_PREDICTORTYPE)
        {
            
            SmithBranchPredictor smithBranchPredictor = new SmithBranchPredictor(Integer.parseInt(args[1]));
            for(String line : lines)
            {
                boolean isTaken = line.charAt(7) == 't';
                
                smithBranchPredictor.MatchPrediction(isTaken);
            }

            System.out.println("number of predictions:		" + smithBranchPredictor.numPredictions);
            System.out.println("number of mispredictions:   " + smithBranchPredictor.numMispredictions);
            System.out.printf("misprediction rate:		%.2f", smithBranchPredictor.CalculateMispredictionRate() * 100.0f);
            System.out.println("%");
            System.out.println("FINAL COUNTER CONTENT:		" + smithBranchPredictor.counterBit); 
        }
        else if(predictorType == BIMODAL_PREDICTORTYPE)
        {
            
            BimodalBranchPredictor bimodalBranchPredictor = new BimodalBranchPredictor(Integer.parseInt(args[1]));
            for(String line : lines)
            {
                boolean isTaken = line.charAt(7) == 't';
                
                bimodalBranchPredictor.MatchPrediction(line.substring(0,6), isTaken, true);
            }

            System.out.println("number of predictions:		" + bimodalBranchPredictor.numPredictions);
            System.out.println("number of mispredictions:   " + bimodalBranchPredictor.numMispredictions);
            System.out.printf("misprediction rate:		%.2f", bimodalBranchPredictor.CalculateMispredictionRate() * 100.0f);
            System.out.println("%");
            System.out.println("FINAL BIMODAL CONTENTS");
            for(int i = 0; i < bimodalBranchPredictor.NUM_PREDICTION_REGISTERS; i++)
            {
                System.out.println(i + "\t" + bimodalBranchPredictor.predictionRegisters[i]);
            }
        }
        else if(predictorType == GSHARE_PREDICTORTYPE)
        {

            
            GShareBranchPredictor gShareBranchPredictor = new GShareBranchPredictor(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            for(String line : lines)
            {
                boolean isTaken = line.charAt(7) == 't';
                
                gShareBranchPredictor.MatchPrediction(line.substring(0,6), isTaken, true);
            }

            System.out.println("number of predictions:		" + gShareBranchPredictor.numPredictions);
            System.out.println("number of mispredictions:   " + gShareBranchPredictor.numMispredictions);
            System.out.printf("misprediction rate:		%.2f", gShareBranchPredictor.CalculateMispredictionRate() * 100.000f);
            System.out.println("%");
            System.out.println("FINAL GSHARE CONTENTS");
            for(int i = 0; i < gShareBranchPredictor.NUM_PREDICTION_REGISTERS; i++)
            {
                System.out.println(i + "\t" + gShareBranchPredictor.predictionRegisters[i]);
            }
        }
        else if(predictorType == HYBRID_PREDICTORTYPE)
        {
            
            GShareBranchPredictor gShareBranchPredictor = new GShareBranchPredictor(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            BimodalBranchPredictor bimodalBranchPredictor = new BimodalBranchPredictor(Integer.parseInt(args[4]));
            HybridBranchPredictor hybridBranchPredictor = new HybridBranchPredictor(Integer.parseInt(args[1]), bimodalBranchPredictor, gShareBranchPredictor);
            for(String line : lines)
            {
                boolean isTaken = line.charAt(7) == 't';
                
                hybridBranchPredictor.MatchPrediction(line.substring(0,6), isTaken);
            }

            System.out.println("number of predictions:		" + hybridBranchPredictor.numPredictions);
            System.out.println("number of mispredictions:   " + hybridBranchPredictor.numMispredictions);
            System.out.printf("misprediction rate:		%.2f", hybridBranchPredictor.CalculateMispredictionRate() * 100.000f);
            System.out.println("%");
            System.out.println("FINAL CHOOSER CONTENTS");
            for(int i = 0; i < hybridBranchPredictor.NUM_CHOOSER_COUNTERS; i++)
            {
                System.out.println(i + "\t" + hybridBranchPredictor.chooserCounters[i]);
            }
            System.out.println("FINAL GSHARE CONTENTS");
            for(int i = 0; i < gShareBranchPredictor.NUM_PREDICTION_REGISTERS; i++)
            {
                System.out.println(i + "\t" + gShareBranchPredictor.predictionRegisters[i]);
            }
            System.out.println("FINAL BIMODAL CONTENTS");
            for(int i = 0; i < bimodalBranchPredictor.NUM_PREDICTION_REGISTERS; i++)
            {
                System.out.println(i + "\t" + bimodalBranchPredictor.predictionRegisters[i]);
            }
        }   
    }
}