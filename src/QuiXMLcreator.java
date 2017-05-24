import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;

//import java.util.List;

//import javax.xml.crypto.Data;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

//For jdk1.5 with built in xerces parser
//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class QuiXMLcreator 
{
	
	ArrayList<OutData> data;
	Document dom;

	@SuppressWarnings("unchecked")
	public void writeQuiXML(ArrayList<OutData> inputData, 
							String outputFile, 
							int maxRank, 
							double maxExpect)
	{
		data = (ArrayList<OutData>)inputData.clone();
		
		createDocument();
		createDOMTree(maxRank, maxExpect);
		printToFile(outputFile);
		System.out.println("Generated file successfully.");

		
		
	}
	
	
	private void printToFile(String outputFile) 
	{
		try
		{
			//print
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);

			//to generate output to console use this serializer
			//XMLSerializer serializer = new XMLSerializer(System.out, format);


			//to generate a file output use fileoutputstream instead of system.out
			XMLSerializer serializer = new XMLSerializer(
			new FileOutputStream(new File(outputFile)), format);
	
			serializer.serialize(dom);

		} catch(IOException ie) {
		    ie.printStackTrace();
		}
		
	}


	/**
	 * Using JAXP in implementation independent manner create a document object
	 * using which we create a xml tree in memory
	 */
	private void createDocument() {

		//get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
		//get an instance of builder
		DocumentBuilder db = dbf.newDocumentBuilder();

		//create an instance of DOM
		dom = db.newDocument();

		}catch(ParserConfigurationException pce) {
			//dump it
			System.out.println("Error while trying to instantiate DocumentBuilder " + pce);
			System.exit(1);
		}

	}

	
	
	/**
	 * The real workhorse which creates the XML structure
	 */
	private void createDOMTree(int maxRank, double maxExpect){

		//create the root element <IdentificationArchive> & <Identifications>
		Element rootEle = dom.createElement("IdentificationArchive");
		dom.appendChild(rootEle);
		Element identificationsEle = dom.createElement("Identifications");
		rootEle.appendChild(identificationsEle);
		

		//No enhanced for
		Iterator<OutData> it  = data.iterator();
		while(it.hasNext()) {
			OutData b = (OutData)it.next();
			//For each OutData object  create the peptide_match element and attach it to root
			if(b.getRank()<=maxRank && b.getExpect()<=maxExpect)
			{
				Element peptide_m = createPepMatchElement(b);
				identificationsEle.appendChild(peptide_m);
			}
		}
			

	}
	
	
	/**
	 * Helper method which creates a XML element <Book>
	 * @param b The book for which we need to create an xml representation
	 * @return XML element snippet representing a book
	 */
	private Element createPepMatchElement(OutData b){

		Element pepMatchEle = dom.createElement("peptide_match");
		//pepMatchEle.setAttribute("Subject", b.getProteinDescription());

		//create elements and attach them to peptide_match Element
		Element RAWFileNameEle = dom.createElement("RAWFileName");
		Element FileNameEle = dom.createElement("FileName");
		Element FirstScanEle = dom.createElement("FirstScan");
		Element ChargeEle = dom.createElement("Charge");
		Element FASTAProteinDescriptionEle = dom.createElement("FASTAProteinDescription");
		Element SequenceEle = dom.createElement("Sequence");
		Element Mr_expEle = dom.createElement("Mr_exp");
		Element MascotScoreEle = dom.createElement("MascotScore");
		Element expectEle = dom.createElement("expect");
		Element rankEle = dom.createElement("rank");
		
		Text             RAWFileNameText = dom.createTextNode(b.getRAWFileName());   
		Text                FileNameText = dom.createTextNode(b.getFileName());   
		Text               FirstScanText = dom.createTextNode(String.valueOf(b.getFirstScan()));   
		Text                  ChargeText = dom.createTextNode(String.valueOf(b.getZ()));   
		Text FASTAProteinDescriptionText = dom.createTextNode(b.getProteinDescription());   
		Text                SequenceText = dom.createTextNode(b.getSequence());   
		Text             Mr_expText = dom.createTextNode(String.valueOf(b.getMr_Exp()));   
		Text             MascotScoreText = dom.createTextNode(String.valueOf(b.getIonsScore()));  
		Text             expectText = dom.createTextNode(String.valueOf(b.getExpect()));  
        Text              rankText = dom.createTextNode(String.valueOf(b.getRank()));
		
		
	    FileNameEle.appendChild(FileNameText);         
	    RAWFileNameEle.appendChild(RAWFileNameText);         
        FirstScanEle.appendChild(FirstScanText);         
        ChargeEle.appendChild(ChargeText);         
        FASTAProteinDescriptionEle.appendChild(FASTAProteinDescriptionText);         
        SequenceEle.appendChild(SequenceText);         
        Mr_expEle.appendChild(Mr_expText);         
        MascotScoreEle.appendChild(MascotScoreText); 
        expectEle.appendChild(expectText);
        rankEle.appendChild(rankText);
 
        pepMatchEle.appendChild(RAWFileNameEle);
        pepMatchEle.appendChild(FileNameEle);
        pepMatchEle.appendChild(FirstScanEle);
        pepMatchEle.appendChild(ChargeEle);
        pepMatchEle.appendChild(FASTAProteinDescriptionEle);
        pepMatchEle.appendChild(SequenceEle);
        pepMatchEle.appendChild(Mr_expEle);
        pepMatchEle.appendChild(MascotScoreEle);
        pepMatchEle.appendChild(expectEle);
        pepMatchEle.appendChild(rankEle);

    	Redundance[] pepMatch_Redundances = b.getRedundances();

    	
        if(pepMatch_Redundances!=null)
        {
           	Element RedundancesEle = dom.createElement("Redundances");
            
        	
        	for(Redundance r : pepMatch_Redundances)
        	{
        		Element redEle = dom.createElement("Red");
        		
        		Element redFASTAIndexEle = dom.createElement("FASTAIndex");
        		Element redFASTAProteinDescriptionEle = dom.createElement("FASTAProteinDescription");

        		Text redFASTAIndexText = dom.createTextNode(String.valueOf(r.getFASTAIndex()));   
        		Text redFASTAProteinDescriptionText = dom.createTextNode(r.getFASTAProteinDescription());   

        		redFASTAIndexEle.appendChild(redFASTAIndexText);
        		redFASTAProteinDescriptionEle.appendChild(redFASTAProteinDescriptionText);
        		
        		redEle.appendChild(redFASTAIndexEle);
        		redEle.appendChild(redFASTAProteinDescriptionEle);
        		
        		RedundancesEle.appendChild(redEle);        		
        	}
        	
        	pepMatchEle.appendChild(RedundancesEle);
        }
        
        
		return pepMatchEle;

	}


	
	
}
