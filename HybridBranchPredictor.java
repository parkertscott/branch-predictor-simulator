public class HybridBranchPredictor
{
    int NUM_CHOOSER_COUNTERS;
    int[] chooserCounters;
    int numPredictions = 0;
    int numMispredictions = 0;
    BimodalBranchPredictor bimodalBranchPredictor;
    GShareBranchPredictor gShareBranchPredictor;
    int k = 0;

    public HybridBranchPredictor(int k_var, BimodalBranchPredictor bbp, GShareBranchPredictor gbp)
    {
        k = k_var;
        NUM_CHOOSER_COUNTERS = (int)Math.pow(2, k);
        bimodalBranchPredictor = bbp;
        gShareBranchPredictor = gbp;
        chooserCounters = new int[NUM_CHOOSER_COUNTERS];
        for(int i = 0; i < NUM_CHOOSER_COUNTERS; i++)
        {
            chooserCounters[i] = 1;
        }
    }

    public void MatchPrediction(String pc_hex, boolean isTaken)
    {
        boolean matched = true;
        // determine the branch's index
        String pc_binary = MathExtended.hexToBinary(pc_hex);
        String index_binary = pc_binary.substring(pc_binary.length() - 2 - k, pc_binary.length() - 2);
        int index_int = Integer.parseInt(index_binary, 2);

        if(chooserCounters[index_int] >= 2)
        {
            boolean gShareMatched = gShareBranchPredictor.MatchPrediction(pc_hex, isTaken, true);
            boolean bimodalMatched = bimodalBranchPredictor.MatchPrediction(pc_hex, isTaken, false);
            if(!gShareMatched)
            {
                numMispredictions++;
            }
            UpdateChooserCounter(index_int, gShareMatched, bimodalMatched);
        }
        else
        {
            boolean gShareMatched = gShareBranchPredictor.MatchPrediction(pc_hex, isTaken, false);
            boolean bimodalMatched = bimodalBranchPredictor.MatchPrediction(pc_hex, isTaken, true);
            if(!bimodalMatched)
            {
                numMispredictions++;
            }
            UpdateChooserCounter(index_int, gShareMatched, bimodalMatched);
        }

        numPredictions++;
    }

    public double CalculateMispredictionRate(){
        return ((double)numMispredictions)/(numPredictions);
    }

    public void UpdateChooserCounter(int index_int, boolean gShareMatched, boolean bimodalMatched)
    {
        if(gShareMatched && !bimodalMatched)
        {
            chooserCounters[index_int]++;
            if(chooserCounters[index_int] > 3)
                chooserCounters[index_int] = 3;

        }
        else if(bimodalMatched && !gShareMatched)
        {
            chooserCounters[index_int]--;
            if(chooserCounters[index_int] < 0)
                chooserCounters[index_int] = 0;

        }
    }
}
