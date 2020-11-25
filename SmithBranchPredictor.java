public class SmithBranchPredictor
{
    int NUM_COUNTER_BITS;
    int counterBit = 0;
    int MIN_COUNTER = 0;
    int MAX_COUNTER = 0;
    int MID_COUNTER = 0;
    int numPredictions = 0;
    int numMispredictions = 0;

    public SmithBranchPredictor(int numCounterBits)
    {
        NUM_COUNTER_BITS = numCounterBits;
        MAX_COUNTER = (int)Math.pow(2, NUM_COUNTER_BITS) - 1;
        MID_COUNTER = (int)Math.pow(2, NUM_COUNTER_BITS)/2;
        counterBit = (int)Math.pow(2, NUM_COUNTER_BITS - 1);
    }

    public void MatchPrediction(boolean isTaken)
    {
        // predicted taken
        if(counterBit >= MID_COUNTER)
        {
            if(!isTaken)
            {
                numMispredictions++;
            }
        }
        else
        {
            // not predicted taken
            if(isTaken)
            {
                numMispredictions++;
            }
        }

        // incrementing/decrementing the counter bit within saturation bounds        
        if(isTaken)
        {
            counterBit++;
            if(counterBit > MAX_COUNTER)
            {
                counterBit = MAX_COUNTER;
            }
        }
        else
        {
            counterBit--;
            if(counterBit < MIN_COUNTER)
            {
                counterBit = MIN_COUNTER;
            }
        }

        numPredictions++;
    }

    public double CalculateMispredictionRate(){
        return ((double)numMispredictions)/(numPredictions);
    }
}
