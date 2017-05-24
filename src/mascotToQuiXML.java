import java.io.*;
import java.util.ArrayList;
//import java.util.List;

import matrix_science.msparser.*;
//import java.util.*;

//import com.sun.org.apache.xpath.internal.operations.String;

//import java.util.List;

//import org.w3c.dom.Element;


//import java.util.regex.*;           // comment out if using version of JDK prior to 1.4


public class mascotToQuiXML 
{
	
	
	static {
        try 
        {
            System.loadLibrary("msparserj");
        } 
        catch (UnsatisfiedLinkError e) 
        {
            System.err.println("Native code library failed to load. "
                               + "Is msparserj.dll on the path?\n" + e);
            System.exit(0);
        }
    }
    
    public static void main(String argv[])
    {
    	String version= "1.0";
    	
   		System.out.println("mascotToQuiXML v. " + version);
  		System.out.println("Author: Pedro Navarro");
   		System.out.println("Protein Chemistry & Proteomics Lab. ");
   		System.out.println("Centro de Biologia Molecular Severo Ochoa (CBMSO) (CSIC-UAM)");
   		System.out.println("If you experience any problem, or want to participate in any way ");
   		System.out.println("with the project, look up at the QuiXoT wiki: http://150.244.205.155/QuiXoT");
   		
    	
    	ArrayList<String> arguments=new ArrayList<String>();
    	arguments.add("-r");
    	arguments.add("-e");
    	arguments.add("-s");
    	arguments.add("-f");
    	arguments.add("-dta");
    	arguments.add("-h");
    	arguments.add("--genMGF");
    	arguments.add("--help");
    	
        // ----- Object creation -----
        if(argv.length < 1) {
            System.out.println("Must specify a folder as parameter");
            System.exit(0);
        }
    	if(argv[0].startsWith("-h")||argv[0].startsWith("--help"))
    	{
    		System.out.println("");
       	    System.out.println("MascotToQuiXML -- Help");
       		System.out.println("");
       		System.out.println("Usage: java -jar mascotToQuiXML.jar DIRECTORY <-r maxRank> <-e maxEvalue> <-s ScanNumberParser> <-f rawFileParser> <-dta> ");
       		System.out.println("");
       		System.out.println("Where:");
       		System.out.println("maxRank is the maximum rank you accept for a peptide identification. We recommend -r 1");
       		System.out.println("maxEvalue is the maximum E-value you accept for a peptide identification. We recommend -e 1");
       		System.out.println("ScanNumberParser is a string which you may use to parse the scan number from the peptide header");
       		System.out.println("rawFileParser is a string which you may use to parse the experiment raw file name from the peptide header");
       		System.out.println("-dta may be use in case of the mascot searches were made by using .dta files as input data. This option is not compatible with -s and -f (they will be ignored)");
       		System.out.println("--genMGF generate MGF files of the identificated MS/MS peak lists.");
       		System.exit(0);
       	        		
    	}
    	
        
        String folderName = argv[0];
        String Argumstr="";
        int maxRank = 1000;
        double maxExpect = 1000;
        String ScanNumberParser ="FinneganScanNumber";
    	String rawFileParser = "rawFile";
    	boolean sequestDtaFormat =false;
    	boolean generateMGFs=false;
        // uses Java 1.4 regex
        String regex = "(\\s)|(,)";
        String regexDta = "(\\.)";
        ArrayList<Integer> ionsPeakList= new ArrayList<Integer>();

        int argum;
        for(argum=1; argum < argv.length; argum++) 
        {
        	Argumstr = argv[argum];
        	
        	boolean argFound=false;
        	for(String s:arguments)
        	{
        		if(Argumstr.contains(s))
        		{
        			argFound=true;
        		}
        	}
        	 
        	if(!argFound)
        	{
        		System.out.println("argument parameter not recognized: "+Argumstr);
        		System.exit(0);
        	}
        	

        	if(Argumstr.contains("-r"))
        	{
        		maxRank = Integer.parseInt(argv[argum+1]);
        		argum++;
        	}   
        	if(Argumstr.contains("-e"))
        	{
        		maxExpect = Double.parseDouble(argv[argum+1]); 
        		argum++;
        	}
           	if(Argumstr.contains("-s"))
        	{
        		ScanNumberParser = argv[argum+1];
        		argum++;
        	}
        	if(Argumstr.contains("-f"))
        	{
        		rawFileParser = argv[argum+1];
        		argum++;
        	}
        	if(Argumstr.contains("-dta"))
        	{
        		sequestDtaFormat = true;
        	}
        	if(Argumstr.contains("--genMGF"))
        	{
        		generateMGFs=true;        		
        	}        	       	
        	
        }
        
        
        File f = new File(folderName); 
        String fileList[]=null;
        if( f.exists () && f.isDirectory ()) 
        { 
	        fileList = f.list(); 
	        for(int i=0;i<fileList.length;i++) 
	        { 
        		fileList[i] = folderName + "\\" + fileList[i];
                //System.out.println(fileList[i]); 
	        } 
	        
        } 
        
        ms_mascotresfile mascot_file; 
        ms_searchparams searchParams;
        ArrayList<Varmods> vmods=new ArrayList<Varmods>();
        ArrayList<OutData> queries=new ArrayList<OutData>();
       
        
        char[] charsForMods=new char[10];
        charsForMods[0]='*'; 
        charsForMods[1]='@'; 
        charsForMods[2]='#'; 
        charsForMods[3]='$'; 
        charsForMods[4]='{'; 
        charsForMods[5]='|'; 
        charsForMods[6]='_'; 
        charsForMods[7]='¬'; 
        charsForMods[8]='=';
        charsForMods[9]='^';
     
        boolean modifsDeclared=false;
        
        if(fileList!=null)
        {
	        for(String fil : fileList)
	        {       	
	        	if(fil.contains(".dat"))
	        	{
	        		mascot_file = new ms_mascotresfile(fil, 0, "");	
	        		searchParams = mascot_file.params();
	        	
	        		searchParams.getMODS();
	        		searchParams.getVarModsName(1); 
	        		searchParams.getVarModsDelta(1); 
	        		
	        		
	        		
	        		//System.out.println(searchParams.getMODS()          );
	        		
	        		if(!modifsDeclared)
	        		{
		        		for(int varMod=0;varMod<charsForMods.length;varMod++)
		        		{		        			
			        		//System.out.println(String.valueOf(varMod)+" : "+searchParams.getVarModsName(varMod)  );
			        		//System.out.println(String.valueOf(searchParams.getVarModsDelta(varMod) ) );
			        	
			        		if(searchParams.getVarModsName(varMod).length()!=0)
			        		{
			        			Varmods vm = new Varmods();
			        			
			        			vm.setIndex(varMod);
			        			vm.setName(searchParams.getVarModsName(varMod));
			        			vm.setDeltaMass(searchParams.getVarModsDelta(varMod));
			        			vm.setAssignedChar(charsForMods[varMod]);
			        			
			        			vmods.add(vm);		        			
			        		}
		        		}
		        		modifsDeclared=true;
	        		}
	        		
	        		
	        		/*
	        		searchParams.getVarModsNeutralLoss(1); 
	        		searchParams.getVarModsNeutralLosses(1); 
	        		searchParams.getVarModsPepNeutralLoss(1); 
	        		searchParams.getVarModsReqPepNeutralLoss(1); 
	        		*/
	        		
	        	
	        	      if (mascot_file.isValid()) 
	        	      {	
	        	          
	        	            if(mascot_file.isMSMS()) 
	        	            {   
	        	            	
	        	            	
	        	            	ionsPeakList.clear();
	        	            	
	        	            	System.out.println(fil);
		        	        
	        	                ms_mascotresults results=null;
	        	                ArrayList<Integer> processed_queries = new ArrayList<Integer>();
	        	                
	        	                int prot_hit;
	        	                ms_protein prot;
	        	                String accession;
	        	                String description;
	        	                //double mass;
	        	                int num_peps;
	        	                int i;
	        	                int query;
	        	                int p;
	        	                ms_peptide pep;

	        	                //int mascothits=mascot_file.getNumQueries();	        	                
	        	                
	        	                //boolean ey= mascot_file.anyPeptideSummaryMatches();
	        	                
	        	                results = new ms_peptidesummary(mascot_file,
	        	                		ms_mascotresults.MSRES_DUPE_REMOVE_A |
	        	                		ms_mascotresults.MSRES_DUPE_REMOVE_B |
	        	                		ms_mascotresults.MSRES_DUPE_REMOVE_C |
	        	                		ms_mascotresults.MSRES_DUPE_REMOVE_D |
	        	                		//ms_mascotresults.MSRES_DUPE_REMOVE_E |
	        	                		//ms_mascotresults.MSRES_DUPE_REMOVE_F |
	        	                		//ms_mascotresults.MSRES_DUPE_REMOVE_G |
	        	                		//ms_mascotresults.MSRES_DUPE_REMOVE_H |
	        	                		//ms_mascotresults.MSRES_DUPE_REMOVE_I |	        	                		
	        	                		//ms_mascotresults.MSRES_GROUP_PROTEINS |
	                                    //ms_mascotresults.MSRES_SHOW_SUBSETS |
	                                    ms_mascotresults.MSRES_REQUIRE_BOLD_RED ,
                                        0.01,
                                        10000,
                                        null,
                                        0,
                                        0, 
                                        null);
	        	                
	        	                /*
	        	                ms_proteinsummary results2 = new  ms_proteinsummary(mascot_file,
	        	                								ms_mascotresults.MSRES_NOFLAG,
	        	                								//ms_mascotresults.MSRES_DUPE_REMOVE_A |
	        	                								//ms_mascotresults.MSRES_DUPE_REMOVE_B |
	        	                								//ms_mascotresults.MSRES_DUPE_REMOVE_C |
	        	                								//ms_mascotresults.MSRES_DUPE_REMOVE_D ,
	        	                								//ms_mascotresults.MSRES_DUPE_REMOVE_E |
	        	                								//ms_mascotresults.MSRES_DUPE_REMOVE_F |
	        	                								//ms_mascotresults.MSRES_DUPE_REMOVE_G |
	        	                								//ms_mascotresults.MSRES_DUPE_REMOVE_H |
	        	                								//ms_mascotresults.MSRES_DUPE_REMOVE_I |
	        	                								//ms_mascotresults.MSRES_GROUP_PROTEINS |
							                                    //ms_mascotresults.MSRES_SHOW_SUBSETS,
						                                        0.05,
						                                        50,
						                                        null, 
				                                        		null);
	        	                
	        	                int numProts = results2.getNumberOfHits();
	        	                */
	        	                
	        	                
        	                    if((mascot_file.getLastError())>0) System.out.println("Error : "+mascot_file.getLastErrorString());

        	                    
        	                    int protScore_cutoff=results.getProteinScoreCutoff(20);
        	                    
        	                    System.out.println("Prot score cut-off: "+protScore_cutoff);
        	                    
	        	                //get the first protein hit
	        	                prot_hit = 1;
	        	                prot = results.getHit(prot_hit);
	        	                System.out.println("Number of hits: "+results.getNumberOfHits());

	        	                while(prot_hit <= results.getNumberOfHits())            
	        	                {
	        	                	
	        	                	prot=results.getHit(prot_hit);	        	                	
	        	                	
	        	                    accession = prot.getAccession();
	        	                    description = results.getProteinDescription(accession);
	        	                    //mass = results.getProteinMass(accession);
	        	         
	        	                	
	        	                    // Each protein has a number of peptides that matched - list them
	        	                    //int num_pepsDisplay = prot.getNumDisplayPeptides(); // prot.getNumPeptides();
	        	                    
	        	                    /*
	        	                    System.out.println("Hit: "+prot_hit+" # of peptides: "+num_peps);
	        	                    
	        	                    System.out.println("Accession   : "+accession);
	        	                    System.out.println("Description : "+description);
	        	                    System.out.println("Score       : "+OutData.roundWholeNumber(prot.getScore()));
	        	                    System.out.println("Mass        : "+mass);
	        	                    System.out.println("Frame       : "+prot.getFrame());
	        	                    System.out.println("Coverage    : "+prot.getCoverage());
	        	                    System.out.println("RMS error   : "+prot.getRMSDeltas(results));	        	                    
	        	                	*/
	        	                    
	        	                    
	        	                    // Each protein has a number of peptides that matched - list them
	        	                    num_peps = prot.getNumPeptides();
	        	                    for(i=1; i <= num_peps; i++) {
	        	                       
	        	                    	query = prot.getPeptideQuery(i);
	        	                        p     = prot.getPeptideP(i);
	        	                        boolean isProcessed=true;
	        	                        
	        	                        if(!processed_queries.contains(query))
	        	                        {	        	                        
	        	                        	processed_queries.add(query);
	        	                        	isProcessed = false;
	        	                        }
	        	                        
	        	                        if((p != -1) &&  (query != -1) && (isProcessed == false)) //&&(prot.getPeptideDuplicate(p) != ms_protein.DUPE_DuplicateSameQuery) 
	        	                        {
	        	                            pep = results.getPeptide(query,p);
	        	                            
	        	                            if(pep != null) {
	        	                                /*
	        	                            	displayPeptideInfo(true,pep,results,prot.getPeptideDuplicate(i)
	        	                                == ms_protein.DUPE_Duplicate, prot.getPeptideIsBold(i),
	        	                                prot.getPeptideShowCheckbox(i));
	        	                                */
	        		        	                OutData currQuery=new OutData();

	        	                                int q;	        	                                
	        	                                q= pep.getQuery();
	        	                                
	        	                                ms_inputquery inputquery= new ms_inputquery(mascot_file, q);
	        	                                
	        	                                
	        	                                
	        	                                //System.out.println("Peptide hit");
	        	                                if(pep.getAnyMatch()) {
	        	                                    
	        	                                	/*
	        	                                	System.out.println("  Query       : "+q);
	        	                                    System.out.println("  Rank        : "+pep.getRank());
	        	                                    System.out.println("  Matched     : "+pep.getAnyMatch());
	        	                                    System.out.println("  missedCleave: "+pep.getMissedCleavages());
	        	                                    System.out.println("  mrCalc      : "+pep.getMrCalc());
	        	                                    System.out.println("  delta       : "+pep.getDelta());
	        	                                    System.out.println("  observed    : "+pep.getObserved());
	        	                                    System.out.println("  charge      : "+pep.getCharge());
	        	                                    System.out.println("  mrExp       : "+pep.getMrExperimental());
	        	                                    System.out.println("  ionsMatched : "+pep.getNumIonsMatched());
	        	                                    System.out.println("  peptideStr  : "+pep.getPeptideStr());
	        	                                    System.out.println("  peaksUsed1  : "+pep.getPeaksUsedFromIons1());
	        	                                    System.out.println("  varModsStr  : "+pep.getVarModsStr());
	        	                                    System.out.println("  readable mod: "+results.getReadableVarMods(q, pep.getRank(),2));
	        	                                    System.out.println("  ionsScore   : "+pep.getIonsScore());
	        	                                    System.out.println("  seriesUsedS : "+pep.getSeriesUsedStr());
	        	                                    System.out.println("  peaksUsed2  : "+pep.getPeaksUsedFromIons2());
	        	                                    System.out.println("  peaksUsed3  : "+pep.getPeaksUsedFromIons3());
	        	                                    System.out.print("  idth, hth, p: "  +results.getPeptideIdentityThreshold(q,20));
	        	                                    System.out.print(",  "               +results.getHomologyThreshold(q,20));
	        	                                    System.out.println(",  "             +results.getProbOfPepBeingRandomMatch(pep.getIonsScore(),q));
	        	                                    System.out.println();
	        	                                    */
	        	                                    
	        	                                	
	        	                                    
	        	                                    String[] residues_split=results.getTerminalResiduesString(q, pep.getRank()).split(",");
	        	                                    
	        	                           	                                    
	        	                                    currQuery.setProteinDescription(accession+" | "+description);
	        	                                    currQuery.setFileName(fil);	        	                                    
	        	                                    
	        	                                    String sequence=pep.getPeptideStr(false);
	        	                                    String VarModsStr=pep.getVarModsStr();
	        	                                    String seqWithMods=getSequenceWithMods(residues_split, sequence, vmods,VarModsStr);
	        	                                    
	        	                                    String[] sequence_split = seqWithMods.split(":");
	        	                                    String sequence_="";
	        	                                    String sequence_try =seqWithMods;
	        	                                    try
	        	                                    {
	        	                                    	sequence_try=sequence_split[0];
        	                                    	}
	        	                                    finally
	        	                                    {
	        	                                    	sequence_=sequence_try;
        	                                    	}
	        	                                    
	        	                                    
	        	                                    currQuery.setSequence(sequence_);
	        	                                    currQuery.setZ(pep.getCharge());
	        	                                    currQuery.setIonsScore(pep.getIonsScore());
	        	                                    currQuery.setExpect(results.getPeptideExpectationValue(pep.getIonsScore(), q));
	       	        	                 	        currQuery.setMr_Exp(pep.getMrExperimental());
	       	        	                 	        currQuery.setRank(pep.getRank());
	        	                                    String title=inputquery.getStringTitle(true);
	        	                                    //System.out.println(title);
	        	                                    //System.out.println(inputquery.getPeakIntensity(1, 1));
	        	                                    
	        	                                    
	        	                                    
	        	                                    if(!(title.equals(""))) 
	        	                                    {
	        	                                        //Pattern pat = Pattern.compile(regex);
	        	                                        if(sequestDtaFormat==false)
	        	                                        {
		        	                                        String[] title_split;
		        	                                        
		        	                                        title_split=title.split(regex);
		        	                                        
		        	                                        for(int j=0;j<title_split.length;j++)
		        	                                        {
		        	                                        	//System.out.println(title_split[j]);	
		        	                                        	if(title_split[j].contains(ScanNumberParser))
		        	                                        	{	        	                                        		
		        	                                        		currQuery.setFirstScan(Integer.parseInt(title_split[j+1])); 
		        	                                        	}
		        	                                        	if(title_split[j].contains(rawFileParser))
		        	                                        	{
		        	                                        		currQuery.setRAWFileName(title_split[j+1]);
		        	                                        	}
		        	                                        
		        	                                        }	        	                                        
	        	                                        }
	        	                                        
	        	                                        if(sequestDtaFormat==true)
	        	                                        {
		        	                                        String[] title_split;
		        	                                        
		        	                                        title_split=title.split(regexDta);
		        	                                        
		        	                                        currQuery.setRAWFileName(title_split[0]+".RAW");
		        	                                        currQuery.setFirstScan(Integer.parseInt(title_split[1]));
	        	                                        	
	        	                                        }
	        	                                        
	        	                                        if(generateMGFs)
	        	         	        	               {	
	        	                                        	String mgfFile = filename(fil)+".mgf"; //.replace(".dat", ".mgf");
	        	                                        	
	        	         	        	            		currQuery.setRAWFileName(mgfFile);  
	        	         	                          		currQuery.setFirstScan(q); 
	        	         			        	              
	        	         	        	               }
        	                                        }	        	                 
	        	                                   
	        	                                    
	        	                                    //Write the peptide redundancies
	        	                                    int numOfRedundances = pep.getNumProteins() - 1;
	     	            	                        
	        	                                    if(numOfRedundances >0)
	        	                                    {
		        	                                    Redundance[] redundances = new Redundance[numOfRedundances];
		        	                                    
		        	                                    for(int k=0;k<numOfRedundances;k++)
		        	                                    {
		        	                                    	redundances[k]=new Redundance();
		        	                                    	ms_protein protRed = pep.getProtein(k+2);
		        	                                    	redundances[k].setFASTAIndex(k+1);
		        	                                    	redundances[k].setFASTAProteinDescription(protRed.getAccession());
		        	                                    }
		        	                                    
		        	                                    currQuery.setRedundances(redundances.clone());
	        	                                    }
	        	                                    
	        	                                    ionsPeakList.add(q);
		            	                        	
	        	                                    queries.add(currQuery);
	        	                                    
	        	                                    
	        	                                } else {
	        	                                    System.out.println("  No match");
	        	                     
	        	                                }
	        	                            	
	        	                            }
	        	                        }
	        	                    }

	        	                	
	        	                	prot_hit++;
	        	                }

	        	                
	        	               if(generateMGFs)
	        	               {
	        	            	   try    	
	        	               	{
	        	            		   
	        	            		String mgfFileName = fil.replace(".dat", ".mgf");   
	        	               		java.io.FileWriter fileWriter = new java.io.FileWriter(mgfFileName);
	        	           					
	        	               		
	        	           			
	        	           			for(Integer iq:ionsPeakList)
	        	           			{
	        	           				 ms_inputquery inputquery= new ms_inputquery(mascot_file, iq);
	        	           				 
	        	           				 String title = inputquery.getStringTitle(true);
	        	           				 String charge = inputquery.getCharge();
	        	           				 String pepMass = inputquery.getPepTolString();
	        	           				 //Falta el pepMass
	        	           				 
	        	           				 String ions = inputquery.getStringIons1();
	        	           				
	        	           				 String[] ionsList = ions.split(",");
	        	           				 
	        	           				 fileWriter.write("BEGIN IONS");
	        	           				 fileWriter.write( '\n' );
	        	           				 fileWriter.write( "PEPMASS="+ pepMass);
	        	           				 fileWriter.write( '\n' );
	        	           				 fileWriter.write( "CHARGE="+ charge );
	        	           				 fileWriter.write( '\n' );
	        	           				 fileWriter.write( "TITLE="+ "query: " + iq.toString()+" , " + title);
	        	           				 fileWriter.write( '\n' );
	        	           				 
	        	           				 for(int in=0;in<ionsList.length;in++)
	        	           				 {
	        	           					 fileWriter.write(ionsList[in].split(":")[0]+'\t'+ionsList[in].split(":")[1]);
	        	           					 fileWriter.write( '\n' );					 
	        	           				 }

	        	           			 	fileWriter.write("END IONS");
	        	           				 
	        	           			}
	        	               	
	        	           			fileWriter.close();
	        	               	
	        	               	} catch (IOException e) {
	        	           			e.printStackTrace();
	        	           		}
	        	               }
	        	            
	        	            }
	        	            
	        	            
	        	      }
	        	}
	        
	        }
	        
	        
	        //Write the QuiXML
	        String outputFile= folderName + "\\" +"merge_QuiXML.xml"; 
	        QuiXMLcreator quiXML = new QuiXMLcreator();	        
	        quiXML.writeQuiXML(queries, outputFile, maxRank, maxExpect);
	        
	        //write the variable modifications list
    		outputFile = folderName +"\\"+"variableModifications.txt";
	        writeVarModsFile(outputFile,vmods);	        		

	        
	        
        }
  

        
        
} 

    public static String filename(String fullPath) 
    {  // gets filename without extension
    	char extensionSeparator='.';
    	char pathSeparator='\\';
    	
        int dot = fullPath.lastIndexOf(extensionSeparator);
        int sep = fullPath.lastIndexOf(pathSeparator);
        return fullPath.substring(sep + 1, dot);
    }
    
    public static String getSequenceWithMods(	String[] terminalResidues,
    											String sequence,
												ArrayList<Varmods> varModifications,
												String modifsInSequence)
    {
    	
    	String finalSequence;
    	
    	char[] modifsInSequence_split=modifsInSequence.toCharArray();
 
    	char[] chSeq=sequence.toCharArray();
    	
    	String[] sequenceInArray=new String[chSeq.length];
    	for(int i=0;i<chSeq.length;i++)
    	{
    		sequenceInArray[i]=String.valueOf(chSeq[i]);
    	}
    	   	

    	
    	//Cterm modification: must be added at the beginning of the sequence
    	if(modifsInSequence_split[0]!='0')
    	{
    		for(Varmods b:varModifications) 
    		{
    			int idxTarget=Integer.parseInt(String.valueOf(b.getIndex()));
    			int idxCurr=Integer.parseInt(String.valueOf(modifsInSequence_split[0]));
    			if(idxCurr == idxTarget)
    			{
    				sequenceInArray[0]=String.valueOf(b.getAssignedChar())+
    									sequenceInArray[0];
    			}    			
    		}
    	}

    	
       	for(int i=1;i<modifsInSequence_split.length-1;i++)
    	{
    		if(modifsInSequence_split[i]!='0')
    		{ 
        		for(Varmods b:varModifications) 
        		{
        			int idxTarget=Integer.parseInt(String.valueOf(b.getIndex()));
        			int idxCurr=Integer.parseInt(String.valueOf(modifsInSequence_split[i]));
        			if(idxCurr == idxTarget)
        			{
        				sequenceInArray[i-1]=sequenceInArray[i-1] + 
        									 String.valueOf(b.getAssignedChar());
        			}    			
        		}    			
    		}
    	}
 
    	
    	//Obtain the final sequence by joining the array sequenceInArray
    	finalSequence="";
    	for(int i=0;i<sequenceInArray.length;i++)
    	{
    		finalSequence=finalSequence+sequenceInArray[i];
    	}
    	
    	finalSequence = terminalResidues[0]+"."+finalSequence+"."+terminalResidues[1];
        
    	
    	return finalSequence;
    }
   
    
    public static void writeVarModsFile(String outputFile,ArrayList<Varmods> varModifs)
    {
    	try    	
    	{
			java.io.FileWriter fileWriter = new java.io.FileWriter(outputFile);
			
			for(Varmods vm:varModifs)
			{
				fileWriter.write(vm.getIndex()+" : "+vm.getName());
				fileWriter.write( '\n' );
				fileWriter.write("assigned char : "+ vm.getAssignedChar() + " delta mass : " + vm.getDeltaMass());
				fileWriter.write( '\n' );					
			}
    	
			fileWriter.close();
    	
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
    	
    }
 
    
}
    
    
    
