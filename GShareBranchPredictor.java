public class GShareBranchPredictor
{
    int NUM_PREDICTION_REGISTERS;
    int[] predictionRegisters;
    int numPredictions = 0;
    int numMispredictions = 0;
    int m = 0;
    int n = 0;
    String branchHistoryRegister = "";

    public GShareBranchPredictor(int m_var, int n_var)
    {
        m = m_var;
        n = n_var;
        for(int i = 0; i < n; i++)
        {
            branchHistoryRegister += '0';
        }
        NUM_PREDICTION_REGISTERS = (int)Math.pow(2, m);
        predictionRegisters = new int[NUM_PREDICTION_REGISTERS];
        for(int i = 0; i < NUM_PREDICTION_REGISTERS; i++)
        {
            predictionRegisters[i] = 4;
        }
    }

    public boolean MatchPrediction(String pc_hex, boolean isTaken, boolean updateBranchPredictors)
    {
        boolean matched = true;
        // determine the branch's index
        String pc_binary = MathExtended.hexToBinary(pc_hex);
        String index_binary = pc_binary.substring(pc_binary.length() - 2 - m, pc_binary.length() - 2);
        index_binary = XOR(index_binary, branchHistoryRegister);
        int index_int = Integer.parseInt(index_binary, 2);

        if(predictionRegisters[index_int] >= 4)
        {
            if(!isTaken)
            {
                numMispredictions++;
                matched = false;
            }
        }
        else
        {
            if(isTaken)
            {
                numMispredictions++;
                matched = false;
            }
        }

        if(isTaken)
        {
            if(updateBranchPredictors)
            {
                predictionRegisters[index_int]++;
                if(predictionRegisters[index_int] > 7)
                    predictionRegisters[index_int] = 7;
            }
            branchHistoryRegister =  '1' + branchHistoryRegister.substring(0, branchHistoryRegister.length() - 1);
        }
        else
        {
            if(updateBranchPredictors)
            {
                predictionRegisters[index_int]--;
                if(predictionRegisters[index_int] < 0)
                    predictionRegisters[index_int] = 0;
            }
            branchHistoryRegister = '0' + branchHistoryRegister.substring(0, branchHistoryRegister.length() - 1);
        }

        numPredictions++;
        return matched;
    }

    public double CalculateMispredictionRate(){
        return ((double)numMispredictions)/(numPredictions);
    }

    public String XOR(String index, String bhr)
    {
        String result = index.substring(0, index.length()-bhr.length());
        for(int i = n; i > 0; i--)
        {
            result = result + "" + (index.charAt(index.length() - i) ^ bhr.charAt(bhr.length() - i));
        }
        return result;
    }
}
