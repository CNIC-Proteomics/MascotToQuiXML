/**
 * @author Pedro
 *
 */
public class OutData 
{

	private String RAWFileName;
	private String FileName;
	private String sequence;
	private String ProteinDescription;
	private int FirstScan;
	private int z;
	private double ionsScore;
	private double expect;
	private double Mr_Exp;
	private int rank;
	private Redundance[] Redundances;
	
	/**
	 * @return the redundances
	 */
	public Redundance[] getRedundances() {
		return Redundances;
	}
	/**
	 * @param redundances the redundances to set
	 */
	public void setRedundances(Redundance[] redundances) {
		Redundances = redundances;
	}
	/** 
	 * @return the z
	 */
	public int getZ() {
		return z;
	}
	/**
	 * @return the rAWFileName
	 */
	public String getRAWFileName() {
		return RAWFileName;
	}
	/**
	 * @return the fileName
	 */
	
	/**
	 * @param fileName the rAWFileName to set
	 */
	public void setRAWFileName(String fileName) {
		RAWFileName = fileName;
	}
	public String getFileName() {
		return FileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		FileName = fileName;
	}

	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}
	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	/**
	 * @return the proteinDescription
	 */
	public String getProteinDescription() {
		return ProteinDescription;
	}
	/**
	 * @param proteinDescription the proteinDescription to set
	 */
	public void setProteinDescription(String proteinDescription) {
		ProteinDescription = proteinDescription;
	}
	/**
	 * @return the firstScan
	 */
	public int getFirstScan() {
		return FirstScan;
	}
	/**
	 * @param firstScan the firstScan to set
	 */
	public void setFirstScan(int firstScan) {
		FirstScan = firstScan;
	}
	/**
	 * @param z the z to set
	 */
	public void setZ(int z) {
		this.z = z;
	}

	/**
	 * @return the ionsScore
	 */
	public double getIonsScore() {
		return ionsScore;
	}
	/**
	 * @param ionsScore the ionsScore to set
	 */
	public void setIonsScore(double ionsScore) {
		this.ionsScore = ionsScore;
	}

    /**
	 * @return the expect
	 */
	public double getExpect() {
		return expect;
	}
	/**
	 * @param expect the expect to set
	 */
	public void setExpect(double expect) {
		this.expect = expect;
	}
	/**
	 * @return the mr_Exp
	 */
	public double getMr_Exp() {
		return Mr_Exp;
	}
	/**
	 * @param mr_Exp the mr_Exp to set
	 */
	public void setMr_Exp(double mr_Exp) {
		Mr_Exp = mr_Exp;
	}


	
	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}
	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}
	public static String roundWholeNumber (double toRound) {
        String myInt = ""+toRound;
        myInt += "\n";
        if(myInt.indexOf(".0\n")>-1) {
            return myInt.substring(0,myInt.indexOf(".0\n"));
        }
        return ""+toRound;
    }
	
}


