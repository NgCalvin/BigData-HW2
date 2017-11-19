import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;	
import java.util.ArrayList;

public class PageRank 
{
	static double DampingFactor = 0.85;
	static double stoppingFactor = 0.0001;
	static int numOfNodes = 875713;
	static int step = 1;
	static int maxStep = 2;
	static ArrayList<ArrayList<String>> rNew = new ArrayList<ArrayList<String>>();
	static ArrayList<ArrayList<String>> rOld = new ArrayList<ArrayList<String>>();

	public static void main(String[] args)
	{
		while(step < maxStep)
		{
			try 
			{
				update();
				cleanup();
				
				if(checkStop())
				{
					System.out.println("PageRank stopping critrion reached. Program stoped.");
					System.exit(0);
				}
				step++;
				System.out.println("PageRank step " + step + "Done" );
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void update() throws IOException
	{
		int counter = 1;
		//This 3 lines make sure that each iteration creates a new file of PageRank Score, just for allowing us to see intermediate result.
		String readPath = "src/r" + (step-1) + ".txt";
		
		//Reading in all R new and set score to 0 for update process.
		try(BufferedReader br2 = new BufferedReader(new FileReader(readPath)))
		{
			for(String line; (line = br2.readLine())!=null;)
			{
				String[] temp2 = line.split(",");
				ArrayList<String> tempLine = new ArrayList<String>();
				tempLine.add(temp2[0]);
				tempLine.add(String.valueOf((1-DampingFactor)/numOfNodes));
				rNew.add(tempLine);
			}
			System.out.println("Done initiating in R new.");
			br2.close();
		}

		System.out.println("Starting PageRank!");
		
		//Reading in the pre-processed file line by line and process them.
		try(BufferedReader br = new BufferedReader(new FileReader("src/pre-processed.txt")))
		{
			try(BufferedReader br2 = new BufferedReader(new FileReader(readPath)))
			{
				//Skipping first Line of both document. It is just description of the format.
				br.readLine();
				br2.readLine();
				
				//Looping each line from the Matrix to change the pageRank Score
				for(String line; (line = br.readLine())!=null;)
				{
					//Splitting line into String[] by delimiter comma.
					String[] temp = line.split(",");
					//Just for calculation
					Double degree = Double.parseDouble(temp[1]);
					
					//Reading r old values of the current checking page.
					String line2 = br2.readLine();
					String[] temp2 = line2.split(",");
					
					//The Real update
					for(int i = 2 ; i < temp.length ; i++)
					{
						for(int j = 0 ; j < rNew.size() ; j++)
						{
							if(rNew.get(j).get(0).equals(temp[i]))
							{
								Double intermediate = Double.parseDouble(rNew.get(j).get(1)) + ((DampingFactor * Double.parseDouble(temp2[1])) / degree);

								rNew.get(j).remove(1);
								rNew.get(j).add(String.valueOf(intermediate));
								break;
							}
						}
					}
					counter++;
					System.out.println(counter + "/ " + numOfNodes);
				}

			}
			br.close();
			}
		
		System.out.println("Finish PageRank algorithm!");
	}
	
	public static boolean checkStop() throws FileNotFoundException, IOException
	{
		boolean checker = false;
		//This 3 lines make sure that each iteration creates a new file of PageRank Score, just for allowing us to see intermediate result.
		String rnew = "src/r" + step + ".txt";
		double sumRnew = 0;
		String rold = "src/r" + (step-1) + ".txt";
		double sumRold = 0;
		
		//Reading in the pre-processed file line by line and process them.
		try(BufferedReader br = new BufferedReader(new FileReader(rnew)))
		{
			//Skipping first Line. It is just description of the format.
			br.readLine();
			
			for(String line; (line = br.readLine())!=null;)
			{
				String[] temp = line.split(",");
				sumRnew += Double.parseDouble(temp[1]);
			}
		}
		
		try(BufferedReader br = new BufferedReader(new FileReader(rold)))
		{
			//Skipping first Line. It is just description of the format.
			br.readLine();
			
			for(String line; (line = br.readLine())!=null;)
			{
				String[] temp = line.split(",");
				sumRold += Double.parseDouble(temp[1]);
			}
		}
		
		System.out.println(Math.abs(sumRnew-sumRold));
		if(Math.abs((sumRnew - sumRold)) < stoppingFactor)
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	public static void cleanup() throws IOException
	{
		System.out.println("Start cleaning up!");
		//Configuring where to write based on number on iteration PR goes
		String outPath = "src/r" + step + ".txt";
		//Writing intermediate result for r new score and later combining.
		PrintWriter pw = new PrintWriter(new File(outPath));
		pw.println("Node ID, PageRank Score");

		for(int i = 0 ; i < rNew.size() ; i++)
		{
			pw.println(rNew.get(i).get(0) + "," + rNew.get(i).get(1) );
		}
		pw.flush();
		pw.close();
		
		System.out.println("Done Cleaning");
	}

}

